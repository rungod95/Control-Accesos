import { ref, onMounted, onBeforeUnmount } from 'vue';
import { BrowserMultiFormatReader } from '@zxing/browser';
import { ui } from '../stores/ui';

export function useQrScanner() {
  const codeReader = new BrowserMultiFormatReader();
  const videoInputDevices = ref([]);
  const selectedDeviceId = ref('');
  const scanning = ref(false);
  const lastResult = ref('');
  const error = ref('');

  async function loadDevices() {
    try {
      const devices = await BrowserMultiFormatReader.listVideoInputDevices();
      videoInputDevices.value = devices;
      if (devices.length > 0) {
        selectedDeviceId.value = devices[0].deviceId;
      }
    } catch (err) {
      error.value = 'No se detectó ninguna cámara disponible.';
      ui.notifyError(error.value);
    }
  }

  async function startScan(targetElementId = 'qr-video') {
    if (!selectedDeviceId.value) {
      await loadDevices();
    }
    if (!selectedDeviceId.value) {
      return;
    }
    scanning.value = true;
    error.value = '';

    try {
      await codeReader.decodeFromVideoDevice(
        selectedDeviceId.value,
        targetElementId,
        (result, err) => {
          if (result) {
            lastResult.value = result.getText();
            ui.notifySuccess('QR leído correctamente');
            stopScan();
          }
          if (err && err.name !== 'NotFoundException') {
            error.value = 'No se pudo interpretar el QR';
          }
        },
      );
    } catch (err) {
      error.value = 'Error al acceder a la cámara';
      ui.notifyError(error.value);
      scanning.value = false;
    }
  }

  function stopScan() {
    codeReader.reset();
    scanning.value = false;
  }

  onMounted(() => {
    loadDevices();
  });

  onBeforeUnmount(() => {
    codeReader.reset();
  });

  return {
    videoInputDevices,
    selectedDeviceId,
    scanning,
    lastResult,
    error,
    startScan,
    stopScan,
  };
}
