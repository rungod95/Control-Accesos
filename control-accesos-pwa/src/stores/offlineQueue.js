import localforage from 'localforage';
import { reactive } from 'vue';
import { ui } from './ui';

const STORAGE_NAME = 'offline-access-queue';

const state = reactive({
  pending: [],
});

localforage.config({
  name: 'control-accesos-pwa',
  storeName: STORAGE_NAME,
});

export async function loadQueue() {
  const data = await localforage.getItem(STORAGE_NAME);
  state.pending = Array.isArray(data) ? data : [];
}

export async function addPending(entry) {
  state.pending.push(entry);
  await localforage.setItem(STORAGE_NAME, state.pending);
  ui.notifyWarning('Operación almacenada offline; se enviará al recuperar conexión.');
}

export async function flushQueue(handler) {
  if (state.pending.length === 0) {
    return;
  }
  const copy = [...state.pending];
  for (const entry of copy) {
    try {
      await handler(entry);
      state.pending.shift();
      await localforage.setItem(STORAGE_NAME, state.pending);
      ui.notifySuccess('Operación offline sincronizada.');
    } catch (err) {
      break;
    }
  }
}

export const offlineQueue = {
  state,
  addPending,
  flushQueue,
  loadQueue,
};
