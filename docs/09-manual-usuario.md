# 09. Manual de usuario

## 9.1 Introducción

RuleTrack es una aplicación web para gestionar reglamentos con versionado, control de visibilidad y asistencia por inteligencia artificial. Este manual describe las funcionalidades principales según el tipo de usuario.

**URL de acceso**: la aplicación está disponible en el navegador sin necesidad de instalar nada.

**Navegadores compatibles**: Chrome, Firefox, Edge, Safari (versiones modernas).

---

## 9.2 Tipos de usuario

| Tipo | Descripción |
|---|---|
| **Usuario externo** | Accede sin cuenta. Solo puede consultar reglamentos públicos. |
| **Usuario miembro** | Registrado en una organización. Consulta reglamentos públicos y de miembros. |
| **Organizador** | Crea y gestiona reglamentos, versiones, conversiones y análisis IA. |

---

## 9.3 Registro e inicio de sesión

### Registrarse

1. Acceder a la aplicación.
2. Pulsar el botón **Registrarse** en la cabecera o en la pantalla principal.
3. Rellenar el formulario:
   - **Nombre de usuario** (único en el sistema).
   - **Nombre y apellidos**.
   - **Fecha de nacimiento** (los organizadores deben ser mayores de 18 años).
   - **Email** (único en el sistema).
   - **DNI** (único en el sistema).
   - **Contraseña**.
   - **Organización**: elegir entre unirse a una existente (introducir su nombre exacto) o crear una nueva.
   - **Rol**: USUARIO o ORGANIZADOR.
4. Pulsar **Crear cuenta**.
5. Al completarse el registro, se inicia sesión automáticamente.

### Iniciar sesión

1. Pulsar el botón **Iniciar sesión** en la cabecera.
2. Introducir **nombre de usuario** y **contraseña**.
3. Pulsar **Entrar**.
4. La aplicación redirige al panel correspondiente al rol del usuario.

### Cerrar sesión

Pulsar el icono de usuario en la cabecera y seleccionar **Cerrar sesión**.

---

## 9.4 Panel principal

### Panel de usuario (`/`)

La pantalla de inicio muestra los **reglamentos visibles** para el usuario autenticado:
- Reglamentos públicos de cualquier organización.
- Reglamentos de la propia organización con visibilidad `SOLO_MIEMBROS`.

Cada reglamento aparece en una tarjeta con título, descripción, badge de visibilidad y número de versiones.

### Panel de organizador (`/organizer`)

El organizador accede a su panel de gestión, con acceso a:
- Lista de todos los reglamentos de su organización.
- Botones de acceso rápido a las funcionalidades principales.
- Indicadores de estado de cada reglamento (versión activa, estado).

---

## 9.5 Gestión de reglamentos (Organizador)

### Ver lista de reglamentos (`/publicaciones`)

Muestra todos los reglamentos de la organización. Para cada uno se puede:
- **Ver detalle**: acceder a la página de ajustes del reglamento.
- **Ver versión activa**: abrir el contenido publicado.
- **Copiar enlace**: generar un enlace directo al reglamento público.

### Crear un nuevo reglamento

**Opción A – Desde cero:**

1. Desde el panel de organizador, pulsar **Nuevo reglamento** o acceder a `/upload`.
2. Subir un documento PDF o DOCX existente, o escribir el contenido directamente.
3. Si se sube un documento, la aplicación lo convierte automáticamente a Markdown.
4. Revisar el contenido en la pantalla de previsualización (`/preview`).
5. Aplicar correcciones IA si se desea (ver sección 9.7).
6. Configurar título, descripción y visibilidad.
7. Pulsar **Publicar** para crear el reglamento con la versión inicial.

### Editar un reglamento (`/publicaciones`)

1. Desde la tabla de reglamentos dentro de publicaciones, pulsar el botón de "ver ajustes" en el reglamento deseado.
2. En la página de ajustes, se pueden realizar las siguientes acciones:
   - Modificar título, descripción o visibilidad.
   - Actualizar reglamento con una nueva versión (ver sección 9.6).
   - Eliminar el reglamento (con confirmación).
3. Guardar los cambios realizados.
### Configurar visibilidad

| Opción | Quién puede ver el reglamento |
|---|---|
| **PUBLICO** | Cualquier persona, incluso sin cuenta |
| **SOLO_MIEMBROS** | Solo los usuarios de la misma organización |
| **PRIVADO** | Solo el creador |

---

## 9.6 Gestión de versiones (Organizador)

### Ver versiones de un reglamento (`/ajustes-publicacion`)

La página de ajustes de publicación muestra todas las versiones del reglamento con su estado:
- `BORRADOR`: en redacción, no visible para usuarios.
- `PUBLICADO`: versión activa y visible.
- `ARCHIVADO`: versión reemplazada por una posterior.

### Crear una nueva versión

1. Desde la página de ajustes de publicación, pulsar **Actualizar versión**.
2. El sistema te mandará a la previsualización del contenido, donde haces clic en "Corregir documento".
3. Se realizan las correcciones y se puede cambiar el nombre de la publicación y la versión del mismo.

### Activar una versión

Al activar una versión:
- Su estado pasa a `PUBLICADO`.
- La versión previamente activa pasa a `ARCHIVADO`.
- Solo puede haber una versión activa por reglamento.

---

## 9.7 Flujo de subida y conversión de documentos

### Subir un documento (`/upload`)

1. Acceder a la página de subida.
2. Arrastrar y soltar un fichero **PDF** o **DOCX**, o usar el selector de fichero.
3. Pulsar **Convertir**.

### Conversión (`/converting`)

La aplicación procesa el documento en el servidor:
- **PDF**: extracción de texto con Apache PDFBox.
- **DOCX**: extracción de texto con Apache POI.

El proceso muestra un indicador de progreso. Una vez completado, redirige automáticamente a la previsualización.

### Previsualización (`/preview`)

Muestra el contenido convertido a Markdown con renderizado visual. Desde aquí se puede:
- Revisar el texto y realizar correcciones manuales.
- Continuar al flujo de corrección IA.

---

## 9.8 Corrección con IA (`/correcting` y `/corrected`)

### Solicitar correcciones

1. Desde la previsualización, pulsar **Corregir documento**.
2. La aplicación envía el contenido al modelo LLM (Groq / OpenAI-compatible).
3. El modelo devuelve entre 5 y 8 sugerencias de corrección en categorías: ortografía, gramática, formalidad y estilo.

### Revisar y aplicar sugerencias (`/correcting`)

Cada sugerencia muestra:
- El **texto original** (fragmento del documento).
- El **texto sugerido** (corrección propuesta).
- La **explicación** (categoría y justificación breve).

Para cada sugerencia se puede:
- **Aplicar**: reemplaza el texto original por la sugerencia en el documento.
- **Ignorar**: descarta la sugerencia sin modificar el documento.

### Resultado (`/corrected`)

Tras revisar todas las sugerencias, se muestra el documento con los cambios aplicados. Desde aquí se continúa al flujo de publicación.

---

## 9.9 Vista pública de reglamentos (`/view/:id`)

Cualquier persona puede acceder a la URL de un reglamento público sin necesidad de cuenta. La página muestra:
- Título y descripción del reglamento.
- Contenido de la versión activa renderizado en Markdown.
- Información de la organización propietaria.
- Badge de visibilidad.

Para compartir un reglamento, copiar la URL del navegador o usar el botón **Copiar enlace** del panel de organizador.

---

## 9.10 Perfil y organización

### Ver perfil (`/perfil`)

Muestra los datos del usuario autenticado: nombre, apellidos, email, organización y rol.

### Editar perfil

Desde la página de perfil, pulsar **Editar** para actualizar nombre y email.

### Ver miembros de la organización (`/miembros-organizacion`)

Lista todos los usuarios registrados en la misma organización, con nombre, apellidos y rol.

### Información de la organización (`/organizacion`)

Muestra el nombre de la organización, número de miembros y reglamentos gestionados.

---

## 9.11 Preguntas frecuentes

**¿Puedo acceder a los reglamentos sin registrarme?**
Sí, los reglamentos con visibilidad PUBLICO son accesibles sin cuenta desde su URL directa o desde la página principal.

**¿Qué formatos de documento puedo subir?**
PDF y DOCX. El tamaño máximo es 500 MB.

**¿Por qué no funciona el análisis IA?**
La funcionalidad IA requiere que el administrador del sistema haya configurado una `LLM_API_KEY` válida. Si el análisis falla, contacta con el administrador.

**¿Puedo recuperar una versión archivada?**
Sí. Desde la lista de versiones, puedes activar cualquier versión archivada, lo que la volverá a publicar y archivará la actual.

**¿Puedo eliminar un reglamento?**
Sí, siempre que seas el organizador propietario. La eliminación es permanente e incluye todas sus versiones, sugerencias e historial.

**¿Cómo sé qué versión está activa?**
La versión con estado PUBLICADO es la activa. Solo puede haber una por reglamento. Se muestra destacada en la lista de versiones.

**¿Qué ocurre si subo un PDF escaneado (solo imágenes)?**
El proceso de conversión extrae únicamente texto digital. Los PDFs escaneados sin OCR producirán un resultado vacío o muy limitado. Se recomienda usar documentos con texto seleccionable.