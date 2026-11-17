# Reporte de Avance del Proyecto

**Equipo:** Juris Magna
**Integrante:** Danna Gabriela Garcia Castillo
**Fecha:** 16/11/2026
**Número de reporte:** 3 (Fase 3 — Backend completo)
**Repositorio GitHub:** https://github.com/martinezguust/desarrollo-web
**Release:** https://github.com/martinezguust/desarrollo-web/releases/tag/V3

# 1. Actividades realizadas

- Programé la edición y eliminación de clientes. Al editar un cliente, si la contraseña llega vacía se conserva la anterior. No se puede eliminar un cliente que todavía tiene casos.
- Programé la edición y eliminación de casos. Al cerrar un caso (Ganado, Perdido o Cancelado) se llena sola la fecha de fin, y al reabrirlo se quita.
- Programé la edición y elieminación de carpetas y notas, y la eliminación de ligas. Al borrar un caso se borran sus carpetas, y al borrar una carpeta se borran sus ligas
- Agregué filtros: los clientes se pueden buscar por abogado y por nombre, y los casos por abogado, cliente, materia, estado o texto.
- Hice el archivo histórico, que muestra los casos cerrados del más reciente al más antiguo y se puede filtrar por estado y buscar por texto
- Cuando un abogado registra un cliente, el administrador recibe un aviso en sus notificaciones.
- Agregué validaciones a los datos de clientes, casos, ligas y notas, con mensajes claros.
- Probé todas mis rutas con CURL, tanto en casos de éxito como de error, y comprobé que los datos se conservan después de reiniciar,

# 2. Evidencias de trabajo

| Actividad | Integrante responsable | Enlace a commit |
|------------|------------------------|-----------------|
| Edición y eliminación de clientes, casos, carpetas, ligas y notas; filtros; histórico; aviso al administrador; validaciones y pruebas CURL | Danna (en pareja con Gustavo) | https://github.com/martinezguust/desarrollo-web/commit/8b3678f97bbb2f1b8276111bd1f722e59f6e08d0 |
| Integración de la rama fase3-danna a main | Danna (en pareja con Gustavo) | https://github.com/martinezguust/desarrollo-web/commit/eba7cb27842058ec5c3e60e8d0702fbec28b38f0 |

**Evidencias de pruebas (CURL):**

Estos son algunos de los comandos que usé para probar mis rutas. Las pruebas completas están en mi sección de `curl/pruebas.sh`.

Cerrar un caso como Ganado; la fecha de fin se llena sola (200):
```bash
curl -X PUT http://localhost:8081/casos/6 \
    -H "Content-Type: application/json" \
    -d '{"clienteId": 4, "abogadoId": 1, "materia": "Inmobiliario", "estado": "Ganado", "driveUrl": "https://drive.google.com/laura-caso"}'
```
Respuesta (parte): `{"clienteNombre":"Laura Prueba","estado":"Ganado","fechaFin":"2026-11-16","numeroExpediente":"EXP-0006",...}`

Archivo histórico filtrado por estado y búsqueda (200):
```bash
curl "http://localhost:8081/casos/historico?estado=Ganado&q=laura"
```
Respuesta: la lista con el caso EXP-0006 de Laura Prueba.

Eliminar un cliente que todavía tiene casos (409):
```bash
curl -X DELETE http://localhost:8081/clientes/4
```
Respuesta: `{"error":"No se puede eliminar: el cliente todavía tiene casos"}`

Editar un cliente con honorarios negativos (400):
```bash
curl -X PUT http://localhost:8081/clientes/4 \
    -H "Content-Type: application/json" \
    -d '{"abogadoId": 1, "nombre": "Laura Prueba", "honorarios": -5}'
```
Respuesta: `{"error":"Los honorarios no pueden ser negativos"}`

Para ejecutar todas: `bash curl/pruebas.sh`. Mis 63 pruebas devolvieron el código esperado.

# 3. Problemas encontrados

- **Fecha de fin al reabrir un caso:** si un caso cerrado se volvía a abrir, conservaba su fecha de fin y aparecía como cerrado aunque ya no lo estuviera. Decidimos que, al reabrirlo, la fecha de fin se quite.
- **Borrado en cascada:** al eliminar un caso o una carpeta había que asegurarse de que no quedaran carpetas o ligas sueltas en la base de datos. Además de probarlo con CURL, revisamos directamente las tablas y no quedó ningún registro sin su caso o su carpeta.
- **Aviso al administrador:** para enviarle el aviso hacía falta encontrar a los usuarios administradores, y esa consulta va en el repositorio de usuarios, que es de Gustavo. Lo acordamos con él y quedó anotado en la descripción del commit,
- **Editar sin cambiar la contraseña:** al editar los datos de un cliente no se debe perder su contraseña. Hice que una contraseña vacía conserve la anterior y lo comprobé iniciando sesión después de editar

# 4. Avance funcional

- **Ya funciona:**
  - Clientes, casos, carpetas, ligas y notas tienen consulta, registro, edición y eliminación.
  - Los filtros de clientes y casos y el archivo histórico dan los resultados esperados.
  - El administrador recibe el aviso cuando se registra un cliente.
  - Las 63 pruebas CURL de mi módulo dan el código esperado, y los datos se conservan al reiniciar.
- **Pendiente para la Fase 4:**
  - Las cuatro páginas del abogado conectadas al backend.
  - Las funciones compartidas de la interfaz (mensajes de éxito y error, menú lateral y notificaciones).

# 5. Uso de IA generativa

- **Herramienta utilizada:** Claude
- **Actividad para la que se utilizó:** fue un apoyo que nos ayudó a programar el backend de mi módulo y a preparar las pruebas CURL.
- **Código o contenido generado:** propuestas de código para la edición y eliminación de clientes, casos, carpetas, ligas y notas, los filtros, el archivo histórico, el aviso al administrador, las validaciones y las pruebas CURL. Yo le indicaba qué necesitaba cada parte y revisaba lo que proponía antes de aceptarlo.
- **Forma en que se verificó el resultado:** compilamos el proyecto, levantamos el backend conectado a MySQL sobre una base de datos limpia y ejecuté todas las pruebas CURL. Revisé en la base de datos que el borrado en cascada funcionara y reinicié el backend para comprobar que los datos se conservaban. Estoy estudiando el código para poder explicarlo en la defensa.

# 6. Próximas actividades

- Hacer las funciones compartidas de la interfaz: mensajes de éxito y error, ventanas emergentes, menú lateral y notificaciones.
- Conectar al backend las páginas de clientes, casos, carpetas y notas del abogado.
- Conectar el dashboard del abogado con sus indicadores
- Mostrar en pantalla los mensajes de error y de confirmación

## Observaciones adicionales

- Decidimos que un cliente marcado como Inactivo ya no pueda iniciar sesión.
- El aviso al administrador usa la función de notificaciones del módulo de Luisa.
- El release de esta fase se llama **V3**.
