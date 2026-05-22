# RuleTrack

URL: https://ruletrack-app-94agc.ondigitalocean.app/

## Datos de prueba para usuario organizador
- ### Usuario: fran_03
- ### Contraseña: Fjrg1303-

Sistema web para la **gestión y versionado de reglamentos** de organizaciones deportivas o civiles.
Permite redactar, versionar y publicar reglamentos con control de visibilidad, corrección asistida por IA y conversión automática de documentos.

---

## Arquitectura

La aplicación sigue una arquitectura de **tres capas** desplegada como servicios Docker independientes comunicados mediante una red interna (`ruletrack-net`). El exterior solo accede a través de Nginx, que actúa como único punto de entrada.

```
                          ┌─────────────────────────────────────────────────────────┐
                          │             Red interna: ruletrack-net                  │
                          │                                                         │
  ┌────────────┐  :80     │  ┌─────────────────┐  /api/*   ┌──────────────────┐   │
  │  Navegador │ ────────►│  │    frontend      │ ─────────►│    backend       │   │
  │  / curl    │          │  │  Nginx 1.27      │           │  Spring Boot 4.0 │   │
  └────────────┘          │  │  · Sirve Angular │           │  · API REST      │   │
                          │  │  · Reverse proxy │           │  · Auth JWT      │   │
                          │  │    /api → back   │           │  · IA (Groq LLM) │   │
                          │  │    /swagger-ui   │           │  :8080 (interno) │   │
                          │  │    /v3 → back    │           └────────┬─────────┘   │
                          │  │  :80 (externo)   │                    │ JDBC :5432  │
                          │  └─────────────────┘            ┌────────▼──────────┐  │
                          │                                  │    postgres       │  │
                          │                                  │  PostgreSQL 16    │  │
                          │                                  │  :5432 (interno)  │  │
                          │                                  │  vol: pg_data     │  │
                          │                                  └───────────────────┘  │
                          └─────────────────────────────────────────────────────────┘
```

### Servicios

| Servicio | Imagen / base | Puerto externo | Función |
|---|---|---|---|
| `frontend` | `nginx:1.27-alpine` | `80` (prod) / `4200` (dev) | Sirve la SPA Angular y hace reverse proxy al backend para `/api/`, `/swagger-ui/` y `/v3/` |
| `backend` | `eclipse-temurin:21-jre` | Solo interno `:8080` | API REST Spring Boot 4: autenticación JWT, gestión de reglamentos y versiones, integración LLM |
| `postgres` | `postgres:16-alpine` | Solo interno `:5432` (dev expone `5432` para herramientas) | Base de datos relacional con persistencia en volumen `postgres_data` |

### Comunicación entre servicios

- **Navegador → Nginx**: HTTP en el puerto `80` (producción) o `4200` (desarrollo).
- **Nginx → Backend**: Nginx reescribe `/api/*`, `/swagger-ui/*` y `/v3/*` hacia `http://ruletrack-backend:8080` usando la resolución DNS interna de Docker.
- **Backend → PostgreSQL**: JDBC sobre `jdbc:postgresql://postgres:5432/ruletrack` dentro de la red Docker. La contraseña viaja únicamente por la red interna.
- El backend **nunca se expone al exterior** en producción: solo Nginx tiene puertos publicados.

---

## Tecnologías

| Capa | Stack |
|---|---|
| Frontend | Angular 20, TypeScript, SCSS, Nginx |
| Backend | Spring Boot 4, Java 21, Spring Security, JWT, JPA/Hibernate |
| Base de datos | PostgreSQL 16 |
| IA | Groq API (LLaMA 3.3 70B) / cualquier endpoint OpenAI-compatible |
| Contenedores | Docker, Docker Compose |
| CI/CD | GitHub Actions → GHCR |

---

## Requisitos

- Docker >= 24 y Docker Compose v2
- (Opcional, solo desarrollo local) Node 22, Java 21, Maven 3.9

---

## Inicio rápido (desarrollo)

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/ruletrack.git
cd ruletrack

# 2. Crear fichero de variables de entorno
cp .env.example .env
# Editar .env y añadir al menos LLM_API_KEY

# 3. Levantar todos los servicios
docker compose up -d --build

# 4. Comprobar que todo está en pie
docker compose ps

# 5. Abrir en el navegador
open http://localhost:4200
```

---

## Documentación adicional

- **Guía de despliegue completa (producción, verificación, troubleshooting)**: [DEPLOY.md](DEPLOY.md)
- **Swagger UI / API interactiva**: `http://localhost:4200/swagger-ui.html`
- **OpenAPI spec (JSON)**: `http://localhost:4200/v3/api-docs`
- **Peticiones HTTP de prueba**: [`backend/docs/ruletrack-api.http`](backend/docs/ruletrack-api.http)
