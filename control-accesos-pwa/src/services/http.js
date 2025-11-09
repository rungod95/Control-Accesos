import axios from 'axios';
import { session } from '../stores/session';
import { ui } from '../stores/ui';

const baseURL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export const http = axios.create({
  baseURL,
  timeout: 10000,
});

http.interceptors.request.use(
  (config) => {
    ui.startLoading();
    if (session.token.value) {
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
  (error) => {
    ui.stopLoading();
    if (error.response?.status === 401) {
      session.clear();
    }

    const status = error.response?.status;
    const message = error.response?.data?.error
      ?? error.response?.data?.message
      ?? 'Error inesperado';

    if (!status) {
      ui.notify({ type: 'error', message: 'No se pudo conectar con el servidor.' });
    } else if (status >= 500) {
      ui.notify({ type: 'error', message });
    } else if (status === 403) {
      ui.notify({ type: 'warning', message });
    }

    return Promise.reject(error);
  },
);
