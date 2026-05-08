# 01. Introduccion, objetivos y antecedentes

## 1.1 Origen de la idea
RuleTrack nace de una necesidad habitual en asociaciones, clubes y organizaciones: gestionar reglamentos que cambian con el tiempo sin perder trazabilidad, control de acceso y calidad del contenido.

En muchos casos, los reglamentos se comparten en documentos sueltos (PDF, DOCX, correo), sin una version oficial clara ni historial de cambios. Esto provoca:

- Dudas sobre cual es la version vigente.
- Dificultad para auditar quien cambio que y cuando.
- Problemas de acceso (documentos privados enviados a personas no autorizadas).
- Alto coste de revision manual de textos largos.

RuleTrack plantea una solucion web unificada para crear, versionar, publicar y revisar reglamentos con apoyo de IA.

## 1.2 Motivacion del proyecto
La motivacion principal es combinar en una sola aplicacion:

- Gestion documental versionada.
- Control de visibilidad por organizacion y rol.
- Asistencia inteligente para revision y mejora de contenido.
- Flujo de despliegue reproducible con Docker y CI/CD.

Ademas, se priorizo una arquitectura que facilitara defensa tecnica del proyecto: separacion frontend/backend, API documentada con OpenAPI y pruebas automatizadas.

## 1.3 Objetivos generales y especificos
### Objetivo general
Desarrollar una plataforma web full stack para gestionar reglamentos con control de versiones, acceso seguro y apoyo de inteligencia artificial.

### Objetivos especificos
- Implementar autenticacion con JWT y roles (ORGANIZADOR y USUARIO).
- Permitir crear reglamentos con visibilidad PUBLICO, SOLO_MIEMBROS o PRIVADO.
- Gestionar versiones (borrador, publicado, archivado) y activacion de version vigente.
- Registrar historial de cambios para auditoria.
- Integrar conversion de documentos (PDF/DOCX) a Markdown.
- Integrar analisis IA para revision, resumen e incoherencias.
- Exponer API documentada con Swagger/OpenAPI.
- Desplegar con Docker Compose en entorno de desarrollo y produccion.
- Configurar pipeline CI/CD en GitHub Actions y publicacion de imagenes en GHCR.

## 1.4 Antecedentes y comparativa breve
### Soluciones tradicionales
- Comparticion por correo o carpetas compartidas: baja trazabilidad.
- Edicion manual de documentos: sin control de versiones estructurado.

### Gestores de documentos generalistas
- Resuelven almacenamiento, pero no siempre integran bien:
  - Version funcional orientada a reglamentos.
  - Control fino de visibilidad por organizacion.
  - Motor de sugerencias IA sobre texto normativo.

### Diferencial de RuleTrack
- Enfoque especifico en reglamentos.
- Versionado formal con estados.
- Historial de cambios por version.
- IA aplicada al dominio documental (revision, resumen, incoherencias).
- Arquitectura desplegable end-to-end con proxy inverso y API documentada.

## 1.5 Alcance inicial
El alcance definido para esta iteracion incluye:

- Backend REST con Spring Boot y PostgreSQL.
- Frontend SPA con Angular.
- Seguridad JWT y control de accesos.
- Documentacion tecnica y de despliegue.
- Pruebas unitarias de servicios criticos.

Quedan fuera del alcance base:

- Multi-tenant avanzado con aislamiento fisico por organizacion.
- Firma digital y validez legal documental.
- Motor completo de notificaciones.
