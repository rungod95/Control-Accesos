import { registerAccess, closeAccess } from './accessLogService';
import { useUi } from '../stores/ui';

const ui = useUi();

export async function processAccessEntry(entry) {
  if (entry.action === 'create') {
    await registerAccess(entry.payload);
    ui.notifySuccess('Acceso registrado correctamente');
  } else if (entry.action === 'close') {
    await closeAccess(entry.payload.id, entry.payload.body);
    ui.notifySuccess('Salida registrada correctamente');
  } else {
    throw new Error('Acción no soportada en cola offline');
  }
}
