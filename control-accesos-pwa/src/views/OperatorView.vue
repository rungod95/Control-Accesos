<script setup>
import { ref, watch } from 'vue';
import RoleSection from '../components/RoleSection.vue';
import { fetchSummary, fetchActive } from '../services/accessLogService';
import { useSession } from '../stores/session';
import { useUi } from '../stores/ui';

const actions = [
  'Panel en tiempo real con accesos activos',
  'Búsqueda y filtros por tipo de usuario, motivo o QR',
  'Descarga rápida de reportes CSV/PDF',
];

const checklist = [
  'Forzar cierre de accesos o marcar incidencias',
  'Alertas cuando un QR caduca o se usa fuera de horario',
  'Modo tablet con controles grandes',
];

const summary = ref(null);
const active = ref([]);
const loading = ref(false);
const error = ref('');

const session = useSession();
const ui = useUi();

async function loadData({ silent = false } = {}) {
  if (!session.isAuthenticated.value) {
    summary.value = null;
    active.value = [];
    return;
  }

  loading.value = true;
  error.value = '';
  try {
    const [summaryData, activeData] = await Promise.all([
      fetchSummary(),
      fetchActive(),
    ]);
    summary.value = summaryData;
    active.value = activeData;
    if (!silent) {
      ui.notifySuccess('Panel del operador actualizado');
    }
  } catch (err) {
    error.value = err.response?.status === 403
      ? 'Este usuario no tiene permisos de operador.'
      : 'No fue posible obtener los accesos activos.';
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
    title="Área del operador"
    emoji="🛠️"
    description="Vista de control situada en garita/planta. Desde aquí se valida cada entrada y se supervisa la ocupación de la mina."
    access-type="Autenticado"
    :actions="actions"
    :checklist="checklist"
  />

  <section class="operator-panel">
    <div class="toolbar" v-if="session.isAuthenticated.value">
      <button type="button" @click="loadData()">Refrescar tablero</button>
    </div>
    <p v-if="loading">Cargando tablero...</p>
    <p v-else-if="error" class="error">{{ error }}</p>

    <div v-else>
      <div class="grid" v-if="summary">
        <article>
          <span>Personas dentro</span>
          <strong>{{ summary.activos }}</strong>
        </article>
        <article>
          <span>Total accesos hoy</span>
          <strong>{{ summary.hoy }}</strong>
        </article>
        <article>
          <span>Registros totales</span>
          <strong>{{ summary.total }}</strong>
        </article>
      </div>

      <div class="active-list" v-if="active.length">
        <h3>Accesos abiertos</h3>
        <table>
          <thead>
            <tr>
              <th>Persona</th>
              <th>Tipo</th>
              <th>Entrada</th>
              <th>QR</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="log in active" :key="log.id">
              <td>{{ log.nombrePersona }}</td>
              <td class="badge">{{ log.tipoUsuario }}</td>
              <td>{{ log.fechaHoraEntrada }}</td>
              <td>{{ log.qrCode || '—' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <p v-else class="empty">No hay accesos abiertos.</p>
    </div>
  </section>
</template>

<style scoped>
.operator-panel {
  margin-top: 1.5rem;
  background: rgba(2, 6, 23, 0.8);
  border-radius: 1.25rem;
  border: 1px solid rgba(148, 163, 184, 0.2);
  padding: 1.5rem;
}

.toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 1rem;
}

.toolbar button {
  border: 1px solid rgba(34, 197, 94, 0.5);
  background: transparent;
  color: #bbf7d0;
  padding: 0.4rem 0.8rem;
  border-radius: 0.75rem;
  cursor: pointer;
}

.toolbar button:hover {
  border-color: rgba(34, 197, 94, 0.8);
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.grid article {
  padding: 1rem;
  border-radius: 0.9rem;
  border: 1px solid rgba(34, 197, 94, 0.3);
  background: rgba(34, 197, 94, 0.1);
}

.grid span {
  color: var(--muted-color);
  font-size: 0.85rem;
}

.grid strong {
  display: block;
  font-size: 1.5rem;
  margin-top: 0.3rem;
}

.active-list table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  padding: 0.65rem;
  text-align: left;
}

thead {
  background: rgba(148, 163, 184, 0.15);
}

tbody tr {
  border-bottom: 1px solid rgba(148, 163, 184, 0.15);
}

.badge {
  text-transform: capitalize;
}

.empty {
  color: var(--muted-color);
}

.error {
  color: #fca5a5;
}
</style>
