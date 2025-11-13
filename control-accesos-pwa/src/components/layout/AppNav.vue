<script setup>
import { RouterLink, useRoute } from 'vue-router';

const route = useRoute();

const links = [
  { path: '/', label: 'Inicio', subtitle: 'Resumen general', emoji: '🏠' },
  { path: '/qr', label: 'Visitante', subtitle: 'QR temporal', emoji: '👋' },
  { path: '/login', label: 'Acceso', subtitle: 'Autenticación', emoji: '🔐' },
];

const isActive = (target) => route.path === target;
</script>

<template>
  <nav class="app-nav">
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
</style>
