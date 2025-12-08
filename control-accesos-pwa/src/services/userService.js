import { http } from './http';

export function fetchUsers() {
  return http.get('/api/users').then((res) => res.data);
}

export function updateUser(id, payload) {
  return http.put(`/api/users/${id}`, payload).then((res) => res.data);
}

export function createUser(payload) {
  return http.post('/api/users', payload).then((res) => res.data);
}

export function deleteUser(id) {
  return http.delete(`/api/users/${id}`).then((res) => res.data);
}
