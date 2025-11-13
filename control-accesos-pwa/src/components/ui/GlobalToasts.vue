<script setup>
import { useUi } from '../../stores/ui';

const ui = useUi();

const TYPE_CLASS = {
  info: 'toast--info',
  success: 'toast--success',
  error: 'toast--error',
  warning: 'toast--warning',
};
</script>

<template>
  <teleport to="body">
    <div class="toast-container">
      <article
        v-for="toast in ui.toasts.value"
        :key="toast.id"
        class="toast"
        :class="TYPE_CLASS[toast.type] ?? TYPE_CLASS.info"
      >
        <span>{{ toast.message }}</span>
        <button type="button" @click="ui.removeToast(toast.id)">×</button>
      </article>
    </div>
  </teleport>
</template>

<style scoped>
.toast-container {
  position: fixed;
  right: 1.5rem;
  bottom: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  z-index: 1000;
}

.toast {
  min-width: 260px;
  max-width: 360px;
  padding: 0.75rem 1rem;
  border-radius: 0.85rem;
  background: rgba(15, 23, 42, 0.9);
  color: #f8fafc;
  border: 1px solid rgba(148, 163, 184, 0.4);
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 15px 30px rgba(2, 6, 23, 0.35);
}

.toast button {
  background: transparent;
  border: none;
  color: inherit;
  font-size: 1.1rem;
  cursor: pointer;
}

.toast--success {
  border-color: rgba(34, 197, 94, 0.7);
}

.toast--error {
  border-color: rgba(248, 113, 113, 0.7);
}

.toast--warning {
  border-color: rgba(234, 179, 8, 0.7);
}

.toast--info {
  border-color: rgba(59, 130, 246, 0.6);
}
</style>
