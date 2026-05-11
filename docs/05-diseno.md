# 05. Diseño

## 5.1 Arquitectura de la aplicación

RuleTrack sigue una arquitectura de **tres capas** desplegadas como contenedores Docker independientes comunicados mediante una red interna (`ruletrack-net`). El exterior solo accede a través de Nginx.

```
                    ┌─────────────────────────────────────────────────────────┐
                    │             Red interna: ruletrack-net                  │
                    │                                                         │
  ┌──────────┐ :80  │  ┌──────────────────┐  /api/*   ┌──────────────────┐  │
  │Navegador │─────►│  │    frontend       │──────────►│    backend       │  │
  └──────────┘      │  │  Nginx 1.27       │           │  Spring Boot 4.0 │  │
                    │  │  · Sirve Angular  │           │  · API REST      │  │
                    │  │  · Reverse proxy  │           │  · Auth JWT      │  │
                    │  │  /api → backend   │           │  · IA (Groq LLM) │  │
                    │  │  /swagger-ui      │           │  :8080 (interno) │  │
                    │  │  :80 (externo)    │           └────────┬─────────┘  │
                    │  └──────────────────┘                    │ JDBC        │
                    │                                 ┌─────────▼──────────┐ │
                    │                                 │    postgres        │ │
                    │                                 │  PostgreSQL 16     │ │
                    │                                 │  vol: postgres_data│ │
                    │                                 └────────────────────┘ │
                    └─────────────────────────────────────────────────────────┘
```

### Servicios

| Servicio | Base | Puerto | Función |
|---|---|---|---|
| `frontend` | Nginx 1.27 Alpine | `80` prod / `4200` dev | Sirve la SPA Angular y hace reverse proxy al backend |
| `backend` | Eclipse Temurin 21 JRE | Solo interno `:8080` | API REST, autenticación JWT, lógica de negocio, integración LLM |
| `postgres` | PostgreSQL 16 Alpine | Solo interno `:5432` | Base de datos relacional con persistencia en volumen |

### Comunicación

- **Navegador → Nginx**: HTTP en el puerto `80` (producción) o `4200` (desarrollo).
- **Nginx → Backend**: reescritura de rutas `/api/*`, `/swagger-ui/*` y `/v3/*` hacia `http://ruletrack-backend:8080`.
- **Backend → PostgreSQL**: JDBC sobre la red Docker interna. El backend nunca se expone al exterior en producción.

---

## 5.2 Estructura del backend

El backend sigue la arquitectura en capas estándar de Spring Boot:

```
com.example.ruletrack/
├── config/           → Configuración CORS, OpenAPI/Swagger, Security
├── controller/       → Endpoints REST (AuthController, ReglamentoController,
│                       VersionReglamentoController, SugerenciaIAController,
│                       HistorialCambiosController, CorrectionController,
│                       DocumentConversionController)
├── dto/              → Objetos de transferencia de datos (request / response)
├── entity/           → Entidades JPA (Usuario, Reglamento, VersionReglamento,
│                       SugerenciaIA, HistorialCambios)
├── exception/        → Manejo centralizado de excepciones (ResourceNotFoundException, …)
├── repository/       → Interfaces Spring Data JPA
├── security/         → JwtTokenProvider, filtros y configuración de seguridad
├── service/          → Lógica de negocio (AuthService, ReglamentoService,
│                       VersionReglamentoService, SugerenciaIAService,
│                       HistorialCambiosService, LlmService)
└── RuletrackApplication.java
```

---

## 5.3 Diagrama entidad-relación

```
┌──────────────────────────────────────────────────────────────────────────┐
│                              BASE DE DATOS                               │
│                                                                          │
│  ┌──────────────────────┐          ┌──────────────────────────────────┐  │
│  │       usuarios        │          │           reglamentos            │  │
│  ├──────────────────────┤          ├──────────────────────────────────┤  │
│  │ PK id                │◄────────┐│ PK id                            │  │
│  │    username (unique) │         ││    titulo                        │  │
│  │    nombre            │         ││    descripcion                   │  │
│  │    apellidos         │         ││    visibilidad (ENUM)            │  │
│  │    fecha_nacimiento  │         ││    created_at                    │  │
│  │    email (unique)    │         ││    updated_at                    │  │
│  │    dni (unique)      │         ││ FK creado_por_id → usuarios.id   │  │
│  │    password          │         │└──────────────────────────────────┘  │
│  │    organizacion_nombre│        │                 │ 1:N                │
│  │    rol (ENUM)        │         │                 ▼                    │
│  │    created_at        │         │ ┌──────────────────────────────────┐ │
│  └──────────────────────┘         │ │       versiones_reglamento       │ │
│           ▲ 1:N                   │ ├──────────────────────────────────┤ │
│           │                       │ │ PK id                            │ │
│  ┌────────┴─────────────┐         │ │    numero_version                │ │
│  │ reglamento_usuarios  │         │ │    version_etiqueta              │ │
│  │    _permitidos       │         │ │    contenido (TEXT/Markdown)     │ │
│  ├──────────────────────┤         │ │    estado (ENUM)                 │ │
│  │ FK reglamento_id     │         │ │    fecha_creacion                │ │
│  │ FK usuario_id        │         │ │ FK reglamento_id                 │ │
│  └──────────────────────┘         │ │ FK creado_por_id → usuarios.id   │ │
│                                   │ └──────────────────────────────────┘ │
│                                   │        │ 1:N           │ 1:N         │
│                                   │        ▼               ▼             │
│                           ┌───────┴──────────┐  ┌───────────────────┐   │
│                           │  sugerencias_ia   │  │ historial_cambios │   │
│                           ├──────────────────┤  ├───────────────────┤   │
│                           │ PK id            │  │ PK id             │   │
│                           │    tipo (ENUM)   │  │ FK usuario_id     │   │
│                           │    contenido     │  │    descripcion    │   │
│                           │    fecha_gen.    │  │    campo_modif.   │   │
│                           │    aplicada      │  │    valor_anterior │   │
│                           │ FK version_id    │  │    valor_nuevo    │   │
│                           └──────────────────┘  │    fecha_cambio   │   │
│                                                  └───────────────────┘   │
└──────────────────────────────────────────────────────────────────────────┘
```

### Enumeraciones

| Entidad | Campo | Valores posibles |
|---|---|---|
| `Usuario` | `rol` | `ORGANIZADOR`, `USUARIO` |
| `Reglamento` | `visibilidad` | `PUBLICO`, `SOLO_MIEMBROS`, `PRIVADO` |
| `VersionReglamento` | `estado` | `BORRADOR`, `PUBLICADO`, `ARCHIVADO` |
| `SugerenciaIA` | `tipo` | `REVISION`, `RESUMEN`, `INCOHERENCIAS` |

---

## 5.4 Diagrama de casos de uso

```
                        ┌─────────────────────────────────────────────────┐
                        │                   Sistema RuleTrack              │
                        │                                                  │
  ┌─────────────┐       │  ┌──────────────────────────────────────────┐   │
  │  Externo    │──────►│  │ Ver reglamentos públicos                 │   │
  │ (sin login) │       │  │ Acceder a vista pública de un reglamento │   │
  └─────────────┘       │  └──────────────────────────────────────────┘   │
                        │                                                  │
  ┌─────────────┐       │  ┌──────────────────────────────────────────┐   │
  │  USUARIO    │──────►│  │ Registrarse / Iniciar sesión             │   │
  │  (miembro)  │       │  │ Consultar perfil                         │   │
  │             │       │  │ Ver reglamentos (públicos + miembros)    │   │
  │             │       │  │ Consultar versión vigente                │   │
  └─────────────┘       │  └──────────────────────────────────────────┘   │
                        │                                                  │
  ┌─────────────┐       │  ┌──────────────────────────────────────────┐   │
  │ ORGANIZADOR │──────►│  │ Todo lo anterior, más:                   │   │
  │             │       │  │ Crear / editar / eliminar reglamentos    │   │
  │             │       │  │ Gestionar versiones (crear, activar)     │   │
  │             │       │  │ Cambiar visibilidad del reglamento       │   │
  │             │       │  │ Subir y convertir documentos (PDF/DOCX)  │   │
  │             │       │  │ Solicitar análisis IA (revisión,         │   │
  │             │       │  │   resumen, incoherencias)                │   │
  │             │       │  │ Marcar sugerencias IA como aplicadas     │   │
  │             │       │  │ Consultar historial de cambios           │   │
  │             │       │  │ Gestionar miembros de la organización    │   │
  └─────────────┘       │  └──────────────────────────────────────────┘   │
                        └─────────────────────────────────────────────────┘
```

---

## 5.5 Diagrama de flujo: publicación de un reglamento

```
  Inicio
    │
    ▼
  Organizador crea reglamento (título, descripción, visibilidad)
    │
    ▼
  ¿Subir documento existente?
  ├─ Sí → Subir PDF/DOCX → Convertir a Markdown → Contenido precargado
  └─ No → Escribir contenido Markdown manualmente
    │
    ▼
  Se crea versión BORRADOR con etiqueta sugerida (ej. "1.0")
    │
    ▼
  ¿Solicitar análisis IA?
  ├─ Sí → Elegir tipo: Revisión / Resumen / Incoherencias
  │         │
  │         ▼
  │       LLM procesa contenido → Sugerencias guardadas
  │         │
  │         ▼
  │       Organizador revisa sugerencias → Aplica cambios manualmente
  └─ No → continuar
    │
    ▼
  Activar versión → Estado cambia a PUBLICADO
  (Las versiones anteriores pasan a ARCHIVADO automáticamente)
    │
    ▼
  Reglamento visible según visibilidad configurada
    │
    ▼
  Fin
```

---

## 5.6 Diseño de la API REST

La API está disponible bajo el prefijo `/api` y documentada con OpenAPI 3 / Swagger UI en `/swagger-ui.html`.

### Autenticación (`/api/auth`)

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `POST` | `/auth/register` | No | Registrar nuevo usuario |
| `POST` | `/auth/login` | No | Iniciar sesión, devuelve JWT |
| `GET` | `/auth/me` | JWT | Datos del usuario autenticado |
| `PUT` | `/auth/me` | JWT | Actualizar perfil |
| `GET` | `/auth/organizacion/existe` | No | Comprobar si existe una organización |
| `GET` | `/auth/organizacion/info` | No | Información pública de una organización |
| `GET` | `/auth/organizacion/miembros` | JWT | Listar miembros de la organización |

### Reglamentos (`/api/reglamentos`)

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `GET` | `/reglamentos/publicos` | No | Listar reglamentos públicos |
| `GET` | `/reglamentos/publico/{id}` | No | Vista pública de un reglamento |
| `POST` | `/reglamentos` | JWT | Crear reglamento con versión inicial |
| `GET` | `/reglamentos/organizacion` | JWT | Reglamentos de la organización |
| `GET` | `/reglamentos/visibles` | JWT | Reglamentos accesibles para el usuario |
| `GET` | `/reglamentos/{id}` | JWT | Obtener reglamento por ID |
| `PUT` | `/reglamentos/{id}` | JWT | Actualizar reglamento |
| `DELETE` | `/reglamentos/{id}` | JWT | Eliminar reglamento |

### Versiones (`/api/reglamentos/{id}/versiones`)

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `GET` | `/reglamentos/{id}/versiones` | JWT | Listar versiones |
| `GET` | `/reglamentos/{id}/versiones/siguiente-etiqueta` | JWT | Sugerir etiqueta de versión |
| `POST` | `/reglamentos/{id}/versiones` | JWT | Crear nueva versión |
| `GET` | `/versiones/{id}` | JWT | Obtener versión por ID |
| `PATCH` | `/versiones/{id}/activar` | JWT | Activar versión (publica y archiva las demás) |
| `PATCH` | `/versiones/{id}/estado` | JWT | Cambiar estado de una versión |

### Sugerencias IA (`/api/versiones/{id}/sugerencias`)

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `GET` | `/versiones/{id}/sugerencias` | JWT | Listar sugerencias de una versión |
| `POST` | `/versiones/{id}/sugerencias/revision` | JWT | Generar revisión de calidad |
| `POST` | `/versiones/{id}/sugerencias/resumen` | JWT | Generar resumen ejecutivo |
| `POST` | `/versiones/{id}/sugerencias/incoherencias` | JWT | Detectar incoherencias |
| `PATCH` | `/sugerencias/{id}/aplicada` | JWT | Marcar sugerencia como aplicada |

### Historial de cambios

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `GET` | `/versiones/{id}/historial` | JWT | Obtener historial de una versión |

### Correcciones y conversión documental

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `POST` | `/corrections` | JWT | Analizar correcciones ortográficas/gramaticales con IA |
| `POST` | `/documents/convert` | JWT | Convertir PDF o DOCX a Markdown |

### Formato de respuesta JWT

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "organizador1",
  "rol": "ORGANIZADOR",
  "organizacionNombre": "MiOrganizacion"
}
```

### Cabecera de autorización

Todas las rutas protegidas requieren:

```
Authorization: Bearer <token>
```

---

## 5.7 Seguridad

- **Autenticación**: JWT firmado con HMAC-SHA256. Expiración configurable (por defecto 24 h).
- **Autorización**: Spring Security con filtro de validación de token en cada request.
- **Control de acceso a contenido**: la capa de servicio valida la visibilidad del reglamento (`PUBLICO` / `SOLO_MIEMBROS` / `PRIVADO`) antes de devolver datos.
- **Passwords**: almacenados con BCrypt (factor de coste por defecto de Spring Security).
- **CORS**: lista blanca de orígenes configurable via `CORS_ALLOWED_ORIGINS`.
- **Secreto JWT**: inyectado mediante variable de entorno `JWT_SECRET`, sin valor por defecto en producción.
