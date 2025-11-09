<script setup>
import { ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { login } from '../services/authService';
import { session } from '../stores/session';

const form = ref({
  username: '',
  password: '',
});

const loading = ref(false);
const error = ref('');

const router = useRouter();
const route = useRoute();

async function handleSubmit() {
  error.value = '';
  loading.value = true;
  try {
    await login(form.value);
    const redirectTo = route.query.redirect ?? '/';
    router.replace(redirectTo);
  } catch (err) {
    error.value = err.response?.status === 401
      ? 'Credenciales inválidas'
      : 'No fue posible iniciar sesión';
    session.clear();
  } finally {
    loading.value = false;
  }
}
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
          type="password"
          placeholder="••••••••"
          required
          autocomplete="current-password"
        />
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
}

input {
  padding: 0.65rem 0.9rem;
  border-radius: 0.65rem;
  border: 1px solid rgba(148, 163, 184, 0.5);
  background: rgba(15, 23, 42, 0.7);
  color: #f8fafc;
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
