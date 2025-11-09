import { reactive, computed } from 'vue';

const TOAST_TIMEOUT = 4500;

function createId() {
  if (crypto?.randomUUID) {
    return crypto.randomUUID();
  }
  return `${Date.now()}-${Math.random().toString(16).slice(2)}`;
}

const state = reactive({
  pendingRequests: 0,
  toasts: [],
});

function startLoading() {
  state.pendingRequests += 1;
}

function stopLoading() {
  state.pendingRequests = Math.max(0, state.pendingRequests - 1);
}

function removeToast(id) {
  const index = state.toasts.findIndex((item) => item.id === id);
  if (index >= 0) {
    state.toasts.splice(index, 1);
  }
}

function notify({ type = 'info', message }) {
  const id = createId();
  state.toasts.push({ id, type, message });
  setTimeout(() => removeToast(id), TOAST_TIMEOUT);
}

const notifyInfo = (message) => notify({ type: 'info', message });
const notifySuccess = (message) => notify({ type: 'success', message });
const notifyWarning = (message) => notify({ type: 'warning', message });
const notifyError = (message) => notify({ type: 'error', message });

export const ui = {
  isLoading: computed(() => state.pendingRequests > 0),
  toasts: computed(() => state.toasts),
  startLoading,
  stopLoading,
  notify,
  notifyInfo,
  notifySuccess,
  notifyWarning,
  notifyError,
  removeToast,
};

export function useUi() {
  return ui;
}
