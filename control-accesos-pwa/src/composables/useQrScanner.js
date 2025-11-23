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

const STORAGE_CAMERA = 'ca_last_camera';

function pickPreferredDevice(devices) {
  const stored = localStorage.getItem(STORAGE_CAMERA);
  if (stored) {
    const matched = devices.find((device) => device.deviceId === stored);
    if (matched) {
      return matched.deviceId;
    }
  }
  const backCamera = devices.find((device) => {
    const label = device.label?.toLowerCase() ?? '';
    return label.includes('back') || label.includes('trasera') || label.includes('rear');
  });
  if (backCamera) {
    return backCamera.deviceId;
  }
  return devices[0]?.deviceId ?? '';
}

async function ensureCameraPermission() {
  if (!navigator.mediaDevices?.getUserMedia) {
    throw new Error('Este navegador no permite acceso a la cámara.');
  }
  const stream = await navigator.mediaDevices.getUserMedia({ video: true });
  stream.getTracks().forEach((track) => track.stop());
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
      await ensureCameraPermission();
      const devices = await listDevices();
      videoInputDevices.value = devices;
      if (devices.length > 0) {
        selectedDeviceId.value = pickPreferredDevice(devices);
      }
    } catch (err) {
      error.value = err?.message ?? 'No se detectó ninguna cámara disponible.';
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
      const deviceId = selectedDeviceId.value;
      if (deviceId) {
        localStorage.setItem(STORAGE_CAMERA, deviceId);
      }
      await codeReader.decodeFromVideoDevice(
        selectedDeviceId.value,
        targetElementId,
        (result) => {
          if (result) {
            lastResult.value = result.getText();
            error.value = '';
            stopScan();
          }
        },
      );
    } catch (err) {
      error.value = 'Error al acceder a la cámara';
      ui.notifyError(error.value);
      scanning.value = false;
    }
  }

  function disposeReader() {
    if (codeReader?.reset) {
      codeReader.reset();
    } else if (codeReader?.stopStreams) {
      codeReader.stopStreams();
    }
    codeReader = undefined;
  }

  function stopScan() {
    if (codeReader) {
      disposeReader();
    }
    scanning.value = false;
    error.value = '';
  }

  onMounted(() => {
    loadDevices();
  });

  onBeforeUnmount(() => {
    disposeReader();
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
