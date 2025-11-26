<script setup>
import { ref, watch, onMounted, onBeforeUnmount, computed } from 'vue';
import RoleSection from '../components/RoleSection.vue';
import UserQrCard from '../components/UserQrCard.vue';
import { searchAccessLogs } from '../services/accessLogService';
import { useSession } from '../stores/session';
import { useUi } from '../stores/ui';
import { offlineQueue, addPending, flushQueue, loadQueue } from '../stores/offlineQueue';
import { processAccessEntry } from '../services/offlineSyncService';

const actions = [
  'Login JWT y refresco del token',
  'Escaneo de QR con la cámara del dispositivo',
  'Confirmar entrada o salida y recibir feedback inmediato',
];

const checklist = [
  'Mostrar hora y turno asignado',
  'Modo offline con cola de accesos pendientes',
  'Historial resumido de las últimas visitas',
];

const recent = ref([]);
const activeOpen = ref([]);
const loading = ref(false);
const error = ref('');

const session = useSession();
const ui = useUi();
const registerForm = ref({
  motivo: '',
  qrCode: session.qrCode.value || '',
});

const closeForm = ref({
  accessId: '',
});

const syncing = ref(false);
const assignedQr = computed(() => session.qrCode.value || '');
const hasAssignedQr = computed(() => Boolean(session.qrCode.value));
const qrDownloadName = computed(() => `${session.username.value || 'mi-qr'}.png`);

function formatLocalDateTime(date = new Date()) {
  const offsetMs = date.getTimezoneOffset() * 60000;
  return new Date(date.getTime() - offsetMs).toISOString().slice(0, 19);
}

async function loadData({ silent = false } = {}) {
  if (!session.isAuthenticated.value) {
    recent.value = [];
    activeOpen.value = [];
    return;
  }
  loading.value = true;
  error.value = '';
  try {
    const qrFilter = session.qrCode.value || registerForm.value.qrCode;
    const data = await searchAccessLogs({ qr: qrFilter });
    const sorted = [...data].sort((a, b) => new Date(b.fechaHoraEntrada) - new Date(a.fechaHoraEntrada));
    recent.value = sorted.slice(0, 5);
    activeOpen.value = sorted.filter((item) => !item.fechaHoraSalida);
    if (!silent) {
      ui.notifySuccess('Tus accesos se han actualizado');
    }
  } catch (err) {
    error.value = err.response?.status === 403
      ? 'Tu rol no tiene acceso a estos datos.'
      : 'No fue posible cargar tus accesos.';
  } finally {
    loading.value = false;
  }
}

watch(
  () => session.isAuthenticated.value,
  () => loadData({ silent: true }),
  { immediate: true },
);

async function handleRegister() {
  if (!registerForm.value.qrCode) {
    ui.notifyWarning('Escanea o introduce un QR primero.');
    return;
  }
  const payload = {
    nombrePersona: session.username.value || 'Trabajador',
    tipoUsuario: 'trabajador',
    motivo: registerForm.value.motivo || 'Entrada QR',
    qrCode: registerForm.value.qrCode,
    fechaHoraEntrada: formatLocalDateTime(),
  };

  const entry = { action: 'create', payload };

  if (navigator.onLine) {
    try {
      await processAccessEntry(entry);
      registerForm.value.motivo = '';
      await loadData({ silent: true });
    } catch (err) {
      ui.notifyError('No se pudo registrar el acceso, se guardará offline.');
      await addPending(entry);
    }
  } else {
    await addPending(entry);
  }
}

async function handleClose() {
  if (!closeForm.value.accessId) {
    ui.notifyWarning('Selecciona el acceso que quieres cerrar.');
    return;
  }
  const accessId = Number(closeForm.value.accessId);
  const selectedAccess = activeOpen.value.find((item) => item.id === accessId);
  if (!selectedAccess) {
    ui.notifyWarning('No se encontró el acceso seleccionado.');
    return;
  }
  const payload = {
    ...selectedAccess,
    fechaHoraSalida: formatLocalDateTime(),
  };
  const entry = {
    action: 'close',
    payload: {
      id: accessId,
      body: payload,
    },
  };

  if (navigator.onLine) {
    try {
      await processAccessEntry(entry);
      closeForm.value.accessId = '';
      await loadData({ silent: true });
    } catch (err) {
      ui.notifyError('No se pudo cerrar el acceso, se guardará offline.');
      await addPending(entry);
    }
  } else {
    await addPending(entry);
  }
}

function useOwnQr() {
  if (session.qrCode.value) {
    registerForm.value.qrCode = session.qrCode.value;
  } else {
    ui.notifyWarning('Aún no tienes un QR asignado.');
  }
}

let flushInterval;

onMounted(async () => {
  await loadQueue();
  flushInterval = setInterval(async () => {
    if (navigator.onLine && offlineQueue.state.pending.length > 0 && !syncing.value) {
      try {
        syncing.value = true;
        await flushQueue(processAccessEntry);
      } finally {
        syncing.value = false;
      }
    }
  }, 5000);
});

onBeforeUnmount(() => {
  if (flushInterval) {
    clearInterval(flushInterval);
  }
});

watch(
  () => session.qrCode.value,
  (value) => {
    if (value && !registerForm.value.qrCode) {
      registerForm.value.qrCode = value;
    }
  },
);
</script>

<template>
  <RoleSection
    title="Área del trabajador"
    emoji="🦺"
    description="Pantalla pensada para operarios internos: al iniciar sesión ven el lector de QR y un resumen de su estado actual."
    access-type="Autenticado"
    :actions="actions"
    :checklist="checklist"
  />

  <section class="worker-registration" v-if="session.isAuthenticated.value">
    <div class="qr-wrapper">
      <UserQrCard
        :value="assignedQr"
        label="Mi QR personal"
        :download-name="qrDownloadName"
      />
      <p class="hint">Descarga tu QR y preséntalo en el control de acceso para registrar entrada/salida.</p>
      <div class="own-qr">
        <div>
          <span>Mi QR asignado</span>
          <strong>{{ hasAssignedQr ? assignedQr : 'Sin asignar' }}</strong>
        </div>
        <button
          type="button"
          class="use-qr-btn"
          @click="useOwnQr"
          :disabled="!hasAssignedQr"
        >
          Copiar/usar mi QR
        </button>
      </div>
    </div>

    <form class="register-form" @submit.prevent="handleRegister">
      <label>
        QR (manual)
        <input v-model="registerForm.qrCode" placeholder="QR-TRAB-001" />
      </label>
      <label>
        Motivo
        <input v-model="registerForm.motivo" placeholder="Inicio de turno" />
      </label>
      <button type="submit">Registrar entrada manual</button>
      <p v-if="offlineQueue.state.pending.length" class="warning">
        {{ offlineQueue.state.pending.length }} acceso(s) pendiente(s) por sincronizar.
      </p>
    </form>

    <form class="close-form" @submit.prevent="handleClose">
      <label>
        Accesos abiertos (sólo los tuyos)
        <select v-model="closeForm.accessId">
          <option value="" disabled>Selecciona un acceso</option>
          <option
            v-for="item in activeOpen"
            :key="item.id"
            :value="item.id"
          >
            {{ item.nombrePersona }} · {{ item.fechaHoraEntrada }}
          </option>
        </select>
      </label>
      <button type="submit">Registrar salida</button>
    </form>
  </section>

  <section class="worker-data">
    <div class="toolbar" v-if="session.isAuthenticated.value">
      <button type="button" @click="loadData()">Actualizar datos</button>
      <button type="button" @click="flushQueue(processAccessEntry)" :disabled="syncing">
        Sincronizar pendientes
        <span v-if="offlineQueue.state.pending.length" class="badge">{{ offlineQueue.state.pending.length }}</span>
      </button>
    </div>
    <p v-if="loading">Cargando datos...</p>
    <p v-else-if="error" class="error">{{ error }}</p>

    <div v-if="recent.length" class="recent">
      <h3>Mis últimos accesos</h3>
      <ul>
        <li v-for="item in recent" :key="item.id">
          <div>
            <strong>{{ item.nombrePersona }}</strong>
            <small>{{ item.tipoUsuario }}</small>
          </div>
          <span>{{ item.fechaHoraEntrada }}</span>
        </li>
      </ul>
    </div>
  </section>
</template>

<style scoped>
.worker-data {
  margin-top: 1.5rem;
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid rgba(148, 163, 184, 0.2);
  border-radius: 1.25rem;
  padding: 1.5rem;
}

.toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 1rem;
}

.toolbar button {
  border: 1px solid rgba(59, 130, 246, 0.5);
  background: transparent;
  color: #bfdbfe;
  padding: 0.4rem 0.8rem;
  border-radius: 0.75rem;
  cursor: pointer;
}

.toolbar button:hover {
  border-color: rgba(59, 130, 246, 0.8);
}

.widgets {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 1rem;
}

.widgets article {
  background: rgba(59, 130, 246, 0.08);
  border-radius: 0.85rem;
  padding: 1rem;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.widgets span {
  color: var(--muted-color);
  font-size: 0.85rem;
}

.widgets strong {
  display: block;
  margin-top: 0.35rem;
  font-size: 1.4rem;
}

.recent {
  margin-top: 1.5rem;
}

.recent h3 {
  margin: 0 0 1rem;
}

.recent ul {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.85rem;
}

.recent li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
  padding-bottom: 0.6rem;
}

.recent strong {
  display: block;
}

.recent small {
  color: var(--muted-color);
  text-transform: capitalize;
}

.error {
  color: #fca5a5;
}

.worker-registration {
  margin-top: 1.5rem;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1rem;
}

.scanner-box,
.register-form,
.close-form,
.qr-wrapper {
  background: rgba(15, 23, 42, 0.65);
  border: 1px solid rgba(148, 163, 184, 0.2);
  border-radius: 1.25rem;
  padding: 1.2rem;
}

.scanner-controls {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  margin-top: 0.75rem;
}

video {
  width: 100%;
  min-height: 220px;
  border-radius: 0.9rem;
  background: rgba(15, 23, 42, 0.3);
}

select,
input {
  width: 100%;
  padding: 0.55rem;
  border-radius: 0.7rem;
  border: 1px solid rgba(148, 163, 184, 0.4);
  background: transparent;
  color: inherit;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.own-qr {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.6rem 0.8rem;
  border: 1px dashed rgba(148, 163, 184, 0.35);
  border-radius: 0.8rem;
  font-size: 0.9rem;
}

.own-qr strong {
  display: block;
  font-size: 1.1rem;
}

.use-qr-btn {
  border-color: rgba(59, 130, 246, 0.6);
  color: #bfdbfe;
}

.register-form button,
.close-form button {
  border: 1px solid rgba(34, 197, 94, 0.5);
  background: transparent;
  color: #bbf7d0;
  padding: 0.5rem 0.8rem;
  border-radius: 0.75rem;
  cursor: pointer;
}

.close-form {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.warning {
  color: #fde68a;
}
</style>
