import axios from 'axios';
import { session } from '../stores/session';
import { ui } from '../stores/ui';

const baseURL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export const http = axios.create({
  baseURL,
  timeout: 10000,
});

let refreshPromise = null;

async function refreshSession() {
  if (!session.hasValidRefreshToken()) {
    throw new Error('No hay refresh token válido');
  }

  if (!refreshPromise) {
    refreshPromise = http.post('/auth/refresh', {
      refreshToken: session.refreshToken.value,
    }, { skipAuthRefresh: true })
      .then((response) => {
        const data = response.data;
        session.setSession({
          token: data.token,
          refreshToken: data.refreshToken,
          expiresAt: data.expiresAt,
          refreshExpiresAt: data.refreshExpiresAt,
          username: data.username ?? session.username.value,
          role: data.role ?? session.role.value,
          fullName: data.fullName ?? session.fullName.value,
          qrCode: data.qrCode ?? session.qrCode.value,
        });
        return data.token;
      })
      .catch((err) => {
        session.clear();
        throw err;
      })
      .finally(() => {
        refreshPromise = null;
      });
  }

  return refreshPromise;
}

http.interceptors.request.use(
  (config) => {
    ui.startLoading();
    if (session.token.value && !config?.skipAuthRefresh) {
      config.headers = config.headers ?? {};
      config.headers.Authorization = `Bearer ${session.token.value}`;
    }
    return config;
  },
  (error) => {
    ui.stopLoading();
    return Promise.reject(error);
  },
);

http.interceptors.response.use(
  (response) => {
    ui.stopLoading();
    return response;
  },
  async (error) => {
    ui.stopLoading();
    const { config } = error;
    const status = error.response?.status;

    if (
      status === 401
      && !config?.skipAuthRefresh
      && !config?._retry
      && session.hasValidRefreshToken()
    ) {
      config._retry = true;
      try {
        await refreshSession();
        config.headers = config.headers ?? {};
        config.headers.Authorization = `Bearer ${session.token.value}`;
        return http(config);
      } catch {
        // si el refresh falla, dejamos que continúe el flujo para limpiar la sesión
      }
    }

    if (status === 401 && !config?.skipAuthRefresh) {
      session.clear();
    }

    const message = error.response?.data?.error
      ?? error.response?.data?.message
      ?? 'Error inesperado';

    if (!status) {
      ui.notify({ type: 'error', message: 'No se pudo conectar con el servidor.' });
    } else if (status >= 500) {
      ui.notify({ type: 'error', message });
    } else if (status === 403) {
      ui.notify({ type: 'warning', message });
    } else if (status === 401 && !config?.skipAuthRefresh) {
      ui.notify({ type: 'warning', message: 'Tu sesión ha expirado. Inicia sesión nuevamente.' });
    }

    return Promise.reject(error);
  },
);
