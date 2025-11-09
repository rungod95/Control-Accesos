<script setup>
import { roles } from '../constants/roles';
</script>

<template>
  <div class="home-grid">
    <article
      v-for="role in roles"
      :key="role.id"
      class="role-card"
    >
      <header>
        <span class="emoji">{{ role.emoji }}</span>
        <div>
          <p class="access-type">{{ role.access }}</p>
          <h2>{{ role.name }}</h2>
          <p class="description">
            {{ role.description }}
          </p>
        </div>
      </header>
      <ul>
        <li v-for="item in role.responsibilities" :key="item">
          <span>•</span> {{ item }}
        </li>
      </ul>
      <RouterLink :to="role.route" class="link">
        Entrar al área
      </RouterLink>
    </article>
  </div>
</template>

<style scoped>
.home-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.25rem;
  padding-bottom: 2rem;
}

.role-card {
  padding: 1.4rem;
  border-radius: 1rem;
  border: 1px solid rgba(148, 163, 184, 0.2);
  background: rgba(15, 23, 42, 0.6);
  box-shadow: 0 15px 35px rgba(15, 23, 42, 0.4);
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

header {
  display: flex;
  gap: 1rem;
}

.emoji {
  font-size: 2rem;
}

.access-type {
  font-size: 0.75rem;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  color: #93c5fd;
  margin: 0;
}

h2 {
  margin: 0.2rem 0 0;
}

.description {
  margin: 0.35rem 0 0;
  color: var(--muted-color);
}

ul {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  color: #e2e8f0;
}

.link {
  margin-top: auto;
  align-self: flex-start;
  text-decoration: none;
  padding: 0.4rem 0.8rem;
  border-radius: 999px;
  border: 1px solid rgba(59, 130, 246, 0.5);
  color: #bfdbfe;
}
</style>
