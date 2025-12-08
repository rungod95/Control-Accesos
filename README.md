# Control de Accesos – API + PWA

Repositorio monorepo del PFG (DAM): incluye la API Spring Boot y la PWA en Vue 3/Vite para gestionar los accesos a la mina mediante QR, roles y funcionamiento offline.

## ¿Qué es?
Control de accesos para una mina: API Spring Boot + PWA (Vue 3/Vite) con roles, QR, historial y funcionamiento offline.

## Estructura
- `control-accesos-api/`: backend (Java 17 + Spring Boot 3, perfiles `dev`/`prod`, seeds en H2/PostgreSQL). Ejecuta Maven y los tests desde esta carpeta.
- `control-accesos-pwa/`: frontend (Vue 3 + Vite + PWA shell, rutas por rol y sincronización offline).
- `documentos/`: entregables del proyecto y material de referencia.
- `README_PROGRESS.md`: notas diarias con el estado de las features en curso.

## Comandos rápidos
- Backend (perfil dev): `cd control-accesos-api && mvn spring-boot:run -Dspring-boot.run.profiles=dev`
- Backend (tests): `cd control-accesos-api && mvn test`
- Backend (build JAR): `cd control-accesos-api && mvn clean package`
- Frontend (dev): `cd control-accesos-pwa && npm run dev` *(usar `nvm use 22` antes)*
- Frontend (build): `cd control-accesos-pwa && npm run build`

## Docker / Compose (inicio rápido recomendado)
Requisitos: Docker + Docker Compose.

- Arrancar todo (PostgreSQL + API + PWA): `docker compose up -d --build`
- URLs por defecto:
  - API: `http://localhost:8080` (Swagger en `/swagger-ui.html`)
  - PWA: `http://localhost:5173`
- Credenciales seeds (dev/prod con data.sql):
  - Admin: `admin` / `admin123`
  - Trabajador: `operario1` / `operario123`
- Variables principales (exporta antes de levantar si quieres cambiar defaults):
  - `DB_NAME`, `DB_USER`, `DB_PASSWORD`
  - `JWT_SECRET`
  - `VITE_API_BASE_URL` (por defecto `http://localhost:8080` para la PWA)
- Servicios y puertos:
  - `db`: Postgres 16 (`5432:5432`)
  - `api`: Spring Boot perfil `prod` (`8080:8080`)
  - `pwa`: Nginx sirviendo el build (`5173:80`)

## Desarrollo sin Docker
- Backend (H2 dev): `cd control-accesos-api && mvn spring-boot:run -Dspring-boot.run.profiles=dev`
- Tests backend: `cd control-accesos-api && mvn test`
- Frontend (dev server): `cd control-accesos-pwa && nvm use 22 && npm install && npm run dev`  
  Asegura que `VITE_API_BASE_URL` apunte a tu backend (`http://localhost:8080` por defecto).

## Documentación detallada
- API: `control-accesos-api/README.md`
- PWA: `control-accesos-pwa/README.md`


