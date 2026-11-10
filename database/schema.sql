-- =====================================================================
-- Juris Magna — Estructura de la base de datos
--
-- Hibernate crea las tablas automáticamente (ddl-auto=update).
-- Este archivo documenta la estructura y también sirve para crearla
-- a mano en MySQL:
--     mysql -h $DB_HOST -u listi -p < database/schema.sql
--
-- Cada integrante agrega sus tablas en su propia sección.
-- Convención: nombres de tabla en singular y snake_case.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS jurismagna
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE jurismagna;


-- =====================================================================
-- Tablas de Gustavo: usuario, abogado, movimiento
-- =====================================================================

-- Cuentas de acceso de los tres roles.
-- nombre, email, telefono y foto_url solo se usan para el perfil del
-- administrador; abogados y clientes guardan sus datos en su propia tabla.
CREATE TABLE IF NOT EXISTS usuario (
    id            INT          NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    rol           ENUM('ADMIN', 'ABOGADO', 'CLIENTE') NOT NULL,
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    nombre        VARCHAR(100) NULL,
    email         VARCHAR(100) NULL,
    telefono      VARCHAR(20)  NULL,
    foto_url      VARCHAR(255) NULL,

    PRIMARY KEY (id),
    CONSTRAINT uq_usuario_username UNIQUE (username)
);

-- Datos profesionales del abogado; cada abogado tiene un usuario.
CREATE TABLE IF NOT EXISTS abogado (
    id           INT          NOT NULL AUTO_INCREMENT,
    usuario_id   INT          NOT NULL,
    nombre       VARCHAR(100) NOT NULL,
    edad         INT          NULL,
    especialidad VARCHAR(100) NULL,
    estudios     VARCHAR(150) NULL,
    experiencia  VARCHAR(100) NULL,
    cedula       VARCHAR(20)  NULL,
    email        VARCHAR(100) NULL,
    telefono     VARCHAR(20)  NULL,
    foto_url     VARCHAR(255) NULL,
    estado       ENUM('Activo', 'Inactivo') NOT NULL DEFAULT 'Activo',

    PRIMARY KEY (id),
    CONSTRAINT uq_abogado_usuario UNIQUE (usuario_id),
    CONSTRAINT fk_abogado_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);

-- Ingresos y egresos del despacho; de aquí salen finanzas y dashboard.
-- "Retención fiscal" se guarda como EGRESO, aunque en la pantalla se
-- muestra en la tabla de ingresos y no entra en las gráficas.
CREATE TABLE IF NOT EXISTS movimiento (
    id       INT           NOT NULL AUTO_INCREMENT,
    tipo     ENUM('INGRESO', 'EGRESO') NOT NULL,
    concepto ENUM(
        'Honorarios',
        'Consultas y asesorías',
        'Nómina',
        'Renta',
        'Servicios',
        'Otros',
        'Retención fiscal'
    ) NOT NULL,
    monto    DECIMAL(12, 2) NOT NULL,
    fecha    DATE           NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT ck_movimiento_monto CHECK (monto > 0)
);


-- =====================================================================
-- Tablas de Danna: cliente, caso, carpeta, liga, documento, nota
-- =====================================================================


-- =====================================================================
-- Tablas de Luisa: mensaje, notificacion, evento, contacto
-- =====================================================================
