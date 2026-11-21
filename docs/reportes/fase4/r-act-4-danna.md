# Reporte de Avance del Proyecto

**Equipo:** Juris Magna
**Integrante:** Danna Gabriela García Castillo
**Fecha:** 20/11/2026
**Número de reporte:** 4 (Fase 4 — Sistema integrado)
**Repositorio GitHub:** https://github.com/martinezguust/desarrollo-web
**Release:** https://github.com/martinezguust/desarrollo-web/releases/tag/V4

---

# 1. Actividades realizadas

- Hice `utilidades.js`, con funciones pequeñas que usan todas las páginas: dar formato al dinero y a las fechas, abrir y cerrar ventanas y mostrar mensajes de confirmación y de error.
- Hice `menu.js`, que maneja la barra lateral y la barra superior: la sección activa, la fecha, la campana de notificaciones, el perfil del usuario y cerrar sesión.
- Conecté al backend las páginas del abogado:
  - **Dashboard:** sus indicadores con datos reales.
  - **Clientes:** vista en cuadrícula y en tabla, búsqueda, y crear, editar y eliminar clientes.
  - **Casos:** filtro por materia, y crear, editar y eliminar casos. Cada caso tiene su espacio de carpetas y ligas.
  - **Notas:** crear, editar, marcar como completada y eliminar.

---

# 2. Evidencias de trabajo

| Actividad | Integrante responsable | Enlace a commit |
|------------|------------------------|-----------------|
| utilidades.js, menu.js y las 4 páginas del abogado | Danna (en pareja con Gustavo) | https://github.com/martinezguust/desarrollo-web/commit/9ce5837c910a8c967343db1b910bda4d17c40d5d |
| Integración de la rama fase4-danna a main | Gustavo | https://github.com/martinezguust/desarrollo-web/commit/e4f10564cab4179a83b7f7fe4bc62826479ff8c9 |

**Evidencias de pruebas:**

Probé mis páginas en Chrome a través de nginx, en `http://localhost`, entrando como abogado (`jramirez`). Revisé que cada página cargara datos reales, creé, edité y eliminé clientes, casos, carpetas, ligas y notas, y comprobé que los errores del backend aparecieran en pantalla.

También comprobé que mis rutas responden a través de nginx:
```bash
curl -s "http://localhost/api/clientes?abogadoId=1"
curl -s "http://localhost/api/casos?abogadoId=1"
```

Y se corrieron todas las pruebas del backend con `bash curl/pruebas.sh`: las 177 pruebas dieron el código esperado.

---

# 3. Problemas encontrados

- **Notas que se desmarcaban:** al editar una nota completada, regresaba a pendiente, porque el backend la marca como no completada si no recibe ese dato. Lo corregí mandando su estado actual al guardar.
- **Carpetas de un caso:** no había forma de abrir las carpetas de un caso desde la tabla. Agregué el botón "📂 Carpetas" en cada caso.

---

# 4. Avance funcional

- **Ya funciona:**
  - El dashboard, los clientes, los casos con sus carpetas y ligas, y las notas del abogado, todo con datos reales.
  - Desde la tabla de clientes se abre el chat con cada cliente, que hizo Luisa.
- **Pendiente para la Fase 5:**
  - Pruebas unitarias.
  - Completar mi parte del README.

---

# 5. Uso de IA generativa

- **Herramienta utilizada:** Claude.
- **Actividad para la que se utilizó:** fue un apoyo que nos ayudó a programar las páginas del abogado y su conexión con el backend, y a probarlas en el navegador.
- **Código o contenido generado:** propuestas de código para `utilidades.js`, `menu.js` y las páginas del dashboard, clientes, casos y notas del abogado. Revisé lo que proponía y lo probé antes de aceptarlo.
- **Modificaciones realizadas por el equipo:** decidimos agregar botones para eliminar casos, carpetas y ligas, y para editar carpetas, porque sin ellos no se podía hacer desde la pantalla. También acordamos que al editar un cliente la contraseña quede vacía y que, si no se escribe una nueva, no se cambie.
- **Forma en que se verificó el resultado:** probé cada página en Chrome a través de nginx (crear, editar, eliminar y errores) y se corrió `curl/pruebas.sh` (177 de 177). Estoy estudiando el código para poder explicarlo en la defensa.

---

# 6. Participación individual

- **Danna Gabriela García Castillo:** Me encargué de las funciones que comparten todas las páginas (`utilidades.js` y `menu.js`) y de las páginas del abogado: su dashboard, clientes, casos con carpetas y ligas, y notas. Revisé con Gustavo que cada página funcionara con datos reales y que los errores se mostraran en pantalla.

---

# 7. Próximas actividades

- Hacer las pruebas unitarias de mi módulo.
- Completar mi parte del README.
- Preparar las evidencias para la entrega final.

---

## Observaciones adicionales

- Al editar un cliente, la contraseña aparece vacía: si no se escribe una nueva, se conserva la anterior.
- El release de esta fase se llama **V4**.