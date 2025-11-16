import { http } from './http';
import { session } from '../stores/session';

export async function login(credentials) {
  const { data } = await http.post('/auth/login', credentials);
  session.setSession({
    token: data.token,
    refreshToken: data.refreshToken,
    expiresAt: data.expiresAt,
    refreshExpiresAt: data.refreshExpiresAt,
    username: credentials.username,
  });
  return data;
}

export function logout() {
  session.clear();
}
