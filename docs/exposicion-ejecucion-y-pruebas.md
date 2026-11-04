# Guía de ejecución y pruebas para la exposición

## 0. Antes de cerrar todo o apagar la computadora

Cerrar las páginas del navegador **no borra nada** ni detiene el proyecto. Solo se perderá la ventana activa.

En cambio:

- Si cierras las terminales donde ejecutaste `spring-boot:run`, los microservicios se detendrán.
- Si apagas la computadora, los microservicios también se detendrán.
- Los datos guardados en MySQL **se conservan**.
- El servicio `MySQL84` está configurado para iniciar automáticamente con Windows, pero las aplicaciones Spring Boot no.

## 1. Antes de la exposición

1. Abre IntelliJ y el proyecto:
   `C:\Users\DANITZA\Downloads\Lab03-Biblioteca-Microservicios`
2. Confirma que MySQL está corriendo:

```cmd
sc query MySQL84
```

Debe mostrar `ESTADO: 4 RUNNING`.

3. Verifica que existen las bases:

```cmd
mysql -u root -p -e "SHOW DATABASES;"
```

Ingresa `root` como contraseña.

Debes ver:

```text
bd_libro
bd_lector
bd_prestamo
```

## 2. Iniciar los microservicios

Abre **tres terminales** en IntelliJ.

### Terminal 1: libro-service

```cmd
mvn -pl libro-service spring-boot:run
```

Espera a ver:

```text
Started LibroServiceApplication
```

### Terminal 2: lector-service

```cmd
mvn -pl lector-service spring-boot:run
```

Espera a ver:

```text
Started LectorServiceApplication
```

### Terminal 3: prestamo-service

```cmd
mvn -pl prestamo-service spring-boot:run
```

Espera a ver:

```text
Started PrestamoServiceApplication
```

No cierres estas terminales durante la demostración.

## 3. Verificación rápida por navegador

Abre estas URLs:

```text
http://localhost:8081/api/libros
http://localhost:8082/api/lectores
http://localhost:8083/api/prestamos
```

Si aún no creaste datos, verás:

```json
[]
```

Eso confirma que los tres servicios y sus conexiones a MySQL funcionan.

## 4. Demostración con Postman

Importa:

```text
postman/Lab03-Biblioteca-Local.postman_environment.json
postman/Lab03-Biblioteca.postman_collection.json
```

Selecciona el ambiente **Lab03 Biblioteca - Local**.

Ejecuta en orden:

1. **Lector → Crear lector válido**
   - Esperado: `201 Created`
   - Guarda automáticamente `lectorId`.

2. **Libro → Crear libro válido**
   - Esperado: `201 Created`
   - Guarda automáticamente `libroId`.

3. **Prestamo y devolucion → Registrar préstamo válido**
   - Esperado: `201 Created`
   - Guarda automáticamente `prestamoId`.
   - El estado debe ser `ACTIVO`.

4. **Prestamo y devolucion → Devolver préstamo**
   - Esperado: `200 OK`
   - El estado debe cambiar a `DEVUELTO`.
   - `fechaDevolucion` tendrá la fecha actual.

## 5. Casos de error para mostrar al profesor

Ejecuta en Postman:

1. **Crear lector inválido: documento incorrecto**
   - Esperado: `400 Bad Request`
   - Muestra JSON con `details` y mensaje comprensible.

2. **Crear libro inválido: stock negativo**
   - Esperado: `400 Bad Request`
   - Detalle en `details.stock`.

3. **Crear libro duplicado**
   - Esperado: `409 Conflict`
   - Mensaje: ya existe un libro con ese ISBN.

4. **Registrar préstamo inválido: lector inexistente**
   - Esperado: `404 Not Found`.

5. **Registrar préstamo inválido: fecha límite anterior**
   - Esperado: `400 Bad Request`.

6. **Devolver préstamo por segunda vez**
   - Esperado: `409 Conflict`.

## 6. Qué puedes cerrar y qué no

| Elemento | ¿Se puede cerrar? | Consecuencia |
|---|---|---|
| Navegador / páginas | Sí | No pierdes datos ni configuración. |
| IntelliJ | No mientras ejecutas | Si cierras IntelliJ, se detienen los microservicios. |
| Terminal 1, 2 o 3 | No durante la demo | El microservicio correspondiente deja de responder. |
| Postman | Se puede cerrar y volver a abrir | La colección y variables se guardan en el archivo importado. |
| MySQL84 | Debe quedar corriendo | Si lo detienes, los microservicios fallarán al consultar datos. |

## 7. Si mañana apagas la computadora

MySQL probablemente iniciará solo porque quedó como servicio automático. Aun así, verifica:

```cmd
sc query MySQL84
```

Luego inicia nuevamente los tres microservicios y repite la demostración.

## 8. Guion breve para la exposición

1. Presenta el problema de la biblioteca.
2. Explica por qué separaste en tres servicios.
3. Muestra el diagrama:
   `docs/diagrama-microservicios.svg`
4. Muestra la estructura de paquetes por servicio.
5. Explica que cada uno tiene su base de datos.
6. Abre Postman y ejecuta:
   - crear lector
   - crear libro
   - préstamo
   - devolución
7. Muestra un error 400 o 409.
8. Cierra con:
   - persistencia con Spring Data JPA
   - DTOs y validaciones
   - referencias entre servicios por ID
   - README y colección Postman como evidencia

## 9. Comandos de respaldo si algo falla

Si un puerto está ocupado:

```cmd
 netstat -ano | findstr :8081
```

Si `mysql` no se reconoce:

```cmd
cd /d "C:\Users\DANITZA\Downloads\Lab03-Biblioteca-Microservicios"
```
y cierra y vuelve a abrir la terminal.

Si necesitas recrear las bases:

```cmd
mysql -u root -p < scripts\01-create-databases.sql
```

No borres `C:\ProgramData\MySQL\MySQL Server 8.4\Data`; es la carpeta de datos de MySQL.
