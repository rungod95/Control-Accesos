import { createRouter, createWebHistory } from 'vue-router';

import HomeView from '../views/HomeView.vue';
import WorkerView from '../views/WorkerView.vue';
import AdminView from '../views/AdminView.vue';
import GuestView from '../views/GuestView.vue';
import LoginView from '../views/LoginView.vue';
import { session } from '../stores/session';

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
  },
  {
    path: '/trabajador',
    name: 'worker',
    component: WorkerView,
    meta: { title: 'Área de trabajador', access: 'autenticado', requiresAuth: true, roles: ['TRABAJADOR', 'ADMIN'] },
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminView,
    meta: { title: 'Área de administrador', access: 'autenticado', requiresAuth: true, roles: ['ADMIN'] },
  },
  {
    path: '/qr',
    name: 'guest',
    component: GuestView,
    meta: { title: 'Visitante / Contratista', access: 'público' },
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { title: 'Iniciar sesión', access: 'público' },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 };
  },
});

router.afterEach((to) => {
  document.title = to.meta?.title
    ? `Control Accesos · ${to.meta.title}`
    : 'Control Accesos';
});

router.beforeEach((to, from, next) => {
  if (to.meta?.requiresAuth && !session.isAuthenticated.value) {
    next({ name: 'login', query: { redirect: to.fullPath } });
    return;
  }

  if (to.meta?.roles && to.meta.roles.length > 0) {
    const role = session.role.value || '';
    if (!to.meta.roles.includes(role)) {
      next({ name: 'home' });
      return;
    }
  }

  if (to.name === 'login' && session.isAuthenticated.value) {
    next({ path: '/' });
    return;
  }

  next();
});

export default router;
