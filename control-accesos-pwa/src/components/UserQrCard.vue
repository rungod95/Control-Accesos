<script setup>
import { ref, watch } from 'vue';
import QRCode from 'qrcode';

const props = defineProps({
  value: {
    type: String,
    default: '',
  },
  label: {
    type: String,
    default: 'QR',
  },
  size: {
    type: Number,
    default: 192,
  },
  downloadName: {
    type: String,
    default: 'qr-code.png',
  },
  showDownload: {
    type: Boolean,
    default: true,
  },
});

const dataUrl = ref('');
const error = ref('');
const isGenerating = ref(false);

async function generateQr(text) {
  if (!text) {
    dataUrl.value = '';
    error.value = '';
    return;
  }
  isGenerating.value = true;
  try {
    dataUrl.value = await QRCode.toDataURL(text, {
      width: props.size,
      margin: 2,
      color: {
        dark: '#0f172a',
        light: '#ffffff',
      },
    });
    error.value = '';
  } catch (err) {
    error.value = 'No se pudo generar el QR';
  } finally {
    isGenerating.value = false;
  }
}

watch(
  () => props.value,
  (value) => {
    generateQr(value);
  },
  { immediate: true },
);
</script>

<template>
  <article class="qr-card">
    <header>
      <p>{{ label }}</p>
      <small v-if="value">Código: {{ value }}</small>
    </header>
    <div class="qr-preview" aria-live="polite">
      <img v-if="dataUrl" :src="dataUrl" width="180" height="180" alt="Código QR" />
      <span v-else-if="isGenerating">Generando QR…</span>
      <span v-else>Sin QR disponible</span>
    </div>
    <a
      v-if="dataUrl && showDownload"
      class="download"
      :href="dataUrl"
      :download="downloadName"
    >
      Descargar PNG
    </a>
    <p v-else-if="error" class="error">{{ error }}</p>
  </article>
</template>

<style scoped>
.qr-card {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  padding: 1rem;
  border-radius: 1rem;
  border: 1px solid rgba(148, 163, 184, 0.25);
  background: rgba(2, 6, 23, 0.7);
}

header p {
  margin: 0;
  font-weight: 600;
}

header small {
  color: var(--muted-color);
}

.qr-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  background: rgba(15, 23, 42, 0.4);
  border-radius: 0.8rem;
}

img {
  border-radius: 0.8rem;
  border: 1px solid rgba(148, 163, 184, 0.2);
  background: #fff;
  padding: 0.5rem;
}

.download {
  text-align: center;
  padding: 0.4rem 0.6rem;
  border-radius: 0.6rem;
  border: 1px solid rgba(59, 130, 246, 0.5);
  text-decoration: none;
  color: #bfdbfe;
}

.error {
  color: #fca5a5;
  margin: 0;
}
</style>
