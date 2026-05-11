# 07. Pruebas

## 7.1 Metodología

Las pruebas del proyecto siguen un enfoque **manual-guiado con cobertura unitaria automatizada** sobre los servicios críticos del backend:

- **Pruebas unitarias** (JUnit 5 + Mockito): capa de servicio del backend con aislamiento total de dependencias.
- **Pruebas manuales** de la API: mediante el fichero `backend/docs/ruletrack-api.http` (compatible con REST Client de VS Code e IntelliJ HTTP Client).
- **Pruebas de integración** básicas: verificadas manualmente con `docker compose up` y las peticiones HTTP del fichero de pruebas.

No se aplica TDD estricto (test-first), pero los tests unitarios se escribieron inmediatamente después de implementar cada servicio, mientras la lógica estaba fresca.

---

## 7.2 Tipos de pruebas

### 7.2.1 Pruebas unitarias del backend

Los tests están ubicados en `backend/src/test/java/com/example/ruletrack/service/` y cubren los dos servicios con mayor lógica de negocio:

#### `AuthServiceTest`

Verifica el comportamiento del servicio de autenticación ante distintos escenarios:

| Test | Escenario verificado |
|---|---|
| `register_success` | Registro exitoso de usuario, token devuelto correctamente |
| `register_duplicateUsername_throwsIllegalArgument` | Fallo si el username ya existe |
| `register_duplicateEmail_throwsIllegalArgument` | Fallo si el email ya está registrado |
| `register_duplicateDni_throwsIllegalArgument` | Fallo si el DNI ya está registrado |
| `register_joinNonExistentOrg_throwsIllegalArgument` | Fallo al unirse a organización inexistente |
| `register_organizadorMenorDeEdad_throwsIllegalArgument` | Fallo si organizador es menor de 18 años |
| `login_success` | Login exitoso, token JWT devuelto |
| `login_badCredentials_throwsException` | Fallo con credenciales incorrectas |

**Dependencias mockeadas**: `UsuarioRepository`, `PasswordEncoder`, `AuthenticationManager`, `JwtTokenProvider`.

#### `ReglamentoServiceTest`

Verifica la gestión de reglamentos, especialmente el control de visibilidad y acceso:

| Test | Escenario verificado |
|---|---|
| `findPublicos_returnsOnlyPublicReglamentos` | Solo devuelve reglamentos con visibilidad PUBLICO |
| `findPublicos_noResults_returnsEmpty` | Lista vacía si no hay reglamentos públicos |
| `findById_found_returnsDTO` | Devuelve el reglamento cuando existe |
| `findById_notFound_throwsResourceNotFoundException` | Lanza excepción si el reglamento no existe |
| `findAll_returnsAll` | Devuelve todos los reglamentos |
| `crear_reglamento_*` | Creación correcta con versión inicial |
| `eliminar_reglamento_*` | Eliminación con verificación de permisos |

**Dependencias mockeadas**: `ReglamentoRepository`, `UsuarioRepository`, `VersionReglamentoRepository`, `HistorialCambiosService`, `SecurityContext`.

### 7.2.2 Prueba de arranque del contexto

`RuletrackApplicationTests` verifica que el contexto de Spring Boot carga sin errores, lo que actúa como smoke test de la configuración de beans y dependencias.

### 7.2.3 Pruebas manuales de la API

El fichero `backend/docs/ruletrack-api.http` contiene **27 peticiones documentadas** que cubren todos los endpoints de la API:

- Autenticación (register, login, me, actualizar perfil, info de organización, miembros)
- Reglamentos (listar públicos, crear, obtener, actualizar, eliminar)
- Versiones (listar, crear, obtener, activar, cambiar estado, siguiente etiqueta)
- Sugerencias IA (listar, generar revisión, resumen, incoherencias, marcar aplicada)
- Historial de cambios

---

## 7.3 Herramientas de testing

| Herramienta | Uso |
|---|---|
| JUnit 5 | Framework de pruebas unitarias |
| Mockito (vía `spring-boot-starter-test`) | Mocking de dependencias |
| AssertJ | Aserciones fluidas y legibles |
| H2 (en memoria) | Base de datos en memoria para tests de integración |
| `spring-security-test` | Utilidades para mockear el `SecurityContext` |
| JaCoCo 0.8.12 | Informe de cobertura de código |
| Karma + Jasmine | Tests unitarios del frontend Angular |

---

## 7.4 Configuración de JaCoCo

JaCoCo está configurado en `pom.xml` para generar el informe de cobertura en la fase `verify`:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <executions>
        <execution>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>verify</phase>
            <goals><goal>report</goal></goals>
        </execution>
    </executions>
</plugin>
```

Para generar el informe:

```bash
cd backend
./mvnw verify
# El informe HTML queda en target/site/jacoco/index.html
```

---

## 7.5 Ejecución de los tests

### Tests unitarios del backend

```bash
cd backend
./mvnw test
```

### Tests con informe de cobertura

```bash
cd backend
./mvnw verify
```

### Tests del frontend

```bash
cd frontend
npm test
```

Los tests de Angular ejecutan Karma con Jasmine en modo headless. Las pruebas incluyen el spec del componente raíz (`app.spec.ts`).

---

## 7.6 Cobertura de código

La cobertura se concentra en la capa de servicio, que contiene la lógica de negocio más relevante. La capa de controladores y repositorios queda cubierta principalmente por las pruebas manuales de integración.

| Capa | Estrategia de prueba |
|---|---|
| `service/` | Tests unitarios automatizados con Mockito |
| `controller/` | Pruebas manuales mediante fichero HTTP |
| `repository/` | Verificación implícita vía integración manual |
| `security/` | Smoke test de contexto + tests de servicio con `SecurityContext` mockeado |
| `entity/` | Sin tests específicos (lógica mínima, solo JPA annotations) |

---

## 7.7 Criterios de aceptación verificados

| Criterio | Método de verificación |
|---|---|
| Registro con datos duplicados es rechazado | Test unitario `AuthServiceTest` |
| Login incorrecto devuelve error | Test unitario `AuthServiceTest` |
| Solo reglamentos PUBLICO son accesibles sin autenticación | Test unitario `ReglamentoServiceTest` |
| Activar versión archiva las anteriores | Prueba manual con API HTTP |
| Conversión de PDF/DOCX produce Markdown legible | Prueba manual con fichero de prueba |
| Sugerencias IA se generan y persisten correctamente | Prueba manual con API HTTP |
| El stack arranca completo con Docker Compose | Prueba de integración manual |
