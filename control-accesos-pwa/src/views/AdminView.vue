<script setup>
import { ref, watch } from 'vue';
import RoleSection from '../components/RoleSection.vue';
import UserQrCard from '../components/UserQrCard.vue';
import { fetchUsers, updateUser, createUser } from '../services/userService';
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
const editingUserId = ref(null);
const qrEditValue = ref('');
const userForm = ref({
  fullName: '',
  username: '',
  qrCode: '',
  role: 'VISITANTE',
});
const creatingVisitor = ref(false);
const lastCreated = ref(null);

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

function startEditQr(user) {
  editingUserId.value = user.id;
  qrEditValue.value = user.qrCode || '';
}

function cancelEditQr() {
  editingUserId.value = null;
  qrEditValue.value = '';
}

async function saveQr(user) {
  const payload = {
    role: user.role,
    fullName: user.fullName,
    password: '',
    qrCode: qrEditValue.value.trim() || null,
  };
  try {
    const updated = await updateUser(user.id, payload);
    user.qrCode = updated.qrCode;
    editingUserId.value = null;
    qrEditValue.value = '';
    ui.notifySuccess(`QR de ${user.username} actualizado`);
  } catch (err) {
    ui.notifyError('No se pudo actualizar el QR');
  }
}

function slugify(value) {
  return value
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/(^-|-$)/g, '')
    || 'visitante';
}

function randomSuffix() {
  return Math.random().toString(36).slice(2, 6);
}

async function handleCreateUser() {
  if (!userForm.value.fullName) {
    ui.notifyWarning('Introduce el nombre del usuario.');
    return;
  }
  creatingVisitor.value = true;
  try {
    const usernameBase = userForm.value.username || slugify(userForm.value.fullName);
    const username = `${usernameBase}-${randomSuffix()}`;
    const password = `usr-${randomSuffix()}${Math.floor(Date.now() % 100)}`;
    const qrCode = (userForm.value.qrCode || `QR-${usernameBase}`)
      .replace(/\s+/g, '-')
      .toUpperCase();

    const payload = {
      username,
      password,
      role: userForm.value.role || 'VISITANTE',
      fullName: userForm.value.fullName,
      qrCode,
    };
    const created = await createUser(payload);
    users.value.push(created);
    lastCreated.value = {
      username,
      password,
      qrCode,
      fullName: created.fullName,
      role: payload.role,
    };
    userForm.value.fullName = '';
    userForm.value.username = '';
    userForm.value.qrCode = '';
    userForm.value.role = 'VISITANTE';
    ui.notifySuccess(`Usuario '${created.fullName}' creado con rol ${payload.role} y QR ${qrCode}`);
  } catch (err) {
    const message = err.response?.data?.error ?? 'No se pudo crear el usuario';
    ui.notifyError(message);
  } finally {
    creatingVisitor.value = false;
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

  <section class="admin-table visitor-card">
    <h3>Crear usuario / QR</h3>
    <form class="visitor-form" @submit.prevent="handleCreateUser">
      <label>
        Nombre completo
        <input v-model="userForm.fullName" placeholder="Nombre Apellido" required />
      </label>
      <label>
        Identificador (opcional)
        <input v-model="userForm.username" placeholder="usuario-corporativo" />
      </label>
      <label>
        Rol
        <select v-model="userForm.role" required>
          <option value="VISITANTE">Visitante</option>
          <option value="TRABAJADOR">Trabajador</option>
          <option value="ADMIN">Admin</option>
        </select>
      </label>
      <label>
        QR personalizado (opcional)
        <input v-model="userForm.qrCode" placeholder="QR-USER-001" />
      </label>
      <button type="submit" class="create-btn" :disabled="creatingVisitor">
        {{ creatingVisitor ? 'Generando...' : 'Crear visitante' }}
      </button>
    </form>
    <div v-if="lastCreated" class="visitor-summary">
      <p><strong>Último usuario creado</strong></p>
      <p>Nombre: {{ lastCreated.fullName }}</p>
      <p>Rol: {{ lastCreated.role }}</p>
      <p>Usuario: <code>{{ lastCreated.username }}</code></p>
      <p>Contraseña temporal: <code>{{ lastCreated.password }}</code></p>
      <p>QR asignado: <code>{{ lastCreated.qrCode }}</code></p>
      <UserQrCard
        :value="lastCreated.qrCode"
        :label="`QR · ${lastCreated.fullName}`"
        :download-name="`qr-${lastCreated.username}.png`"
      />
    </div>
  </section>

  <section class="admin-table">
    <div class="toolbar" v-if="session.isAuthenticated.value">
      <button type="button" @click="loadData()">Actualizar datos</button>
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
          <th>QR</th>
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
          <td>
            <div v-if="editingUserId === user.id" class="qr-editor">
              <input v-model="qrEditValue" placeholder="QR-USER-001" />
              <div class="qr-actions">
                <button type="button" class="save-btn" @click="saveQr(user)">Guardar</button>
                <button type="button" class="link-btn" @click="cancelEditQr">Cancelar</button>
              </div>
            </div>
            <div v-else class="qr-display">
              <span>{{ user.qrCode || '—' }}</span>
              <button type="button" class="link-btn" @click="startEditQr(user)">Editar</button>
            </div>
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

.qr-display {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.qr-editor {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.qr-editor input {
  width: 100%;
  padding: 0.4rem 0.6rem;
  border-radius: 0.5rem;
  border: 1px solid rgba(148, 163, 184, 0.4);
  background: rgba(15, 23, 42, 0.4);
  color: inherit;
}

.qr-actions {
  display: flex;
  gap: 0.4rem;
}

.save-btn {
  border: 1px solid rgba(34, 197, 94, 0.6);
  color: #bbf7d0;
  background: transparent;
  padding: 0.3rem 0.6rem;
  border-radius: 0.6rem;
}

.link-btn {
  border: none;
  background: transparent;
  color: #93c5fd;
  cursor: pointer;
}

.visitor-card {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.visitor-form {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 0.8rem;
}

.visitor-form input {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border-radius: 0.65rem;
  border: 1px solid rgba(148, 163, 184, 0.4);
  background: rgba(15, 23, 42, 0.5);
  color: inherit;
}

.visitor-summary {
  border: 1px dashed rgba(148, 163, 184, 0.35);
  border-radius: 0.8rem;
  padding: 0.8rem 1rem;
  font-size: 0.9rem;
  background: rgba(15, 23, 42, 0.4);
}

.create-btn {
  border: none;
  border-radius: 0.8rem;
  background: linear-gradient(135deg, #34d399, #0ea5e9);
  color: #0f172a;
  font-weight: 600;
  padding: 0.6rem 1rem;
  cursor: pointer;
}

.create-btn:disabled {
  opacity: 0.6;
  cursor: wait;
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
