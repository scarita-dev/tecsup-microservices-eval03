# Conclusiones y preparación de sustentación individual

**Estudiante:** Sofía Cindell Carita Quispe  
**Laboratorio 03:** Sistema de biblioteca con microservicios

## Cinco aprendizajes técnicos

1. **Separación por responsabilidades:** dividir el sistema en `libro-service`, `lector-service` y `prestamo-service` permite que cada servicio tenga un modelo, una base de datos y un ciclo de vida independiente. Los préstamos dependen de los libros y lectores, pero no comparten sus tablas.
2. **Persistencia con Spring Data JPA:** las entidades `Libro`, `Lector` y `Prestamo` se mapean con `@Entity`, y los repositorios extienden `JpaRepository`. Esto simplificó el CRUD, las búsquedas por ISBN/documento y las consultas de préstamos por estado o lector.
3. **Referencias entre servicios mediante IDs:** `prestamo-service` almacena `libroId` y `lectorId` en lugar de claves foráneas físicas. La integridad se valida llamando a las APIs de los otros servicios antes de registrar el préstamo.
4. **DTOs y validaciones:** los `RequestDTO` con anotaciones como `@NotBlank`, `@NotNull`, `@Min` y `@AssertTrue` permiten rechazar información inválida antes de llegar a la base de datos, y devuelven detalles comprensibles por campo.
5. **Manejo de errores y pruebas:** un `@RestControllerAdvice` centraliza excepciones y devuelve respuestas HTTP coherentes (`400`, `404`, `409`, `503`). La colección Postman permite demostrar casos exitosos y fallidos de forma repetible.

## Una dificultad encontrada y su solución

**Dificultad:** Mantener consistente el stock del libro cuando se registra o devuelve un préstamo sin usar una transacción distribuida.

**Solución aplicada:**

- `prestamo-service` llama a `libro-service` para reservar stock antes de guardar el préstamo.
- `libro-service` usa bloqueo pesimista (`@Lock(LockModeType.PESSIMISTIC_WRITE)`) al descontar o reponer stock.
- Si el guardado del préstamo falla, `prestamo-service` ejecuta una compensación para liberar/reservar nuevamente el stock.
- Si el libro no existe o no tiene stock, la API devuelve `404` o `409` con un mensaje comprensible.

Esta solución es adecuada para el alcance del laboratorio. En un sistema productivo de mayor escala se podría evaluar un patrón Saga con mensajería (Kafka/RabbitMQ) o reservas con expiración.

## Guía de sustentación de 5 a 8 minutos

### 1. Contexto y problema

La biblioteca necesita controlar libros, lectores y préstamos. Se modeló con tres microservicios porque cada uno representa una capacidad del negocio distinta.

### 2. Arquitectura

- `libro-service` administra catálogo y stock.
- `lector-service` administra usuarios lectores.
- `prestamo-service` coordina préstamos y devoluciones.
- Cada servicio tiene puerto y base de datos propia.
- La integración es por REST y por IDs lógicos.

### 3. Persistencia y modelo de datos

- `bd_libro.libros`: ISBN único, stock y estado.
- `bd_lector.lectores`: documento único y contacto.
- `bd_prestamo.prestamos`: `lectorId`, `libroId`, fechas y estado.
- No existen llaves foráneas físicas entre bases; la validación se hace por API.

### 4. Flujo de préstamo

1. Se valida lector existente.
2. Se reserva stock del libro.
3. Se crea `Prestamo` con estado `ACTIVO`.
4. Al devolver, se cambia a `DEVUELTO`, se registra fecha y se repone stock.

### 5. Calidad y errores

- DTO + validación para entradas inválidas.
- `@RestControllerAdvice` para errores JSON consistentes.
- Casos Postman: éxito, error 400, 404, 409.
- Perfiles `dev`/`prod` sin credenciales reales.

### 6. Cierre

La solución cumple el objetivo: microservicios separados, persistencia con Spring Data JPA, APIs REST, pruebas y documentación.

## Pregunta de sustentación

**Explique por qué separó el sistema en estos servicios, cómo persiste los datos y qué ocurre cuando la API recibe información inválida.**

### Respuesta sugerida

Separé el sistema en tres servicios porque el dominio tiene responsabilidades distintas:

- **Libro** cambia por catálogo y stock; lo consume principalmente personal de biblioteca.
- **Lector** cambia por registro y actualización de personas; no debería depender de préstamos.
- **Préstamo** coordina una operación entre ambos; guarda la transacción de préstamo/devolución.

Esto evita un monolito donde cualquier cambio en libros afecte directamente a lectores o préstamos. Además permite desplegar y evolucionar cada servicio por separado, conservar bases de datos independientes y reducir acoplamiento.

Los datos se persisten con Spring Data JPA. Cada servicio define sus entidades y repositorios. `libro-service` guarda libros en `bd_libro`, `lector-service` guarda lectores en `bd_lector` y `prestamo-service` guarda préstamos en `bd_prestamo`. Las referencias cruzadas se guardan como IDs, no como llaves foráneas físicas, y se validan por REST antes de operar.

Cuando la API recibe información inválida, los DTO aplican validaciones antes de la lógica de negocio. Por ejemplo, un libro con stock negativo responde `400 Bad Request` con el detalle del campo. Si el recurso no existe, responde `404 Not Found`. Si hay un conflicto de negocio, como ISBN duplicado o libro sin stock, responde `409 Conflict`. Todos los errores se encapsulan en un objeto JSON con `timestamp`, `status`, `error`, `message`, `path` y `details`.

## Cambios pequeños que el docente podría pedir en vivo

- Agregar un campo `telefono` o `correo` al lector.
- Filtrar libros por estado.
- Validar que la fecha límite sea máximo N días después del préstamo.
- Cambiar estado inicial de préstamo a `ACTIVO` o agregar `VENCIDO`.
- Mostrar préstamos por lector.
- Explicar por qué no se usó una llave foránea entre `prestamos.libroId` y `libros.id`.
