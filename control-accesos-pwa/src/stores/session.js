import { reactive, computed, watch } from 'vue';

const STORAGE_TOKEN = 'ca_jwt';
const STORAGE_USERNAME = 'ca_username';

const state = reactive({
  token: localStorage.getItem(STORAGE_TOKEN) ?? '',
  username: localStorage.getItem(STORAGE_USERNAME) ?? '',
});

watch(
  () => state.token,
  (value) => {
    if (value) {
      localStorage.setItem(STORAGE_TOKEN, value);
    } else {
      localStorage.removeItem(STORAGE_TOKEN);
    }
  },
);

watch(
  () => state.username,
  (value) => {
    if (value) {
      localStorage.setItem(STORAGE_USERNAME, value);
    } else {
      localStorage.removeItem(STORAGE_USERNAME);
    }
  },
);

function setSession({ token, username }) {
  state.token = token ?? '';
  state.username = username ?? '';
}

function clear() {
  state.token = '';
  state.username = '';
}

export const session = {
  token: computed(() => state.token),
  username: computed(() => state.username),
  isAuthenticated: computed(() => Boolean(state.token)),
  setSession,
  clear,
};

export function useSession() {
  return session;
}
