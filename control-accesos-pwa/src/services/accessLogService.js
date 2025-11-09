import { http } from './http';

export function fetchSummary() {
  return http.get('/api/accesos/estadisticas').then((res) => res.data);
}

export function fetchActive() {
  return http.get('/api/accesos/activos').then((res) => res.data);
}

export function fetchRecent(limit = 10) {
  return http.get('/api/accesos/ultimos', { params: { limit } }).then((res) => res.data);
}

export function searchAccessLogs(filters = {}) {
  return http.get('/api/accesos', { params: filters }).then((res) => res.data);
}
