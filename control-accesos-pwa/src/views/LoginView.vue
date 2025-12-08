<script setup>
import { ref, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { login } from '../services/authService';
import { session } from '../stores/session';
import { useUi } from '../stores/ui';

const form = ref({
  username: '',
  password: '',
});
const showPassword = ref(false);

const loading = ref(false);
const error = ref('');

const router = useRouter();
const route = useRoute();
const ui = useUi();

const ROLE_HOME = {
  ADMIN: '/admin',
  TRABAJADOR: '/trabajador',
  VISITANTE: '/qr',
  OPERADOR: '/operador',
};

function resolveHomeByRole(role) {
  return ROLE_HOME[role] ?? '/';
}

async function handleSubmit() {
  error.value = '';
  loading.value = true;
  try {
    const data = await login(form.value);
    ui.notifySuccess(`Bienvenido, ${data.fullName || form.value.username}!`);
    const redirectTo = route.query.redirect ?? resolveHomeByRole(data.role);
    await router.replace(redirectTo);
  } catch (err) {
    error.value = err.response?.status === 401
      ? 'Credenciales inválidas'
      : 'No fue posible iniciar sesión';
    ui.notifyError(error.value);
    session.clear();
    loading.value = false;
    return;
  }

  loading.value = false;
}

watch(
  () => session.isAuthenticated.value,
  (value) => {
    if (value && router.currentRoute.value.name === 'login') {
      const redirectTo = route.query.redirect ?? resolveHomeByRole(session.role.value);
      router.replace(redirectTo).catch(() => {});
    }
  },
);
</script>

<template>
  <section class="login-card">
    <header>
      <h2>Iniciar sesión</h2>
      <p>Introduce tus credenciales corporativas para acceder al portal.</p>
    </header>

    <form @submit.prevent="handleSubmit">
      <label>
        Usuario
        <input
          v-model="form.username"
          type="text"
          placeholder="admin"
          required
          autocomplete="username"
        />
      </label>

      <label>
        Contraseña
        <input
          v-model="form.password"
          :type="showPassword ? 'text' : 'password'"
          placeholder="••••••••"
          required
          autocomplete="current-password"
        />
        <button
          type="button"
          class="toggle"
          :aria-label="showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'"
          @click="showPassword = !showPassword"
        >
          <svg
            v-if="!showPassword"
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12Z"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
            <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2" />
          </svg>
          <svg
            v-else
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M3 3l18 18M10.477 10.485A3 3 0 0013.5 13.5m-5.853 2.647C5.134 15.437 3.5 13.5 3.5 13.5s4-7 10-7c1.4 0 2.662.353 3.782.898M14.121 9.88a3 3 0 00-3.999 3.999"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
        </button>
      </label>

      <button type="submit" :disabled="loading">
        {{ loading ? 'Entrando...' : 'Entrar' }}
      </button>

      <p v-if="error" class="error">{{ error }}</p>
    </form>
  </section>
</template>

<style scoped>
.login-card {
  max-width: 480px;
  margin: 0 auto;
  padding: 2rem;
  border-radius: 1.25rem;
  border: 1px solid rgba(148, 163, 184, 0.25);
  background: rgba(2, 6, 23, 0.7);
  box-shadow: 0 20px 45px rgba(2, 6, 23, 0.45);
}

header h2 {
  margin: 0;
}

header p {
  margin: 0.5rem 0 1.5rem;
  color: var(--muted-color);
}

form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

label {
  display: flex;
  flex-direction: column;
  font-size: 0.95rem;
  gap: 0.3rem;
  position: relative;
}

input {
  padding: 0.65rem 0.9rem;
  border-radius: 0.65rem;
  border: 1px solid rgba(148, 163, 184, 0.5);
  background: rgba(15, 23, 42, 0.7);
  color: #f8fafc;
}

.toggle {
  position: absolute;
  right: 0.65rem;
  bottom: 0.65rem;
  border: none;
  background: transparent;
  color: #bfdbfe;
  cursor: pointer;
  padding: 0.1rem;
  display: flex;
  align-items: center;
}

button {
  padding: 0.75rem 1rem;
  border-radius: 0.85rem;
  border: none;
  font-size: 1rem;
  font-weight: 600;
  color: #0f172a;
  background: linear-gradient(135deg, #34d399, #0ea5e9);
  cursor: pointer;
}

button:disabled {
  opacity: 0.7;
  cursor: wait;
}

.error {
  margin: 0;
  color: #fca5a5;
}
</style>
