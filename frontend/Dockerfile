# ── Stage 1: build Angular ────────────────────────────────────────────────────
FROM node:22-alpine AS builder

WORKDIR /app

# Cache de dependencias
COPY package.json package-lock.json ./
RUN npm install --prefer-offline --no-audit --no-fund \
    || npm install --no-audit --no-fund

# Copia el resto y compila en modo producción
COPY . .
RUN npx ng build --configuration production

# ── Stage 2: Nginx ────────────────────────────────────────────────────────────
FROM nginx:1.27-alpine

# Elimina la config por defecto de Nginx
RUN rm -rf /etc/nginx/conf.d/default.conf

# Copia la config como template — envsubst reemplaza ${BACKEND_URL} en arranque
COPY nginx.conf /etc/nginx/templates/default.conf.template

# Copia los archivos compilados de Angular
# El nuevo @angular/build:application genera la salida en dist/<project>/browser
COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
