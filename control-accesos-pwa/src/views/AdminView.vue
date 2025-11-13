<script setup>
import { ref, watch } from 'vue';
import RoleSection from '../components/RoleSection.vue';
import { fetchUsers } from '../services/userService';
import { fetchActive, closeAccess } from '../services/accessLogService';
import { useSession } from '../stores/session';
import { useUi } from '../stores/ui';

const actions = [
  'Gestión de usuarios, roles y permisos',
  'Generación de QR (permanentes y temporales)',
  'Consulta de auditorías e integración con backend',
];

const checklist = [
  'Soporte para CRUD completo vía API REST',
  'Filtros por perfil, estado y fecha',
  'Bloqueo/Reset de contraseñas',
];

const users = ref([]);
const activeAccesses = ref([]);
const loading = ref(false);
const error = ref('');

const session = useSession();
const ui = useUi();

async function loadData({ silent = false } = {}) {
  if (!session.isAuthenticated.value) {
    users.value = [];
    activeAccesses.value = [];
    return;
  }

  loading.value = true;
  error.value = '';
  try {
    const [userList, accessList] = await Promise.all([
      fetchUsers(),
      fetchActive(),
    ]);
    users.value = userList;
    activeAccesses.value = accessList;
    if (!silent) {
      ui.notifySuccess('Usuarios sincronizados');
    }
  } catch (err) {
    error.value = err.response?.status === 403
      ? 'Sólo el rol ADMIN puede consultar usuarios.'
      : 'Error cargando el listado de usuarios.';
  } finally {
    loading.value = false;
  }
}

watch(
  () => session.isAuthenticated.value,
  () => loadData({ silent: true }),
  { immediate: true },
);

async function handleClose(id) {
  try {
    await closeAccess(id, { fechaHoraSalida: new Date().toISOString() });
    ui.notifySuccess('Acceso cerrado');
    await loadData({ silent: true });
  } catch (err) {
    ui.notifyError('No se pudo cerrar el acceso');
  }
}
</script>

<template>
  <RoleSection
    title="Área del administrador"
    emoji="🧭"
    description="Backoffice central donde se configuran perfiles, se emiten QR y se revisan los registros históricos."
    access-type="Autenticado"
    :actions="actions"
    :checklist="checklist"
  />

  <section class="admin-table">
    <div class="toolbar" v-if="session.isAuthenticated.value">
      <button type="button" @click="loadData()">Actualizar datos</button>
      <button type="button" @click="flushQueue(sendAccess)">
        Sincronizar pendientes
        <span v-if="offlineQueue.state.pending.length" class="badge">{{ offlineQueue.state.pending.length }}</span>
      </button>
    </div>
    <p v-if="loading">Cargando usuarios...</p>
    <p v-else-if="error" class="error">{{ error }}</p>

    <table v-else>
      <thead>
        <tr>
          <th>Usuario</th>
          <th>Nombre</th>
          <th>Rol</th>
          <th>Estado</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in users" :key="user.id">
          <td>{{ user.username }}</td>
          <td>{{ user.fullName || 'Sin nombre' }}</td>
          <td class="badge">{{ user.role }}</td>
          <td>
            <span :class="user.enabled ? 'pill success' : 'pill danger'">
              {{ user.enabled ? 'Activo' : 'Bloqueado' }}
            </span>
          </td>
        </tr>
      </tbody>
    </table>

    <p v-if="!loading && !error && users.length === 0" class="empty">
      No hay usuarios registrados.
    </p>
  </section>

  <section class="admin-table" v-if="activeAccesses.length">
    <h3>Accesos abiertos</h3>
    <table>
      <thead>
        <tr>
          <th>Persona</th>
          <th>Tipo</th>
          <th>Entrada</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="access in activeAccesses" :key="access.id">
          <td>{{ access.nombrePersona }}</td>
          <td class="badge">{{ access.tipoUsuario }}</td>
          <td>{{ access.fechaHoraEntrada }}</td>
          <td>
            <button type="button" class="close-btn" @click="handleClose(access.id)">
              Cerrar
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </section>
</template>

<style scoped>
.admin-table {
  margin-top: 1.5rem;
  background: rgba(15, 23, 42, 0.7);
  border-radius: 1.25rem;
  border: 1px solid rgba(148, 163, 184, 0.25);
  padding: 1.5rem;
}

.toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 1rem;
}

.toolbar button {
  border: 1px solid rgba(168, 85, 247, 0.5);
  background: transparent;
  color: #e9d5ff;
  padding: 0.4rem 0.8rem;
  border-radius: 0.75rem;
  cursor: pointer;
}

.toolbar button:hover {
  border-color: rgba(168, 85, 247, 0.8);
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  padding: 0.7rem;
  text-align: left;
}

thead {
  background: rgba(8, 47, 73, 0.5);
}

tbody tr {
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
}

.badge {
  text-transform: capitalize;
}

.pill {
  padding: 0.2rem 0.6rem;
  border-radius: 999px;
  font-size: 0.85rem;
}

.pill.success {
  background: rgba(34, 197, 94, 0.2);
  color: #bbf7d0;
}

.pill.danger {
  background: rgba(248, 113, 113, 0.2);
  color: #fecaca;
}

.close-btn {
  border: 1px solid rgba(248, 113, 113, 0.6);
  background: transparent;
  color: #fecaca;
  padding: 0.35rem 0.7rem;
  border-radius: 0.6rem;
}

.close-btn:hover {
  border-color: rgba(248, 113, 113, 0.9);
}

.empty {
  color: var(--muted-color);
}

.error {
  color: #fca5a5;
}
</style>
