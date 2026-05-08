# 02. Descripcion funcional del sistema

## 2.1 Vision general
RuleTrack es una aplicacion web para gestionar reglamentos con un flujo completo:

1. Registro/login de usuario.
2. Creacion de reglamentos y versiones.
3. Definicion de visibilidad del contenido.
4. Publicacion y consulta segun permisos.
5. Revision asistida por IA.
6. Auditoria de cambios.

## 2.2 Funcionalidades principales
### A. Autenticacion y gestion de perfil
- Registro de usuarios con rol y organizacion.
- Inicio de sesion con token JWT.
- Consulta y actualizacion del perfil.
- Consulta de miembros de la organizacion.

### B. Gestion de reglamentos
- Crear, editar y eliminar reglamentos.
- Definir visibilidad:
  - PUBLICO: visible para cualquier usuario.
  - SOLO_MIEMBROS: visible para miembros de la organizacion.
  - PRIVADO: visible solo al creador o usuarios permitidos.
- Vista publica de reglamentos para compartir contenido.

### C. Versionado
- Crear versiones asociadas a un reglamento.
- Gestionar estado de version: BORRADOR, PUBLICADO, ARCHIVADO.
- Activar una version como vigente, archivando las anteriores.
- Sugerencia automatica de siguiente etiqueta de version.

### D. IA y calidad del contenido
- Analisis de correcciones sobre texto.
- Generacion de revision de calidad.
- Generacion de resumen.
- Deteccion de incoherencias.
- Marcar sugerencias como aplicadas.

### E. Conversion documental
- Carga de documentos PDF o DOCX.
- Extraccion y conversion a Markdown.
- Uso del contenido convertido en el flujo de publicacion/revision.

### F. Historial de cambios
- Registro de acciones sobre versiones.
- Consulta de historial para auditoria.

## 2.3 UI/UX
### Principios aplicados
- Jerarquia visual clara por bloques funcionales.
- Componentes reutilizables (cards, modales, tablas).
- Navegacion orientada a tareas (subir, convertir, corregir, publicar).
- Retroalimentacion inmediata en acciones principales (modales, estado, botones).

### Caracteristicas de interfaz
- SPA en Angular con enrutado por paginas.
- Pantalla diferenciada para organizador y usuario.
- Vista publica para acceso externo a reglamentos.
- Diseño responsive para escritorio y movil.

## 2.4 Usuarios objetivo
- Organizador: crea y gestiona reglamentos/versiones, publica contenido y administra flujo documental.
- Usuario miembro: consulta reglamentos visibles y participa en consumo de contenido.
- Usuario externo (no autenticado): consulta reglamentos publicos.

## 2.5 Casos de uso principales
1. Un organizador crea un reglamento y publica version 1.0.
2. El sistema genera sugerencias IA para mejorar redaccion.
3. El organizador aplica cambios, crea version 1.1 y la activa.
4. Miembros de su organizacion consultan contenido SOLO_MIEMBROS.
5. Usuarios externos acceden a contenido PUBLICO mediante enlace.

## 2.6 Restricciones funcionales
- Algunas operaciones requieren autenticacion JWT.
- El acceso a contenido depende de visibilidad y organizacion.
- Las operaciones de IA dependen de disponibilidad y clave del proveedor LLM.
