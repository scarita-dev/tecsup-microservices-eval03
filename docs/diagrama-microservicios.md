# Diagrama de microservicios - Laboratorio 03

## Vista general

```mermaid
flowchart LR
    Cliente[Cliente / Postman]

    subgraph LS[libro-service :8081]
        LC[LibroController]
        LSvc[LibroService]
        LR[LibroRepository]
        LC --> LSvc --> LR
    end

    subgraph RDR[lector-service :8082]
        RC[LectorController]
        RSvc[LectorService]
        RR[LectorRepository]
        RC --> RSvc --> RR
    end

    subgraph PS[prestamo-service :8083]
        PC[PrestamoController]
        PSvc[PrestamoService]
        PR[PrestamoRepository]
        PLC[LibroClient]
        PRC[LectorClient]
        PC --> PSvc --> PR
        PSvc --> PLC
        PSvc --> PRC
    end

    DBL[(bd_libro)]
    DBR[(bd_lector)]
    DBP[(bd_prestamo)]

    Cliente -->|REST /api/libros| LC
    Cliente -->|REST /api/lectores| RC
    Cliente -->|REST /api/prestamos| PC

    LR --> DBL
    RR --> DBR
    PR --> DBP

    PLC -->|REST /api/libros/{id}/reservar y /liberar| LC
    PRC -->|REST /api/lectores/{id}| RC
```

## Responsabilidades

| Microservicio | Puerto | Base de datos | Responsabilidad |
|---|---:|---|---|
| `libro-service` | 8081 | `bd_libro` | CRUD de libros, ISBN único, stock y estado. Expone operaciones internas `reservar`/`liberar` para descontar o reponer stock. |
| `lector-service` | 8082 | `bd_lector` | CRUD de lectores con documento, nombres y contacto. Documento único. |
| `prestamo-service` | 8083 | `bd_prestamo` | Registro de préstamos, devolución, actualización de fecha límite y consulta de historial. Referencia lectores y libros por ID. |

## Reglas de integración

- No hay claves foráneas físicas entre microservicios.
- `prestamo-service` guarda `lectorId` y `libroId` como IDs lógicos.
- Antes de crear un préstamo, `prestamo-service` valida que el lector exista.
- Antes de descontar stock, `prestamo-service` llama a `libro-service`; si el libro no existe, responde 404; si no hay stock o está no disponible, responde 409.
- Al devolver, `prestamo-service` cambia el estado a `DEVUELTO` y solicita reponer stock.
- Cada servicio mantiene su propia base de datos; la consistencia entre servicios se coordina por API REST y compensaciones en caso de error.
