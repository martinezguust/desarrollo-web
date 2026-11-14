# Reporte de Avance del Proyecto

**Equipo:** Juris Magna  
**Integrante:** María Luisa García Sánchez  
**Fecha:** 13 de noviembre de 2026  
**Número de reporte:** 2 (Fase 2 — Backend básico)  
**Repositorio GitHub:** https://github.com/martinezguust/desarrollo-web

---

# 1. Actividades realizadas

- Creación de las entidades y repositorios de mensajes, notificaciones, eventos y contactos.
- Consulta y envío de mensajes de texto en las conversaciones del sistema: abogado con cliente y administrador con abogado. Se agregó la validación para comprobar que quien escribe pertenezca a la conversación correspondiente.
- Implementación de notificaciones automáticas al abogado cuando recibe un mensaje de su cliente o del administrador.
- Consulta y registro de notificaciones asociadas a cada usuario.
- Consulta y registro de eventos del calendario. Se agregaron validaciones para el color, la hora y el alcance de cada evento.
- Consulta y registro de contactos.
- Incorporación de datos de prueba: seis mensajes en las conversaciones de dos clientes y dos notificaciones para el administrador.
- Elaboración de casos de prueba mediante CURL para revisar las rutas de los cuatro módulos, incluyendo solicitudes correctas y situaciones de error.

---

# 2. Evidencias de trabajo

| Actividad | Integrante responsable | Enlace a commit |
|---|---|---|
| Entidades, repositorios y rutas de mensajes, notificaciones, eventos y contactos; corrección de zona horaria, datos de prueba y pruebas CURL. | María Luisa García Sánchez | [Ver commit](https://github.com/martinezguust/desarrollo-web/commit/cf18f3d67295583e44be2242300adc761eb96c56) |
| Integración de los cambios de la fase 2 a la rama principal. | María Luisa García Sánchez | [Ver commit](https://github.com/martinezguust/desarrollo-web/commit/c53315ececa9291b2332d5c10d3477a6638eb34a) |

---

# 3. Problemas encontrados

- **Horas corridas 6 horas (zona horaria):** se identificó que los mensajes de prueba presentaban un desfase de seis horas. La conexión a la base de datos estaba configurada con la zona horaria UTC, por lo que se modificó el parámetro `serverTimezone` a `America/Mexico_City` en el archivo `application.properties` para ajustar el manejo de las horas a la zona horaria de México.

- **Consultas por relación:** para las notificaciones y los eventos se contemplaron consultas relacionadas con el usuario correspondiente. Se revisó que estas consultas estuvieran correctamente vinculadas para evitar errores al recuperar la información.

---

# 4. Avance funcional

- **Ya está desarrollado:**
  - Las operaciones GET y POST de mensajes, notificaciones, eventos y contactos.
  - La validación de que el remitente pertenezca a la conversación al enviar un mensaje.
  - La generación automática de notificaciones para el abogado cuando recibe mensajes del cliente o del administrador.
  - Las validaciones de color, alcance y formato de hora para los eventos.
  - La incorporación de datos de prueba de mensajes y notificaciones.
  - Se cuenta con 19 casos de prueba definidos en el archivo `curl/pruebas.sh`, distribuidos entre los módulos de mensajes, notificaciones, eventos y contactos.

- **Pendiente para la Fase 3:**
  - Enviar notas de voz, imágenes y archivos por el chat.
  - Marcar mensajes y notificaciones como leídos.
  - Filtrar eventos por mes y contactos por propietario.
  - Editar y eliminar los registros de los módulos correspondientes.

---

# 5. Uso de IA generativa

- **Herramienta utilizada:** Claude.
- **Actividad para la que se utilizó:** resolver dudas durante el desarrollo de los módulos de mensajes, notificaciones, eventos y contactos, así como obtener orientación para preparar las pruebas CURL e identificar posibles causas de los errores encontrados.
- **Código o contenido generado:** orientación sobre el funcionamiento de las rutas GET y POST, ejemplos de solicitudes CURL y explicaciones sobre la configuración de la zona horaria. No se utilizó para generar el código de los módulos.
- **Modificaciones realizadas:** se realizaron ajustes en las consultas, validaciones y configuraciones de acuerdo con las necesidades de cada módulo y la estructura del proyecto.
- **Forma en que se verificó el resultado:** mediante la compilación del proyecto, el levantamiento del backend y la revisión de las respuestas obtenidas al realizar las pruebas CURL.

---

# 6. Participación individual

- **María Luisa García Sánchez:** durante esta fase me encargué del desarrollo de los módulos de mensajes, notificaciones, eventos y contactos. Trabajé en la creación de sus entidades, repositorios, servicios y controladores, así como en las operaciones para consultar y registrar información. También incorporé las validaciones necesarias, la generación automática de notificaciones y los datos de prueba. Además, revisé la configuración de la zona horaria y preparé los casos de prueba con CURL para comprobar el funcionamiento de las rutas.

---

# 7. Próximas actividades

- Probar la conexión a MySQL desde mi computadora.
- Incorporar el envío de notas de voz, imágenes y archivos en el chat.
- Agregar filtros para consultar mensajes, eventos y contactos.
- Implementar las operaciones para editar y eliminar los registros de mis módulos.
- Comprobar que los datos y archivos se conserven después de reiniciar el sistema.

---

## Observaciones adicionales

- La notificación al abogado se genera desde el propio módulo de mensajes. Esta funcionalidad puede aprovecharse en otros módulos para notificar al administrador cuando se agregue un cliente.
- Los días festivos del calendario no se guardan en la base de datos, ya que se contempla calcularlos y mostrarlos directamente en la página del calendario.