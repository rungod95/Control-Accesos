<script setup>
defineProps({
  title: {
    type: String,
    required: true,
  },
  description: {
    type: String,
    required: true,
  },
  emoji: {
    type: String,
    default: '✨',
  },
  actions: {
    type: Array,
    default: () => [],
  },
  checklist: {
    type: Array,
    default: () => [],
  },
  accessType: {
    type: String,
    default: '',
  },
});
</script>

<template>
  <section class="role-section">
    <header>
      <span class="emoji">{{ emoji }}</span>
      <div>
        <h2>{{ title }}</h2>
        <p>{{ description }}</p>
      </div>
    </header>

    <div class="role-meta" v-if="accessType">
      <span class="pill">{{ accessType }}</span>
    </div>

    <div class="role-grid">
      <article v-if="actions.length">
        <h3>Flujos clave</h3>
        <ul>
          <li v-for="item in actions" :key="item">
            <span>➡️</span> {{ item }}
          </li>
        </ul>
      </article>

      <article v-if="checklist.length">
        <h3>Checklist</h3>
        <ul>
          <li v-for="item in checklist" :key="item">
            <span>✔</span> {{ item }}
          </li>
        </ul>
      </article>
    </div>
  </section>
</template>

<style scoped>
.role-section {
  padding: 1.5rem;
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid rgba(148, 163, 184, 0.2);
  border-radius: 1.25rem;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.35);
}

header {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
}

.emoji {
  font-size: 2rem;
}

h2 {
  margin: 0;
}

p {
  margin: 0.35rem 0 0;
  color: var(--muted-color);
}

.role-meta {
  margin: 1rem 0;
}

.pill {
  background: rgba(59, 130, 246, 0.2);
  border: 1px solid rgba(59, 130, 246, 0.4);
  color: #bfdbfe;
  padding: 0.2rem 0.75rem;
  border-radius: 999px;
  font-size: 0.85rem;
}

.role-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

article {
  background: rgba(255, 255, 255, 0.02);
  border-radius: 0.8rem;
  padding: 1rem;
  border: 1px dashed rgba(148, 163, 184, 0.4);
}

ul {
  list-style: none;
  padding: 0;
  margin: 0.8rem 0 0;
  display: flex;
  flex-direction: column;
  gap: 0.55rem;
}

li {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  color: #e2e8f0;
}
</style>
