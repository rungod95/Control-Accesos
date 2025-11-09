<script setup>
import { RouterLink } from 'vue-router';
import { roles } from '../../constants/roles';
</script>

<template>
  <nav class="app-nav">
    <RouterLink
      v-for="role in roles"
      :key="role.id"
      :to="role.route"
      class="nav-link"
      active-class="nav-link--active"
    >
      <span class="emoji">{{ role.emoji }}</span>
      <span class="text">
        <strong>{{ role.name }}</strong>
        <small>{{ role.access }}</small>
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
  text-decoration: none;
  color: inherit;
  transition: border 0.2s, transform 0.2s;
}

.nav-link:hover {
  border-color: rgba(59, 130, 246, 0.4);
  transform: translateY(-1px);
}

.nav-link--active {
  border-color: rgba(16, 185, 129, 0.8);
  background: rgba(16, 185, 129, 0.08);
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
