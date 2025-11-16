import { reactive, computed, watch } from 'vue';

const STORAGE_TOKEN = 'ca_jwt';
const STORAGE_REFRESH_TOKEN = 'ca_refresh_jwt';
const STORAGE_USERNAME = 'ca_username';
const STORAGE_EXPIRES_AT = 'ca_token_exp';
const STORAGE_REFRESH_EXPIRES_AT = 'ca_refresh_exp';

function readNumber(key) {
  const value = localStorage.getItem(key);
  return value ? Number(value) : 0;
}

const state = reactive({
  token: localStorage.getItem(STORAGE_TOKEN) ?? '',
  refreshToken: localStorage.getItem(STORAGE_REFRESH_TOKEN) ?? '',
  username: localStorage.getItem(STORAGE_USERNAME) ?? '',
  expiresAt: readNumber(STORAGE_EXPIRES_AT),
  refreshExpiresAt: readNumber(STORAGE_REFRESH_EXPIRES_AT),
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
  () => state.refreshToken,
  (value) => {
    if (value) {
      localStorage.setItem(STORAGE_REFRESH_TOKEN, value);
    } else {
      localStorage.removeItem(STORAGE_REFRESH_TOKEN);
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

watch(
  () => state.expiresAt,
  (value) => {
    if (value) {
      localStorage.setItem(STORAGE_EXPIRES_AT, String(value));
    } else {
      localStorage.removeItem(STORAGE_EXPIRES_AT);
    }
  },
);

watch(
  () => state.refreshExpiresAt,
  (value) => {
    if (value) {
      localStorage.setItem(STORAGE_REFRESH_EXPIRES_AT, String(value));
    } else {
      localStorage.removeItem(STORAGE_REFRESH_EXPIRES_AT);
    }
  },
);

function setSession({ token, username, refreshToken, expiresAt, refreshExpiresAt }) {
  state.token = token ?? '';
  state.username = username ?? '';
  state.refreshToken = refreshToken ?? '';
  state.expiresAt = expiresAt ?? 0;
  state.refreshExpiresAt = refreshExpiresAt ?? 0;
}

function clear() {
  state.token = '';
  state.username = '';
  state.refreshToken = '';
  state.expiresAt = 0;
  state.refreshExpiresAt = 0;
}

function hasValidRefreshToken() {
  return Boolean(state.refreshToken) && state.refreshExpiresAt > Date.now();
}

export const session = {
  token: computed(() => state.token),
  refreshToken: computed(() => state.refreshToken),
  username: computed(() => state.username),
  expiresAt: computed(() => state.expiresAt),
  refreshExpiresAt: computed(() => state.refreshExpiresAt),
  isAuthenticated: computed(() => Boolean(state.token)),
  hasValidRefreshToken,
  setSession,
  clear,
};

export function useSession() {
  return session;
}
