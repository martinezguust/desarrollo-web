# Reporte de Avance del Proyecto

**Equipo:** Juris Magna  
**Integrante:** María Luisa García Sánchez  
**Fecha:** 16 de noviembre de 2026  
**Número de reporte:** 3 (Fase 3 — Backend completo)  
**Repositorio GitHub:** https://github.com/martinezguust/desarrollo-web

---

# 1. Actividades realizadas

- Incorporé el envío de notas de voz, imágenes y documentos en el chat. Los archivos se almacenan en el servidor y pueden consultarse y descargarse posteriormente.
- Implementé la operación para marcar como leídos los mensajes de una conversación, considerando únicamente los mensajes recibidos por el usuario que está leyendo.
- Agregué la eliminación de mensajes. Cuando un mensaje tiene un archivo adjunto, también se elimina el archivo correspondiente del servidor.
- Implementé la operación para marcar como leídas todas las notificaciones de un usuario y agregué la opción para eliminarlas.
- Agregué filtros para consultar los eventos del calendario por alcance, año y mes.
- Incorporé las operaciones para editar y eliminar eventos.
- Agregué filtros para consultar los contactos de acuerdo con el tipo y el identificador de su propietario.
- Incorporé las operaciones para editar y eliminar contactos.
- Agregué la validación del propietario de cada contacto, comprobando que el usuario exista y que corresponda al tipo indicado, ya sea administrador o abogado.
- Incorporé validaciones en los datos de eventos, contactos y notificaciones para evitar registros con información incorrecta o incompleta.
- Revisé el manejo de errores en las operaciones de mis módulos para proporcionar respuestas claras cuando se reciben datos incorrectos o se intenta acceder a registros inexistentes.
- Preparé y ejecuté las pruebas CURL de mis módulos, incluyendo casos de éxito y de error. También comprobé que los datos y archivos se conservaran después de reiniciar el backend.

---

# 2. Evidencias de trabajo

| Actividad | Integrante responsable | Enlace a commit |
|---|---|---|
| Incorporación de archivos en el chat, mensajes y notificaciones leídos, filtros de eventos y contactos, validación de propietarios, edición, eliminación y pruebas CURL. | María Luisa García Sánchez | [Ver commit](https://github.com/martinezguust/desarrollo-web/commit/f61259df43c5c58d0675c236fb3320187feacfd5) |
| Integración de la rama fase3-luisa a main. | Equipo Juris Magna | [Ver integración](https://github.com/martinezguust/desarrollo-web/commit/6f420e140438492349d36ebab92fa6c4616314dc) |

**Evidencias de pruebas (CURL):**

Estos son algunos de los comandos que utilicé para probar las rutas de mis módulos. Las pruebas completas se encuentran en mi sección del archivo `curl/pruebas.sh`.

**Enviar una nota de voz por el chat (200):**

```bash
curl -X POST http://localhost:8081/mensajes/archivos \
    -F "hilo=cliente-1" \
    -F "remitente=cliente" \
    -F "tipo=audio" \
    -F "archivo=@audio.webm"
```
Respuesta (parte):

```json
{
  "archivoUrl": "/uploads/mensajes/9-audio.webm",
  "nombreArchivo": "audio.webm",
  "tipo": "audio",
  "leido": false
}
```

**Enviar un archivo de texto como si fuera imagen (400):**

```bash
curl -X POST http://localhost:8081/mensajes/archivos \
    -F "hilo=cliente-1" \
    -F "remitente=cliente" \
    -F "tipo=img" \
    -F "archivo=@nota.txt"
```
Respuesta:

```json
{
  "error": "Tipo de archivo no permitido; se aceptan: jpg, jpeg, png, gif, webp"
}
```

**Consultar los eventos de un abogado únicamente en noviembre de 2026 (200):**

```bash
curl "http://localhost:8081/eventos?alcance=abogado-1&anio=2026&mes=11"
```

Respuesta: se obtiene la lista de eventos correspondientes a noviembre. La audiencia del 28 de noviembre aparece y el evento de diciembre no se incluye en los resultados.

**Registrar un contacto con un propietario que no corresponde al tipo indicado (400):**

```bash
curl -X POST http://localhost:8081/contactos \
    -H "Content-Type: application/json" \
    -d '{"propietarioTipo":"ABOGADO","propietarioId":1,"nombre":"Mal propietario"}'
```
Respuesta:

```json
{
  "error": "El usuario 1 no es ABOGADO"
}
```

**Ejecución de todas las pruebas:**

```bash
bash curl/pruebas.sh
```

Se cuenta con 61 pruebas de mis módulos, las cuales devolvieron los códigos de respuesta esperados, tanto en situaciones correctas como en casos de error.

---

# 3. Problemas encontrados

- **Nombre del archivo y mensaje incompleto:** para guardar un archivo adjunto se necesita primero registrar el mensaje, ya que el nombre del archivo utiliza el identificador del mensaje. El problema era que, si después ocurría un error al guardar el archivo, podía quedar registrado un mensaje sin su archivo. Se ajustó el proceso para que, si ocurre un error, no se conserve un registro incompleto.

- **Qué mensajes marcar como leídos:** al abrir una conversación no se deben marcar como leídos los mensajes que el propio usuario envió, sino únicamente los que recibió. Por eso se incorporó el identificador de quien está leyendo para marcar solamente los mensajes correspondientes.

- **Contactos con un propietario inválido:** se identificó que era necesario comprobar que el usuario relacionado con un contacto existiera y que su rol coincidiera con el tipo de propietario indicado. Se agregó esta validación antes de guardar el contacto.

- **Hora con formato incorrecto:** se contempló la validación del formato de hora HH:MM en los eventos. Cuando el formato no es correcto, el sistema rechaza el dato con un mensaje general de formato incorrecto. Aunque funciona, el mensaje podría mejorarse para indicar específicamente qué dato tiene el problema.

---

# 4. Avance funcional

- **Ya funciona:**
  - El chat permite enviar mensajes de texto, notas de voz, imágenes y documentos.
  - Los archivos adjuntos se almacenan en el servidor y pueden descargarse.
  - Los mensajes recibidos y las notificaciones se pueden marcar como leídos.
  - Se pueden eliminar mensajes, incluyendo sus archivos adjuntos, y eliminar notificaciones.
  - Los eventos cuentan con consulta, registro, filtros por alcance, año y mes, edición y eliminación.
  - Los contactos cuentan con consulta, registro, filtros por propietario, edición y eliminación.
  - Se valida que los propietarios de los contactos existan y correspondan con el tipo indicado.
  - Los datos de eventos, contactos y notificaciones cuentan con validaciones.
  - Las 61 pruebas CURL de mis módulos devuelven los códigos de respuesta esperados.
  - Se comprobó que los datos y archivos se conservan después de reiniciar el backend.

- **Pendiente para la Fase 4:**
  - Conectar al backend las siete páginas correspondientes a mis módulos, relacionadas con el cliente, los calendarios y los contactos.
  - Incorporar el chat en pantalla con envío de texto, notas de voz, imágenes y archivos.
  - Desarrollar el calendario mensual con días festivos y eventos.
  - Agregar la lectura en voz alta del comentario del abogado.

---

# 5. Uso de IA generativa

- **Herramienta utilizada:** Claude.
- **Actividad para la que se utilizó:** como apoyo para resolver dudas durante el desarrollo de los módulos de mensajes, notificaciones, eventos y contactos, comprender el funcionamiento de las operaciones y recibir orientación para preparar y revisar las pruebas CURL.
- **Código o contenido generado:** no se utilizó la herramienta para generar el código de los módulos. Se consultó para obtener explicaciones, ejemplos de funcionamiento y orientación sobre posibles errores durante el desarrollo.
- **Modificaciones realizadas por el equipo:** se revisaron y adaptaron las funcionalidades de acuerdo con los requerimientos del proyecto. Se ajustaron las validaciones de archivos, el marcado de mensajes como leídos, la comprobación de propietarios y el manejo de los registros al editar y eliminar información.
- **Forma en que se verificó el resultado:** mediante la compilación del proyecto, el levantamiento del backend conectado a MySQL sobre una base de datos limpia y la ejecución de las pruebas CURL. También revisé que los archivos se guardaran y eliminaran de la carpeta del servidor cuando correspondía, y reinicié el backend para comprobar que los datos y archivos se conservaran. Además, estoy estudiando el funcionamiento del código para poder explicarlo durante la defensa del proyecto.

---

# 6. Participación individual

- **María Luisa García Sánchez:** durante esta fase continué trabajando en los módulos de mensajes, notificaciones, eventos y contactos. Me encargué de incorporar el envío de notas de voz, imágenes y documentos en el chat, así como las operaciones para marcar mensajes y notificaciones como leídos y eliminarlos. También trabajé en los filtros, la edición y eliminación de eventos y contactos, y en las validaciones para comprobar que los propietarios de los contactos existieran y correspondieran con el tipo indicado. Finalmente, preparé y ejecuté las pruebas CURL de mis módulos para comprobar las respuestas de las rutas en situaciones correctas y de error, además de verificar que los datos y archivos se conservaran después de reiniciar el backend.

---

# 7. Próximas actividades

- Desarrollar el chat en pantalla con envío de mensajes de texto, notas de voz, imágenes y documentos.
- Implementar el calendario mensual con sus eventos y días festivos.
- Conectar al backend las páginas del cliente, los calendarios y los contactos.
- Incorporar la lectura en voz alta del comentario del abogado.
- Realizar pruebas de integración de las páginas con las rutas del backend.

---

## Observaciones adicionales

- Las notificaciones desarrolladas en mi módulo también son utilizadas por el módulo de Danna para avisar al administrador cuando se registra un cliente.
- Los archivos adjuntos de los mensajes se almacenan en la carpeta `uploads/mensajes/` del servidor.