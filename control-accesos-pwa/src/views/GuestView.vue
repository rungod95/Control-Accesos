<script setup>
import RoleSection from '../components/RoleSection.vue';

const actions = [
  'Escanear o introducir manualmente el QR temporal recibido',
  'Leer instrucciones de seguridad antes de entrar',
  'Mostrar código QR en modo alto contraste',
];

const checklist = [
  'No requiere autenticación',
  'Confirmación por SMS o email con código extra',
  'Redirección automática al panel del operador tras validar',
];
</script>

<template>
  <RoleSection
    title="Visitantes y contratistas"
    emoji="👋"
    description="Flujo público pensado para personas externas. Reciben un enlace PWA para mostrar su QR temporal incluso sin conexión."
    access-type="Público"
    :actions="actions"
    :checklist="checklist"
  />
</template>
