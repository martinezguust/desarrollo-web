# Reporte de Avance del Proyecto

**Equipo:** Juris Magna
**Integrante:** Gustavo Adolfo Martínez Martínez
**Fecha:** 20/11/2026
**Número de reporte:** 4 (Fase 4 — Sistema integrado)
**Repositorio GitHub:** https://github.com/martinezguust/desarrollo-web
**Release:** https://github.com/martinezguust/desarrollo-web/releases/tag/V4

---

# 1. Actividades realizadas

- Hice la hoja de estilos única del sistema (`estilos.css`), ordenada y fácil de leer, que usan todas las páginas.
- Programé `api.js`, por donde pasan todas las llamadas al backend con `fetch`. Si el backend responde con un error, el mensaje llega a la página para mostrarlo en pantalla.
- Programé `sesion.js`, que guarda quién inició sesión y protege cada página según el rol: si alguien entra sin sesión o con otro rol, regresa al inicio de sesión.
- Hice la página de inicio de sesión para los tres roles, con un mensaje claro cuando los datos son incorrectos.
- Conecté al backend las páginas del administrador:
  - **Dashboard:** indicadores y gráficas con datos reales.
  - **Abogados:** registro con foto, perfil, edición, cambio de contraseña y eliminación con confirmación.
  - **Finanzas:** tarjetas, gráficas, registros, nuevo, editar y eliminar movimiento, y el reporte en PDF.
  - **Archivo histórico:** casos cerrados con filtro por estado y búsqueda.
- Configuré nginx para que el sistema completo salga de una sola dirección: las páginas en `/`, la API en `/api/` y los archivos subidos en `/uploads/`.

---

# 2. Evidencias de trabajo

| Actividad | Integrante responsable | Enlace a commit |
|------------|------------------------|-----------------|
| Inicio de sesión, páginas del administrador, estilos.css, api.js, sesion.js y configuración de nginx | Gustavo | https://github.com/martinezguust/desarrollo-web/commit/9338ac49ba842c44186c784c90989c1bb444d381 |
| Integración de la rama fase4-gustavo a main | Gustavo | https://github.com/martinezguust/desarrollo-web/commit/96ff3febcfb9a0e34412e127ec90aea74e045451 |

**Evidencias de pruebas:**

Probé el sistema en Chrome a través de nginx, en `http://localhost`: inicié sesión con cada rol, revisé que cada página cargara datos reales, creé, edité y eliminé registros, y comprobé que los errores del backend aparecieran en pantalla.

También comprobé que la API funciona a través de nginx, con la ruta `/api/`:
```bash
curl -s -X POST http://localhost/api/sesiones \
    -H "Content-Type: application/json" \
    -d '{"username": "admin", "password": "admin123", "rol": "ADMIN"}'
```

Y volví a correr todas las pruebas del backend con `bash curl/pruebas.sh`: las 177 pruebas dieron el código esperado.

---

# 3. Problemas encontrados

- **Puerto ocupado:** el nginx que usamos en las prácticas de clase ya ocupaba el puerto 80. Hice una configuración propia para el proyecto y, para las pruebas, la levanté en otro puerto.
- **Fecha del PDF:** en la noche, el archivo del reporte de finanzas salía con la fecha del día siguiente, porque tomaba la hora universal. Lo cambié para que use la fecha de México.
- **Archivos grandes:** nginx rechazaba fotos y archivos de más de 1 MB. Le subí el límite a 10 MB, igual que el backend.

---

# 4. Avance funcional

- **Ya funciona:**
  - El inicio de sesión de los tres roles y la protección de las páginas.
  - Las cuatro páginas del administrador conectadas al backend con datos reales.
  - El reporte de finanzas en PDF.
  - El sistema completo servido por nginx.
- **Pendiente para la Fase 5:**
  - Pruebas unitarias.
  - Completar mi parte del README con las instrucciones de instalación.

---

# 5. Uso de IA generativa

- **Herramienta utilizada:** Claude.
- **Actividad para la que se utilizó:** fue un apoyo que nos ayudó a programar las páginas de mi módulo y su conexión con el backend, y a probarlas en el navegador.
- **Código o contenido generado:** propuestas de código para los estilos, `api.js`, `sesion.js`, el inicio de sesión, las páginas del administrador, el reporte PDF y la configuración de nginx. Yo le indicaba qué necesitaba cada parte y revisaba lo que proponía antes de aceptarlo.
- **Modificaciones realizadas por el equipo:** decidimos que todos los errores se mostraran en pantalla con un mensaje claro y que cada página revisara el rol antes de abrirse. También agregamos el botón para crear movimientos, el botón para editarlos y la confirmación antes de eliminar un abogado, porque sin ellos no se podía hacer desde la pantalla.
- **Forma en que se verificó el resultado:** probé cada página en Chrome a través de nginx (crear, editar, eliminar y errores), abrí el PDF generado para revisarlo y corrí `curl/pruebas.sh` (177 de 177). Estoy estudiando el código para poder explicarlo en la defensa.

---

# 6. Participación individual

- **Gustavo Adolfo Martínez Martínez:** Me encargué de la base del frontend que usan las tres: los estilos, la conexión con la API y el manejo de la sesión. Hice el inicio de sesión, las páginas del administrador con el reporte de finanzas en PDF y la configuración de nginx. Probé que todo funcionara de punta a punta y junté el trabajo de las tres en la rama principal.

---

# 7. Próximas actividades

- Hacer las pruebas unitarias de mi módulo.
- Completar mi parte del README con las instrucciones para levantar el sistema.
- Preparar las evidencias para la entrega final.

---

## Observaciones adicionales

- Para levantar el sistema se necesita el backend corriendo, MySQL encendido y nginx con la configuración del proyecto. Lo vamos a dejar explicado en el README.
- El release de esta fase se llama **V4**.