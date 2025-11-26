<script setup>
import { ref, watch, onMounted, computed } from 'vue';
import RoleSection from '../components/RoleSection.vue';
import UserQrCard from '../components/UserQrCard.vue';
import { searchAccessLogs, fetchRecent } from '../services/accessLogService';
import { useSession } from '../stores/session';
import { useUi } from '../stores/ui';

const actions = [
  'Login JWT y refresco del token',
  'Descarga y uso de su QR personal',
  'Ver su historial de accesos reciente',
];

const checklist = [
  'Mostrar QR asignado siempre disponible',
  'Historial resumido de las últimas visitas',
];

const recent = ref([]);
const loading = ref(false);
const error = ref('');

const session = useSession();
const ui = useUi();
const assignedQr = computed(() => session.qrCode.value || '');
const hasAssignedQr = computed(() => Boolean(session.qrCode.value));
const qrDownloadName = computed(() => `${session.username.value || 'mi-qr'}.png`);

async function loadData({ silent = false } = {}) {
  if (!session.isAuthenticated.value) {
    recent.value = [];
    return;
  }
  loading.value = true;
  error.value = '';
  try {
    const qrFilter = session.qrCode.value;
    let data = [];
    try {
      data = await searchAccessLogs({ qr: qrFilter });
    } catch (innerErr) {
      // fallback a recientes globales si el endpoint filtrado falla
      const recentGlobal = await fetchRecent(10);
      data = recentGlobal.filter((item) => !qrFilter || item.qrCode === qrFilter);
      if (!data.length) {
        throw innerErr;
      }
    }
    const sorted = [...data].sort((a, b) => new Date(b.fechaHoraEntrada) - new Date(a.fechaHoraEntrada));
    recent.value = sorted.slice(0, 5);
    if (!silent) {
      ui.notifySuccess('Tus accesos se han actualizado');
    }
  } catch (err) {
    const message = err.response?.data?.error
      ?? (err.response?.status === 403 ? 'Tu rol no tiene acceso a estos datos.' : 'No fue posible cargar tus accesos.');
    error.value = message;
  } finally {
    loading.value = false;
  }
}

watch(
  () => session.isAuthenticated.value,
  () => loadData({ silent: true }),
  { immediate: true },
);

function handleCopyQr() {
  if (!assignedQr.value) {
    ui.notifyWarning('Aún no tienes un QR asignado.');
    return;
  }
  navigator.clipboard?.writeText(assignedQr.value).then(() => {
    ui.notifySuccess('QR copiado al portapapeles.');
  }).catch(() => {
    ui.notifyError('No se pudo copiar el QR.');
  });
}

onMounted(() => {
  if (session.isAuthenticated.value) {
    loadData({ silent: true });
  }
});

watch(
  () => session.qrCode.value,
  (value) => {
    if (value) {
      loadData({ silent: true });
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
          @click="handleCopyQr"
          :disabled="!hasAssignedQr"
        >
          Copiar/usar mi QR
        </button>
      </div>
    </div>
  </section>

  <section class="worker-data">
    <div class="toolbar" v-if="session.isAuthenticated.value">
      <button type="button" @click="loadData()">Actualizar datos</button>
    </div>
    <p v-if="loading">Cargando datos...</p>
    <p v-else-if="error" class="error">{{ error }}</p>

    <div v-if="recent.length" class="recent">
      <h3>Mis últimos accesos</h3>
      <ul>
        <li v-for="item in recent" :key="item.id">
          <div class="access-details">
            <div class="header">
              <strong>{{ item.nombrePersona }}</strong>
              <span class="status-chip" :class="{ open: !item.fechaHoraSalida }">
                {{ item.fechaHoraSalida ? 'Salida registrada' : 'Entrada abierta' }}
              </span>
            </div>
            <small class="muted">Tipo: {{ item.tipoUsuario }}</small>
            <small>Entrada: {{ item.fechaHoraEntrada }}</small>
            <small>Salida: {{ item.fechaHoraSalida || '—' }}</small>
          </div>
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
  flex-direction: column;
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

.qr-wrapper {
  background: rgba(15, 23, 42, 0.65);
  border: 1px solid rgba(148, 163, 184, 0.2);
  border-radius: 1.25rem;
  padding: 1.2rem;
}

.hint {
  color: var(--muted-color);
}

.worker-registration {
  margin-top: 1.5rem;
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

.access-details {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}

.access-details .header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.5rem;
}

.status-chip {
  padding: 0.15rem 0.55rem;
  border-radius: 999px;
  border: 1px solid rgba(34, 197, 94, 0.5);
  color: #bbf7d0;
  font-size: 0.8rem;
}

.status-chip.open {
  border-color: rgba(234, 179, 8, 0.7);
  color: #fcd34d;
}

.muted {
  color: var(--muted-color);
}
</style>
