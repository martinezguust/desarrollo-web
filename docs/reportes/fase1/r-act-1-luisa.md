# Reporte de Avance del Proyecto

**Equipo:** Juris Magna  
**Integrante:** María Luisa García Sánchez  
**Fecha:** 10/11/2026  
**Número de reporte:** 1 (Fase 1 — Análisis y diseño)  
**Repositorio GitHub:** https://github.com/martinezguust/desarrollo-web

---

# 1. Actividades realizadas

Durante esta primera fase me enfoqué en analizar, diseñar y organizar las funcionalidades de los módulos que tengo asignados: mensajes, notificaciones, eventos y contactos.

Las actividades que realicé fueron:

* Definí los requerimientos correspondientes a mis módulos, identificados como RF-08, RF-09 y RF-11 a RF-14.
* Diseñé el funcionamiento general del módulo de mensajes y los tipos de conversaciones que se podrán realizar entre clientes, abogados y administrador.
* Planeé las opciones que tendrá el chat para enviar mensajes de texto, audios y archivos.
* Definí las funciones principales del módulo de notificaciones, considerando la consulta, el registro, la lectura y la eliminación de avisos.
* Diseñé la organización general de los calendarios del administrador y los abogados, contemplando la creación, modificación, consulta y eliminación de eventos.
* Planeé cómo se manejarán los contactos manuales y los que aparecerán automáticamente dependiendo del tipo de usuario.
* Definí las tablas de la base de datos que corresponderán a mis módulos: `mensaje`, `notificacion`, `evento` y `contacto`.
* Documenté la descripción de mis tablas en el README del proyecto.
* Organicé las rutas de la API que se necesitarán para la comunicación entre el frontend y el backend.
* Revisé y confirmé las reglas de funcionamiento relacionadas con los mensajes, eventos y contactos.

---

# 2. Evidencias de trabajo

| Actividad | Integrante responsable | Enlace a commit |
|---|---|---|
| Requerimientos RF-08, RF-09 y RF-11 a RF-14 | Luisa | [Ver commit](https://github.com/martinezguust/desarrollo-web/commit/b3d6cf1d554e15ddbdc842925cfa025247976bc0) |
| Tablas `mensaje`, `notificacion`, `evento` y `contacto` | Luisa | [Ver commit](https://github.com/martinezguust/desarrollo-web/commit/e82fab05ef65a91f0c380dbe4bce876c824a8278) |
| Descripción de mis tablas en el README | Luisa | [Ver commit](https://github.com/martinezguust/desarrollo-web/commit/79b39a5c727ce72929a82a230d5dd28e0436c4ff) |
| Rutas de la API de mensajes, notificaciones, eventos y contactos | Luisa | [Ver commit](https://github.com/martinezguust/desarrollo-web/commit/43752d5416465014a55996ae718cd02b7ee6f2d3) |
| Confirmación de las reglas de chat, eventos y contactos | Luisa | [Ver commit](https://github.com/martinezguust/desarrollo-web/commit/6ec022924a871daf902b705863e24a578f034877) |

**Evidencias visuales:**

<!-- Agregar aquí capturas de los commits en GitHub o de la documentación de los módulos. -->

---

# 3. Problemas encontrados

Durante el análisis y diseño de los módulos identifiqué algunos aspectos que fue necesario considerar para definir correctamente su funcionamiento:

* **Comunicación entre usuarios:** fue necesario distinguir los diferentes tipos de conversaciones permitidas entre clientes, abogados y administrador.
* **Envío de archivos y audios:** se consideraron los formatos permitidos y el límite de tamaño de 10 MB para los archivos.
* **Mensajes leídos:** se contempló la necesidad de identificar cuándo un mensaje ya fue leído por el usuario correspondiente.
* **Administración de eventos:** fue necesario definir qué información tendrá cada evento y cómo se organizarán los calendarios del administrador y los abogados.
* **Días festivos:** se estableció que los días festivos de México se mostrarán en los calendarios sin almacenarse como registros en la base de datos.
* **Contactos manuales y automáticos:** fue necesario diferenciar los contactos que se registrarán manualmente de los usuarios que aparecerán automáticamente según el tipo de usuario.

Estos aspectos se consideraron durante el diseño para evitar confusiones al momento de desarrollar los módulos.

---

# 4. Avance funcional

Durante esta primera fase me enfoqué en definir y documentar cómo estarán organizados los módulos que tengo asignados.

* **Mensajes:** se definieron los tipos de conversaciones y las opciones de comunicación mediante texto, audio y archivos.
* **Notificaciones:** se establecieron las funciones que permitirán consultar, registrar, marcar como leídas y eliminar notificaciones.
* **Eventos:** se definió la organización de los calendarios y las funciones para administrar eventos.
* **Contactos:** se estableció la diferencia entre los contactos manuales y los que aparecerán automáticamente.

**Estado actual:** los cuatro módulos se encuentran en la etapa de análisis y diseño. Se cuenta con la definición de sus requerimientos, las tablas de base de datos, las rutas de la API y las reglas generales. Todavía no se ha realizado la programación ni la implementación de sus funcionalidades.

---

# 5. Uso de IA generativa

* **Herramienta utilizada:** ChatGPT.
* **Actividad para la que se utilizó:** utilicé ChatGPT como apoyo para comprender los requerimientos del proyecto, organizar mis responsabilidades y estructurar las ideas relacionadas con el diseño de los módulos asignados.
* **Código o contenido generado:** no utilicé inteligencia artificial para generar, modificar, depurar ni probar código. Su uso se limitó a recibir orientación y apoyo para la organización y redacción de la documentación.
* **Modificaciones realizadas:** revisé y adapté la información para que correspondiera con los requerimientos de Juris Magna y las responsabilidades que tengo asignadas.
* **Forma en que se verificó el resultado:** comparé la información con la descripción del proyecto, los requerimientos establecidos y las indicaciones del profesor.

---

# 6. Participación individual

**María Luisa García Sánchez:**

Durante esta primera fase me encargué del análisis, diseño y organización de los módulos de mensajes, notificaciones, eventos y contactos. Definí los requerimientos correspondientes, las funciones principales que tendrá cada módulo, los tipos de usuarios que podrán utilizarlos y las reglas que deberán contemplarse para su funcionamiento. También documenté las tablas de la base de datos, agregué su descripción al README y organicé las rutas de la API necesarias para desarrollar e integrar estos módulos en las siguientes etapas del proyecto.

---

# 7. Próximas actividades

* Continuar con el desarrollo de los módulos que tengo asignados, siguiendo el diseño establecido.
* Elaborar las interfaces correspondientes a mensajes, notificaciones, eventos y contactos.
* Desarrollar las funcionalidades necesarias para cada módulo.
* Trabajar en la conexión de mis módulos con la base de datos y el backend.
* Revisar la integración de mis módulos con el resto del sistema.
* Realizar las pruebas correspondientes cuando las funcionalidades estén desarrolladas.

---

## Observaciones adicionales

Esta primera fase corresponde únicamente al análisis y diseño del proyecto. Las actividades realizadas se enfocaron en definir los requerimientos, las funcionalidades, la estructura de datos y las rutas necesarias para los módulos asignados. 