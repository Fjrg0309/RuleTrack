# 10. Conclusiones

> Este apartado es el más importante de la documentación y el que más debería destacar en la defensa del proyecto.

---

## 10.1 Evaluación crítica respecto a los objetivos iniciales

El proyecto planteaba el desarrollo de una plataforma web full stack para gestionar reglamentos con control de versiones, acceso seguro y apoyo de inteligencia artificial. A continuación se evalúa cada objetivo específico declarado en la fase inicial:

| Objetivo | Estado | Observaciones |
|---|---|---|
| Autenticación JWT con roles (ORGANIZADOR / USUARIO) | Completado | Implementado con Spring Security + JJWT. Filtro JWT en toda la cadena de seguridad. |
| Visibilidad de reglamentos (PUBLICO / SOLO\_MIEMBROS / PRIVADO) | Completado | Lógica de acceso por organización y creador implementada en la capa de servicio. |
| Versionado con estados (BORRADOR / PUBLICADO / ARCHIVADO) | Completado | Activación de versión archiva automáticamente las anteriores. |
| Historial de cambios para auditoría | Completado | `HistorialCambiosService` registra cambios sobre versiones. |
| Conversión de documentos PDF/DOCX a Markdown | Completado | PDFBox (PDF) y Apache POI (DOCX) en el servidor. |
| Análisis IA (revisión, resumen, incoherencias) | Completado | `LlmService` con integración OpenAI-compatible (Groq por defecto). |
| Corrección asistida por IA | Completado | Endpoint `/corrections` con análisis gramatical y estilístico. |
| API documentada con Swagger/OpenAPI | Completado | SpringDoc genera documentación accesible en `/swagger-ui.html`. |
| Despliegue con Docker Compose (dev y prod) | Completado | Dos ficheros compose con configuración diferenciada y Nginx como reverse proxy. |
| Pipeline CI/CD en GitHub Actions con GHCR | Completado | Build, tests y publicación de imágenes automatizados en cada push a `main`. |
| Pruebas unitarias de servicios críticos | Completado | Tests de `AuthService` y `ReglamentoService` con JUnit 5 y Mockito. |

---

## 10.2 Grado de cumplimiento del alcance propuesto

El alcance inicial se ha cumplido íntegramente. Todos los módulos descritos en la fase de diseño han sido implementados y son funcionales:

- **Backend**: API REST completa con 7 controladores, lógica de negocio en 6 servicios y modelo de datos con 5 entidades principales.
- **Frontend**: SPA Angular con 17 páginas/rutas, 4 componentes reutilizables, sistema de autenticación basado en JWT con interceptor HTTP, gestión de estado reactiva con RxJS y soporte de tema claro/oscuro.
- **Infraestructura**: dockerización completa con Nginx como único punto de entrada en producción, pipeline CI/CD funcional con caché de capas Docker para builds incrementales.

Los elementos **excluidos del alcance base** (definidos desde el inicio) no han sido implementados ni eran objetivo:
- Multi-tenant avanzado con aislamiento físico por organización.
- Firma digital y validez legal de documentos.
- Motor completo de notificaciones push o por email.

---

## 10.3 Decisiones técnicas que marcarían la diferencia

Mirando en retrospectiva, varias decisiones técnicas resultaron especialmente acertadas:

**1. JWT stateless**: eliminar la gestión de sesiones en servidor simplificó el despliegue en contenedores, evitó la necesidad de caché compartida entre instancias y alineó el diseño con las prácticas estándar de APIs REST modernas.

**2. Integración LLM OpenAI-compatible**: diseñar la integración IA como agnóstica al proveedor (cualquier endpoint `/chat/completions` funciona) aporta una flexibilidad real. Cambiar de Groq a OpenAI o a un modelo local con Ollama solo requiere modificar variables de entorno.

**3. Contenido en Markdown**: almacenar el contenido normativo en Markdown evita el lock-in a un editor WYSIWYG específico, mantiene el texto portable y es la salida natural de los modelos LLM, lo que simplifica la integración.

**4. ITCSS + Custom Properties con tema oscuro**: adoptar una arquitectura CSS bien estructurada desde el inicio evitó la deuda técnica de estilos que suele acumularse en proyectos frontend. El tema oscuro se logró con solo sobreescribir variables CSS, sin duplicar reglas.

**5. Pipeline CI/CD desde el inicio**: configurar GitHub Actions desde las primeras semanas del proyecto (no al final) permitió detectar errores de build antes de que se acumularan y garantizó que cada commit generaba imágenes publicables.

---

## 10.4 Dificultades superadas más relevantes

La dificultad más significativa fue la **gestión de los timeouts en la integración LLM**. Los modelos de lenguaje grandes pueden tardar más de 30 segundos en responder con documentos extensos, lo que superaba los límites de plataformas cloud de nivel gratuito. La solución — truncar el contenido a 2500 caracteres antes del envío y configurar timeouts explícitos en el cliente HTTP — fue un buen ejemplo de cómo la pragmática resuelve un problema que habría bloqueado la funcionalidad.

Otro reto técnico fue la **sincronización de arranque** entre el backend (Spring Boot) y PostgreSQL en Docker Compose. La configuración `hikari.initializationFailTimeout=-1` combinada con el `healthcheck` de Postgres y la directiva `depends_on: condition: service_healthy` resolvió definitivamente los errores intermitentes de arranque.

---

## 10.5 Mejoras futuras

Las siguientes mejoras están identificadas como trabajo futuro prioritario:

### Funcionales
- **Notificaciones**: avisos por email o en la propia plataforma cuando se publica una nueva versión o se realizan cambios en un reglamento suscrito.
- **Editor enriquecido**: integrar un editor Markdown visual (ej. TipTap, Quill) en lugar de la edición en texto plano.
- **Firma digital**: integración con un proveedor de firma electrónica para dar validez legal a los reglamentos publicados.
- **Exportación**: descarga de la versión activa en PDF directamente desde la interfaz, con formato y cabecera de la organización.
- **Búsqueda de contenido**: búsqueda de texto completo dentro del contenido de los reglamentos (PostgreSQL full-text search).

### Técnicas
- **Cobertura de tests**: ampliar los tests unitarios a todos los servicios (`VersionReglamentoService`, `SugerenciaIAService`, `LlmService`) y añadir tests de integración con `@SpringBootTest` y Testcontainers.
- **Paginación en la API**: añadir paginación a los endpoints de listado para manejar organizaciones con muchos reglamentos.
- **Caché de sugerencias IA**: evitar llamadas duplicadas al LLM si el contenido no ha cambiado entre solicitudes.
- **HTTPS nativo**: incluir configuración de Certbot/Let's Encrypt en la guía de despliegue para facilitar la activación de SSL.
- **Observabilidad**: añadir métricas con Micrometer/Prometheus y trazas distribuidas con OpenTelemetry para monitorización en producción.

---

## 10.6 Lecciones aprendidas

**Sobre la arquitectura**: separar frontend y backend en contenedores independientes desde el inicio tiene un coste de configuración inicial (CORS, proxy, variables de entorno) que se amortiza rápidamente. La claridad en los límites entre capas facilita las pruebas y el despliegue.

**Sobre la integración de IA**: los LLM son potentes pero no deterministas. Las respuestas varían entre llamadas y pueden no respetar formatos de salida esperados. Diseñar la integración con validación defensiva de la respuesta (parseo con fallback, truncado de entrada) es indispensable para una integración robusta.

**Sobre Docker y CI/CD**: el pipeline de GitHub Actions no es solo una herramienta de entrega; es también una red de seguridad que ejecuta los tests automáticamente ante cada cambio. Haberlo configurado desde el primer sprint del proyecto fue una de las mejores decisiones del desarrollo.

**Sobre la documentación técnica**: documentar a medida que se desarrolla (entidades con Javadoc, endpoints con OpenAPI, fichero HTTP con peticiones de prueba) es significativamente más eficiente que hacerlo todo al final. La documentación tardía tiende a ser incompleta y menos precisa.

**Sobre el alcance**: delimitar con claridad qué queda fuera del alcance es tan importante como definir qué está dentro. Haber excluido explícitamente la firma digital, notificaciones y multi-tenant avanzado desde el inicio evitó la expansión descontrolada del alcance (*scope creep*) y permitió entregar un producto coherente y completo dentro de los plazos.

---

## 10.7 Reflexión final

RuleTrack demuestra que es posible construir una aplicación web completa y desplegable de forma reproducible, combinando tecnologías modernas del ecosistema Java y Angular, con integración de inteligencia artificial, en el contexto de un proyecto de desarrollo individual.

El resultado es una plataforma funcional que resuelve un problema real — la gestión caótica de documentos normativos en organizaciones — con una arquitectura que favorece la mantenibilidad, la seguridad y la escalabilidad futura.

Las decisiones tomadas a lo largo del proyecto están fundamentadas en criterios técnicos documentados, lo que permite a cualquier desarrollador incorporarse al proyecto, entender el razonamiento detrás de cada componente y extenderlo con nuevas funcionalidades.
