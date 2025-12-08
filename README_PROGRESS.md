# Work In Progress Notes

## Current Branch
- `develop` (chore/postgres-docker mergeada)
- Ramas integradas recientemente:
  - `chore/tests-access-logs` (tests de estadisticas/ultimos + password change) → mergeada en `develop`
  - `chore/postgres-docker` (Dockerfiles API/PWA, compose con Postgres, ajustes search/seed) → mergeada en `develop`
- Latest successful commands:
  - Backend tests: `cd control-accesos-api && mvn test`
  - Frontend build: `cd control-accesos-pwa && npm run build`
  - Stack Docker: `docker compose up -d --build`

## Completed Recently
- Docker-compose con Postgres 16 + API + PWA (puertos 5432/8080/5173), Dockerfiles para backend/frontend.
- Prod apunta a Postgres (dialecto/driver), seeds idempotentes; búsqueda de accesos con rangos seguros para Postgres.
- Tests ampliados: estadisticas/ultimos accesos, cambio de contraseña propio, fixtures de test.
- PWA: botón de QR del trabajador con mejor contraste; toggle de ojo para ver/ocultar contraseña en login; se eliminaron bloques de “Flujos clave / Checklist” de las vistas.
- PWA apuntando a API `http://localhost:8080` por defecto en build docker; README con guía rápida de Compose.

## Pending Actions
- Frontend: badge offline/sync, guardas de router por rol pulidas.
- Backend/PWA: expiración/revocación de QRs, endpoints de export/report.
- Docs: README API (OpenAPI) pendiente de completar.
- Opcional: empaquetado ZIP (JAR + dist) para compartir sin Docker.

## Quick Commands
- Docker stack: `docker compose up -d --build`
- Backend (dev profile H2): `cd control-accesos-api && mvn spring-boot:run -Dspring-boot.run.profiles=dev`
- Tests API: `cd control-accesos-api && mvn test`
- Frontend: `cd control-accesos-pwa && nvm use 22 && npm run dev`
- Build PWA: `cd control-accesos-pwa && npm run build`

## Credentials / Seeds (dev)
- Admin: `admin` / `admin123`
- Trabajador: `operario1` / `operario123`

## Troubleshooting Notes
- Si login muestra "No fue posible iniciar sesión", confirmar que el backend responde en `http://localhost:8080` (curl debe devolver 403) y que se arrancó con `cd control-accesos-api && mvn spring-boot:run` tras limpiar `target/`.
- H2 console disponible en `http://localhost:8080/h2-console` (JDBC `jdbc:h2:mem:accesosdb`, user `sa`).
