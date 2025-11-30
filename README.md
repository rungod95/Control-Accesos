# Control de Accesos – API + PWA

Repositorio monorepo del PFG (DAM): incluye la API Spring Boot y la PWA en Vue 3/Vite para gestionar los accesos a la mina mediante QR, roles y funcionamiento offline.

## Estructura
- `control-accesos-api/`: backend (Java 17 + Spring Boot 3, perfiles `dev`/`prod`, seeds en H2/MySQL). Ejecuta Maven y los tests desde esta carpeta.
- `control-accesos-pwa/`: frontend (Vue 3 + Vite + PWA shell, rutas por rol y sincronización offline).
- `documentos/`: entregables del proyecto y material de referencia.
- `README_PROGRESS.md`: notas diarias con el estado de las features en curso.

## Comandos rápidos
- Backend (perfil dev): `cd control-accesos-api && mvn spring-boot:run -Dspring-boot.run.profiles=dev`
- Backend (tests): `cd control-accesos-api && mvn test`
- Backend (build JAR): `cd control-accesos-api && mvn clean package`
- Frontend (dev): `cd control-accesos-pwa && npm run dev` *(usar `nvm use 22` antes)*
- Frontend (build): `cd control-accesos-pwa && npm run build`

## Docker / Compose
- Arrancar stack completo (PostgreSQL + API + PWA): `docker-compose up --build`
- Variables principales: `DB_NAME`, `DB_USER`, `DB_PASSWORD` (Postgres), `JWT_SECRET`, `VITE_API_BASE_URL` (build de la PWA).
- Servicios: `db` (Postgres 16, puerto 5432), `api` (Spring Boot perfil `prod`, puerto 8080), `pwa` (Nginx sirviendo build, puerto 5173).

## Documentación detallada
- API: `control-accesos-api/README.md`
- PWA: `control-accesos-pwa/README.md`

Consulta `README_PROGRESS.md` para ver en qué rama y tareas estamos trabajando y `AGENTS.md` para las guías internas del repo.
