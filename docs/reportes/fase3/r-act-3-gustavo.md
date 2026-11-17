# Reporte de Avance del Proyecto

**Equipo:** Juris Magna
**Integrante:** Gustavo Adolfo Martínez Martínez
**Fecha:** 16/11/2026
**Número de reporte:** 3 (Fase 3 — Backend completo)
**Repositorio GitHub:** https://github.com/martinezguust/desarrollo-web
**Release:** https://github.com/martinezguust/desarrollo-web/releases/tag/V3

---

# 1. Actividades realizadas

- Hice que todos los errores del sistema respondan con el mismo formato: un mensaje claro en español y el código correcto (datos inválidos, sin permiso, no encontrado o en conflicto). Danna y Luisa también usan este formato en sus módulos.
- Agregué validaciones a los datos de abogados y movimientos, para que el sistema avise qué falta o qué está mal antes de guardar.
- Hice la página de estado del backend con Thymeleaf. Muestra que el backend está funcionando, cuántos abogados, clientes, casos activos y mensajes hay, y la lista de todas las rutas de la API.
- Programé la edición y eliminación de abogados, el cambio de contraseña y la subida de su foto. No se puede eliminar a un abogado que todavía tiene clientes o casos.
- Programé el perfil del administrador: consultar y editar sus datos, cambiar su contraseña y subir su foto.
- Programé la edición y eliminación de movimientos financieros, y la consulta de los movimientos de un mes.
- Hice el reporte de finanzas de los últimos meses: ingresos, egresos, gastos por concepto, neto, fondos disponibles y rentabilidad.
- Hice los indicadores del dashboard del administrador y del dashboard de cada abogado, calculados con los datos reales.
- Corregí el inicio de sesión del cliente para que muestre su nombre real.
- Reorganicé las pruebas CURL: ahora cada prueba revisa que el código de respuesta sea el esperado y al final aparece un resumen de cuántas pasaron.
- Comprobé que los datos se conservan después de reiniciar el backend.

---

# 2. Evidencias de trabajo

| Actividad | Integrante responsable | Enlace a commit |
|------------|------------------------|-----------------|
| Formato común de errores, validaciones, página de estado, edición y eliminación de abogados y movimientos, perfil del administrador, finanzas, dashboards y pruebas CURL | Gustavo | https://github.com/martinezguust/desarrollo-web/commit/c7713e1dae71c75f0841b71531cc76a2d4bf7b1a |
| Integración de la rama fase3-gustavo a main | Gustavo | https://github.com/martinezguust/desarrollo-web/commit/c6aa68a2018408be4f8a7e1bb902cbb538c8ec10 |

**Evidencias de pruebas (CURL):**

Estos son algunos de los comandos que usé para probar mis rutas. Las pruebas completas están en mi sección de `curl/pruebas.sh`.

Inicio de sesión de un cliente, que ahora devuelve su nombre real (200):
```bash
curl -X POST http://localhost:8081/sesiones \
    -H "Content-Type: application/json" \
    -d '{"username": "cvega", "password": "cliente123", "rol": "CLIENTE"}'
```
Respuesta: `{"usuarioId":4,"rol":"CLIENTE","nombre":"Carlos Vega","perfilId":1}`

Indicadores del dashboard del administrador (200):
```bash
curl http://localhost:8081/dashboards/admin
```
Respuesta (inicio): `{"personal":5,"abogados":2,"casos":{"total":5,"activos":2,"enProceso":1,"ganados":1,"perdidos":1},"efectividadCerrados":50,...}`

Eliminar un abogado que todavía tiene clientes y casos (409):
```bash
curl -X DELETE http://localhost:8081/abogados/1
```
Respuesta: `{"error":"No se puede eliminar: el abogado todavía tiene clientes o casos asignados"}`

Reporte de finanzas con un número de meses fuera de rango (400):
```bash
curl "http://localhost:8081/finanzas?meses=13"
```
Respuesta: `{"error":"El parámetro meses debe estar entre 1 y 12"}`

Para ejecutar todas: `bash curl/pruebas.sh`. Mis 53 pruebas devolvieron el código esperado.

---

# 3. Problemas encontrados

- **Datos que dejaban las pruebas anteriores:** al repetir las pruebas aparecía "usuario repetido", y los indicadores del dashboard no coincidían con los esperados porque se sumaban los registros creados en otras corridas. Reorganicé las pruebas para que cada una cree, edite y borre sus propios datos, y antes de cada corrida dejé la base de datos limpia.
- **Revisar los resultados uno por uno:** con tantas pruebas era fácil pasar por alto un código incorrecto. Agregué al archivo de pruebas una función que compara cada respuesta con el código esperado y al final muestra cuántas fallaron.
- **Errores con formatos distintos:** algunos errores, como una ruta que no existe o un método no permitido, respondían con un formato diferente y en inglés. Los incluí en el manejo común para que todos respondan igual y en español.
- **Consultas en el módulo de Danna:** para el inicio de sesión del cliente y los indicadores necesité dos consultas nuevas en los repositorios de clientes y casos, que son de Danna. Lo acordamos y quedó anotado en la descripción del commit.

---

# 4. Avance funcional

- **Ya funciona:**
  - Todos los errores del sistema responden con el mismo formato y un mensaje claro.
  - La página de estado del backend muestra los conteos reales y las 58 rutas de la API.
  - Abogados, perfil del administrador y movimientos tienen consulta, registro, edición y eliminación.
  - El reporte de finanzas y los indicadores de los dashboards dan las cifras esperadas.
  - Las 53 pruebas CURL de mi módulo dan el código esperado, y los datos se conservan al reiniciar.
- **Pendiente para la Fase 4:**
  - Las páginas del inicio de sesión y del administrador conectadas al backend.
  - El reporte de finanzas en PDF con datos reales.
  - La configuración de nginx.

---

# 5. Uso de IA generativa

- **Herramienta utilizada:** Claude.
- **Actividad para la que se utilizó:** fue un apoyo que nos ayudó a programar el backend de mi módulo y a preparar las pruebas CURL.
- **Código o contenido generado:** propuestas de código para el manejo de errores, las validaciones, la página de estado, la edición y eliminación de abogados y movimientos, el perfil del administrador, el reporte de finanzas, los dashboards y las pruebas CURL. Yo le indicaba qué necesitaba cada parte y revisaba lo que proponía antes de aceptarlo.
- **Modificaciones realizadas por el equipo:** decidimos que todos los errores respondieran en español y con el mismo formato, que no se pudiera eliminar a un abogado con clientes o casos, y que un abogado inactivo ya no pudiera iniciar sesión. También pedí que las pruebas limpiaran sus propios datos, porque al repetirlas los resultados no coincidían con los esperados.
- **Forma en que se verificó el resultado:** compilé el proyecto, levanté el backend conectado a MySQL sobre una base de datos limpia y ejecuté todas las pruebas CURL. Comparé las cifras del reporte de finanzas y de los dashboards con las que esperábamos, y reinicié el backend para comprobar que los datos se conservaban. Estoy estudiando el código para poder explicarlo en la defensa.

---

# 6. Participación individual

- **Gustavo Adolfo Martínez Martínez:** Me encargué del manejo común de errores, que también usan Danna y Luisa, de la página de estado del backend, de la edición y eliminación de abogados y movimientos, del perfil del administrador, del reporte de finanzas y de los dashboards. También reorganicé las pruebas CURL para que muestren un resumen, revisé que las cifras fueran las esperadas y junté el trabajo de las tres en la rama principal.

---

# 7. Próximas actividades

- Conectar al backend la página de inicio de sesión y las cuatro páginas del administrador.
- Generar el reporte de finanzas en PDF con datos reales.
- Configurar nginx para servir las páginas y la API desde la misma dirección.
- Mostrar en pantalla los mensajes de error y de confirmación.

---

## Observaciones adicionales

- Decidimos que un abogado marcado como Inactivo ya no pueda iniciar sesión, y que al eliminar a un abogado también se borren sus notas y su foto.
- El formato común de errores lo usan también los módulos de Danna y Luisa.
- El release de esta fase se llama **V3**.