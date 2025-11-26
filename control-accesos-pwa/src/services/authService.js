import { http } from './http';
import { session } from '../stores/session';

export async function login(credentials) {
  const { data } = await http.post('/auth/login', credentials);
  session.setSession({
    token: data.token,
    refreshToken: data.refreshToken,
    expiresAt: data.expiresAt,
    refreshExpiresAt: data.refreshExpiresAt,
    username: data.username ?? credentials.username,
    role: data.role,
    fullName: data.fullName,
    qrCode: data.qrCode,
  });
  return data;
}

export function logout() {
  session.clear();
}
