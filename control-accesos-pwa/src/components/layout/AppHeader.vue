<script setup>
import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';
import { session } from '../../stores/session';
import { logout } from '../../services/authService';
import { offlineQueue, flushQueue } from '../../stores/offlineQueue';
import { processAccessEntry } from '../../services/offlineSyncService';

const router = useRouter();
const userLabel = computed(() => session.fullName.value || session.username.value || 'Invitado');
const pendingCount = computed(() => offlineQueue.state.pending.length);
const hasPending = computed(() => pendingCount.value > 0);
const syncing = ref(false);

function handleLogout() {
  logout();
  router.push({ name: 'login' });
}

function goLogin() {
  router.push({ name: 'login' });
}

async function handleSync() {
  if (!hasPending.value || syncing.value) {
    return;
  }
  syncing.value = true;
  try {
    await flushQueue(processAccessEntry);
  } finally {
    syncing.value = false;
  }
}
</script>

<template>
  <header class="app-header">
    <div>
      <p class="app-subtitle">Control de accesos a las instalaciones</p>
      <h1>ACELOR S.A</h1>

    </div>
    <div class="header-actions">
      <div class="app-tag">
        <span>PWA Ready</span>
      </div>
      <button
        v-if="session.isAuthenticated.value"
        type="button"
        class="sync-btn"
        :disabled="!hasPending || syncing"
        @click="handleSync"
      >
        {{ hasPending ? `Sincronizar (${pendingCount})` : 'Sin pendientes' }}
      </button>
      <div class="session-panel" v-if="session.isAuthenticated.value">
        <span class="user-chip">🔐 {{ userLabel }}</span>
        <button type="button" @click="handleLogout">Salir</button>
      </div>
      <div class="session-panel" v-else>
        <span class="user-chip">🔓 Invitado</span>
        <button type="button" @click="goLogin">Iniciar sesión</button>
      </div>
    </div>
  </header>
</template>

<style scoped>
 .app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1.5rem;
  padding-block: 1.5rem;
  border-bottom: 1px solid var(--border-color);
}

.app-subtitle {
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #6ee7b7;
  margin-bottom: 0.25rem;
}

h1 {
  font-size: clamp(1.8rem, 3vw, 2.4rem);
  margin: 0;
}

.app-description {
  color: var(--muted-color);
  margin: 0.5rem 0 0;
  max-width: 48rem;
}

.header-actions {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.app-tag {
  background: rgba(15, 118, 110, 0.15);
  border: 1px solid rgba(52, 211, 153, 0.4);
  color: #a7f3d0;
  padding: 0.4rem 0.8rem;
  border-radius: 999px;
  font-size: 0.85rem;
  white-space: nowrap;
}

.session-panel {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.user-chip {
  padding: 0.3rem 0.8rem;
  border-radius: 999px;
  background: rgba(148, 163, 184, 0.15);
}

.sync-btn {
  background: transparent;
  border: 1px solid rgba(59, 130, 246, 0.5);
  color: #bfdbfe;
  padding: 0.35rem 0.75rem;
  border-radius: 0.6rem;
  cursor: pointer;
}

.sync-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

button {
  background: transparent;
  border: 1px solid rgba(148, 163, 184, 0.5);
  color: inherit;
  padding: 0.35rem 0.75rem;
  border-radius: 0.6rem;
  cursor: pointer;
}

button:hover {
  border-color: rgba(59, 130, 246, 0.7);
}

@media (max-width: 768px) {
  .app-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .app-tag {
    margin-top: 0.75rem;
  }
}
</style>
