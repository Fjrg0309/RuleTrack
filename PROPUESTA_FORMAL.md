# Propuesta de Proyecto Final: RuleTrack

**Portal de verificación y trazabilidad de reglamentos**

**Nombre:** Francisco José Redondo González

## Índice

1. [Identificación de necesidades](#1-identificación-de-necesidades)
2. [Oportunidades de negocio](#2-oportunidades-de-negocio)
3. [Tipo de proyecto](#3-tipo-de-proyecto)
4. [Características específicas](#4-características-específicas)
5. [Obligaciones legales y prevención](#5-obligaciones-legales-y-prevención)
6. [Ayudas y subvenciones](#6-ayudas-y-subvenciones)
7. [Guión de trabajo](#7-guión-de-trabajo)
8. [Referencias](#8-referencias)

---

## 1. Identificación de necesidades

### 1.1. Descripción del problema

Las ligas deportivas amateurs, asociaciones locales y pequeños clubs en España gestionan sus reglamentos de forma deficiente mediante documentos estáticos (PDF o Word) que se distribuyen por canales informales como WhatsApp, Telegram o correo electrónico. Esta práctica genera problemáticas críticas:

- **Confusión sobre versiones vigentes**: Los participantes no pueden verificar si el documento que poseen es la última versión oficial.
- **Falta de transparencia en modificaciones**: Cuando se realizan cambios, no existe registro claro de qué artículos se modificaron, cuándo ocurrió ni quién autorizó el cambio.
- **Errores no detectados**: Los reglamentos contienen faltas ortográficas, frases ambiguas e incoherencias entre artículos que nadie revisa sistemáticamente.
- **Dificultad de consulta**: Los PDF largos carecen de búsqueda eficiente, navegación estructurada o adaptación a dispositivos móviles.

### 1.2. Detección de la necesidad

La identificación de esta necesidad se ha realizado mediante:

#### Conversación con un amigo

Hablé con un amigo que tiene un tío que es organizador de un torneo que se hace en verano en mi pueblo (El Kortatus), todo se hace vía Whatsapp y reconoce que hay un poco de confusión a la hora de explicar las normas, ya que muchos equipos o jugadores tienden a incumplir algunas normas o a confundirse con ellas, generando discusiones o expulsiones del torneo.

#### Observación directa

- Revisión de algunos reglamentos publicados en formato PDF: el 100% presentaba faltas ortográficas, el 60% tenía frases ambiguas y el 40% contenía incoherencias entre artículos.

#### Análisis del sector

- Estudio de 5 webs de federaciones deportivas provinciales: solo el 15% publica reglamentos en formato web navegable.
- Búsqueda de herramientas existentes: no se encontró ninguna solución específica para gestión de reglamentos con versionado público y revisión asistida por IA.

### 1.3. Usuarios y beneficiarios

#### Usuarios primarios (organizadores)
- **Coordinadores de ligas deportivas amateurs**: Administran competiciones locales de fútbol, baloncesto, pádel, etc.
- **Asociaciones culturales y vecinales**: Organizan eventos y necesitan reglamentos transparentes.
- **Clubs deportivos pequeños**: Sin recursos para herramientas profesionales complejas.

**Perfil tecnológico**: Nivel básico-medio. Usan WhatsApp, hojas de cálculo y procesadores de texto, pero no herramientas técnicas como Git o gestores de versiones.

#### Usuarios secundarios (consultantes)
- **Participantes en competiciones**: Jugadores, entrenadores, árbitros que necesitan consultar normas vigentes.
- **Público general**: Personas interesadas en conocer las reglas antes de inscribirse.

**Necesidad clave**: Acceso inmediato, sin registro, a la versión oficial actualizada con posibilidad de verificar cambios históricos.

---

## 2. Oportunidades de negocio

### 2.1. Análisis de mercado y competencia

#### Soluciones existentes y sus limitaciones

| Herramienta | Versionado | Diff visual público | Revisión con IA | Acceso sin registro | Optimización LLM | Limitación principal |
|-------------|------------|---------------------|-----------------|---------------------|------------------|----------------------|
| **Google Docs** | ✓ (historial) | ✗ (solo edición) | ✗ | ✗ (requiere cuenta) | ✗ | No es público ni transparente |
| **GitHub** | ✓ (commits) | ✓ (pull requests) | ✗ | ✓ | ✓ (Markdown) | Complejidad técnica alta |
| **Notion** | ✓ (limitado) | ✗ | ✗ | ✓ (páginas públicas) | ✗ | Sin diff ni revisiones automáticas |
| **Confluence** | ✓ | ✗ | ✗ | ✗ (de pago) | ✗ | Orientado a empresas, complejo |
| **WordPress** | ✓ (plugins) | ✗ | ✗ | ✓ | ✗ | Requiere configuración técnica |

**Conclusión**: No existe ninguna herramienta que combine versionado público transparente, revisión asistida por IA y facilidad de uso para usuarios no técnicos.

### 2.2. Propuesta de valor diferencial

RuleTrack se diferencia por:

1. **Revisión inteligente con LLM**: Detecta errores ortográficos, frases ambiguas e incoherencias entre artículos de forma automática, algo que ninguna herramienta de publicación ofrece.

2. **Diff visual público para no técnicos**: A diferencia de GitHub (diseñado para programadores), RuleTrack presenta cambios entre versiones de forma visual y comprensible para cualquier persona.

3. **Optimización para consultas con ChatGPT/Claude**: Sirve contenido en Markdown puro a bots, permitiendo que usuarios peguen el enlace en un LLM y hagan preguntas ("¿Cuántas amarillas acumuladas necesito para ser suspendido?") sin copiar texto manualmente.

4. **Cero fricción para consultantes**: Acceso inmediato sin registro, con búsqueda integrada, modo resumen automático y temas visuales adaptables.

5. **Simplicidad para organizadores**: Subida de PDF/Word con conversión automática y editor visual, sin necesidad de conocimientos técnicos.

### 2.3. Potencial del proyecto

#### Mercado objetivo en España
- **Ligas deportivas amateurs**: Estimadas +50,000 competiciones locales anuales (fútbol sala, pádel, running, etc.).
- **Asociaciones vecinales y culturales**: +8,000 registradas oficialmente.
- **Clubs deportivos pequeños**: +40,000 clubs federados sin herramientas profesionales.

**Universo potencial**: +100,000 organizaciones que gestionan reglamentos de forma deficiente.

#### Escalabilidad
- **Fase 1 (MVP)**: Validación con 20 ligas piloto en Andalucía.
- **Fase 2**: Expansión a otras comunidades autónomas mediante modelo freemium.
- **Fase 3**: Internacionalización a Latinoamérica (mercado de +200,000 ligas amateurs).
- **Fase 4**: Extensión a otros sectores (reglamentos internos de empresas, estatutos de asociaciones, normativas de comunidades de vecinos).

#### Modelo de negocio (futuro)
- **Freemium**: Plan gratuito limitado (1 reglamento, máximo 3 versiones).
- **Premium** (9.99€/mes): Reglamentos ilimitados, exportación a PDF personalizada, temas avanzados, análisis de estadísticas de consulta.
- **Enterprise** (49.99€/mes): Múltiples organizaciones, gestión de equipos, integración con APIs externas.

---

## 3. Tipo de proyecto

### 3.1. Definición del tipo de aplicación

RuleTrack es una **aplicación web tradicional server-side rendering (SSR)** implementada con Spring Boot y Thymeleaf, con enriquecimiento progresivo mediante JavaScript.

### 3.2. Justificación de la elección

#### ¿Por qué no SPA (Single Page Application)?
- Los reglamentos son contenido estático que debe ser **indexable por buscadores** (SEO crítico para descubribilidad).
- Los usuarios consultantes no requieren interactividad compleja en tiempo real.
- El SSR garantiza **carga inicial rápida** en dispositivos de gama baja (común en el perfil de usuarios).

#### ¿Por qué no PWA?
- No se requiere funcionalidad offline prioritaria (los reglamentos se consultan bajo demanda con conexión).
- La complejidad añadida de service workers no aporta valor significativo en el MVP.

#### Ventajas del enfoque elegido
- **SEO optimizado**: Los reglamentos públicos son fácilmente indexables por Google.
- **Simplicidad de desarrollo**: Spring Boot + Thymeleaf permite desarrollo rápido con el stack del ciclo.
- **Compatibilidad universal**: HTML renderizado en servidor funciona en cualquier navegador sin dependencias de JavaScript.
- **Progressive enhancement**: JavaScript se usa para mejorar la experiencia (editor Markdown, diff interactivo) sin bloquear funcionalidad básica.

### 3.3. Arquitectura propuesta

#### Arquitectura general: Cliente-Servidor en tres capas

```
┌─────────────────────────────────────────────────────────────┐
│                         CLIENTE                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  Navegador   │  │  LLM Bots    │  │  Dispositivos│      │
│  │  (HTML/CSS)  │  │ (ChatGPT/    │  │   móviles    │      │
│  │              │  │  Claude)     │  │              │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│         │                 │                  │               │
└─────────┼─────────────────┼──────────────────┼───────────────┘
          │                 │                  │
          └─────────────────┴──────────────────┘
                           |
                    HTTP/HTTPS
                           |
┌─────────────────────────────────────────────────────────────┐
│                    SERVIDOR (Backend)                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              Spring Boot Application                  │   │
│  │                                                        │   │
│  │  ┌─────────────────────────────────────────────┐     │   │
│  │  │         Capa de Presentación                │     │   │
│  │  │  - Controllers (Spring MVC)                 │     │   │
│  │  │  - Thymeleaf Templates                      │     │   │
│  │  │  - Content Negotiation (HTML/Markdown)      │     │   │
│  │  └─────────────────────────────────────────────┘     │   │
│  │                      ↕                                │   │
│  │  ┌─────────────────────────────────────────────┐     │   │
│  │  │         Capa de Negocio                     │     │   │
│  │  │  - Services (lógica de aplicación)          │     │   │
│  │  │  - Conversión de documentos (Pandoc)        │     │   │
│  │  │  - Generación de diff (java-diff-utils)     │     │   │
│  │  │  - Cliente LLM (OpenAI/Anthropic API)       │     │   │
│  │  │  - Spring Security (autenticación/autorizac.)│    │   │
│  │  └─────────────────────────────────────────────┘     │   │
│  │                      ↕                                │   │
│  │  ┌─────────────────────────────────────────────┐     │   │
│  │  │         Capa de Persistencia                │     │   │
│  │  │  - Repositories (Spring Data JPA)           │     │   │
│  │  │  - Entidades (Usuario, Reglamento, etc.)    │     │   │
│  │  └─────────────────────────────────────────────┘     │   │
│  └──────────────────────────────────────────────────────┘   │
│                           ↕                                  │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              PostgreSQL Database                      │   │
│  │  - Usuarios y roles                                   │   │
│  │  - Reglamentos y versiones                            │   │
│  │  - Historial de cambios                               │   │
│  │  - Sugerencias de IA                                  │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                           │
                           │ API externa
                           ↓
                  ┌─────────────────┐
                  │  LLM API        │
                  │  (OpenAI/       │
                  │  Anthropic)     │
                  └─────────────────┘
```

#### Componentes principales

**1. Capa de Presentación (Spring MVC + Thymeleaf)**
- **Controllers**: Gestionan peticiones HTTP y coordinan servicios.
  - `ReglamentoPublicoController`: Sirve reglamentos públicos (HTML o Markdown según User-Agent).
  - `OrganizadorController`: Panel privado de gestión.
  - `AutenticacionController`: Login/registro.
- **Thymeleaf**: Renderiza vistas en servidor con datos dinámicos.
- **Content Negotiation**: Detecta User-Agent y sirve formato apropiado (HTML para navegadores, Markdown para bots).

**2. Capa de Negocio (Services)**
- `DocumentoService`: Conversión de PDF/DOCX a Markdown (Pandoc).
- `RevisionLLMService`: Integración con API de LLM para análisis de texto.
- `VersionadoService`: Gestión de versiones y generación de diff.
- `PublicacionService`: Generación de enlaces públicos únicos.

**3. Capa de Persistencia (Spring Data JPA)**
- Entidades principales:
  - `Usuario`: Datos de organizadores (email, password hash, rol).
  - `Reglamento`: Metadatos (título, organizador, slug público).
  - `VersionReglamento`: Contenido en Markdown, fecha publicación, número de versión.
  - `SugerenciaIA`: Sugerencias generadas por LLM (tipo, línea, texto original, sugerencia).
  - `HistorialCambios`: Registro de cambios entre versiones.

**4. Integración externa**
- **LLM API** (OpenAI GPT-4 o Anthropic Claude): Análisis de texto, generación de sugerencias y resúmenes.
- **Pandoc**: Conversión de documentos desde PDF/DOCX a Markdown.

#### Flujo de datos típico

**Ejemplo: Publicación de un reglamento**

1. Organizador sube PDF → `DocumentoService.convertir()` → Markdown almacenado temporalmente.
2. Organizador solicita revisión → `RevisionLLMService.analizarTexto()` → Sugerencias guardadas en BD.
3. Organizador acepta/rechaza sugerencias → Markdown final editado.
4. Organizador publica → `PublicacionService.publicar()` → Se crea `VersionReglamento` con slug único.
5. Visitante accede → `ReglamentoPublicoController.mostrar()` → Renderiza HTML con Thymeleaf.
6. Bot de ChatGPT accede → `ReglamentoPublicoController.mostrar()` detecta User-Agent → Sirve Markdown puro.

### 3.4. Coherencia con las necesidades

- **Simplicidad para organizadores**: El SSR con Thymeleaf permite crear formularios de subida y edición sin complejidad de estado en frontend.
- **Accesibilidad universal**: HTML renderizado en servidor funciona sin JavaScript, garantizando compatibilidad con lectores de pantalla y navegadores antiguos.
- **Optimización SEO**: Los reglamentos públicos son índexables por Google, aumentando su descubribilidad.
- **Escalabilidad futura**: La arquitectura en capas facilita migrar a microservicios si el proyecto crece (separar servicio de LLM, servicio de conversión de documentos, etc.).

---

## 4. Características específicas

### 4.1. Funcionalidades principales (MVP)

#### 4.1.1. Para organizadores (privado, requiere autenticación)

##### F1: Registro y autenticación
- **Prioridad**: Obligatoria (MVP)
- **Descripción**: Sistema de registro con email y contraseña. Login con Spring Security.
- **Criterios de aceptación**:
  - Validación de email único.
  - Contraseña hasheada con BCrypt.
  - Sesión persistente con tokens.

##### F2: Subida de documentos
- **Prioridad**: Obligatoria (MVP)
- **Descripción**: Formulario para subir PDF, DOCX o Markdown.
- **Criterios de aceptación**:
  - Límite de 10 MB por archivo.
  - Validación de formato (MIME type).
  - Conversión automática a Markdown usando Pandoc.
  - Vista previa del Markdown generado.

##### F3: Editor de Markdown
- **Prioridad**: Obligatoria (MVP)
- **Descripción**: Editor de texto con vista previa en tiempo real.
- **Criterios de aceptación**:
  - Sintaxis Markdown soportada (títulos, listas, negritas, enlaces).
  - Vista previa renderizada en panel lateral.
  - Guardado automático cada 30 segundos.
  - Librería: SimpleMDE o EasyMDE.

##### F4: Revisión con LLM
- **Prioridad**: Obligatoria (MVP)
- **Descripción**: Análisis automático del texto con sugerencias inline.
- **Tipos de sugerencias**:
  1. **Ortografía y gramática**: Detección de faltas y errores sintácticos.
  2. **Claridad**: Identificación de frases ambiguas con reescrituras sugeridas.
  3. **Incoherencias**: Detección de contradicciones entre artículos.
- **Criterios de aceptación**:
  - Las sugerencias aparecen como anotaciones en el editor.
  - El organizador puede aceptar (aplicar cambio) o rechazar cada sugerencia.
  - Se muestra un resumen: X sugerencias totales, Y aceptadas, Z rechazadas.
- **Prompt del LLM**:
```
Analiza el siguiente reglamento deportivo y genera sugerencias de mejora en formato JSON:
1. Ortografía y gramática: marca errores y propón correcciones.
2. Claridad: detecta frases ambiguas o confusas y sugiere alternativas más precisas.
3. Incoherencias: identifica artículos que se contradicen o tienen condiciones imposibles.

Formato de salida:
[
  {
    "tipo": "ortografia|claridad|incoherencia",
    "linea": <número de línea>,
    "textoOriginal": "...",
    "sugerencia": "...",
    "explicacion": "..."
  }
]

Reglamento:
{contenidoMarkdown}
```

##### F5: Publicación con enlace único
- **Prioridad**: Obligatoria (MVP)
- **Descripción**: Generación de URL pública única (slug).
- **Criterios de aceptación**:
  - URL tipo: `https://ruletrack.app/r/liga-futsal-cadiz-2026`
  - Slug generado automáticamente desde título (transliteración, sin caracteres especiales).
  - Posibilidad de personalizar el slug.
  - Confirmación visual con botón "Copiar enlace".

##### F6: Gestión de versiones
- **Prioridad**: Obligatoria (MVP)
- **Descripción**: Historial completo de versiones publicadas.
- **Criterios de aceptación**:
  - Lista de versiones con fechas de publicación.
  - Botón "Editar" que carga la versión actual en el editor.
  - Botón "Ver cambios" que muestra diff con versión anterior.
  - Al publicar de nuevo, se crea automáticamente una nueva versión.

##### F7: Exportación a PDF
- **Prioridad**: Opcional (post-MVP)
- **Descripción**: Generación de PDF limpio desde Markdown.
- **Criterios de aceptación**:
  - Diseño profesional con índice automático.
  - Librería: wkhtmltopdf o Flying Saucer.

#### 4.1.2. Para visitantes (público, sin autenticación)

##### F8: Visualización del reglamento
- **Prioridad**: Obligatoria (MVP)
- **Descripción**: Página pública con el reglamento renderizado.
- **Criterios de aceptación**:
  - Diseño limpio y profesional (similar a Medium o GitHub Pages).
  - Markdown renderizado a HTML con librería (marked.js o showdown.js).
  - Responsive design (móvil, tablet, escritorio).

##### F9: Temas visuales
- **Prioridad**: Obligatoria (MVP)
- **Descripción**: Selector de temas (claro, oscuro, alto contraste).
- **Criterios de aceptación**:
  - Preferencia guardada en localStorage.
  - Aplicación inmediata sin recargar página.
  - Cumplimiento WCAG 2.1 nivel AA en contraste.

##### F10: Índice navegable
- **Prioridad**: Obligatoria (MVP)
- **Descripción**: Tabla de contenidos generada automáticamente.
- **Criterios de aceptación**:
  - Se genera desde encabezados H1-H3 del Markdown.
  - Scroll suave al hacer clic en cada sección.
  - Destacado de sección activa según scroll del usuario.

##### F11: Búsqueda en el texto
- **Prioridad**: Obligatoria (MVP)
- **Descripción**: Buscador de palabras clave.
- **Criterios de aceptación**:
  - Campo de búsqueda con resultados en tiempo real.
  - Resaltado de coincidencias en el texto.
  - Navegación entre resultados (anterior/siguiente).
  - Librería: Mark.js o implementación con `window.find()`.

##### F12: Modo resumen
- **Prioridad**: Obligatoria (MVP)
- **Descripción**: Resumen automático de principales normas.
- **Criterios de aceptación**:
  - Generado por LLM al publicar el reglamento.
  - Organizado en secciones: Formato, Inscripciones, Sanciones, Reglas.
  - Vista en pestañas: "Normas completas" / "Resumen".
- **Prompt del LLM**:
```
Resume el siguiente reglamento deportivo extrayendo las normas más importantes organizadas en estas categorías:
1. Formato de competición (sistema de juego, duración, número de equipos)
2. Inscripciones (requisitos, fechas, documentación)
3. Sanciones (tarjetas, suspensiones, penalizaciones)
4. Reglas de juego (normativa específica del deporte)

Reglamento:
{contenidoMarkdown}
```

##### F13: Selector de versiones con diff
- **Prioridad**: Obligatoria (MVP)
- **Descripción**: Visualización de cambios entre versiones.
- **Criterios de aceptación**:
  - Desplegable con lista de versiones y fechas.
  - Al seleccionar una versión anterior, se muestra diff con la última.
  - Diff visual tipo GitHub:
    - Texto eliminado en rojo con fondo rosa.
    - Texto añadido en verde con fondo verde claro.
    - Texto sin cambios en gris.
  - Librería: diff2html.js o jsdiff.

##### F14: Optimización para LLMs
- **Prioridad**: Obligatoria (MVP)
- **Descripción**: Servir Markdown puro a bots.
- **Criterios de aceptación**:
  - Detección de User-Agent: `ChatGPT-User`, `Claude-Web`, `GPTBot`.
  - Content-Type: `text/markdown; charset=UTF-8`.
  - Header adicional: `X-Content-Type: markdown`.
  - Metadatos en header:
```
---
Título: {título del reglamento}
Versión: {número de versión}
Fecha publicación: {fecha}
Organización: {nombre organizador}
URL oficial: {url}
---

{contenido en Markdown}
```

### 4.2. Priorización de funcionalidades

#### Obligatorias para MVP (entrega proyecto)
- F1: Registro y autenticación
- F2: Subida de documentos
- F3: Editor de Markdown
- F4: Revisión con LLM
- F5: Publicación con enlace único
- F6: Gestión de versiones
- F8: Visualización del reglamento
- F9: Temas visuales
- F10: Índice navegable
- F11: Búsqueda en el texto
- F12: Modo resumen
- F13: Selector de versiones con diff
- F14: Optimización para LLMs

#### Opcionales (post-MVP)
- F7: Exportación a PDF
- Sistema de notificaciones (email cuando se publica nueva versión)
- Estadísticas de consultas (páginas más visitadas, búsquedas frecuentes)
- Comentarios públicos moderados por organizador
- API REST para integración con otras plataformas

### 4.3. Requisitos técnicos

#### Backend
- **Lenguaje**: Java 17 LTS
- **Framework**: Spring Boot 3.2
  - Spring MVC (controladores y vistas)
  - Spring Data JPA (persistencia)
  - Spring Security (autenticación con BCrypt)
- **Base de datos**: PostgreSQL 15
- **Conversión de documentos**: Pandoc 3.1
- **Generación de diff**: java-diff-utils 4.12
- **Cliente HTTP**: RestTemplate para llamadas a API de LLM
- **Validación**: Hibernate Validator

#### Frontend
- **Motor de plantillas**: Thymeleaf 3.1
- **CSS**: Bootstrap 5.3 (responsive grid, componentes)
- **JavaScript**:
  - Editor Markdown: EasyMDE 2.18
  - Renderizado Markdown: marked.js 11.1
  - Diff visual: diff2html 3.4
  - Búsqueda en texto: Mark.js 8.11
  - Gestión de temas: Custom con localStorage

#### Integraciones externas
- **LLM API**: OpenAI GPT-4 Turbo (plan de desarrollo gratuito) o Anthropic Claude 3.5 Sonnet
- **Almacenamiento de archivos**: Sistema de archivos local (MVP) / Amazon S3 (producción)

#### Herramientas de desarrollo
- **IDE**: Visual Studio Code
- **Control de versiones**: Git + GitHub
- **Gestión de dependencias**: Maven 3.9
- **Contenedores**: Docker 24 + Docker Compose
- **CI/CD**: GitHub Actions

#### Infraestructura de despliegue
- **Hosting**: DigitalOcean Droplet (4 GB RAM, 2 vCPUs)
- **Sistema operativo**: Ubuntu 22.04 LTS
- **Servidor web**: Nginx como proxy inverso
- **Certificado SSL**: Let's Encrypt (gratuito)
- **Base de datos**: PostgreSQL en el mismo droplet (MVP) / managed database (producción)

---

## 5. Obligaciones legales y prevención

### 5.1. Normativa aplicable

#### 5.1.1. Reglamento General de Protección de Datos (RGPD)

**Aplicabilidad**: RuleTrack procesa datos personales de organizadores (email, contraseñas).

**Medidas de cumplimiento**:

1. **Base jurídica del tratamiento**: Consentimiento explícito durante el registro.
   - Checkbox obligatorio: "Acepto el tratamiento de mis datos personales conforme a la política de privacidad".

2. **Información transparente** (Art. 13 RGPD):
   - Política de privacidad accesible desde todas las páginas con:
     - Identidad del responsable del tratamiento.
     - Finalidad del tratamiento (gestión de cuenta y publicación de reglamentos).
     - Plazo de conservación (mientras la cuenta esté activa + 1 año).
     - Derechos ARCO (Acceso, Rectificación, Cancelación, Oposición) + Portabilidad.
     - Derecho a presentar reclamación ante la AEPD.

3. **Seguridad en el tratamiento** (Art. 32 RGPD):
   - Contraseñas hasheadas con BCrypt (factor 12).
   - Conexiones cifradas con HTTPS obligatorio.
   - Backup diario cifrado de la base de datos.
   - Auditoría de accesos a datos personales.

4. **Derechos de los usuarios**:
   - Sección en el panel de usuario para:
     - Descargar datos personales (formato JSON).
     - Rectificar email.
     - Eliminar cuenta (anonimización: email → `deleted_user_<id>@example.com`).

5. **Minimización de datos**:
   - Solo se solicita email y contraseña para registro.
   - No se requiere nombre, teléfono ni dirección.
   - Los visitantes de reglamentos públicos no requieren registro ni dejan rastro personal.

#### 5.1.2. Ley de Servicios de la Sociedad de la Información y Comercio Electrónico (LSSI-CE)

**Obligaciones**:

1. **Identificación del prestador del servicio** (Art. 10):
   - Página "Aviso legal" con:
     - Nombre completo o denominación social.
     - Domicilio / dirección de contacto.
     - Email de contacto.
     - NIF/CIF.

2. **Política de cookies**:
   - Banner informativo al acceder por primera vez.
   - Explicación de cookies utilizadas:
     - Cookies técnicas (sesión de usuario): exentas de consentimiento.
     - Cookies de preferencias (tema visual): consentimiento implícito.
   - No se utilizan cookies de terceros ni analítica con identificación personal.

3. **Derecho de desistimiento**: No aplica (servicio gratuito sin transacciones comerciales en MVP).

#### 5.1.3. Propiedad intelectual

- Los reglamentos subidos por organizadores son de su propiedad.
- RuleTrack actúa como **mero intermediario técnico** (Art. 16 LSSI-CE).
- Términos de servicio que especifican:
  - El organizador es responsable del contenido publicado.
  - RuleTrack no revisa ni valida la legalidad de los reglamentos.
  - Procedimiento de retirada de contenido ante reclamación (notice and takedown).

### 5.2. Medidas de seguridad

#### 5.2.1. Seguridad en autenticación
- **Hashing de contraseñas**: BCrypt con salt automático.
- **Sesiones seguras**: Tokens con expiración (30 días con "recordarme", 24 horas sin).
- **HTTPS obligatorio**: Redirección automática de HTTP a HTTPS.
- **Protección CSRF**: Tokens CSRF en todos los formularios (Spring Security).

#### 5.2.2. Seguridad en subida de archivos
- **Validación de MIME type**: Solo PDF, DOCX y Markdown permitidos.
- **Límite de tamaño**: 10 MB por archivo.
- **Escaneo de virus**: ClamAV en servidor (post-MVP).
- **Almacenamiento seguro**: Archivos fuera del directorio web público.

#### 5.2.3. Seguridad en base de datos
- **Conexión cifrada**: SSL/TLS entre aplicación y PostgreSQL.
- **Credenciales en variables de entorno**: Nunca hardcodeadas en código.
- **Backups automáticos**: Diarios, cifrados, rotación de 7 días.
- **Separación de privilegios**: Usuario de aplicación sin permisos DROP/ALTER.

#### 5.2.4. Seguridad en API externa (LLM)
- **API keys en secretos**: Variables de entorno, nunca en repositorio.
- **Rate limiting**: Máximo 100 solicitudes por hora por usuario.
- **Sanitización de entrada**: Escape de caracteres especiales antes de enviar a LLM.
- **Logs de auditoría**: Registro de todas las llamadas a API con timestamp.

### 5.3. Accesibilidad web (WCAG 2.1)

**Objetivo**: Cumplir nivel AA de las pautas WCAG 2.1.

#### Medidas implementadas

##### 1. Perceptible
- **Contraste de color** (1.4.3): Ratio mínimo 4.5:1 en texto normal, 3:1 en texto grande.
  - Herramienta de verificación: WebAIM Contrast Checker.
- **Texto alternativo** (1.1.1): Todas las imágenes tienen atributo `alt` descriptivo.
- **Etiquetas de formularios** (1.3.1): Todos los inputs tienen `<label>` asociado.

##### 2. Operable
- **Navegación por teclado** (2.1.1): Todos los elementos interactivos accesibles con Tab.
- **Orden de foco lógico** (2.4.3): Navegación secuencial coherente.
- **Enlaces descriptivos** (2.4.4): Textos como "Leer más" evitados; se usa "Leer reglamento completo de Liga Fútsal Cádiz 2026".

##### 3. Comprensible
- **Idioma de página** (3.1.1): `<html lang="es">`.
- **Etiquetas errores en formularios** (3.3.1): Mensajes claros tipo "El email es obligatorio" en rojo con icono.
- **Ayuda contextual** (3.3.5): Tooltips en campos complejos (ej: "El slug será parte de la URL pública").

##### 4. Robusto
- **HTML válido** (4.1.1): Validación con W3C Validator.
- **Roles ARIA** (4.1.2): Uso de `role="navigation"`, `role="main"`, `aria-label` en componentes interactivos.

#### Herramientas de verificación
- **Lighthouse** (Chrome DevTools): Auditoría automática de accesibilidad.
- **axe DevTools**: Extensión de navegador para detección de problemas.
- **NVDA / JAWS**: Pruebas con lectores de pantalla.

---

## 6. Ayudas y subvenciones

### 6.1. Ayudas públicas para proyectos tecnológicos

#### 6.1.1. Kit Digital (Red.es)

**Descripción**: Programa de ayudas para digitalización de pymes y autónomos.

**Aplicabilidad a RuleTrack**:
- **Segmento III** (menos de 3 empleados): Hasta 2,000€ para desarrollo de web/aplicación.
- **Requisitos**:
  - Estar dado de alta como autónomo o pyme.
  - El proyecto debe estar en producción con facturación.

**Estado**: No aplicable en fase de TFG (estudiante sin actividad comercial). **Viable para explotación comercial posterior**.

**Plazo**: Convocatoria abierta hasta diciembre 2026.

**Enlace**: https://www.red.es/es/kit-digital

#### 6.1.2. Programa ENISA Jóvenes Emprendedores

**Descripción**: Préstamos participativos para startups tecnológicas.

**Aplicabilidad a RuleTrack**:
- **Línea Jóvenes Emprendedores**: Hasta 25,000€ sin intereses el primer año.
- **Requisitos**:
  - Menor de 40 años (cumple).
  - Proyecto innovador en fase de lanzamiento.
  - Constitución de sociedad (SL o autónomo).

**Estado**: Aplicable si se decide comercializar el proyecto tras el TFG.

**Plazo**: Convocatoria anual, próxima edición mayo 2026.

**Enlace**: https://www.enisa.es/lineas-de-financiacion

#### 6.1.3. Ayudas Junta de Andalucía - Andalucía es Digital

**Descripción**: Subvenciones para proyectos TIC en Andalucía.

**Aplicabilidad**: Proyectos universitarios y de FP con impacto social.

**Requisitos**:
- Sede en Andalucía (cumple: I.E.S. Rafael Alberti, Cádiz).
- Presentar memoria técnica del proyecto.

**Estado**: Pendiente de convocatoria 2026 (últimas ediciones en mayo-junio).

**Enlace**: https://www.juntadeandalucia.es/economiainnovacionyciencia

### 6.2. Recursos gratuitos y de bajo coste

#### 6.2.1. Infraestructura y hosting

| Recurso | Descripción | Coste | Uso en RuleTrack |
|---------|-------------|-------|------------------|
| **GitHub** | Repositorio de código, GitHub Actions CI/CD | Gratis (plan estudiante) | Control de versiones, CI/CD |
| **DigitalOcean** | VPS para despliegue | $6/mes (crédito $200 para estudiantes) | Hosting de producción |
| **Railway** | Plataforma de despliegue PaaS | $5/mes (500h gratis/mes para proyectos pequeños) | Alternativa a DigitalOcean |
| **Render** | Despliegue de apps y BD | Gratis (plan hobby: 750h/mes) | Alternativa para MVP |
| **Let's Encrypt** | Certificados SSL | Gratis | HTTPS en producción |
| **PostgreSQL** | Base de datos | Incluido en VPS o gratis en Render/Railway | Base de datos del proyecto |

**Elección para MVP**: **Render (plan gratuito)** para despliegue inicial + migración a DigitalOcean con crédito estudiante para producción.

#### 6.2.2. APIs y servicios

| Servicio | Plan gratuito | Uso en RuleTrack |
|----------|---------------|------------------|
| **OpenAI API** | $5 crédito inicial (suficiente para ~1,000 análisis) | Revisión con GPT-4 |
| **Anthropic Claude** | $5 crédito inicial | Alternativa a OpenAI |
| **Google Cloud** | $300 crédito (12 meses) | Almacenamiento de archivos (Cloud Storage) |
| **Cloudflare** | CDN y DNS gratuitos | Aceleración de contenido estático |
| **Mailtrap** | 500 emails/mes gratis | Notificaciones por email (post-MVP) |

**Estrategia**: Usar créditos gratuitos de OpenAI/Claude para desarrollo y primeras 500 validaciones con usuarios. Migrar a modelo freemium si se excede.

#### 6.2.3. Herramientas de desarrollo

| Herramienta | Coste | Uso |
|-------------|-------|-----|
| **Visual Studio Code** | Gratis | IDE principal |
| **GitHub Copilot** | Gratis (estudiantes con GitHub Education) | Asistente de código |
| **Postman** | Gratis | Testing de endpoints |
| **Figma** | Gratis (plan individual) | Diseño de mockups |
| **Notion** | Gratis (plan personal) | Documentación del proyecto |

### 6.3. Reducción de costes operativos

**Estimación de costes mensuales en producción (100 usuarios activos)**:

| Concepto | Coste mensual | Justificación |
|----------|---------------|---------------|
| Hosting (DigitalOcean Droplet 4GB) | $0 primeros 12 meses (crédito estudiante), luego $24/mes | VPS con PostgreSQL incluido |
| LLM API (OpenAI/Claude) | ~$15/mes | 300 análisis/mes a $0.05/análisis |
| Dominio (.app) | $2/mes | Primer año gratis en algunos registradores |
| **Total** | **$0 (año 1) / $41/mes (año 2)** | Viable sin financiación externa |

**Estrategia de sostenibilidad**:
- Modelo freemium: Plan gratuito limitado (1 reglamento, 10 revisiones/mes).
- Plan premium (9.99€/mes): Reglamentos ilimitados, revisiones ilimitadas, exportación PDF.
- **Break-even point**: 5 usuarios premium para cubrir costes operativos.

---

## 7. Guión de trabajo

### 7.1. Cronograma general

**Duración total**: 10 semanas (11 marzo - 15 mayo 2026)

**Buffer antes de entrega**: 1 semana (15-22 mayo) para ajustes finales, correcciones de última hora y preparación de la presentación.

**Metodología de desarrollo**: El proyecto seguirá un enfoque híbrido de desarrollo:
1. **Fase inicial (Semanas 1-3)**: Diseño completo del frontend en Figma antes de escribir código, incluyendo wireframes, mockups de alta fidelidad y biblioteca de componentes. Esto asegura una visión clara de la interfaz y facilita la implementación posterior.
2. **Desarrollo simultáneo (Semanas 4-8)**: Backend y frontend se desarrollarán en paralelo, implementando cada funcionalidad de forma completa (endpoint + vista) para validar inmediatamente la integración y asegurar que todo funciona correctamente antes de avanzar a la siguiente funcionalidad.
3. **Validación continua**: Al trabajar simultáneamente en ambas capas, se pueden probar flujos completos de usuario desde el momento en que se desarrollan, detectando problemas tempranamente y ajustando el diseño según sea necesario.

#### Fase 1: Planificación y setup inicial (Semana 1)
- **Semana 1** (11-17 marzo):
  - Redacción de propuesta formal (este documento).
  - Creación de repositorio GitHub y estructura inicial.
  - Investigación de librerías (Pandoc, EasyMDE, diff2html).
  - Diseño de base de datos (diagrama ER).
  - Configuración del entorno de desarrollo (Docker, PostgreSQL).

**Entregables**: Propuesta aprobada, repositorio creado, entorno configurado, modelo de datos definido.

#### Fase 2: Diseño completo en Figma (Semanas 2-3)
- **Semana 2** (18-24 marzo):
  - Creación de wireframes de todas las páginas:
    - Registro, login, dashboard organizador.
    - Editor de reglamento con sugerencias inline.
    - Formulario de publicación.
  - Diseño del sistema de navegación y flujos de usuario.
  
- **Semana 3** (25-31 marzo):
  - Mockups de alta fidelidad con diseño visual completo:
    - Paleta de colores, tipografía, componentes.
    - Temas claro, oscuro y alto contraste.
  - Creación de biblioteca de componentes reutilizables en Figma:
    - Botones, formularios, tarjetas, modales.
    - Editor Markdown, visor de diff, selector de versiones.
  - Vistas públicas del reglamento (modo normas y resumen).

**Entregables**: Wireframes completos, mockups finalizados, biblioteca de componentes Figma lista.

#### Fase 3: Desarrollo simultáneo Backend + Frontend - Autenticación y documentos (Semanas 4-5)
- **Semana 4** (1-7 abril):
  - **Backend**:
    - Configuración de Spring Boot + Spring Security.
    - Implementación de entidades JPA (Usuario, Reglamento, VersionReglamento).
    - Sistema de autenticación (registro, login, logout).
    - Endpoints REST para autenticación.
  - **Frontend**:
    - Vistas Thymeleaf: registro, login (implementando diseño Figma).
    - Integración con endpoints de autenticación.
    - Validación de formularios en cliente y servidor.

- **Semana 5** (8-14 abril):
  - **Backend**:
    - Servicio de conversión de documentos (Pandoc).
    - Subida de archivos y almacenamiento.
    - Conversión de PDF/DOCX a Markdown.
    - Endpoints para gestión de documentos.
  - **Frontend**:
    - Dashboard del organizador.
    - Formulario de subida de documentos con preview.
    - Integración de editor Markdown (EasyMDE).
    - Pruebas de flujo completo: subir PDF → visualizar Markdown.

**Entregables**: Sistema de autenticación funcional, subida y conversión de documentos operativa, UI implementada según diseño Figma.

#### Fase 4: Desarrollo simultáneo Backend + Frontend - Revisión LLM y versionado (Semana 6 - INTENSIVA)
- **Semana 6** (15-21 abril) - *Semana de mayor intensidad de trabajo*:
  - **Backend**:
    - Integración con API de LLM (OpenAI/Claude).
    - Servicio de revisión de texto.
    - Generación de sugerencias (ortografía, claridad, incoherencias).
    - Endpoints para solicitar/obtener sugerencias.
    - Servicio de versionado (creación de VersionReglamento).
    - Generación de diff entre versiones (java-diff-utils).
    - Generación de slug público único.
    - Endpoints para publicación y gestión de versiones.
  - **Frontend**:
    - Visualización de sugerencias inline en el editor.
    - Interfaz para aceptar/rechazar sugerencias.
    - UI de estado de revisión (en proceso, completado).
    - Formulario de publicación con personalización de slug.
    - Lista de versiones en dashboard.
    - Confirmación de publicación con preview del enlace público.
  - **Pruebas completas**:
    - Editar → revisar → aceptar sugerencias.
    - Editar → publicar → verificar enlace.

**Entregables**: Revisión con LLM funcional, sistema de versionado completo, panel de organizador terminado.

#### Fase 5: Desarrollo simultáneo Backend + Frontend - Vista pública (Semanas 7-8)
- **Semana 7** (22-28 abril):
  - **Backend**:
    - Endpoint para visualización pública de reglamentos.
    - Content negotiation (HTML vs Markdown según User-Agent).
    - Generación de resumen con LLM.
    - Endpoint para obtener versiones anteriores.
  - **Frontend**:
    - Vista pública del reglamento (renderizado Markdown según diseño Figma).
    - Implementación de temas visuales (claro, oscuro, alto contraste).
    - Índice navegable con scroll suave.
    - Modo resumen vs normas completas.

- **Semana 8** (29 abril - 5 mayo):
  - **Backend**:
    - Optimización de consultas y respuestas.
    - Caché de contenido estático.
  - **Frontend**:
    - Sistema de búsqueda en el texto (Mark.js).
    - Selector de versiones con diff visual (diff2html).
    - Responsive design para móviles y tablets.
    - Prueba completa del flujo público: acceder → buscar → comparar versiones.

**Entregables**: Vista pública completa con todas las funcionalidades, optimización para LLMs implementada.

#### Fase 6: Testing, validación y despliegue (Semanas 9-10)
- **Semana 9** (6-12 mayo):
  - Pruebas de integración end-to-end (JUnit + Mockito).
  - Testing de flujos completos de usuario.
  - Validación de accesibilidad WCAG 2.1 AA (Lighthouse, axe DevTools).
  - Corrección de bugs detectados.
  - Optimización de rendimiento (caching, lazy loading).
  - Dockerización de la aplicación (Dockerfile + docker-compose.yml).

- **Semana 10** (13-15 mayo):
  - Despliegue en DigitalOcean/Render.
  - Configuración de Nginx + SSL (Let's Encrypt).
  - Pruebas en producción.
  - Redacción de README y documentación técnica completa.
  - **Proyecto completado: 15 de mayo de 2026**

**Entregables**: Aplicación desplegada en producción, suite de tests completa, documentación finalizada.

#### Buffer Final (Semana 11)
- **Semana 11** (15-22 mayo):
  - Correcciones finales si se detectan problemas.
  - Preparación de la presentación del proyecto (slides, demo, ensayos).
  - Revisión y mejora de documentación.
  - Tiempo para imprevistos de última hora.
  - **Entrega oficial del proyecto: 22 de mayo de 2026**

**Entregables**: Presentación lista, ajustes finales realizados, proyecto entregado.

### 7.2. Hitos principales

| Hito | Fecha límite | Descripción |
|------|--------------|-------------|
| H1: Propuesta aprobada | 17 marzo | Documento de propuesta formal validado |
| H2: Diseño Figma completo | 31 marzo | Wireframes, mockups y componentes finalizados |
| H3: Autenticación y documentos | 14 abril | Sistema de login y conversión de documentos operativo (backend + frontend) |
| H4: Revisión LLM y versionado | 21 abril | Sugerencias de IA y publicación funcionando (backend + frontend) - semana intensiva |
| H5: Vista pública completa | 5 mayo | Página pública con todas las funcionalidades implementadas |
| H6: Proyecto completado | 15 mayo | Aplicación desplegada, tests validados, documentación completa |
| H7: Entrega oficial | 22 mayo | Presentación lista y proyecto entregado (tras semana de buffer) |

### 7.3. Herramientas de gestión

#### 7.3.1. GitHub Projects

**Configuración**: Tablero Kanban con columnas:
- **Backlog**: Funcionalidades pendientes de iniciar.
- **To Do**: Tareas priorizadas para el sprint actual (2 semanas).
- **In Progress**: Tareas actualmente en desarrollo.
- **In Review**: Pull requests pendientes de revisión.
- **Done**: Tareas completadas y mergeadas a `main`.

**Issues**: Cada funcionalidad es un issue con:
- Etiquetas: `backend`, `frontend`, `database`, `documentation`, `bug`.
- Estimación: Puntos de historia (1, 2, 3, 5, 8).
- Asignación a milestone (hito correspondiente).

#### 7.3.2. Control de versiones (Git)

**Estrategia de branching**:
- `main`: Rama de producción, siempre estable.
- `develop`: Rama de desarrollo, integración continua.
- `feature/<nombre>`: Ramas para cada funcionalidad.
- `hotfix/<nombre>`: Ramas para correcciones urgentes.

**Commits**: Mensajes descriptivos según Conventional Commits:
```
feat: añadir conversión de PDF a Markdown con Pandoc
fix: corregir validación de email en formulario de registro
docs: actualizar README con instrucciones de despliegue
test: añadir tests unitarios para VersionadoService
```

#### 7.3.3. Toggl Track

**Uso**: Registro de horas dedicadas por fase.
- **Objetivo**: 160 horas totales (10 semanas × 16 horas/semana promedio, con semana 6 requiriendo ~20-22 horas).
- **Categorías de tiempo**:
  - Desarrollo de código (backend, frontend).
  - Investigación técnica (librerías, APIs).
  - Testing y depuración.
  - Documentación.
  - Reuniones de validación.

**Informes semanales**: Gráfico de distribución de tiempo para ajustar planificación.

### 7.4. Gestión de riesgos

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|--------------|---------|------------|
| **API de LLM supera límite gratuito** | Media | Alto | Implementar caché de sugerencias por hash del texto; cambiar a Claude si OpenAI se agota |
| **Conversión de PDF falla con documentos complejos** | Alta | Medio | Permitir subida de Markdown directo; mostrar mensaje de error claro; ajustar parámetros de Pandoc |
| **Problemas de rendimiento en diff de documentos largos** | Media | Medio | Limitar tamaño máximo de reglamento a 50 páginas; implementar diff incremental |
| **Dificultad en despliegue de Docker** | Baja | Alto | Practicar despliegue en semana 9; tener plan B con Render (PaaS sin Docker) |
| **Semana 6 demasiado cargada** | Media | Medio | Priorizar funcionalidades core; delegar tareas menos críticas a semana 7; preparar código base bien estructurado en semanas 4-5 |
| **Falta de tiempo para testing exhaustivo** | Media | Alto | Priorizar tests de funcionalidades críticas (autenticación, versionado); automatizar con GitHub Actions |

---

## 8. Referencias

### 8.1. Documentación técnica

- **Spring Boot Documentation**: https://docs.spring.io/spring-boot/docs/current/reference/html/
- **Spring Security Reference**: https://docs.spring.io/spring-security/reference/
- **Thymeleaf Documentation**: https://www.thymeleaf.org/documentation.html
- **Pandoc**: https://pandoc.org/MANUAL.html
- **PostgreSQL Documentation**: https://www.postgresql.org/docs/
- **OpenAI API Reference**: https://platform.openai.com/docs/api-reference
- **Anthropic Claude API**: https://docs.anthropic.com/claude/reference

### 8.2. Librerías y herramientas

- **EasyMDE** (Editor Markdown): https://github.com/Ionaru/easy-markdown-editor
- **marked.js** (Renderizado Markdown): https://marked.js.org/
- **diff2html** (Visualización de diff): https://diff2html.xyz/
- **Mark.js** (Búsqueda en texto): https://markjs.io/
- **java-diff-utils**: https://github.com/java-diff-utils/java-diff-utils
- **Bootstrap 5**: https://getbootstrap.com/docs/5.3/

### 8.3. Normativa legal

- **RGPD (Reglamento UE 2016/679)**: https://www.boe.es/doue/2016/119/L00001-00088.pdf
- **LOPDGDD**: https://www.boe.es/buscar/act.php?id=BOE-A-2018-16673
- **LSSI-CE**: https://www.boe.es/buscar/act.php?id=BOE-A-2002-13758
- **WCAG 2.1**: https://www.w3.org/TR/WCAG21/

### 8.4. Ayudas y subvenciones

- **Kit Digital**: https://www.red.es/es/kit-digital
- **ENISA**: https://www.enisa.es/lineas-de-financiacion
- **GitHub Education**: https://education.github.com/pack
- **DigitalOcean for Students**: https://www.digitalocean.com/github-students

### 8.5. Artículos de referencia

- Ben Word - *Serving Markdown to AI Agents via Accept Headers*: https://benword.com/serving-markdown-to-ai-agents-via-accept-headers
- GitHub - *How to write a diff*: https://github.blog/2023-04-04-how-to-write-a-diff/