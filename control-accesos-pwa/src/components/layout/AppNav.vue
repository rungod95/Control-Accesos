<script setup>
import { computed } from 'vue';
import { RouterLink, useRoute } from 'vue-router';
import { session } from '../../stores/session';

const route = useRoute();

const ROLE_HOME = {
  ADMIN: { path: '/admin', label: 'Admin', subtitle: 'Usuarios y accesos', emoji: '🧭' },
  TRABAJADOR: { path: '/trabajador', label: 'Trabajador', subtitle: 'Mi QR', emoji: '🦺' },
};

const publicLinks = [
  { path: '/qr', label: 'QR', subtitle: 'Escanear código', emoji: '👋' },
  { path: '/login', label: 'Acceso', subtitle: 'Autenticación', emoji: '🔐' },
];

const links = computed(() => {
  if (route.path === '/') {
    return [];
  }
  if (!session.isAuthenticated.value) {
    return publicLinks;
  }
  const roleLink = ROLE_HOME[session.role.value] ? [ROLE_HOME[session.role.value]] : [];
  return [...roleLink, { path: '/qr', label: 'QR', subtitle: 'Escanear código', emoji: '👋' }];
});

const isActive = (target) => route.path === target;
</script>

<template>
  <nav v-if="links.length" class="app-nav">
    <RouterLink
      v-for="link in links"
      :key="link.path"
      class="nav-link"
      :class="{ 'nav-link--active': isActive(link.path) }"
      :to="link.path"
    >
      <span class="emoji">{{ link.emoji }}</span>
      <span class="text">
        <strong>{{ link.label }}</strong>
        <small>{{ link.subtitle }}</small>
      </span>
    </RouterLink>
  </nav>
  <div v-else class="app-nav-spacer" aria-hidden="true"></div>
</template>

<style scoped>
.app-nav {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 0.75rem;
  padding-block: 1.5rem;
}

.nav-link {
  display: flex;
  gap: 0.8rem;
  align-items: center;
  padding: 0.85rem 1rem;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid transparent;
  border-radius: 0.8rem;
  color: inherit;
  transition: border 0.2s, transform 0.2s;
  text-decoration: none;
}

.nav-link:hover {
  border-color: rgba(59, 130, 246, 0.4);
  transform: translateY(-1px);
}

.nav-link--active {
  border-color: rgba(16, 185, 129, 0.8);
  background: rgba(16, 185, 129, 0.08);
  box-shadow: 0 10px 20px rgba(16, 185, 129, 0.25);
}

.emoji {
  font-size: 1.5rem;
}

.text {
  display: flex;
  flex-direction: column;
}

small {
  color: var(--muted-color);
}

.app-nav-spacer {
  height: 1rem;
}
</style>
