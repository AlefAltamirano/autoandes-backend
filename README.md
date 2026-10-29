# AutoAndes API
Spring Boot 4.1.2 · Java 21 · Oracle · Flyway · Spring Security/JWT.

## Ejecutar
Requisitos: JDK 21+, Oracle accesible y usuario con permisos de crear tablas y secuencias en su propio esquema. Maven Wrapper descarga Maven cuando haga falta.
Desde la carpeta del proyecto en PowerShell:
```powershell
$env:JAVA_HOME='C:\Users\Asus\.jdks\ms-21.0.12.1'
.\mvnw.cmd spring-boot:run
```
En Linux/macOS: `./mvnw spring-boot:run`.
Configuración local por defecto: puerto 8080; Oracle `localhost:1522/FREEPDB1`, esquema `AUTOANDES_DB`, clave de laboratorio `123456`.
Variables opcionales: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `PORT`. Para cambiar la firma JWT: `APP_SECURITY_JWT_SECRET` (mínimo 32 bytes). Las claves por defecto son exclusivamente de desarrollo.
Swagger: http://localhost:8080/swagger-ui.html. Pulsar Authorize e introducir el JWT del login.
No iniciar una segunda instancia si el puerto 8080 está ocupado.

## Migraciones y reglas
V1 crea el esquema original y parte de la semilla; V2 prepara admin. V3 completa seis clientes, ocho servicios (SRV-008 inactivo), ocho vehículos, tres órdenes REGISTRADAS y una ANULADA con sus detalles, roles y usuarios BCrypt. Se conserva V1/V2 para no romper los checksums de las migraciones ya aplicadas.
V3 usa MERGE para los usuarios y tolera roles de seguridad existentes. El usuario de Oracle necesita CREATE SEQUENCE.
El correlativo usa una secuencia Oracle; puede dejar huecos si una transacción falla, sin reutilizar números ni colisionar entre peticiones.
Las reglas RN-01 a RN-05 viven en servicios. El registro y la anulación son transaccionales. El bloqueo del vehículo serializa la validación del kilometraje. La SPA muestra un estimado; el servicio recupera el precio de Oracle y calcula importes con aritmética decimal.
Reporte: JPQL con SUM y GROUP BY en OrdenServicioRepository, excluye ANULADA; no carga todo para sumar en Java.
Seguridad: unión centralizada de permisos por rol, hasAuthority en endpoints, filtro con firma y expiración, sesión STATELESS y CORS para http://localhost:4200.

## Verificación
```powershell
.\mvnw.cmd test
python tests/verificar_api.py
```
La primera orden inicia contexto JPA/Flyway contra Oracle. La segunda requiere el backend iniciado y realiza las pruebas HTTP reales. Crea y elimina servicio SRV-099 y vehículo ZZT-999; conserva una orden de prueba ANULADA por trazabilidad. Usar un esquema de desarrollo.
`tests/resultados-api.json`: resultados de la última ejecución (sin tokens ni contraseñas).
Importar `postman/AutoAndes.postman_collection.json` y ejecutarla en orden: almacena los tokens y los IDs automáticamente e incluye ejemplos reales de 200/201, 400, 401, 403, 404 y 409.
Para comprobar SQL: `docs/verificacion_oracle.sql` muestra cabeceras, detalles, totales, semilla y ausencia de huérfanos.

## Usuarios de prueba
Contraseña de los cuatro usuarios: `Auto2026*`.
- `admin`: ADMIN, todos los permisos.
- `asesor`: ASESOR, consulta y registra vehículos y órdenes; no anula ni ve reportes.
- `jefe`: JEFE_TALLER, mantiene servicios, anula órdenes y consulta reportes; no registra órdenes ni modifica vehículos.
- `inactivo`: ASESOR inactivo, login rechazado con 401.

## Contrato del API
Base: `http://localhost:8080/api/v1`. Solo el login es público; usar `Authorization: Bearer <token>`.
- `POST /auth/login`: username, password; devuelve token, tipo, expiraEnSegundos, nombreCompleto, roles, permisos.
- `GET /auth/me`: usuario autenticado y permisos.
- `GET /clientes`: VEHICULO_LEER.
- `GET /servicios`, `GET /servicios/{id}`: SERVICIO_LEER.
- `POST /servicios`, `PUT /servicios/{id}`, `DELETE /servicios/{id}`, `PATCH /servicios/{id}/estado`: SERVICIO_ESCRIBIR.
- `GET /vehiculos?clienteId`, `GET /vehiculos/{id}`: VEHICULO_LEER.
- `POST /vehiculos`, `PUT /vehiculos/{id}`, `DELETE /vehiculos/{id}`: VEHICULO_ESCRIBIR.
- `POST /ordenes`: ORDEN_REGISTRAR; vehiculoId, kilometraje, detalles [{servicioId,cantidad}]. No acepta precios ni total del cliente.
- `GET /ordenes?placa&estado&desde&hasta`, `GET /ordenes/{id}`: ORDEN_LEER.
- `PATCH /ordenes/{id}/anular`: ORDEN_ANULAR; {motivo}.
- `GET /reportes/ingresos-por-servicio?desde&hasta`: REPORTE_VER.
Fechas `desde` y `hasta`: `YYYY-MM-DD`, límites inclusivos por día.
Altas: 201; consultas/ediciones: 200; eliminación: 204. Errores: 400 validación, 401 autenticación, 403 permiso, 404 inexistente, 409 regla de negocio. Formato común: timestamp ISO, status, error, mensaje, ruta; campos solo en validación.

## Entrega y trazabilidad
Trabajo en `examen-a-altamirano`, integrado en `develop` y `main`; etiqueta `v1.0-examen-u2`.
Las capturas documentan ejecución real. La demostración individual y la explicación del código corresponden al estudiante; revisar la guía de defensa antes de presentar.
