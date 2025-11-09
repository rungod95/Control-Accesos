<script setup>
import AppHeader from './components/layout/AppHeader.vue';
import AppNav from './components/layout/AppNav.vue';
</script>

<template>
  <div class="app-shell">
    <AppHeader />
    <AppNav />
    <main>
      <RouterView />
    </main>
  </div>
</template>
