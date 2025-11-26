<script setup>
import { onMounted } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import { session } from '../stores/session';

const router = useRouter();
const ROLE_HOME = {
  ADMIN: '/admin',
  TRABAJADOR: '/trabajador',
};

onMounted(() => {
  if (session.isAuthenticated.value) {
    const target = ROLE_HOME[session.role.value] ?? '/';
    router.replace(target).catch(() => {});
  }
});
</script>

<template>
  <div class="home-landing">
    <div class="options">
      <RouterLink to="/qr" class="card">
        <div class="card-header">
          <span class="emoji">👋</span>
          <h3>Validar QR</h3>
        </div>
        <p class="description">Escanea o introduce tu QR (visitante o personal).</p>
      </RouterLink>
      <RouterLink to="/login" class="card">
        <div class="card-header">
          <span class="emoji">🔐</span>
          <h3>Acceso con contraseña</h3>
        </div>
        <p class="description">Para administradores, operadores o trabajadores autenticados.</p>
      </RouterLink>
    </div>
  </div>
</template>

<style scoped>
.home-landing {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding-bottom: 2rem;
}

.options {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 0.75rem;
}

.card {
  padding: 1.2rem;
  border-radius: 1rem;
  border: 1px solid rgba(148, 163, 184, 0.2);
  background: rgba(15, 23, 42, 0.6);
  box-shadow: 0 15px 35px rgba(15, 23, 42, 0.35);
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  text-decoration: none;
  color: inherit;
  transition: border 0.2s, transform 0.2s;
}

.card:hover {
  border-color: rgba(59, 130, 246, 0.4);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  gap: 0.7rem;
  align-items: center;
}

.emoji {
  font-size: 1.6rem;
}

.description {
  margin: 0;
  color: var(--muted-color);
}
</style>
