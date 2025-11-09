<script setup>
import RoleSection from '../components/RoleSection.vue';

const actions = [
  'Panel en tiempo real con accesos activos',
  'Búsqueda y filtros por tipo de usuario, motivo o QR',
  'Descarga rápida de reportes CSV/PDF',
];

const checklist = [
  'Forzar cierre de accesos o marcar incidencias',
  'Alertas cuando un QR caduca o se usa fuera de horario',
  'Modo tablet con controles grandes',
];
</script>

<template>
  <RoleSection
    title="Área del operador"
    emoji="🛠️"
    description="Vista de control situada en garita/planta. Desde aquí se valida cada entrada y se supervisa la ocupación de la mina."
    access-type="Autenticado"
    :actions="actions"
    :checklist="checklist"
  />
</template>
