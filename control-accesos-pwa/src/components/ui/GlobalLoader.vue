<script setup>
import { useUi } from '../../stores/ui';

const ui = useUi();
</script>

<template>
  <teleport to="body">
    <div v-if="ui.isLoading.value" class="loader-overlay" aria-live="polite">
      <div class="spinner"></div>
      <p>Cargando datos...</p>
    </div>
  </teleport>
</template>

<style scoped>
.loader-overlay {
  position: fixed;
  inset: 0;
  background: rgba(2, 6, 23, 0.65);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  z-index: 999;
}

.spinner {
  width: 48px;
  height: 48px;
  border: 4px solid rgba(148, 163, 184, 0.2);
  border-top-color: #38bdf8;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
