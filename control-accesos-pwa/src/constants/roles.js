export const roles = [
  {
    id: 'worker',
    name: 'Trabajador',
    emoji: '🦺',
    description: 'Accede con su usuario (JWT) para registrar entrada y salida escaneando su QR personal.',
    access: 'Autenticado',
    route: '/trabajador',
    responsibilities: [
      'Escanear su QR al entrar y salir',
      'Consultar su historial más reciente',
      'Reportar incidencias básicas',
    ],
  },
  {
    id: 'admin',
    name: 'Administrador',
    emoji: '🧭',
    description: 'Gestiona usuarios, roles, QR y auditorías. Configura perfiles y exportaciones.',
    access: 'Autenticado',
    route: '/admin',
    responsibilities: [
      'Alta/baja de usuarios y visitantes',
      'Definir roles y permisos',
      'Consultar logs y auditorías',
    ],
  },
  {
    id: 'guest',
    name: 'Visitante / Contratista',
    emoji: '👋',
    description: 'Usa un QR temporal enviado por correo/SMS para validar su entrada.',
    access: 'Público (no autenticado)',
    route: '/qr',
    responsibilities: [
      'Confirmar identidad y QR',
      'Leer instrucciones de seguridad',
      'Compartir datos de contacto para emergencias',
    ],
  },
];
