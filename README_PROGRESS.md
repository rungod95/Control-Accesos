# Work In Progress Notes

## Current Branch
- `feature/pwa-shell` (trabajo activo en la PWA: sesión persistente + QR personal por usuario)
- Ramas integradas recientemente:
  - `feature/pwa-shell` (estructura monorepo + PWA shell) → mergeada en `develop`
  - `feature/integration-tests-v2` (suite de integración auth/usuarios/analytics) → mergeada en `develop`
  - `chore/pom-plugin-versions` (fijar versiones de plugins Maven) → mergeada en `develop`
- Latest successful commands:
  - Backend: `cd control-accesos-api && mvn spring-boot:run -Dspring-boot.run.profiles=dev`
  - Backend tests: `cd control-accesos-api && mvn test`
  - Frontend: `cd control-accesos-pwa && npm run build`

## Completed Recently
- Sesión persistente con access/refresh tokens; wiring completo en PWA para renovar tokens y mantener usuario logueado.
- Campo `qrCode` añadido a `user_account`, seeds y DTOs; `/auth/login`, `/auth/refresh` y `/api/users/me` devuelven username/rol/nombre/QR.
- Worker view actualizada: muestra QR gráfico descargable, precarga su código y puede registrar entrada/salida sin reescribirlo.
- Generador de visitantes en `/admin`: crea usuario VISITANTE + QR y muestra PNG descargable; hook de cámara recuerda dispositivo y limpia streams al navegar.
- Endpoint público `POST /api/accesos/visitas/scan` que alterna entrada/salida según QR; GuestView lo usa para validar la visita y mostrar instrucciones de seguridad.

## Pending Actions
- Ajustar navegación por rol una vez autenticado (revisar guardas para operadores).
- Badge offline global (sincronización manual en header) y estado persistente.
- Añadir expiración/revocación de QRs y flujo de compartición segura.
- Export/report endpoints y preparación migración a PostgreSQL (Docker Compose).
- Revisar por qué `mvn test` informa `Tests run: 0` y recuperar la suite de integración.

## Quick Commands
- Backend (dev profile): `cd control-accesos-api && mvn spring-boot:run -Dspring-boot.run.profiles=dev`
- Frontend: `cd control-accesos-pwa && npm run dev` (usar `nvm use 22`)
- Tests API: `cd control-accesos-api && mvn test`
- Build PWA: `cd control-accesos-pwa && npm run build`

## Credentials / Seeds (dev)
- Admin: `admin` / `admin123`
- Trabajador: `operario1` / `operario123`

## Troubleshooting Notes
- Si login muestra "No fue posible iniciar sesión", confirmar que el backend responde en `http://localhost:8080` (curl debe devolver 403) y que se arrancó con `cd control-accesos-api && mvn spring-boot:run` tras limpiar `target/`.
- H2 console disponible en `http://localhost:8080/h2-console` (JDBC `jdbc:h2:mem:accesosdb`, user `sa`).
