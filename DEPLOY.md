# Guía de despliegue — RuleTrack

## Requisitos previos

| Herramienta | Versión mínima |
|---|---|
| Docker Engine | 24.x |
| Docker Compose | v2 (incluido en Docker Desktop) |
| Git | cualquiera reciente |

---

## 1. Obtener el código

```bash
git clone https://github.com/tu-usuario/ruletrack.git
cd ruletrack
```

---

## 2. Variables de entorno

```bash
cp .env.example .env
```

Editar `.env` con los valores reales:

| Variable | Descripción | Valor por defecto (dev) |
|---|---|---|
| `DB_USERNAME` | Usuario PostgreSQL | `ruletrack` |
| `DB_PASSWORD` | Contraseña PostgreSQL | `ruletrack_password_segura` |
| `JWT_SECRET` | Secreto JWT (≥ 32 caracteres) | Cambiar en producción |
| `LLM_API_KEY` | API key del proveedor LLM (Groq, OpenAI…) | *(obligatorio para IA)* |
| `LLM_API_URL` | Endpoint OpenAI-compatible | `https://api.groq.com/openai/v1` |
| `LLM_MODEL` | Modelo a usar | `llama-3.3-70b-versatile` |
| `BACKEND_URL` | URL interna del backend (nginx → backend) | `http://ruletrack-backend:8080` |
| `CORS_ALLOWED_ORIGINS` | Origen permitido en CORS (solo prod) | URL pública del frontend |

> **Seguridad**: el fichero `.env` está en `.gitignore`. Nunca lo subas al repositorio.

---

## 3. Despliegue en desarrollo

```bash
# Construir imágenes y levantar en segundo plano
docker compose up -d --build

# Ver estado de los contenedores
docker compose ps
```

Salida esperada:

```
NAME                   IMAGE                STATUS          PORTS
ruletrack-postgres     postgres:16-alpine   Up (healthy)    0.0.0.0:5432->5432/tcp
ruletrack-backend      ruletrack-backend    Up              0.0.0.0:8000->8080/tcp
ruletrack-frontend     ruletrack-frontend   Up              0.0.0.0:4200->80/tcp
```

Aplicación disponible en `http://localhost:4200`.

---

## 4. Despliegue en producción

```bash
# Usando el compose de producción
docker compose -f docker-compose.prod.yml up -d --build

docker compose -f docker-compose.prod.yml ps
```

En producción solo Nginx está expuesto al exterior (`:80`).
El backend queda en la red interna.

### Producción con base de datos gestionada (DigitalOcean, RDS…)

Añadir en `.env`:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://host-managed-db:5432/ruletrack?sslmode=require
DB_USERNAME=doadmin
DB_PASSWORD=tu_password
CORS_ALLOWED_ORIGINS=https://tu-dominio.com
BACKEND_URL=http://ruletrack-backend:8080
```

---

## 5. Verificación del despliegue

### 5.1 Estado de los contenedores

```bash
docker compose ps
docker compose logs --tail=30 backend
docker compose logs --tail=20 frontend
```

### 5.2 Verificar el servidor web (reverse proxy)

```bash
# Cabeceras HTTP — debe responder Nginx
curl -I http://localhost:4200

# Ejemplo de respuesta esperada:
# HTTP/1.1 200 OK
# Server: nginx/1.27.x
# Content-Type: text/html
```

### 5.3 Verificar el backend a través del proxy

```bash
# Registro de usuario (a través de Nginx → proxy → backend)
curl -s -X POST http://localhost:4200/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username":"testuser",
    "nombre":"Test","apellidos":"User",
    "fechaNacimiento":"1995-01-01",
    "email":"test@ejemplo.com",
    "dni":"12345678A",
    "password":"clave1234",
    "rol":"USUARIO",
    "organizacionNombre":"TestOrg",
    "crearOrganizacion":true
  }' | jq .

# Login y guardar token
TOKEN=$(curl -s -X POST http://localhost:4200/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"clave1234"}' | jq -r .token)

echo "Token: $TOKEN"

# Reglamentos públicos (sin autenticación)
curl -s http://localhost:4200/api/reglamentos/publicos | jq .

# Endpoint autenticado
curl -s http://localhost:4200/api/auth/me \
  -H "Authorization: Bearer $TOKEN" | jq .
```

### 5.4 Verificar Swagger UI

```bash
# Spec OpenAPI disponible a través del proxy
curl -s http://localhost:4200/v3/api-docs | jq .info

# O abrir en navegador:
# http://localhost:4200/swagger-ui.html
```

### 5.5 Logs del proxy con peticiones reales

```bash
# Ver peticiones en tiempo real que pasan por Nginx
docker compose logs -f frontend
```

---

## 6. Prueba de carga ligera

Requiere `ab` (Apache Bench, incluido en `apache2-utils`):

```bash
# 100 peticiones, 10 concurrentes al endpoint público
ab -n 100 -c 10 http://localhost:4200/api/reglamentos/publicos

# Ejemplo de métricas esperadas:
# Requests per second:    ~80 [#/sec]
# Time per request:       ~125 ms (mean)
# Failed requests:        0
```

Alternativa sin instalar nada:

```bash
# 50 peticiones secuenciales midiendo tiempos
for i in $(seq 1 50); do
  curl -o /dev/null -s -w "%{time_total}\n" \
    http://localhost:4200/api/reglamentos/publicos
done | awk '{sum+=$1} END {printf "Media: %.3fs sobre %d peticiones\n", sum/NR, NR}'
```

---

## 7. Troubleshooting

### El backend no arranca (error de conexión a BD)

```bash
# Ver logs del backend
docker compose logs backend

# Ver si postgres está healthy
docker compose ps postgres

# Reiniciar solo el backend tras que postgres esté sano
docker compose restart backend
```

### Error 502 Bad Gateway en /api/

```bash
# El backend no está respondiendo; comprobar logs
docker compose logs backend --tail=50

# Verificar que el backend responde internamente
docker compose exec frontend curl -s http://ruletrack-backend:8080/api/reglamentos/publicos
```

### Cambios en código no se reflejan

```bash
# Reconstruir la imagen del servicio afectado
docker compose up -d --build backend
# o
docker compose up -d --build frontend
```

### Limpiar todo y empezar desde cero

```bash
docker compose down -v   # elimina también los volúmenes (¡borra la BD!)
docker compose up -d --build
```

### Variables de entorno no se aplican

```bash
# Verificar que .env está en el directorio raíz del proyecto
ls -la .env

# Ver qué variables lee el compose
docker compose config | grep -A5 environment
```

---

## 8. Estructura del proyecto

```
ruletrack/
├── docker-compose.yml          # Entorno de desarrollo
├── docker-compose.prod.yml     # Entorno de producción
├── .env.example                # Plantilla de variables de entorno
├── backend/
│   ├── Dockerfile              # Multi-stage build: Maven → JRE 21
│   ├── src/                    # Código fuente Spring Boot
│   └── docs/
│       └── ruletrack-api.http  # 30 peticiones de prueba (REST Client)
└── frontend/
    ├── Dockerfile              # Multi-stage build: Node → Nginx
    ├── nginx.conf              # Configuración reverse proxy + SPA fallback
    └── src/                    # Código fuente Angular
```
