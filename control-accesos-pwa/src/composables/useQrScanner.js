import { ref, onMounted, onBeforeUnmount } from 'vue';
import { ui } from '../stores/ui';

let BrowserMultiFormatReaderClass;

async function getReader() {
  if (!BrowserMultiFormatReaderClass) {
    const module = await import('@zxing/browser');
    BrowserMultiFormatReaderClass = module.BrowserMultiFormatReader;
  }
  return new BrowserMultiFormatReaderClass();
}

let listDevicesFn;

async function listDevices() {
  if (!listDevicesFn) {
    const module = await import('@zxing/browser');
    listDevicesFn = module.BrowserMultiFormatReader.listVideoInputDevices;
  }
  return listDevicesFn();
}

export function useQrScanner() {
  let codeReader;
  const videoInputDevices = ref([]);
  const selectedDeviceId = ref('');
  const scanning = ref(false);
  const lastResult = ref('');
  const error = ref('');

  async function loadDevices() {
    try {
      const devices = await listDevices();
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
      if (!codeReader) {
        codeReader = await getReader();
      }
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
    if (codeReader) {
      codeReader.reset();
    }
    scanning.value = false;
  }

  onMounted(() => {
    loadDevices();
  });

  onBeforeUnmount(() => {
    if (codeReader) {
      codeReader.reset();
    }
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
