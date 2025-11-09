<script setup>
import { ref } from 'vue';
import RoleSection from '../components/RoleSection.vue';
import { useQrScanner } from '../composables/useQrScanner';

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

const code = ref('');
const scanner = useQrScanner();

function fillManual() {
  code.value = code.value.trim().toUpperCase();
}
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

  <section class="guest-card">
    <div class="scanner">
      <video id="qr-video" playsinline></video>
      <div class="scanner-actions">
        <label>
          Cámara
          <select v-model="scanner.selectedDeviceId">
            <option v-for="device in scanner.videoInputDevices" :key="device.deviceId" :value="device.deviceId">
              {{ device.label || 'Cámara' }}
            </option>
          </select>
        </label>
        <button type="button" @click="scanner.scanning ? scanner.stopScan() : scanner.startScan('qr-video')">
          {{ scanner.scanning ? 'Detener' : 'Escanear QR' }}
        </button>
      </div>
      <p v-if="scanner.error" class="error">{{ scanner.error }}</p>
      <p v-if="scanner.lastResult" class="success">Último QR: {{ scanner.lastResult }}</p>
    </div>

    <form class="manual-entry" @submit.prevent="fillManual">
      <label>
        Código QR temporal
        <input v-model="code" placeholder="QR-INV-001" />
      </label>
      <button type="submit">Guardar para mostrar</button>
    </form>
  </section>
</template>

<style scoped>
.guest-card {
  margin-top: 1.5rem;
  display: grid;
  gap: 1.5rem;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
}

.scanner,
.manual-entry {
  background: rgba(15, 23, 42, 0.7);
  border: 1px solid rgba(148, 163, 184, 0.25);
  border-radius: 1.25rem;
  padding: 1.5rem;
}

video {
  width: 100%;
  min-height: 220px;
  border-radius: 0.8rem;
  background: rgba(15, 23, 42, 0.4);
}

.scanner-actions {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  margin-top: 1rem;
}

select,
input {
  width: 100%;
  padding: 0.5rem;
  border-radius: 0.6rem;
  border: 1px solid rgba(148, 163, 184, 0.5);
  background: rgba(15, 23, 42, 0.5);
  color: #f8fafc;
}

button {
  border: 1px solid rgba(59, 130, 246, 0.5);
  background: transparent;
  color: #bfdbfe;
  padding: 0.5rem 0.8rem;
  border-radius: 0.75rem;
  cursor: pointer;
}

.error {
  color: #fca5a5;
}

.success {
  color: #86efac;
}
</style>
