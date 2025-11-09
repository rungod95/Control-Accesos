<script setup>
import { ref, watch } from 'vue';
import RoleSection from '../components/RoleSection.vue';
import { fetchSummary, fetchRecent } from '../services/accessLogService';
import { useSession } from '../stores/session';
import { useUi } from '../stores/ui';

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

const summary = ref(null);
const recent = ref([]);
const loading = ref(false);
const error = ref('');

const session = useSession();
const ui = useUi();

async function loadData({ silent = false } = {}) {
  if (!session.isAuthenticated.value) {
    summary.value = null;
    recent.value = [];
    return;
  }

  loading.value = true;
  error.value = '';
  try {
    const [summaryData, recentData] = await Promise.all([
      fetchSummary(),
      fetchRecent(5),
    ]);
    summary.value = summaryData;
    recent.value = recentData;
    if (!silent) {
      ui.notifySuccess('Resumen de accesos actualizado');
    }
  } catch (err) {
    error.value = err.response?.status === 403
      ? 'Tu rol no tiene acceso a los datos de accesos.'
      : 'No fue posible cargar la información.';
  } finally {
    loading.value = false;
  }
}

watch(
  () => session.isAuthenticated.value,
  () => loadData({ silent: true }),
  { immediate: true },
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

  <section class="worker-data">
    <div class="toolbar" v-if="session.isAuthenticated.value">
      <button type="button" @click="loadData()">Actualizar datos</button>
    </div>
    <p v-if="loading">Cargando datos...</p>
    <p v-else-if="error" class="error">{{ error }}</p>

    <div v-else class="widgets" v-if="summary">
      <article>
        <span>Activos</span>
        <strong>{{ summary.activos }}</strong>
      </article>
      <article>
        <span>Total hoy</span>
        <strong>{{ summary.hoy }}</strong>
      </article>
      <article>
        <span>Últimos 7 días</span>
        <strong>{{ summary.ultimaSemana }}</strong>
      </article>
    </div>

    <div v-if="recent.length" class="recent">
      <h3>Últimos accesos</h3>
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
</style>
