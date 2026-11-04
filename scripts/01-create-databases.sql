-- =========================================================
-- Laboratorio 03 - Sistema de biblioteca con microservicios
-- Bases de datos sugeridas por la guía:
--   bd_libro, bd_lector y bd_prestamo
-- Ejecutar como usuario con privilegios para crear bases de datos.
-- =========================================================

CREATE DATABASE IF NOT EXISTS bd_libro
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

CREATE DATABASE IF NOT EXISTS bd_lector
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

CREATE DATABASE IF NOT EXISTS bd_prestamo
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

-- =========================================================
-- bd_libro.libros
-- =========================================================
USE bd_libro;

CREATE TABLE IF NOT EXISTS libros (
    id BIGINT NOT NULL AUTO_INCREMENT,
    isbn VARCHAR(30) NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    autor VARCHAR(120) NOT NULL,
    stock INT NOT NULL,
    estado VARCHAR(20) NOT NULL,
    version BIGINT NULL,
    creado_en DATETIME(6) NOT NULL,
    actualizado_en DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_libros_isbn UNIQUE (isbn),
    CONSTRAINT chk_libros_stock CHECK (stock >= 0),
    CONSTRAINT chk_libros_estado CHECK (estado IN ('DISPONIBLE', 'NO_DISPONIBLE', 'MANTENIMIENTO'))
) ENGINE=InnoDB;

-- =========================================================
-- bd_lector.lectores
-- =========================================================
USE bd_lector;

CREATE TABLE IF NOT EXISTS lectores (
    id BIGINT NOT NULL AUTO_INCREMENT,
    documento VARCHAR(20) NOT NULL,
    nombres VARCHAR(150) NOT NULL,
    contacto VARCHAR(120) NOT NULL,
    creado_en DATETIME(6) NOT NULL,
    actualizado_en DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_lectores_documento UNIQUE (documento)
) ENGINE=InnoDB;

-- =========================================================
-- bd_prestamo.prestamos
-- Las referencias lector_id y libro_id son IDs lógicos.
-- No hay claves foráneas físicas entre microservicios.
-- =========================================================
USE bd_prestamo;

CREATE TABLE IF NOT EXISTS prestamos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    lector_id BIGINT NOT NULL,
    libro_id BIGINT NOT NULL,
    fecha_prestamo DATE NOT NULL,
    fecha_limite DATE NOT NULL,
    fecha_devolucion DATE NULL,
    estado VARCHAR(20) NOT NULL,
    creado_en DATETIME(6) NOT NULL,
    actualizado_en DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_prestamos_fechas CHECK (fecha_limite >= fecha_prestamo),
    CONSTRAINT chk_prestamos_estado CHECK (estado IN ('ACTIVO', 'DEVUELTO', 'CANCELADO'))
) ENGINE=InnoDB;

-- Crear índices solo si no existen para que el script pueda ejecutarse más de una vez.
SET @idx_prestamos_lector_id := (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = 'bd_prestamo'
      AND table_name = 'prestamos'
      AND index_name = 'idx_prestamos_lector_id'
);
SET @sql := IF(@idx_prestamos_lector_id = 0,
    'CREATE INDEX idx_prestamos_lector_id ON prestamos (lector_id)',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_prestamos_libro_id := (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = 'bd_prestamo'
      AND table_name = 'prestamos'
      AND index_name = 'idx_prestamos_libro_id'
);
SET @sql := IF(@idx_prestamos_libro_id = 0,
    'CREATE INDEX idx_prestamos_libro_id ON prestamos (libro_id)',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_prestamos_estado := (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = 'bd_prestamo'
      AND table_name = 'prestamos'
      AND index_name = 'idx_prestamos_estado'
);
SET @sql := IF(@idx_prestamos_estado = 0,
    'CREATE INDEX idx_prestamos_estado ON prestamos (estado)',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =========================================================
-- Datos mínimos de prueba (opcional)
-- Descomentar si desea iniciar con registros de ejemplo.
-- =========================================================
-- USE bd_lector;
-- INSERT INTO lectores (documento, nombres, contacto, creado_en, actualizado_en)
-- VALUES ('12345678', 'Sofía Cindell Carita Quispe', 'sofiacinta@example.com', NOW(6), NOW(6));
--
-- USE bd_libro;
-- INSERT INTO libros (isbn, titulo, autor, stock, estado, version, creado_en, actualizado_en)
-- VALUES ('978-612000001-0', 'Clean Code', 'Robert C. Martin', 3, 'DISPONIBLE', 0, NOW(6), NOW(6));
