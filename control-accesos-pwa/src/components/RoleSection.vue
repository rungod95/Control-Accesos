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
</style>
