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
