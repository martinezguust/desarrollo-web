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
-- Tablas de Danna: cliente, caso, carpeta, liga, nota
-- (los documentos del cliente se manejan con su enlace a Drive)
-- =====================================================================

-- Clientes del despacho; cada cliente tiene un usuario y un abogado.
-- comentario es el texto que el cliente escucha con síntesis de voz.
CREATE TABLE IF NOT EXISTS cliente (
    id          INT            NOT NULL AUTO_INCREMENT,
    usuario_id  INT            NOT NULL,
    abogado_id  INT            NOT NULL,
    nombre      VARCHAR(100)   NOT NULL,
    edad        INT            NULL,
    estudios    VARCHAR(150)   NULL,
    ine         VARCHAR(20)    NULL,
    email       VARCHAR(100)   NULL,
    telefono    VARCHAR(20)    NULL,
    descripcion VARCHAR(500)   NULL,
    avance      INT            NOT NULL DEFAULT 0,
    honorarios  DECIMAL(12, 2) NOT NULL DEFAULT 0,
    drive_url   VARCHAR(255)   NULL,
    comentario  TEXT           NULL,
    estado      ENUM('Activo', 'Inactivo') NOT NULL DEFAULT 'Activo',

    PRIMARY KEY (id),
    CONSTRAINT uq_cliente_usuario UNIQUE (usuario_id),
    CONSTRAINT fk_cliente_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    CONSTRAINT fk_cliente_abogado FOREIGN KEY (abogado_id) REFERENCES abogado (id),
    CONSTRAINT ck_cliente_avance CHECK (avance BETWEEN 0 AND 100),
    CONSTRAINT ck_cliente_honorarios CHECK (honorarios >= 0)
);

-- Casos (expedientes). El backend genera numero_expediente: EXP-0001, ...
-- materia admite las 18 del formulario original y se valida en el servicio:
--   Civil, Penal, Mercantil, Familiar, Laboral, Administrativo, Agrario,
--   Constitucional, Fiscal / Tributario, Ambiental, Internacional,
--   Corporativo, Inmobiliario, Migratorio, Seguridad Social,
--   Propiedad Intelectual, Concursal / Quiebras, Electoral
-- fecha_fin se llena cuando el caso pasa a Ganado, Perdido o Cancelado.
CREATE TABLE IF NOT EXISTS caso (
    id                INT          NOT NULL AUTO_INCREMENT,
    numero_expediente VARCHAR(10)  NOT NULL,
    cliente_id        INT          NOT NULL,
    abogado_id        INT          NOT NULL,
    materia           VARCHAR(40)  NOT NULL,
    estado            ENUM('Activo', 'EnProceso', 'Ganado', 'Perdido', 'Cancelado') NOT NULL DEFAULT 'Activo',
    fecha_inicio      DATE         NOT NULL,
    fecha_fin         DATE         NULL,
    drive_url         VARCHAR(255) NULL,

    PRIMARY KEY (id),
    CONSTRAINT uq_caso_expediente UNIQUE (numero_expediente),
    CONSTRAINT fk_caso_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (id),
    CONSTRAINT fk_caso_abogado FOREIGN KEY (abogado_id) REFERENCES abogado (id)
);

-- Carpetas del drive de cada caso. Si se borra el caso, se borran sus carpetas.
CREATE TABLE IF NOT EXISTS carpeta (
    id       INT          NOT NULL AUTO_INCREMENT,
    caso_id  INT          NOT NULL,
    numero   VARCHAR(20)  NULL,
    materia  VARCHAR(50)  NULL,
    proyecto VARCHAR(150) NULL,
    estado   VARCHAR(30)  NULL,
    liga_url VARCHAR(255) NULL,
    fecha    DATE         NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_carpeta_caso FOREIGN KEY (caso_id) REFERENCES caso (id) ON DELETE CASCADE
);

-- Ligas (enlaces) dentro de una carpeta. Si se borra la carpeta, se borran sus ligas.
CREATE TABLE IF NOT EXISTS liga (
    id         INT          NOT NULL AUTO_INCREMENT,
    carpeta_id INT          NOT NULL,
    nombre     VARCHAR(150) NOT NULL,
    url        VARCHAR(255) NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_liga_carpeta FOREIGN KEY (carpeta_id) REFERENCES carpeta (id) ON DELETE CASCADE
);

-- Notas privadas del abogado.
CREATE TABLE IF NOT EXISTS nota (
    id         INT          NOT NULL AUTO_INCREMENT,
    abogado_id INT          NOT NULL,
    titulo     VARCHAR(150) NOT NULL,
    contenido  TEXT         NULL,
    completada BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha      DATE         NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_nota_abogado FOREIGN KEY (abogado_id) REFERENCES abogado (id)
);


-- =====================================================================
-- Tablas de Luisa: mensaje, notificacion, evento, contacto
-- (los días festivos se calculan en calendario.js, no se guardan)
-- =====================================================================

-- Mensajes del chat. hilo identifica la conversación:
--   cliente-{clienteId}         abogado <-> cliente
--   admin-abogado-{abogadoId}   admin   <-> abogado
-- Los audios, imágenes y archivos se guardan en backend/uploads/mensajes/.
CREATE TABLE IF NOT EXISTS mensaje (
    id             INT          NOT NULL AUTO_INCREMENT,
    hilo           VARCHAR(40)  NOT NULL,
    remitente      ENUM('abogado', 'cliente', 'admin') NOT NULL,
    tipo           ENUM('texto', 'audio', 'img', 'file') NOT NULL DEFAULT 'texto',
    texto          TEXT         NULL,
    archivo_url    VARCHAR(255) NULL,
    nombre_archivo VARCHAR(150) NULL,
    fecha_hora     DATETIME     NOT NULL,
    leido          BOOLEAN      NOT NULL DEFAULT FALSE,

    PRIMARY KEY (id),
    INDEX idx_mensaje_hilo (hilo)
);

-- Notificaciones de la campana (admin y abogado).
CREATE TABLE IF NOT EXISTS notificacion (
    id         INT          NOT NULL AUTO_INCREMENT,
    usuario_id INT          NOT NULL,
    mensaje    VARCHAR(255) NOT NULL,
    leida      BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_hora DATETIME     NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_notificacion_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE
);

-- Eventos del calendario. alcance separa las agendas:
--   admin            agenda del administrador
--   abogado-{id}     agenda de cada abogado
-- color es uno de: #D4AF37, #27AE60, #2980B9, #C0392B, #8E44AD, #E67E22
CREATE TABLE IF NOT EXISTS evento (
    id          INT          NOT NULL AUTO_INCREMENT,
    titulo      VARCHAR(150) NOT NULL,
    fecha       DATE         NOT NULL,
    hora        TIME         NULL,
    lugar       VARCHAR(150) NULL,
    descripcion TEXT         NULL,
    color       VARCHAR(7)   NOT NULL DEFAULT '#D4AF37',
    alcance     VARCHAR(20)  NOT NULL,
    usuario_id  INT          NOT NULL,

    PRIMARY KEY (id),
    INDEX idx_evento_alcance_fecha (alcance, fecha),
    CONSTRAINT fk_evento_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE
);

-- Contactos agregados a mano. propietario_id es el id del usuario dueño.
CREATE TABLE IF NOT EXISTS contacto (
    id               INT          NOT NULL AUTO_INCREMENT,
    propietario_tipo ENUM('ADMIN', 'ABOGADO') NOT NULL,
    propietario_id   INT          NOT NULL,
    nombre           VARCHAR(100) NOT NULL,
    telefono         VARCHAR(20)  NULL,
    email            VARCHAR(100) NULL,
    notas            VARCHAR(255) NULL,

    PRIMARY KEY (id),
    INDEX idx_contacto_propietario (propietario_tipo, propietario_id)
);
