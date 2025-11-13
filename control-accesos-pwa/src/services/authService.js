import { http } from './http';
import { session } from '../stores/session';

export async function login(credentials) {
  const { data } = await http.post('/auth/login', credentials);
  session.setSession({ token: data.token, username: credentials.username });
  return data;
}

export function logout() {
  session.clear();
}
