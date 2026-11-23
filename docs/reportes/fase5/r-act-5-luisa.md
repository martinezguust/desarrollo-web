# Reporte de Avance del Proyecto

**Equipo:** Juris Magna  
**Integrante:** María Luisa García Sánchez  
**Fecha:** 22/11/2026  
**Número de reporte:** 5 (Fase 5 — Versión candidata)  
**Repositorio GitHub:** [https://github.com/martinezguust/desarrollo-web](https://github.com/martinezguust/desarrollo-web)  
**Release:** [https://github.com/martinezguust/desarrollo-web/releases/tag/V5](https://github.com/martinezguust/desarrollo-web/releases/tag/V5)

---

# 1. Actividades realizadas

- Probé nuevamente mi módulo de comunicación y agenda, incluyendo mensajes de texto, archivos, imágenes, notas de voz, notificaciones, eventos y contactos, tanto con las pruebas CURL como en el navegador mediante nginx.
- Corregí el problema de los datos que se acumulaban al ejecutar las pruebas. Actualicé el script `curl/pruebas.sh` para que eliminara los mensajes, archivos, notificaciones, eventos y contactos creados durante cada ejecución.
- Limpié de la base de datos local los registros que habían quedado de ejecuciones anteriores, conservando los datos originales del proyecto.
- Agregué al `README.md` la documentación de mi módulo, las instrucciones para ejecutar el backend, probar la API con CURL directamente y a través de nginx, y ejecutar las pruebas unitarias.
- Revisé por última vez las siete páginas de mi módulo y comprobé que cargaran correctamente. También envié un mensaje desde el chat del cliente y verifiqué que llegara a la conversación correspondiente.

---

# 2. Evidencias de trabajo

| Actividad | Integrante responsable | Enlace a commit |
|---|---|---|
| Revisión final de comunicación y agenda, limpieza de datos de prueba y documentación del módulo | Luisa (en pareja con Gustavo) | [Commit 53df907](https://github.com/martinezguust/desarrollo-web/commit/53df907329f41ed22b7a9a895a4717da0c450ccd) |
| Integración de la rama `fase5-luisa` a `main` | Gustavo | [Commit 4704081](https://github.com/martinezguust/desarrollo-web/commit/47040811d8649b8670c02f3f3470c22b459b1d61) |

**Archivos modificados:**

- `README.md`
- `backend/src/main/resources/data.sql`
- `curl/pruebas.sh`

**Pruebas realizadas:**

- Ejecuté las pruebas CURL mediante `bash curl/pruebas.sh` y obtuve **190 pruebas, con 0 fallas**, de las cuales 71 corresponden a mi módulo. La salida completa está en `evidencias/curl-luisa.txt`.
- Ejecuté las pruebas unitarias existentes con `cd backend && ./mvnw test` y obtuve el resultado **BUILD SUCCESS**. Mis pruebas unitarias específicas todavía están pendientes.
- Después de ejecutar las pruebas, comprobé que el hilo `cliente-1` conservara sus cuatro mensajes originales y que las consultas de contactos y eventos del alcance `abogado-1` devolvieran `[]`.

**Algunos resultados de mis pruebas CURL:**

| Prueba | Código esperado | Respuesta |
|---|---|---|
| Enviar una nota de voz | 200 | Se guarda el audio y se puede descargar. |
| Enviar un `.txt` como imagen | 400 | Se rechaza por tipo de archivo no permitido. |
| Consultar eventos de un abogado por mes | 200 | Se devuelven los eventos del mes solicitado. |
| Registrar un contacto con un usuario que no es abogado | 400 | Se rechaza el registro por tipo de usuario incorrecto. |

**Capturas de mis páginas:**

- `evidencias/luisa-cliente-caso.png`
- `evidencias/luisa-cliente-chat.png`
- `evidencias/luisa-cliente-perfil.png`
- `evidencias/luisa-admin-calendario.png`
- `evidencias/luisa-admin-contactos.png`
- `evidencias/luisa-abogado-calendario.png`
- `evidencias/luisa-abogado-contactos.png`

---

# 3. Problemas encontrados

- **Datos de prueba acumulados:** cada vez que ejecutaba las pruebas CURL quedaban mensajes, notificaciones, eventos y contactos guardados. Lo solucioné actualizando el script para eliminar los registros creados durante las pruebas.
- **Archivos que no se borraban:** las imágenes, notas de voz y documentos de prueba permanecían guardados en el servidor. Incorporé su eliminación junto con los mensajes correspondientes.

---

# 4. Avance funcional

- **Ya funciona:** el chat con mensajes de texto, notas de voz, imágenes y archivos; las notificaciones; los calendarios con días festivos y eventos; los contactos; y la vista del cliente con el avance de su asunto, los comentarios y el perfil de su abogado.
- **Comprobaciones realizadas:** revisé mis siete páginas mediante nginx, comprobé la comunicación con el backend y verifiqué que las pruebas CURL eliminaran los datos que creaban.
- **Pendiente:** terminar mis pruebas unitarias, comprobar el funcionamiento del proyecto en mi laptop siguiendo únicamente el README y preparar mi parte del video y del documento de la entrega final.

---

# 5. Uso de IA generativa

- **Herramienta utilizada:** Claude.
- **Actividad para la que se utilizó:** fue un apoyo para revisar mi módulo, corregir los detalles que salieron en las pruebas y redactar mi parte del README.
- **Código o contenido generado:** la limpieza de los datos de prueba y mi parte del README. Las pruebas unitarias no las hizo Claude: las hago yo con mi propia IA.
- **Modificaciones realizadas por el equipo:** decidimos que las pruebas CURL borren todo lo que crean, para que el chat y la agenda no se llenen de datos de prueba, y que el README explique cómo correr las pruebas.
- **Forma en que se verificó el resultado:** se corrieron las pruebas CURL (190 de 190) y se revisó que la base de datos quedara limpia; además revisé mis páginas en el navegador. Estoy estudiando el código para poder explicarlo en la defensa.

---

# 6. Participación individual

- **María Luisa García Sánchez:** durante esta fase revisé mi módulo de comunicación y agenda, corregí el problema de los datos que quedaban guardados después de las pruebas y documenté mi módulo en el README. También revisé con Gustavo que mis páginas funcionaran correctamente y comprobé los resultados de las pruebas.

---

# 7. Próximas actividades

- Terminar mis pruebas unitarias y subirlas al repositorio.
- Comprobar el funcionamiento del proyecto en mi laptop siguiendo las instrucciones del README.
- Continuar con las pruebas de integración.
- Preparar mi parte del video y del documento de la entrega final.
- Estudiar mis módulos para poder explicarlos durante la defensa.

---

## Observaciones adicionales

- Las pruebas CURL pasaron de 177 a 190, con 0 fallas; 71 corresponden a mi módulo.
- Mis cambios quedaron registrados en los commits `53df907` y `4704081`. El release de esta fase se identifica como **V5**.