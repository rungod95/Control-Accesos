# Work In Progress Notes

## Current Branch
- `feature/pwa-shell`
- Latest successful commands:
  - Backend: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
  - Frontend: `npm run build`

## Completed Today
- Reorganised repo en dos carpetas (`control-accesos-api`, `control-accesos-pwa`).
- Bootstrapped PWA (Vue + Vite) con rutas por rol, lector QR, cola offline e integración Axios/JWT.
- Añadidas vistas para notas, cierres manuales desde operador/admin y botones de sincronización offline.
- Ajustes de seguridad: H2 console liberada y CORS en `/auth/login`.

## Pending Actions
- Detectar rol tras login y redirigir automáticamente al dashboard correspondiente.
- Mostrar badge global con operaciones offline pendientes y añadir botón "Sincronizar ahora" en header.
- Añadir controles para export/report endpoints y preparar futura migración a PostgreSQL.

## Quick Commands
- Backend (dev profile): `cd control-accesos-api && mvn spring-boot:run -Dspring-boot.run.profiles=dev`
- Frontend: `cd control-accesos-pwa && npm run dev` (usar `nvm use 22`)
- Tests API: `cd control-accesos-api && mvn -q test`
- Build PWA: `cd control-accesos-pwa && npm run build`

## Credentials / Seeds (dev)
- Admin: `admin` / `admin123`
- Trabajador: `operario1` / `operario123`

## Troubleshooting Notes
- Si login muestra "No fue posible iniciar sesión", confirmar que el backend responde en `http://localhost:8080` (curl debe devolver 403) y que se arrancó con `cd control-accesos-api && mvn spring-boot:run` tras limpiar `target/`.
- H2 console disponible en `http://localhost:8080/h2-console` (JDBC `jdbc:h2:mem:accesosdb`, user `sa`).
