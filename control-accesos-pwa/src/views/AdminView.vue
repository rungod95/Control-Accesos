<script setup>
import RoleSection from '../components/RoleSection.vue';

const actions = [
  'Gestión de usuarios, roles y permisos',
  'Generación de QR (permanentes y temporales)',
  'Consulta de auditorías e integración con backend',
];

const checklist = [
  'Soporte para CRUD completo vía API REST',
  'Filtros por perfil, estado y fecha',
  'Bloqueo/Reset de contraseñas',
];
</script>

<template>
  <RoleSection
    title="Área del administrador"
    emoji="🧭"
    description="Backoffice central donde se configuran perfiles, se emiten QR y se revisan los registros históricos."
    access-type="Autenticado"
    :actions="actions"
    :checklist="checklist"
  />
</template>
