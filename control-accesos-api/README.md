# Control de Accesos a la Mina – API (Spring Boot)

> Nota: este módulo vive en `control-accesos-api/`. Ejecuta los comandos Maven desde este directorio.

API REST en Java 17 + Spring Boot 3 para registrar accesos mediante QR.

## Requisitos
- Java 17
- Maven 3.9+
- (Opcional) MySQL 8 para perfil `prod`

## Ejecutar en DEV (H2)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
Consola H2: http://localhost:8080/h2-console  
JDBC URL: `jdbc:h2:mem:accesosdb`

## Ejecutar en PROD (MySQL)
Edita `src/main/resources/application-prod.properties` con tu usuario/contraseña y ejecuta:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## Endpoints
- `GET    /api/accesos` – listar
- `GET    /api/accesos/{id}` – detalle
- `POST   /api/accesos` – crear (JSON)
- `PUT    /api/accesos/{id}` – actualizar
- `DELETE /api/accesos/{id}` – borrar

### Autenticación
- `POST /auth/login` – devuelve un JWT de acceso (1h) y un refresh token (14 días).
- `POST /auth/refresh` – recibe `{ "refreshToken": "..." }` y entrega un nuevo par de tokens sin pedir credenciales.
- `GET /api/users/me` – datos del usuario autenticado (username, rol, nombre y QR asignado).
- `POST /api/accesos/visitas/scan` – endpoint público para visitantes; recibe `{ "qrCode": "QR-XXX" }` y alterna entrada/salida según si ya tenía acceso abierto.

> Ambos tokens se firman con `jwt.secret`. Ajusta `jwt.expiration` (ms) y `jwt.refresh-expiration` (ms) en `application-*.properties` para definir su vigencia.

### Ejemplo POST
```json
{
  "nombrePersona": "Visita Mantenimiento",
  "tipoUsuario": "visitante",
  "motivo": "Mantenimiento cinta",
  "qrCode": "QR-VM-003"
}
```

## Git (sugerido)
```bash
git init
git branch -M main
git add .
git commit -m "feat: init Spring Boot project (H2 dev + MySQL prod)"
git checkout -b develop
git checkout -b feature/init-project
```

## Siguientes pasos
- Añadir autenticación JWT y roles.
- Endpoints de generación/validación de QR.
- Exportación a PDF/Excel.
- Tests de integración (Spring Boot Test).
