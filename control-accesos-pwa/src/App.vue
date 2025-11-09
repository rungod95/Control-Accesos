<script setup>
import AppHeader from './components/layout/AppHeader.vue';
import AppNav from './components/layout/AppNav.vue';
import GlobalLoader from './components/ui/GlobalLoader.vue';
import GlobalToasts from './components/ui/GlobalToasts.vue';
</script>

<template>
  <div class="app-shell">
    <AppHeader />
    <AppNav />
    <main>
      <RouterView />
    </main>
    <GlobalLoader />
    <GlobalToasts />
  </div>
</template>
