# Reporte de Avance del Proyecto

**Equipo:** Juris Magna
**Integrante:** Gustavo Adolfo Martínez Martínez
**Fecha:** 13/11/2026
**Número de reporte:** 2 (Fase 2 — Backend básico)
**Repositorio GitHub:** https://github.com/martinezguust/desarrollo-web
**Release:** https://github.com/martinezguust/desarrollo-web/releases/tag/V2

---

# 1. Actividades realizadas

- Creé el proyecto del backend con Spring Boot y Java 17, que después usaron también Danna y Luisa para sus módulos.
- Configuré la conexión a MySQL. La dirección, el usuario y la contraseña de la base de datos no van escritos en el código: cada integrante los indica en su propia computadora.
- Hice las entidades y los repositorios de usuarios, abogados y movimientos financieros.
- Programé el inicio de sesión para los tres roles (administrador, abogado y cliente), con contraseñas encriptadas.
- Programé la consulta y el registro de abogados. Al registrar un abogado también se crea su usuario para entrar al sistema.
- Programé la consulta y el registro de movimientos financieros (ingresos y egresos), con validación del tipo, el concepto y el monto.
- Agregué datos de prueba: el administrador, dos abogados, tres clientes y los movimientos de agosto a octubre de 2026.
- Probé todas mis rutas con CURL, tanto en casos de éxito como de error.

---

# 2. Evidencias de trabajo

| Actividad | Integrante responsable | Enlace a commit |
|------------|------------------------|-----------------|
| Proyecto Spring Boot, conexión a MySQL, entidades, repositorios, inicio de sesión, abogados, movimientos y pruebas CURL | Gustavo | https://github.com/martinezguust/desarrollo-web/commit/5a7692c29c269b6c79cb6c0d8fad2d3cd0583e77 |
| Integración de la rama fase2-gustavo a main | Gustavo | https://github.com/martinezguust/desarrollo-web/commit/65238197591d6cbb1ed435e014b216345385f7ad |

**Evidencias de pruebas (CURL):**

Estos son algunos de los comandos que usé para probar mis rutas. Las pruebas completas están en `curl/pruebas.sh`.

Inicio de sesión correcto (200):
```bash
curl -i -X POST http://localhost:8081/sesiones \
  -H "Content-Type: application/json" \
  -d '{"usuario":"admin","contrasena":"admin123"}'
```

Inicio de sesión con contraseña incorrecta (401):
```bash
curl -i -X POST http://localhost:8081/sesiones \
  -H "Content-Type: application/json" \
  -d '{"usuario":"admin","contrasena":"incorrecta"}'
```

Consultar abogados (200) y un abogado que no existe (404):
```bash
curl -i http://localhost:8081/abogados
curl -i http://localhost:8081/abogados/999
```

Para ejecutar todas: `bash curl/pruebas.sh`. Todas mis pruebas devolvieron el código esperado.

---

# 3. Problemas encontrados

- **MySQL apagado:** al principio el backend no se conectaba a la base de datos porque el servicio de MySQL estaba detenido en mi Windows. Lo inicié con permisos de administrador y la conexión respondió.
- **Permisos del usuario listi:** ya con MySQL encendido, la conexión entraba pero no tenía acceso a la base de datos `jurismagna`. Para darle permisos tenía que entrar como administrador de MySQL (root), pero no recordaba esa contraseña. Tuve que restablecerla siguiendo el procedimiento de MySQL para Windows y después ya pude crear la base de datos y darle los permisos a listi.
- **Error 500 en el inicio de sesión del abogado:** el administrador entraba bien, pero el abogado recibía un error interno. La consulta que busca al abogado a partir de su usuario estaba mal planteada. La corregí y el inicio de sesión funcionó para todos los roles.
- **Mensajes de error demasiado largos:** al principio las respuestas de error mostraban todo el detalle interno del servidor. Ajusté la configuración para que solo muestren un mensaje claro, como "Usuario, contraseña o rol incorrectos".

---

# 4. Avance funcional

- **Ya funciona:**
  - El backend se conecta a MySQL y crea sus tablas solo.
  - Al arrancar, carga los datos de prueba sin duplicarlos.
  - Las 15 pruebas CURL de mi módulo dan el resultado esperado.
- **Pendiente para la Fase 3:**
  - Editar y eliminar abogados y movimientos.
  - El reporte de finanzas y los indicadores del dashboard.
  - Respuestas de error con un formato común para todo el sistema.
  - La página de estado del backend.
  - Que el inicio de sesión del cliente muestre su nombre real.

---

# 5. Uso de IA generativa

- **Herramienta utilizada:** Claude.
- **Actividad para la que se utilizó:** nos ayudó a programar el backend de mi módulo, a preparar las pruebas CURL y a encontrar la causa de los errores que fueron saliendo.
- **Código o contenido generado:** propuestas de código para el proyecto Spring Boot, la configuración de conexión, las clases de usuarios, abogados y movimientos, los datos de prueba y las pruebas CURL. Yo le indicaba qué necesitaba cada parte y revisaba lo que proponía antes de aceptarlo.
- **Modificaciones realizadas por el equipo:** decidimos que los datos de conexión no se escribieran en el código para que cada integrante use los de su computadora, y que los errores mostraran solo un mensaje claro. Cuando las pruebas fallaron (el error 500 del inicio de sesión del abogado y los permisos de MySQL), revisé la causa y pedí las correcciones.
- **Forma en que se verificó el resultado:** compilé el proyecto, levanté el backend conectado a MySQL y ejecuté las pruebas CURL, revisando que cada respuesta tuviera el código y los datos correctos. También estoy estudiando el código para poder explicarlo en la defensa.

---

# 6. Participación individual

- **Gustavo Adolfo Martínez Martínez:** Me encargué de crear la base del backend, que después usaron Danna y Luisa para sus módulos. Hice el inicio de sesión, la parte de abogados y la de movimientos financieros, y las probé con CURL. También resolví los problemas para conectar el proyecto con MySQL en mi computadora y, al final, junté el trabajo de las tres en la rama principal.

---

# 7. Próximas actividades

- Editar y eliminar abogados y movimientos.
- Hacer el reporte de finanzas y los indicadores del dashboard con datos reales.
- Dar un formato común a los mensajes de error y hacer la página de estado del backend.
- Comprobar que los datos se conservan después de reiniciar.

---

## Observaciones adicionales

- Creé el proyecto del backend antes que las demás partes porque Danna y Luisa trabajan sobre él.
- Para que el sistema funcione, cada integrante necesita tener MySQL encendido en su computadora y darle permisos al usuario listi. Lo vamos a agregar a las instrucciones de instalación.
- El release de esta fase se llama **V2**.