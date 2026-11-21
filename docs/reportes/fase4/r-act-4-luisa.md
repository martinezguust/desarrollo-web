# Reporte de Avance del Proyecto

**Equipo:** Juris Magna  
**Integrante:** María Luisa García Sánchez  
**Fecha:** 20/11/2026  
**Número de reporte:** 4 (Fase 4 — Desarrollo frontend)  
**Repositorio GitHub:** https://github.com/martinezguust/desarrollo-web
---

# 1. Actividades realizadas

- Integré el módulo de chat al frontend mediante el archivo `chat.js`, conectando las interfaces con los servicios del backend para consultar y enviar mensajes.
- Incorporé en la interfaz el envío de mensajes de texto, imágenes, documentos y notas de voz, así como la visualización de archivos adjuntos y un visor de imágenes.
- Agregué la actualización periódica de los mensajes cada cinco segundos mientras el chat permanece abierto, junto con la actualización del estado de lectura y la indicación visual de mensajes pendientes.
- Desarrollé el calendario mensual compartido mediante `calendario.js`, conectándolo con el backend para consultar y administrar eventos.
- Incorporé la visualización de días festivos mexicanos, además de las operaciones para agregar, editar y eliminar eventos y seleccionar sus colores.
- Conecté las páginas de contactos del administrador y del abogado con los servicios del backend. Para el administrador integré la consulta de abogados, el acceso al chat y la administración de sus contactos propios; para el abogado, la consulta de clientes y el acceso a los chats correspondientes.
- Conecté los calendarios del administrador y del abogado para mostrar y gestionar sus eventos.
- Desarrollé las páginas correspondientes a la vista del cliente: Mi Caso, Chat y Mi Perfil.
- En la página Mi Caso incorporé la visualización del avance del asunto, los datos del abogado asignado, el comentario mediante síntesis de voz y el acceso a los documentos de Drive.
- Realicé ajustes en los estilos del frontend para mejorar la visualización en pantallas pequeñas, incluyendo la adaptación de la vista móvil.

---

# 2. Evidencias de trabajo

| Actividad | Integrantes responsables | Evidencia |
|---|---|---|
| Desarrollo e integración de chat, calendario, contactos y vista del cliente | María Luisa García Sánchez y Gustavo Adolfo Martínez Martínez | [Commit 859971f](https://github.com/martinezguust/desarrollo-web/commit/859971f49c2f73fdbd52998817c29845ac438ee9) |
| Integración de la rama `fase4-luisa` a `main` | María Luisa García Sánchez y Gustavo Adolfo Martínez Martínez | [Commit d3716f0](https://github.com/martinezguust/desarrollo-web/commit/d3716f09ebfe9d7e3f686496ebdfcc5807b3d756) |

**Archivos principales trabajados:**

- `frontend/js/compartidos/chat.js`
- `frontend/js/compartidos/calendario.js`
- `frontend/js/admin/admin-contactos.js`
- `frontend/js/abogado/abogado-contactos.js`
- `frontend/js/cliente/cliente-caso.js`
- `frontend/css/estilos.css`

**Evidencias de pruebas:**

Como parte de las pruebas generales del equipo, se reportó la ejecución de `bash curl/pruebas.sh`, con 177 pruebas y sin fallas. También se reportaron pruebas en Chrome a través de nginx para revisar la carga de las páginas, las operaciones de creación, edición y eliminación de registros y la presentación de errores del backend.

Las notas de voz quedaron pendientes de una comprobación con un micrófono real.

---

# 3. Problemas encontrados

- Al ingresar una hora con un formato incorrecto, por ejemplo `25:99`, se mostraba un mensaje genérico relacionado con un error en el JSON. Se incorporó una validación para mostrar un mensaje más claro: "La hora debe tener el formato HH:MM".
- Los contactos propios del administrador no cuentan con una función de chat, debido a que el backend contempla los hilos de comunicación entre clientes y abogados y entre administrador y abogados, conforme al alcance establecido para el sistema.

---

# 4. Avance funcional

Durante esta fase avancé en la integración del frontend con los servicios del backend, incorporando las interfaces correspondientes a los módulos de comunicación, calendario, contactos y vista del cliente.

Entre los avances se encuentran:

- Interfaz de chat conectada con el backend para el envío y consulta de mensajes y archivos.
- Actualización periódica de mensajes y estados de lectura.
- Calendarios mensuales con consulta y administración de eventos y visualización de días festivos mexicanos.
- Páginas de contactos del administrador y del abogado conectadas con sus respectivos servicios.
- Vista del cliente con consulta de su caso, avance, abogado asignado, chat y perfil.
- Adaptaciones de la interfaz para mejorar su visualización en dispositivos móviles.

**Pendientes para la siguiente fase:**

- Comprobar el funcionamiento de las notas de voz utilizando un micrófono real.
- Realizar las pruebas unitarias correspondientes.
- Completar la documentación de mi parte en el README.
- Continuar con las pruebas de integración y preparar las evidencias para la entrega final.

---

# 5. Uso de IA generativa

- **Herramienta utilizada:** ChatGPT.
- **Actividad para la que se utilizó:** Como apoyo para resolver dudas durante el desarrollo frontend, comprender el funcionamiento de las operaciones de JavaScript, revisar la conexión con los servicios del backend y orientar las pruebas de las interfaces.
- **Código o contenido generado:** No se utilizó IA generativa para generar el código del proyecto.
- **Modificaciones realizadas:** Revisé el comportamiento de las interfaces y las validaciones para identificar problemas, mejorar los mensajes de error y comprobar la comunicación con el backend.
- **Forma en que se verificó el resultado:** Mediante la revisión de los archivos del proyecto, la comprobación de las operaciones implementadas y las pruebas de integración reportadas por el equipo.

---

# 6. Participación individual

**María Luisa García Sánchez:** Durante esta fase trabajé en colaboración con Gustavo en el desarrollo e integración de los módulos de comunicación, calendario, contactos y vista del cliente. Participé en el desarrollo de las funcionalidades compartidas de chat y calendario, la conexión de las páginas del administrador y del abogado con los servicios correspondientes y el desarrollo de las páginas del cliente para consultar su caso, comunicarse mediante el chat y revisar su perfil. También participé en la integración de los cambios a la rama principal y en los ajustes de las interfaces y validaciones para mejorar su funcionamiento y presentación.

---

# 7. Próximas actividades

- Realizar las pruebas unitarias de los módulos correspondientes.
- Completar mi sección del README con la documentación necesaria.
- Comprobar las notas de voz utilizando un micrófono real.
- Preparar las evidencias de funcionamiento e integración para la entrega final.

---

**Observaciones adicionales**

El módulo de chat consulta los mensajes nuevos cada cinco segundos mientras permanece abierto, permitiendo mantener actualizada la conversación y el estado de lectura de los mensajes.