# 06. Desarrollo

## 6.1 Stack tecnológico

### Backend

| Tecnología | Versión | Rol |
|---|---|---|
| Java | 21 (LTS) | Lenguaje principal |
| Spring Boot | 4.0.5 | Framework web y de inyección de dependencias |
| Spring Security | (incluido en Boot) | Autenticación y autorización |
| Spring Data JPA / Hibernate | (incluido en Boot) | Capa de persistencia ORM |
| JJWT | 0.12.6 | Generación y validación de tokens JWT |
| PostgreSQL Driver | (última compatible) | Conector JDBC |
| Apache PDFBox | 3.0.3 | Extracción de texto desde PDF |
| Apache POI | 5.3.0 | Extracción de texto desde DOCX |
| SpringDoc OpenAPI | 2.8.3 | Generación automática de Swagger UI |
| Lombok | (última) | Reducción de boilerplate (getters, builders, etc.) |
| JaCoCo | 0.8.12 | Cobertura de código en tests |
| H2 (test) | (última) | Base de datos en memoria para tests unitarios |
| Maven | 3.9+ | Gestión de dependencias y ciclo de build |

### Frontend

| Tecnología | Versión | Rol |
|---|---|---|
| Angular | 20.3 | Framework SPA |
| TypeScript | ~5.9 | Lenguaje principal |
| SCSS | (incluido en Angular) | Estilos con preprocesador |
| RxJS | ~7.8 | Programación reactiva, gestión de streams |
| jsPDF | ^4.2 | Exportación a PDF desde el navegador |
| pdf.js | ^5.6 | Renderizado de PDF en el navegador |
| Nginx | 1.27 Alpine | Servidor web y reverse proxy en contenedor |

### Infraestructura y herramientas

| Herramienta | Uso |
|---|---|
| Docker | Contenedorización de todos los servicios |
| Docker Compose | Orquestación local (dev) y producción |
| GitHub Actions | Pipeline CI/CD |
| GHCR (GitHub Container Registry) | Publicación de imágenes Docker |
| Git | Control de versiones |

---

## 6.2 Secuencia de desarrollo

El proyecto se desarrolló siguiendo un orden lógico de dependencias:

1. **Scaffolding inicial**: generación del proyecto Spring Boot (Spring Initializr) y Angular (Angular CLI), configuración de Docker Compose básico.

2. **Capa de datos**: definición de entidades JPA (`Usuario`, `Reglamento`, `VersionReglamento`, `SugerenciaIA`, `HistorialCambios`) y sus repositorios Spring Data.

3. **Seguridad**: implementación de `JwtTokenProvider`, filtro de autenticación JWT, configuración de Spring Security y endpoints de `/api/auth` (register, login).

4. **API core**: desarrollo de controladores y servicios para reglamentos (`ReglamentoController`, `ReglamentoService`) y versiones (`VersionReglamentoController`, `VersionReglamentoService`).

5. **Historial de auditoría**: `HistorialCambiosService` para registrar automáticamente cambios relevantes sobre versiones.

6. **Integración IA**: `LlmService` (cliente HTTP RestClient contra endpoint OpenAI-compatible), servicios de sugerencias y controladores para revisión, resumen e incoherencias.

7. **Conversión documental**: `DocumentConversionController` con PDFBox (PDF → texto) y Apache POI (DOCX → texto), seguido de normalización a Markdown.

8. **Frontend base**: enrutado Angular, componentes de layout (header, footer, sidebar), servicio de autenticación con interceptor JWT.

9. **Páginas de la aplicación**: desarrollo iterativo de cada página (login, registro, home, reglamentos, versiones, upload, corrección, publicación, etc.).

10. **Dockerización y CI/CD**: Dockerfiles multi-stage, configuración de `docker-compose.prod.yml`, pipeline GitHub Actions para build y push a GHCR.

11. **Pruebas y documentación**: escritura de tests unitarios con JUnit 5 y Mockito, documentación de la API con OpenAPI y redacción de la documentación del proyecto.

---

## 6.3 Decisiones técnicas clave

### Elección de Spring Boot 4 con Java 21
Spring Boot 4 + Java 21 era la combinación más moderna disponible en el momento del desarrollo. Java 21 aporta Virtual Threads (mejora de concurrencia), records y mejoras en pattern matching. La arquitectura basada en `@RestController`, `@Service` y `@Repository` simplifica el testing con Mockito.

### JWT sin estado (stateless)
Se optó por una arquitectura sin sesiones en servidor (stateless). Cada petición lleva el token JWT en la cabecera `Authorization`. Esto facilita el escalado horizontal y simplifica el despliegue en contenedores, eliminando la necesidad de sticky sessions o un almacén de sesiones compartido.

### Contenido en Markdown
El contenido de las versiones de reglamentos se almacena como texto Markdown en la base de datos. Esta decisión permite:
- Portabilidad del contenido (independiente del frontend).
- Renderizado en el frontend sin librerías pesadas de edición enriquecida.
- Compatibilidad con la salida de los modelos LLM, que generan texto Markdown de forma natural.

### LLM OpenAI-compatible (Groq)
La integración IA se diseñó contra la API OpenAI-compatible, lo que permite cambiar de proveedor (OpenAI, Groq, Ollama, etc.) solo modificando variables de entorno (`LLM_API_URL`, `LLM_API_KEY`, `LLM_MODEL`). Se eligió Groq por su velocidad de inferencia y plan gratuito generoso (LLaMA 3.3 70B por defecto).

### Límite de timeout en LLM
El `LlmService` configura explícitamente timeouts de conexión (10 s) y lectura (15 s) para no bloquear el backend indefinidamente. El contenido se trunca a 2500 caracteres antes de enviarse al LLM para garantizar respuestas dentro del margen temporal.

### Reverse proxy Nginx
En producción, Nginx es el único punto de entrada. Reescribe rutas `/api/*` hacia el backend interno, eliminando la exposición directa del puerto 8080 y permitiendo gestionar SSL/TLS en un solo punto.

### ITCSS en estilos Angular
Los estilos globales del frontend siguen la metodología ITCSS (Inverted Triangle CSS), lo que facilita la mantenibilidad y evita colisiones de especificidad al escalar el número de componentes.

---

## 6.4 Dificultades y cómo se superaron

| Dificultad | Solución |
|---|---|
| Timeouts en llamadas al LLM con documentos grandes | Truncado del contenido a 2500 caracteres y ajuste explícito de timeouts en `RestClient` |
| Gestión del ciclo de vida de versiones (activar/archivar) | Lógica transaccional en `VersionReglamentoService`: activar una versión archiva automáticamente todas las demás del mismo reglamento |
| CORS entre Angular (dev) y Spring Boot | Configuración centralizada de CORS en Spring Security con `CORS_ALLOWED_ORIGINS` como variable de entorno |
| Arranque del backend antes de que Postgres esté listo | `hikari.initializationFailTimeout=-1` en `application.properties` y `healthcheck` de Postgres en `docker-compose.yml` con `depends_on: condition: service_healthy` |
| Conversión de documentos binarios en el navegador | Procesamiento server-side con PDFBox/POI para evitar dependencias pesadas en el frontend |
| Testing de seguridad (filtros JWT) | Uso de `spring-security-test` y mocking del `SecurityContext` en tests de servicio |

---

## 6.5 Control de versiones

- **Herramienta**: Git con repositorio en GitHub.
- **Convención de commits**: mensajes descriptivos en presente (ej. `Add JWT filter`, `Fix CORS config`).
- **Ramas**: rama principal `main` con desarrollo directo para proyecto individual; sin estrategia de branching compleja (no se requería trabajo en equipo).
- **CI/CD**: cada push a `main` dispara el pipeline de GitHub Actions.

---

## 6.6 Fragmentos de código relevantes

### Filtro JWT (Spring Security)

El filtro extrae el token de la cabecera `Authorization`, lo valida con `JwtTokenProvider` y establece el contexto de seguridad para el resto de la cadena de filtros:

```java
// JwtAuthenticationFilter – extractToken + validate + setAuthentication
String token = extractToken(request);
if (token != null && tokenProvider.validateToken(token)) {
    String username = tokenProvider.getUsername(token);
    UserDetails ud = userDetailsService.loadUserByUsername(username);
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);
}
```

### Activación de versión (transacción)

Cuando se activa una versión, todas las demás versiones PUBLICADAS del mismo reglamento pasan a ARCHIVADO:

```java
// VersionReglamentoService.activarVersion()
List<VersionReglamento> publicadas = versionRepository
    .findByReglamentoIdAndEstado(reglamentoId, EstadoVersion.PUBLICADO);
publicadas.forEach(v -> v.setEstado(EstadoVersion.ARCHIVADO));
versionRepository.saveAll(publicadas);

version.setEstado(EstadoVersion.PUBLICADO);
versionRepository.save(version);
```

### Integración LLM (RestClient)

Llamada al endpoint `/chat/completions` con el patrón OpenAI-compatible:

```java
// LlmService – llamada genérica al LLM
Map<String, Object> body = Map.of(
    "model", model,
    "messages", List.of(
        Map.of("role", "system", "content", systemPrompt),
        Map.of("role", "user",   "content", userPrompt)
    ),
    "temperature", 0.3
);
String raw = restClient.post()
    .uri("/chat/completions")
    .body(body)
    .retrieve()
    .body(String.class);
```

### Interceptor JWT Angular

El interceptor añade automáticamente el token a todas las peticiones HTTP salientes:

```typescript
// auth.interceptor.ts
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(AuthService).getToken();
  if (token) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }
  return next(req);
};
```
