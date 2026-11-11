# Reporte de Avance del Proyecto

**Equipo:** Juris Magna
**Integrante:** Gustavo Adolfo Martínez Martínez
**Fecha:** 10/11/2026
**Número de reporte:** 1 (Fase 1 — Análisis y diseño)
**Repositorio GitHub:** https://github.com/martinezguust/desarrollo-web
**Release:** https://github.com/martinezguust/desarrollo-web/releases/tag/V1

---

# 1. Actividades realizadas

En esta primera fase me encargué de preparar la base del proyecto para que el equipo pudiera empezar a trabajar:

- Creé el repositorio en GitHub, agregué el webhook del profesor e invité a Danna y a Luisa como colaboradoras.
- Organicé las carpetas del proyecto (frontend, backend, base de datos, nginx, pruebas y documentación).
- Agregué el logo del sistema como imagen dentro del proyecto.
- Escribí la primera versión del README: qué es el sistema, qué problema resuelve, quiénes lo usan, cómo está organizado, quiénes formamos el equipo y cómo nos repartimos el trabajo.
- Definí mis requerimientos (RF-01 a RF-05), así como los requerimientos no funcionales y las restricciones del proyecto.
- Diseñé mis tablas de la base de datos: `usuario`, `abogado` y `movimiento`.
- Definí las rutas de la API que me corresponden: inicio de sesión, abogados, dashboard y finanzas.
- Al final, junté el trabajo de las tres en la rama principal y preparé las carpetas para guardar los reportes de cada fase.

---

# 2. Evidencias de trabajo

| Actividad | Integrante responsable | Enlace a commit |
|------------|------------------------|-----------------|
| Estructura inicial del repositorio | Gustavo | https://github.com/martinezguust/desarrollo-web/commit/7d939e54bd1c9a086a3eef405916f66eefa3cf11 |
| Descripción del proyecto, equipo y reparto | Gustavo | https://github.com/martinezguust/desarrollo-web/commit/b9b0eb51f86c6fb3d5c2ac4b57922ce162f174fe |
| Requerimientos RF-01 a RF-05, no funcionales y restricciones | Gustavo | https://github.com/martinezguust/desarrollo-web/commit/81ce6d9f9ea9a55d0055c10a0ccb04f00fe126fc |
| Tablas usuario, abogado y movimiento | Gustavo | https://github.com/martinezguust/desarrollo-web/commit/29fc7e979e5472b5f72f24470179d6980456ac5f |
| Descripción de mis tablas en el README | Gustavo | https://github.com/martinezguust/desarrollo-web/commit/3c3105b6d902331818cfbb765439396d6621e19a |
| Rutas de la API | Gustavo | https://github.com/martinezguust/desarrollo-web/commit/47694fd6b4517e2cfdda22bb8212cd4dd627a625 |
| Rutas del perfil del administrador | Gustavo | https://github.com/martinezguust/desarrollo-web/commit/7d5e4d3 |
| Corrección de una sección repetida en el README | Gustavo | https://github.com/martinezguust/desarrollo-web/commit/f8fea69 |
| Ajuste de archivos que no se suben al repositorio | Gustavo | https://github.com/martinezguust/desarrollo-web/commit/b2e5b53 |
| Carpetas para los reportes de avance | Gustavo | https://github.com/martinezguust/desarrollo-web/commit/1d89642 |

**Evidencias visuales:**

<!-- Pega aquí 1 o 2 capturas, por ejemplo:
![Repositorio en GitHub](captura-repo.png)
![Ramas de la fase 1](captura-ramas.png) -->

---

# 3. Problemas encontrados

- **Los números de finanzas de la maqueta no coincidían.** Las tarjetas de resumen mostraban cantidades distintas a las de las tablas. Decidimos tomar como correctas las tablas, porque son las que coinciden con las gráficas, y que las tarjetas se calculen con los datos reales.
- **Los datos de ejemplo eran de meses pasados.** Como el sistema muestra los últimos tres meses, la pantalla de finanzas iba a salir vacía. Cambiamos las fechas a agosto–octubre de 2026, con las mismas cantidades.
- **Faltaban algunos conceptos de dinero.** Al revisar las pantallas de finanzas vimos que se necesitaban "Consultas y asesorías" y "Retención fiscal", así que los agregamos al diseño.
- **Mostrar contraseñas en los perfiles.** Como las contraseñas se guardan cifradas, ya no se pueden mostrar; en los perfiles se verán ocultas.
- **El perfil del administrador no tenía dónde guardarse.** Agregamos los datos necesarios y las rutas para ver y editar ese perfil.
- **Dificultades con Git y GitHub.** Al principio tuve que configurar mi usuario de git y crear un token para poder subir los cambios. También hubo un momento en que no podía juntar las ramas porque tenía un cambio pendiente sin guardar; lo revisé, lo guardé y después ya se pudo.

---

# 4. Avance funcional

- **Listo:** el repositorio con su estructura, el README con el diseño completo de las tres integrantes, el diseño de la base de datos y el release V1.
- **Pendiente:** todavía no hay partes del sistema que se puedan ejecutar; eso empieza en la Fase 2. También falta probar el diseño de la base de datos directamente en MySQL.

---

# 5. Uso de IA generativa

- **Herramienta utilizada:** Claude.
- **Actividad para la que se utilizó:** analizar las pantallas del sistema, redactar el README y diseñar las tablas y las rutas de la API.
- **Código o contenido generado:** el README, el archivo de la base de datos y la estructura del repositorio.
- **Modificaciones realizadas por el equipo:** las decisiones del diseño las tomamos nosotros; por ejemplo, calcular los indicadores con datos reales y quitar la tabla de documentos para usar el enlace a Drive.
- **Forma en que se verificó el resultado:** revisamos cada cambio antes de guardarlo y comprobamos en GitHub que todo quedara correcto.

---

# 6. Participación individual

- **Gustavo Adolfo Martínez Martínez:** Me encargué de dejar listo el repositorio para que el equipo pudiera trabajar. Diseñé mi parte del sistema (inicio de sesión, administración y finanzas) y apoyé a Danna y a Luisa con el diseño de sus partes. Al final junté el trabajo de las tres y resolví los problemas que fueron saliendo con Git.

---

# 7. Próximas actividades

- Crear el proyecto en Spring Boot y conectarlo con MySQL.
- Programar las clases de usuario, abogado y movimiento.
- Hacer que funcionen el inicio de sesión y las rutas para consultar y registrar abogados y movimientos.
- Probar todo con CURL.

---

## Observaciones adicionales

- El cálculo de efectividad de los abogados se mantiene igual en todas las pantallas para que los números siempre coincidan.
- A partir de la Fase 2 haremos un solo commit por subfase, con un resumen de los cambios.