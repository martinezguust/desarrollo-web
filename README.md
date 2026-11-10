# Juris Magna

Sistema de Gestión de Expedientes y Citas para un Bufete Jurídico (ficticio, con fines académicos).

Proyecto final de la materia **Desarrollo Web** (LISTI, agosto 2026 – enero 2027).

---

## 1. Descripción

Juris Magna nació como prototipo en la materia de Experiencia de Usuario: un solo `index.html` con HTML, CSS y JS juntos y los datos fijos en memoria. Ese prototipo se evaluó con usuarios (SUS y pruebas de usabilidad), por lo que **el diseño visual se conserva**.

En este proyecto se convierte en un sistema funcional de 3 capas, con backend, base de datos y código ordenado.

### Problema que resuelve

1. **Desorden documental:** los expedientes en carpetas físicas se pierden o se traspapelan.
2. **Incertidumbre del cliente:** el cliente no sabe cómo va su caso y llama constantemente al despacho.
3. **Gestión de citas ineficiente:** no hay una agenda estructurada.

### Roles

| Rol | Secciones |
|---|---|
| Administrador | Dashboard ejecutivo, Gestión de abogados, Finanzas (reporte PDF), Archivo histórico, Contactos, Calendario |
| Abogado | Mi dashboard, Mis clientes, Archivos y casos (drive), Mis notas, Contactos, Calendario |
| Cliente (vista móvil) | Mi caso (anillo de avance, semáforo, comentario con voz, documentos), Chat, Mi perfil |

### Arquitectura

```
Navegador (HTML + CSS + JS puros, API Fetch)
        │  http://localhost/          → archivos de frontend/
        │  http://localhost/api/...   → nginx quita /api
        ▼
nginx (puerto 80)
        │
        ▼
Spring Boot 17 (puerto 8081) — controlador → servicio → repositorio
        │  Spring Data JPA / Hibernate
        ▼
MySQL (base de datos jurismagna)
```

### Tecnologías

- **Frontend:** HTML5, CSS3 y JavaScript puro (sin frameworks), Chart.js y jsPDF.
- **Backend:** Java 17, Spring Boot, Spring Data JPA (Hibernate), Thymeleaf y BCrypt (`spring-security-crypto`).
- **Base de datos:** MySQL.
- **Servidor web:** nginx.
- **Entorno:** WSL, Git y VS Code.

---

## 2. Equipo

| Integrante | GitHub |
|---|---|
| Gustavo Adolfo Martínez Martínez | [@martinezguust](https://github.com/martinezguust) |
| Danna Gabriela García Castillo | [@dannacastilloo](https://github.com/dannacastilloo) |
| María Luisa García Sánchez | [@luisagsan32-uv](https://github.com/luisagsan32-uv) |

---

## 3. Reparto de responsabilidades

Cada integrante es dueña de sus módulos **completos**: HTML, JS, controlador, servicio, repositorio, modelo, tablas, pruebas CURL, su sección del README y sus pruebas unitarias.

| | Gustavo | Danna | Luisa |
|---|---|---|---|
| **Módulo** | Login, administración y finanzas | Módulo del abogado | Comunicación, agenda y vista del cliente |
| **Páginas** | `index.html`, `admin-dashboard.html`, `admin-abogados.html`, `admin-finanzas.html`, `admin-historico.html` | `abogado-dashboard.html`, `abogado-clientes.html`, `abogado-casos.html`, `abogado-notas.html` | `cliente-caso.html`, `cliente-chat.html`, `cliente-perfil.html`, `admin-calendario.html`, `abogado-calendario.html`, `admin-contactos.html`, `abogado-contactos.html` |
| **JS de página** | `login.js`, `admin-dashboard.js`, `admin-abogados.js`, `admin-finanzas.js`, `admin-historico.js` | `abogado-dashboard.js`, `abogado-clientes.js`, `abogado-casos.js`, `abogado-notas.js` | `cliente-caso.js`, `cliente-chat.js`, `cliente-perfil.js`, `admin-calendario.js`, `abogado-calendario.js`, `admin-contactos.js`, `abogado-contactos.js` |
| **JS compartido** | `api.js`, `sesion.js` | `utilidades.js`, `menu.js` | `chat.js`, `calendario.js` |
| **Backend** | Sesion, Usuario, Abogado, Dashboard, Finanza, `PlantillaControlador` + `inicio.html` (Thymeleaf) | Cliente, Caso, Carpeta, Nota | Mensaje, Notificacion, Evento, Contacto |
| **Modelos y repositorios** | Usuario, Abogado, Movimiento | Cliente, Caso, Carpeta, Liga, Documento, Nota | Mensaje, Notificacion, Evento, Contacto |
| **Tablas** | `usuario`, `abogado`, `movimiento` | `cliente`, `caso`, `carpeta`, `liga`, `documento`, `nota` | `mensaje`, `notificacion`, `evento`, `contacto` |
| **General** | Repositorio, proyecto Spring Boot, `estilos.css`, `nginx/jurismagna.conf`, instalación de nginx en el README | Instalación de MySQL y creación de la BD en el README | Cómo correr el backend, probar con CURL y ejecutar las pruebas en el README |

**Dependencias entre módulos:**
- El histórico (Gustavo) usa `/casos/historico` de Danna.
- El dashboard del abogado (Danna) usa `/dashboards/abogados/{id}` de Gustavo.
- La vista del cliente (Luisa) usa `/clientes/{id}` y `/clientes/{id}/documentos` de Danna, y `/abogados/{id}` de Gustavo.

**Archivos compartidos (partes iguales):** `README.md`, `database/schema.sql`, `data.sql` y `curl/pruebas.sh`. Cada quien agrega su parte en una sección con su nombre.

---

## 4. Fases y releases

| Fase | Fechas | Entregable | Release | Lo crea |
|---|---|---|---|---|
| 1. Análisis y diseño | 10–11 nov | Diseño inicial | `v0.1-diseno` | Gustavo |
| 2. Backend básico (POST y GET) | 12–14 nov | API funcional inicial | `v0.2-api-inicial` | Danna |
| 3. Backend completo (PUT, DELETE, filtros) | 15–17 nov | API REST completa | `v0.3-api-completa` | Luisa |
| 4. Desarrollo frontend | 18–20 nov | Sistema integrado | `v0.4-integrado` | Gustavo |
| 5. Pruebas y documentación | 21–22 nov | Versión candidata | `v1.0-candidata` | Luisa |
| 6. Evaluación | 23–25 nov | Entrega y defensa | — | — |

Cada fase se divide en una subfase por integrante y se trabaja en una rama por subfase (`fase1-gustavo`, `fase2-danna`, …).

---

## 5. Requerimientos

### Requerimientos funcionales — Gustavo

| ID | Requisito | Cómo se cumple |
|---|---|---|
| RF-01 | Inicio de sesión con tres roles: Administrador, Abogado y Cliente | `POST /sesiones` valida usuario, contraseña (BCrypt) y rol; `sesion.js` guarda la sesión en `sessionStorage` y cada página revisa el rol |
| RF-02 | El admin registra, edita y elimina abogados (foto, cédula, credenciales) | CRUD de `/abogados`, subida de foto (`POST /abogados/{id}/foto`) y cambio de contraseña (`PUT /abogados/{id}/password`) |
| RF-03 | Dashboard con KPIs: casos activos, ganados, perdidos, total de personal, honorarios | `GET /dashboards/admin` calcula los indicadores con datos reales de la BD |
| RF-04 | Exportar reporte financiero en PDF con un clic | Botón "Exportar Reporte PDF" que genera y descarga el PDF con jsPDF usando los datos de `GET /finanzas` |
| RF-05 | Archivo histórico de casos cerrados con filtro por estado y búsqueda por texto | `admin-historico.html` consume `GET /casos/historico?estado=&q=` |

<!-- Requerimientos de Danna (RF-06, RF-07, RF-10) -->

<!-- Requerimientos de Luisa (RF-08, RF-09, RF-11 a RF-14) -->

| ID | Requisito | Responsable |
|---|---|---|
| RF-15 | Modal de confirmación visual tras cada operación exitosa | Todas |

### Requerimientos no funcionales

| ID | Categoría | Requisito | Criterio de aceptación |
|---|---|---|---|
| RNF-01 | Usabilidad | Admin y abogado navegables sin capacitación previa | Se conserva el diseño validado (SUS ≥ 70) |
| RNF-02 | Accesibilidad | Vista del cliente usable en celulares de gama baja (≥ 4.5") | Compatible con Android |
| RNF-03 | Rendimiento | Carga rápida en conexión 3G | Menos de 4 s; peso menor a 500 KB sin contar librerías |
| RNF-04 | Compatibilidad | Funciona en Chrome, Firefox, Safari y Edge | Probado en los 4 navegadores |
| RNF-05 | Seguridad | Contraseñas encriptadas y acceso controlado por rol | BCrypt; cada página valida el rol con `sesion.js`; nunca se devuelve `password_hash` |
| RNF-06 | Portabilidad | Se usa desde el navegador sin instalar nada en el dispositivo | Acceso vía nginx |
| RNF-07 | Consistencia | Misma paleta, tipografía e iconografía en todas las páginas | Un solo `estilos.css` |
| RNF-08 | Retroalimentación | Toda operación muestra confirmación o error | Visible en menos de 500 ms |
| RNF-09 | Mantenibilidad | Código legible y ordenado por capas | Una instrucción por línea, sangría de 4 espacios, nombres en español |
| RNF-10 | Reproducibilidad | Se instala en otro WSL siguiendo solo el README | Probado con un clon limpio |

### Restricciones

| ID | Restricción |
|---|---|
| R-01 | Arquitectura de 3 capas: HTML/CSS/JS → Spring Boot → MySQL |
| R-02 | Backend en Java 17 con Spring Boot y Hibernate; frontend en HTML, CSS y JS puros (sin frameworks) |
| R-03 | Comunicación con API Fetch usando JSON y query string |
| R-04 | Desarrollo y ejecución en WSL, con Git y VS Code |
| R-05 | Repositorio en GitHub llamado `desarrollo-web`, con un release por fase |
| R-06 | Equipo de 3 integrantes, cada una con su cuenta de GitHub y commits propios |
| R-07 | El diseño visual del proyecto de UX no se modifica |
| R-08 | El uso de IA se documenta; las pruebas unitarias las hace cada integrante |
| R-09 | Entrega el 23 de noviembre de 2026 |
| R-10 | Thymeleaf en al menos una vista del backend |

---

## 6. Diseño de la base de datos

Base de datos **`jurismagna`**. Hibernate crea las tablas (`ddl-auto=update`). La estructura completa está en [`database/schema.sql`](database/schema.sql).

### Tablas de Gustavo

```
usuario 1 ──── 0..1 abogado        movimiento (independiente)
```

| Tabla | Campos | Notas |
|---|---|---|
| `usuario` | id, username (único), password_hash, rol (`ADMIN`, `ABOGADO`, `CLIENTE`), activo, nombre, email, telefono, foto_url | Cuentas de acceso de los tres roles. `nombre`, `email`, `telefono` y `foto_url` (opcionales) son para el perfil del administrador |
| `abogado` | id, usuario_id (FK única → `usuario`), nombre, edad, especialidad, estudios, experiencia, cedula, email, telefono, foto_url, estado (`Activo`/`Inactivo`) | Datos profesionales y de contacto del abogado |
| `movimiento` | id, tipo (`INGRESO`/`EGRESO`), concepto, monto (DECIMAL 12,2), fecha | Conceptos: Honorarios, Consultas y asesorías, Nómina, Renta, Servicios, Otros, Retención fiscal |

**Reglas de `movimiento`:**
- `monto` siempre es positivo; el `tipo` dice si suma o resta.
- "Retención fiscal" se guarda como `EGRESO`. En la pantalla aparece en la tabla de ingresos con signo "−" (como en el diseño original), cuenta en el neto y **no** entra en las gráficas de barras ni en la dona de gastos.
- Los datos de prueba reproducen las cifras del diseño original en agosto, septiembre y octubre de 2026.

<!-- Tablas de Danna -->

<!-- Tablas de Luisa -->
