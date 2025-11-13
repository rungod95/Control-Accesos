<script setup>
import { useRoute } from 'vue-router';
import AppHeader from './components/layout/AppHeader.vue';
import AppNav from './components/layout/AppNav.vue';
import GlobalLoader from './components/ui/GlobalLoader.vue';
import GlobalToasts from './components/ui/GlobalToasts.vue';

const route = useRoute();
</script>

<template>
  <div class="app-shell">
    <AppHeader />
    <AppNav />
    <main>
      <RouterView :key="route.fullPath" />
    </main>
    <GlobalLoader />
    <GlobalToasts />
  </div>
</template>
