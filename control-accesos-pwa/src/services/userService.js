import { http } from './http';

export function fetchUsers() {
  return http.get('/api/users').then((res) => res.data);
}
