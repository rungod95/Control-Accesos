# Repository Guidelines

## Project Structure & Module Organization
The API lives under `src/main/java/com/mina/accesos`. Controllers sit in `controller`, services in `service`, domain models in `domain`, repositories in `repository`, and security filters/config live in `security`. Shared exceptions are under `exception`. Profile-specific configs reside in `src/main/resources` (`application-dev.properties` for in-memory H2, `application-prod.properties` for MySQL). Static seed data is in `data.sql`. Maven outputs land in `target/`; keep that folder out of commits.

## Build, Test, and Development Commands
Use `mvn spring-boot:run -Dspring-boot.run.profiles=dev` for local dev with H2 and auto-loaded seed data. Switch to prod datasource via `mvn spring-boot:run -Dspring-boot.run.profiles=prod` after filling MySQL credentials. Run `mvn clean package` before delivering features to ensure the JAR builds. `mvn test` executes unit/integration tests; add `-Dspring.profiles.active=dev` when a test suite needs seeded data.

## Coding Style & Naming Conventions
Stick to Java 17, four-space indentation, and rely on Lombok annotations already enabled in the build. Keep packages under `com.mina.accesos.*` and group classes by layer. Name controllers with `*Controller`, services with `*Service`, entities in singular PascalCase (e.g., `AccessLog`). Method names follow camelCase verbs; constants go in ALL_CAPS. Format imports and code using your IDE's Java style configured to Google/Oracle defaults before committing.

## Testing Guidelines
Place tests in `src/test/java/com/mina/accesos/...` mirroring the main package. Use `@SpringBootTest` for controller-service flows and slice tests (`@WebMvcTest`, `@DataJpaTest`) for focused coverage. Name classes `*Test` and methods `shouldDoSomething_whenCondition`. Target meaningful coverage on new services, especially around JWT and security filters. Running `mvn test` locally is required before opening a PR.

## Commit & Pull Request Guidelines
Recent history shows short imperative commits (`Init Spring Boot Project`, `"Añadido Jwt"`). Keep that style, optionally prefixing a scope (`feat:`, `fix:`). Each PR should link related issues, explain the profile(s) touched, and include curl or Postman samples when endpoints change. Attach screenshots of the H2 console or relevant logs when documenting fixes to access control.
