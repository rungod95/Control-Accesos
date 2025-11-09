import { createRouter, createWebHistory } from 'vue-router';

import HomeView from '../views/HomeView.vue';
import WorkerView from '../views/WorkerView.vue';
import OperatorView from '../views/OperatorView.vue';
import AdminView from '../views/AdminView.vue';
import GuestView from '../views/GuestView.vue';

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
    meta: { title: 'Área de trabajador', access: 'autenticado' },
  },
  {
    path: '/operador',
    name: 'operator',
    component: OperatorView,
    meta: { title: 'Área de operador', access: 'autenticado' },
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminView,
    meta: { title: 'Área de administrador', access: 'autenticado' },
  },
  {
    path: '/qr',
    name: 'guest',
    component: GuestView,
    meta: { title: 'Visitante / Contratista', access: 'público' },
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

export default router;
