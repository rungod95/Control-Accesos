<template>
  <header class="app-header">
    <div>
      <p class="app-subtitle">Control de accesos a la mina</p>
      <h1>Portal PWA (Vue 3)</h1>
      <p class="app-description">
        Tres áreas autenticadas (trabajador, operador, administrador) y un flujo público para visitantes/contratistas.
      </p>
    </div>
    <div class="app-tag">
      <span>PWA Ready</span>
    </div>
  </header>
</template>

<style scoped>
.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1.5rem;
  padding-block: 1.5rem;
  border-bottom: 1px solid var(--border-color);
}

.app-subtitle {
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #6ee7b7;
  margin-bottom: 0.25rem;
}

h1 {
  font-size: clamp(1.8rem, 3vw, 2.4rem);
  margin: 0;
}

.app-description {
  color: var(--muted-color);
  margin: 0.5rem 0 0;
  max-width: 48rem;
}

.app-tag {
  background: rgba(15, 118, 110, 0.15);
  border: 1px solid rgba(52, 211, 153, 0.4);
  color: #a7f3d0;
  padding: 0.4rem 0.8rem;
  border-radius: 999px;
  font-size: 0.85rem;
  white-space: nowrap;
}

@media (max-width: 768px) {
  .app-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .app-tag {
    margin-top: 0.75rem;
  }
}
</style>
