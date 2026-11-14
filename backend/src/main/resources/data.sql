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


-- =====================================================================
-- Datos de Danna: cliente, caso, carpeta, nota
-- (los usuarios de los clientes son los id 4, 5 y 6 de la tabla usuario)
-- =====================================================================

INSERT IGNORE INTO cliente (id, usuario_id, abogado_id, nombre, edad, estudios, ine, email, telefono, descripcion, avance, honorarios, drive_url, comentario, estado) VALUES
    (1, 4, 1, 'Carlos Vega',   34, 'Ingeniería',     'VECC840312', 'carlos@mail.com',  '55 1111 2222', 'Disputa de propiedad inmueble', 65, 45000.00, '',
        'Hola Carlos, tu caso avanza bien. La audiencia quedó fijada para el 28 de mayo a las 10am en el Juzgado 5. Necesito que revises y firmes los documentos a la brevedad.', 'Activo'),
    (2, 5, 1, 'María Torres',  42, 'Contaduría',     'TOMM810415', 'maria@mail.com',   '55 3333 4444', 'Divorcio y custodia',           30, 28000.00, '', '', 'Activo'),
    (3, 6, 2, 'Roberto Silva', 51, 'Administración', 'SIRR720820', 'roberto@mail.com', '55 5555 6666', 'Fraude empresarial',            80, 95000.00, '', '', 'Activo');

INSERT IGNORE INTO caso (id, numero_expediente, cliente_id, abogado_id, materia, estado, fecha_inicio, fecha_fin, drive_url) VALUES
    (1, 'EXP-0001', 1, 1, 'Civil',     'Activo',    '2025-03-15', NULL,         ''),
    (2, 'EXP-0002', 2, 1, 'Familiar',  'EnProceso', '2025-04-15', NULL,         ''),
    (3, 'EXP-0003', 1, 1, 'Mercantil', 'Ganado',    '2025-01-15', '2025-05-10', 'https://drive.google.com/caso3'),
    (4, 'EXP-0004', 3, 2, 'Penal',     'Activo',    '2025-05-01', NULL,         ''),
    (5, 'EXP-0005', 3, 2, 'Penal',     'Perdido',   '2024-11-15', '2025-04-20', 'https://drive.google.com/caso5');

INSERT IGNORE INTO carpeta (id, caso_id, numero, materia, proyecto, estado, liga_url, fecha) VALUES
    (1, 1, '001', 'Civil', 'Disputa Inmueble', 'Activo', 'https://drive.google.com', '2025-03-16');


-- =====================================================================
-- Datos de Luisa: mensaje, notificacion
-- (en el diseño original los eventos y los contactos empiezan vacíos)
-- =====================================================================

-- Conversaciones de ejemplo de Carlos Vega (cliente-1) y María Torres (cliente-2)
INSERT IGNORE INTO mensaje (id, hilo, remitente, tipo, texto, archivo_url, nombre_archivo, fecha_hora, leido) VALUES
    (1, 'cliente-1', 'cliente', 'texto', 'Buenos días, ¿hay novedades de mi caso?',                              NULL, NULL, '2026-11-10 09:00:00', TRUE),
    (2, 'cliente-1', 'abogado', 'texto', 'Hola Carlos, la audiencia está fijada para el 28 de mayo a las 10am.', NULL, NULL, '2026-11-10 09:05:00', TRUE),
    (3, 'cliente-1', 'cliente', 'texto', 'Entendido, ahí estaré. ¿Qué documentos necesito?',                     NULL, NULL, '2026-11-10 09:08:00', TRUE),
    (4, 'cliente-1', 'abogado', 'texto', 'El contrato de arrendamiento y el acta notarial.',                     NULL, NULL, '2026-11-10 09:10:00', TRUE),
    (5, 'cliente-2', 'cliente', 'texto', '¿Cuándo será la próxima cita?',                                        NULL, NULL, '2026-11-10 10:00:00', TRUE),
    (6, 'cliente-2', 'abogado', 'texto', 'María, te agendaré la semana próxima.',                                NULL, NULL, '2026-11-10 10:15:00', TRUE);

-- Notificaciones de la campana del admin (usuario 1)
INSERT IGNORE INTO notificacion (id, usuario_id, mensaje, leida, fecha_hora) VALUES
    (1, 1, 'Dr. Juan Ramírez agregó a Carlos Vega',  FALSE, '2026-11-10 09:00:00'),
    (2, 1, 'Dr. Juan Ramírez agregó a María Torres', FALSE, '2026-11-10 09:05:00');
