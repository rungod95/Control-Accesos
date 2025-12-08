import { http } from './http';

export function changePassword(payload) {
  return http.post('/api/users/me/password', payload);
}
