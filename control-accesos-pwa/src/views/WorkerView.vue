<script setup>
import RoleSection from '../components/RoleSection.vue';

const actions = [
  'Login JWT y refresco del token',
  'Escaneo de QR con la cámara del dispositivo',
  'Confirmar entrada o salida y recibir feedback inmediato',
];

const checklist = [
  'Mostrar hora y turno asignado',
  'Modo offline con cola de accesos pendientes',
  'Historial resumido de las últimas visitas',
];
</script>

<template>
  <RoleSection
    title="Área del trabajador"
    emoji="🦺"
    description="Pantalla pensada para operarios internos: al iniciar sesión ven el lector de QR y un resumen de su estado actual."
    access-type="Autenticado"
    :actions="actions"
    :checklist="checklist"
  />
</template>
