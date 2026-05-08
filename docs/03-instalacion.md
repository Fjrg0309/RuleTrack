# 03. Instalacion y preparacion del entorno

## 3.1 Requisitos previos
### Requisitos minimos recomendados
- Docker Engine 24+
- Docker Compose v2
- Git

### Requisitos opcionales (desarrollo sin Docker)
- Java 21
- Maven 3.9+
- Node.js 22+
- Angular CLI 20.x

## 3.2 Clonado del repositorio
```bash
git clone https://github.com/tu-usuario/ruletrack.git
cd ruletrack
```

## 3.3 Variables de entorno
Copiar el archivo de ejemplo:

```bash
cp .env.example .env
```

Configurar como minimo:

- DB_USERNAME
- DB_PASSWORD
- JWT_SECRET
- LLM_API_URL
- LLM_API_KEY
- LLM_MODEL
- BACKEND_URL

### Ejemplo de desarrollo
```env
DB_USERNAME=ruletrack
DB_PASSWORD=ruletrack_password_segura
JWT_SECRET=cambia-este-secreto-en-produccion-minimo-32-chars
LLM_API_URL=https://api.openai.com/v1
LLM_API_KEY=tu_api_key_aqui
LLM_MODEL=gpt-4o
BACKEND_URL=http://ruletrack-backend:8080
```

## 3.4 Arranque en desarrollo con Docker Compose
```bash
docker compose up -d --build
docker compose ps
```

Servicios esperados:
- frontend: http://localhost:4200
- backend: http://localhost:8000
- postgres: localhost:5432

## 3.5 Arranque en produccion con Docker Compose
```bash
docker compose -f docker-compose.prod.yml up -d --build
docker compose -f docker-compose.prod.yml ps
```

En produccion el backend no se expone externamente; solo queda accesible a traves del frontend/nginx.

## 3.6 Verificacion rapida
```bash
curl -I http://localhost:4200
curl -s http://localhost:4200/v3/api-docs
```

## 3.7 Ejecucion local por separado (sin Docker)
### Backend
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm ci
npm start
```

## 3.8 Scripts y ficheros relevantes
- docker-compose.yml: entorno de desarrollo
- docker-compose.prod.yml: entorno de produccion
- backend/Dockerfile: build multi-stage Java
- frontend/Dockerfile: build Angular + servido con nginx

## 3.9 Problemas frecuentes
### Error de conexion a BD
- Verificar variables DB en .env.
- Esperar healthcheck de postgres.

### Error de IA
- Revisar LLM_API_KEY y LLM_API_URL.

### Backend no accesible en produccion
- Revisar BACKEND_URL en frontend/nginx y compose de produccion.
