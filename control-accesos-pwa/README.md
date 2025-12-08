# Control Accesos PWA (Vue 3 + Vite)

Estructura base del frontend progresivo descrito en la propuesta: tres áreas autenticadas (trabajador, operador, administrador) y un flujo público para visitantes/contratistas. El shell ya incluye:

- Router con rutas `/trabajador`, `/operador`, `/admin` y `/qr`.
- Componentes de layout (`AppHeader`, `AppNav`) y secciones reutilizables para cada rol.
- Configuración inicial para convertir la app en PWA con `vite-plugin-pwa`.

> **Requisito:** Vite 7 necesita Node.js ≥ 20.19 (o la rama 22 LTS). Si tu entorno aún usa Node 18, instala una versión reciente con `nvm install 22 && nvm use 22` antes de ejecutar los scripts.

## Scripts

```bash
npm install          # instala dependencias
npm run dev          # arranca el servidor en http://localhost:5173
npm run build        # genera artefactos de producción (dist/)
npm run preview      # sirve la build para pruebas
```

## Variables de entorno

Copia `.env.example` a `.env` y ajusta la URL del backend si es necesario:

```bash
cp .env.example .env
VITE_API_BASE_URL=http://localhost:8080
```

Los servicios (`axios`) utilizarán este valor para apuntar al Spring Boot.

## Próximos pasos sugeridos

1. Conectar cada vista con la API (`control-accesos-api`) usando Axios y JWT almacenado de forma segura.
2. Añadir lector de QR (por ejemplo, `@zxing/browser`) en la vista de trabajador/visitante.
3. Activar caché offline y sincronización diferida (IndexedDB/localforage + service worker).
4. Sustituir los iconos del manifest por recursos reales (PNG 192/512 px).
