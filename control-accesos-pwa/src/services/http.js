import axios from 'axios';
import { session } from '../stores/session';

const baseURL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export const http = axios.create({
  baseURL,
  timeout: 10000,
});

http.interceptors.request.use((config) => {
  if (session.token.value) {
    config.headers.Authorization = `Bearer ${session.token.value}`;
  }
  return config;
});

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      session.clear();
    }
    return Promise.reject(error);
  },
);
