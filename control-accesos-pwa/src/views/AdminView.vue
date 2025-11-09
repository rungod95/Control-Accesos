<script setup>
import { ref, watch } from 'vue';
import RoleSection from '../components/RoleSection.vue';
import { fetchUsers } from '../services/userService';
import { useSession } from '../stores/session';

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
const loading = ref(false);
const error = ref('');

const session = useSession();

async function loadUsers() {
  if (!session.isAuthenticated.value) {
    users.value = [];
    return;
  }

  loading.value = true;
  error.value = '';
  try {
    users.value = await fetchUsers();
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
  () => loadUsers(),
  { immediate: true },
);
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
</template>

<style scoped>
.admin-table {
  margin-top: 1.5rem;
  background: rgba(15, 23, 42, 0.7);
  border-radius: 1.25rem;
  border: 1px solid rgba(148, 163, 184, 0.25);
  padding: 1.5rem;
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

.empty {
  color: var(--muted-color);
}

.error {
  color: #fca5a5;
}
</style>
