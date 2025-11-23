<script setup>
import { ref, watch, onMounted } from 'vue';
import RoleSection from '../components/RoleSection.vue';
import { useQrScanner } from '../composables/useQrScanner';
import { scanVisitor } from '../services/accessLogService';
import { useUi } from '../stores/ui';

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
const {
  videoInputDevices,
  selectedDeviceId,
  scanning,
  lastResult,
  error: scannerError,
  startScan,
  stopScan,
} = useQrScanner();
const ui = useUi();
const scanStatus = ref(null);
const scanLoading = ref(false);
const lastProcessedCode = ref('');
const lastProcessedAt = ref(0);

async function handleScan(value) {
  if (!value) {
    return;
  }
  const normalized = value.trim();
  if (!normalized) {
    return;
  }
  const now = Date.now();
  if (normalized === lastProcessedCode.value && now - lastProcessedAt.value < 3000) {
    return;
  }
  lastProcessedCode.value = normalized;
  lastProcessedAt.value = now;
  code.value = normalized;
  scanLoading.value = true;
  try {
    const data = await scanVisitor(normalized);
    scanStatus.value = data;
    ui.notifySuccess(data.entrada
      ? `Bienvenido, ${data.fullName || 'visitante'}`
      : `Salida registrada. Hasta pronto ${data.fullName || 'visitante'}`);
  } catch (err) {
    scanStatus.value = null;
    const message = err.response?.data?.error ?? 'No se pudo validar el QR';
    ui.notifyError(message);
  } finally {
    scanLoading.value = false;
  }
}

function fillManual() {
  handleScan(code.value);
}

watch(
  () => lastResult.value,
  (value) => {
    if (value) {
      handleScan(value);
    }
  },
);

onMounted(() => {
  startScan('qr-video').catch(() => {});
});
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
          <select v-model="selectedDeviceId">
            <option v-for="device in videoInputDevices" :key="device.deviceId" :value="device.deviceId">
              {{ device.label || 'Cámara' }}
            </option>
          </select>
        </label>
        <button type="button" @click="scanning ? stopScan() : startScan('qr-video')">
          {{ scanning ? 'Detener' : 'Escanear QR' }}
        </button>
      </div>
      <p v-if="scannerError" class="error">{{ scannerError }}</p>
      <p v-if="lastResult" class="success">Último QR: {{ lastResult }}</p>
    </div>

    <form class="manual-entry" @submit.prevent="fillManual">
      <label>
        Código QR temporal
        <input v-model="code" placeholder="QR-INV-001" />
      </label>
      <button type="submit">Guardar para mostrar</button>
    </form>

    <div v-if="scanStatus" class="visit-status" :class="{ 'visit-status--exit': !scanStatus.entrada }">
      <h4>{{ scanStatus.entrada ? 'Acceso registrado' : 'Salida completada' }}</h4>
      <p v-if="scanStatus.fullName">Visitante: <strong>{{ scanStatus.fullName }}</strong></p>
      <p>QR: <code>{{ scanStatus.qrCode }}</code></p>
      <template v-if="scanStatus.entrada">
        <p>Por favor, repasa las siguientes instrucciones de seguridad antes de entrar:</p>
        <ul>
          <li>Usa casco, chaleco reflectante y calzado de seguridad.</li>
          <li>Sigue siempre las indicaciones del personal de la mina.</li>
          <li>Mantente en las zonas autorizadas y reporta cualquier incidencia.</li>
        </ul>
      </template>
      <template v-else>
        <p>Gracias por la visita. Vuelve a escanear tu QR para una nueva entrada.</p>
      </template>
    </div>
    <p v-if="scanLoading" class="info">Validando QR...</p>
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

.visit-status {
  border: 1px solid rgba(34, 197, 94, 0.4);
  border-radius: 0.9rem;
  padding: 1rem;
  background: rgba(15, 118, 110, 0.15);
}

.visit-status--exit {
  border-color: rgba(59, 130, 246, 0.4);
  background: rgba(15, 23, 42, 0.35);
}

.visit-status ul {
  margin: 0.6rem 0 0;
  padding-left: 1.2rem;
}

.info {
  color: #bfdbfe;
}
</style>
