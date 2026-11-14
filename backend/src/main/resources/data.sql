-- =====================================================================
-- Juris Magna — Datos de prueba (los del DB del diseño original)
--
-- Se ejecuta cada vez que arranca el backend. Los INSERT IGNORE con id
-- fijo hacen que no se dupliquen los datos al reiniciar.
-- Cada integrante agrega sus datos en su propia sección.
-- =====================================================================


-- =====================================================================
-- Datos de Gustavo: usuario, abogado, movimiento
-- =====================================================================

-- Contraseñas encriptadas con BCrypt:
--   admin / admin123,  jramirez y smendoza / abogado123,
--   cvega, mtorres y rsilva / cliente123
INSERT IGNORE INTO usuario (id, username, password_hash, rol, activo, nombre, email, telefono, foto_url) VALUES
    (1, 'admin',    '$2a$10$L2EbzUFgh.fuBAPVxpDbqeyvz6gsgTtNHArjYbo5IL.mMr6KEtS.K', 'ADMIN',   TRUE, 'Administrador', NULL, NULL, NULL),
    (2, 'jramirez', '$2a$10$FRcyWtQjbB1Avww4vA7Yz.YOtIphl2Ed2rY59taPC71p9qEowD72S', 'ABOGADO', TRUE, NULL, NULL, NULL, NULL),
    (3, 'smendoza', '$2a$10$o.s2NnqkoDUURpId4Yc8muntKy6ZDm8ufkxAunVe5ugI22aGSs.zy', 'ABOGADO', TRUE, NULL, NULL, NULL, NULL),
    (4, 'cvega',    '$2a$10$iPT4wbfhhWEY5zLYgDxDpuTtTjSZwhBJe17K6sS/D6vOJUAbG8mxK', 'CLIENTE', TRUE, NULL, NULL, NULL, NULL),
    (5, 'mtorres',  '$2a$10$/9R6xJlbBS7xW2qvhZMlTuxchEkqw4AZc6rpspP8JW2X.teh.Hcae', 'CLIENTE', TRUE, NULL, NULL, NULL, NULL),
    (6, 'rsilva',   '$2a$10$JUFOimsnXrMszwfBBOZnnO5LOJHNJjzxlr1Hdj0IHFayRjI7VKNqe', 'CLIENTE', TRUE, NULL, NULL, NULL, NULL);

INSERT IGNORE INTO abogado (id, usuario_id, nombre, edad, especialidad, estudios, experiencia, cedula, email, telefono, foto_url, estado) VALUES
    (1, 2, 'Dr. Juan Ramírez',   45, 'Derecho Civil', 'LLM Harvard',    '20 años', '12345678', 'juan@jurismagna.mx',  '55 1234 5678', NULL, 'Activo'),
    (2, 3, 'Dra. Sofía Mendoza', 38, 'Derecho Penal', 'Doctorado UNAM', '12 años', '87654321', 'sofia@jurismagna.mx', '55 9876 5432', NULL, 'Activo');

-- Montos de las tablas de finanzas originales (febrero–abril),
-- movidos a agosto–octubre de 2026
INSERT IGNORE INTO movimiento (id, tipo, concepto, monto, fecha) VALUES
    -- Agosto
    (1,  'INGRESO', 'Honorarios',            480000.00, '2026-08-15'),
    (2,  'INGRESO', 'Consultas y asesorías', 120000.00, '2026-08-20'),
    (3,  'EGRESO',  'Nómina',                180000.00, '2026-08-30'),
    (4,  'EGRESO',  'Renta',                  45000.00, '2026-08-05'),
    (5,  'EGRESO',  'Servicios',              12000.00, '2026-08-25'),
    -- Septiembre
    (6,  'INGRESO', 'Honorarios',            580000.00, '2026-09-15'),
    (7,  'INGRESO', 'Consultas y asesorías', 110000.00, '2026-09-20'),
    (8,  'EGRESO',  'Nómina',                180000.00, '2026-09-30'),
    (9,  'EGRESO',  'Renta',                  45000.00, '2026-09-05'),
    (10, 'EGRESO',  'Servicios',              14200.00, '2026-09-25'),
    -- Octubre
    (11, 'INGRESO', 'Honorarios',            700000.00, '2026-10-15'),
    (12, 'INGRESO', 'Consultas y asesorías', 140000.00, '2026-10-20'),
    (13, 'INGRESO', 'Otros',                  40000.00, '2026-10-22'),
    (14, 'EGRESO',  'Nómina',                180000.00, '2026-10-30'),
    (15, 'EGRESO',  'Renta',                  45000.00, '2026-10-05'),
    (16, 'EGRESO',  'Servicios',              18000.00, '2026-10-25'),
    -- Retención fiscal del trimestre (se muestra con "−" en la tabla de ingresos)
    (17, 'EGRESO',  'Retención fiscal',      240000.00, '2026-10-31');
