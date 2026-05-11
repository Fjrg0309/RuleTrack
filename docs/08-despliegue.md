# 08. Despliegue

> Para los criterios de evaluación del módulo (criterio 7 y criterio 8) ver: [08-despliegue-eval.md](08-despliegue-eval.md)

---

## 8.1 Entorno de despliegue

RuleTrack está diseñado para desplegarse en cualquier entorno con soporte de Docker Compose. La infraestructura objetivo principal es un **VPS o servidor en la nube** (DigitalOcean, Hetzner, AWS EC2, etc.) con Docker instalado.

El pipeline CI/CD publica las imágenes Docker en **GitHub Container Registry (GHCR)**, desde donde pueden descargarse en cualquier servidor de destino.

---

## 8.2 Requisitos del servidor

| Herramienta | Versión mínima |
|---|---|
| Docker Engine | 24.x |
| Docker Compose | v2 (incluido en Docker Desktop) |
| Git | cualquiera reciente |

---

## 8.3 Pipeline CI/CD (GitHub Actions)

El fichero `.github/workflows/ci.yml` define tres jobs:

### Job 1: `backend-ci` — Build y tests del backend

- Se ejecuta en todo push a `main` o `develop` y en PRs a `main`.
- Configura Java 21 (Temurin) con caché Maven.
- Ejecuta los tests unitarios (`AuthServiceTest`, `ReglamentoServiceTest`).
- Publica los informes Surefire como artefacto del workflow.

### Job 2: `frontend-ci` — Build de producción del frontend

- Se ejecuta en paralelo con `backend-ci`.
- Configura Node 22 con caché npm.
- Instala dependencias con `npm ci`.
- Construye el bundle de producción Angular con `ng build --configuration production`.

### Job 3: `docker-publish` — Build y push a GHCR

- Solo se ejecuta en push a `main` (no en PRs).
- Depende de que `backend-ci` y `frontend-ci` pasen correctamente.
- Autentica en GHCR con `GITHUB_TOKEN` (sin secretos adicionales necesarios).
- Construye y publica dos imágenes con doble etiqueta:
  - `:latest`
  - `:<sha-del-commit>`
- Utiliza caché de GitHub Actions (`cache-from: type=gha`) para acelerar builds sucesivos.

```yaml
# Resumen del pipeline
on:
  push:    { branches: [main, develop] }
  pull_request: { branches: [main] }

jobs:
  backend-ci:  → mvn test (AuthServiceTest, ReglamentoServiceTest)
  frontend-ci: → npm ci + ng build --configuration production
  docker-publish: (solo en push a main)
    → ghcr.io/<repo>/backend:latest + :<sha>
    → ghcr.io/<repo>/frontend:latest + :<sha>
```

---

## 8.4 Proceso de despliegue

### Paso 1: Clonar el repositorio

```bash
git clone https://github.com/mi_usuario/ruletrack.git
cd ruletrack
```

### Paso 2: Configurar variables de entorno

```bash
cp .env.example .env
```

Editar `.env` con los valores de producción:

| Variable | Descripción |
|---|---|
| `DB_USERNAME` | Usuario PostgreSQL |
| `DB_PASSWORD` | Contraseña PostgreSQL (robusta) |
| `JWT_SECRET` | Secreto JWT (mínimo 32 caracteres, generado aleatoriamente) |
| `LLM_API_KEY` | API key del proveedor LLM (Groq, OpenAI…) |
| `LLM_API_URL` | Endpoint OpenAI-compatible (por defecto Groq) |
| `LLM_MODEL` | Modelo LLM (por defecto `llama-3.3-70b-versatile`) |
| `BACKEND_URL` | URL interna del backend: `http://ruletrack-backend:8080` |
| `CORS_ALLOWED_ORIGINS` | Origen CORS permitido (URL pública del frontend) |
| `SPRING_DATASOURCE_URL` | URL JDBC (solo si se usa BD gestionada externa) |

> El fichero `.env` está en `.gitignore`. Nunca debe subirse al repositorio.

### Paso 3: Arrancar en producción

```bash
docker compose -f docker-compose.prod.yml up -d --build
docker compose -f docker-compose.prod.yml ps
```

En producción:
- Solo el **frontend/Nginx** expone el puerto `80` al exterior.
- El backend queda exclusivamente en la red Docker interna.
- Nginx actúa como reverse proxy reenviando `/api/*`, `/swagger-ui/*` y `/v3/*` al backend.

### Paso 4: Verificar el despliegue

```bash
# Cabeceras HTTP – debe responder Nginx
curl -I http://localhost

# Spec OpenAPI a través del proxy
curl -s http://localhost/v3/api-docs | jq .info

# Swagger UI
# http://<servidor>/swagger-ui.html
```

---

## 8.5 Configuración de Nginx

El fichero `frontend/nginx.conf` configura Nginx como:

1. **Servidor estático**: sirve los ficheros del bundle Angular desde `/usr/share/nginx/html`.
2. **Reverse proxy para la API**: todas las peticiones a `/api/`, `/swagger-ui/` y `/v3/` se reenvían al backend usando la variable de entorno `BACKEND_URL`.
3. **SPA fallback**: las rutas desconocidas sirven `index.html` para que Angular gestione el enrutado del lado del cliente.

---

## 8.6 Dockerfiles (build multi-stage)

### Backend (`backend/Dockerfile`)

Build en dos etapas:
1. **Etapa `build`**: imagen Maven + Java 21 para compilar y empaquetar el JAR.
2. **Etapa final**: imagen `eclipse-temurin:21-jre` ligera solo con el JRE y el JAR ejecutable.

Resultado: imagen final de menor tamaño sin incluir Maven ni el código fuente.

### Frontend (`frontend/Dockerfile`)

Build en dos etapas:
1. **Etapa `build`**: imagen Node 22 para instalar dependencias y construir el bundle de producción Angular.
2. **Etapa final**: imagen `nginx:1.27-alpine` con solo los ficheros estáticos y la configuración de Nginx.

---

## 8.7 Despliegue con base de datos gestionada

Para usar una base de datos gestionada (DigitalOcean Managed Databases, AWS RDS, etc.):

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://host-db:5432/ruletrack?sslmode=require
DB_USERNAME=admin
DB_PASSWORD=password_segura
```

En este caso, el servicio `postgres` del compose puede omitirse o eliminarse.

---

## 8.8 Troubleshooting de despliegue

| Problema | Diagnóstico | Solución |
|---|---|---|
| Backend no arranca | `docker compose logs backend` | Esperar a que Postgres esté `healthy`; verificar `DB_*` en `.env` |
| Error 502 Bad Gateway en `/api/` | `docker compose logs backend --tail=50` | El backend no responde; reiniciar con `docker compose restart backend` |
| Cambios de código no reflejados | — | `docker compose up -d --build backend` o `--build frontend` |
| Variables de entorno no aplicadas | `docker compose config` | Verificar que `.env` está en el directorio raíz |
| Puerto 80 ocupado en producción | `ss -tlnp \| grep :80` | Parar el servicio conflictivo o cambiar el puerto en el compose |

---

## 8.9 URL de producción

La URL de la aplicación en producción depende del servidor donde se despliegue. Con un dominio configurado y Nginx sirviendo en el puerto `80`, la aplicación queda accesible en:

```
https://ruletrack-app-94agc.ondigitalocean.app/

```

Para HTTPS se recomienda añadir un certificado SSL con Let's Encrypt (Certbot) configurado en Nginx, o usar el SSL terminado en el proveedor cloud (load balancer, CDN).
