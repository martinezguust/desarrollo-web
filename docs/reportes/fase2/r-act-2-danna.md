# Reporte de Avance del Proyecto

**Equipo:** Juris Magna  
**Integrante:** Danna Gabriela Garcia Castillo  
**Fecha:** 13/11/2026  
**Número de reporte:** 2 (Fase 2 — Backend básico)  
**Repositorio GitHub:** https://github.com/martinezguust/desarrollo-web  

# 1. Actividades realizadas

En esta segunda fase me dediqué a darle vida a las funciones principales de mi modulo dentro del backend:

- Programé las estructuras de datos y las consultas necesarias para manejar clientes, casos, carpetas, ligas y notas.
- Implementé el registro y la consulta de clientes. Al dar de alta a un cliente, el sistema le crea automáticamente su cuenta de usuario con contraseña cifrada para que pueda ingresar a la plataforma.
- Desarrollé la creación y consulta de casos: El sistema se encarga de generar por sí solo el número de expediente (`EXP-0006`, `EXP-0007`, etc.), verifica que se elija una de las 18 materias y uno de los 5 estados válidos, y guarda la fecha de cierre automáticamente cuando el caso finaliza
- Dejé listas las opciones para consultar y guardar carpetas dentro de los casos, asi como los enlaces o ligas dentro de cada carpeta
- Implementé el registro y consulta de las notas privadas de cada abogado.
- Agregué datos de prueba al sistema (tres clientes, cinco casos y una carpeta) para poder validar que todo guardara bien.
- Probé todas las rutas usando comandos CURL, verificando tanto cuando los datos son correctos como cuando deben marcar error,

# 2. Evidencias de trabajo

| Actividad | Integrante responsable | Enlace a commit |
|------------|------------------------|-----------------|
| Entidades, repositorios y rutas de clientes, casos, carpetas, ligas y notas; datos de prueba y pruebas CURL | Danna (en pareja con Gustavo) | https://github.com/martinezguust/desarrollo-web/commit/cb915fcb722d114a1158645cc945018a78f282a6 |
| Integración de la rama fase2-danna a main | Danna (en pareja con Gustavo) | https://github.com/martinezguust/desarrollo-web/commit/a8128eae3f0044dc13765907dbdd7c299e63f54b |

# 3. Problemas encontrados

- **Prevención de errores de consulta desde el inicio:** Al ver que en la parte de Gustavo una búsqueda entre tablas relacionadas había provocado un fallo interno, nos adelantamos y planteamos desde el principio nuestras consultas de forma correcta (por ejemplo, al buscar las notas de un abogado), logrando evitar ese mismo error
- **Registros duplicados por pruebas previas:** Como ya existían datos de prueba creados anteriormente, al volver a ejecutar todo marcaba que los usuarios estaban repetidos. Limpiamos esos datos antes de correr la batería completa de pruebas
- **Nombre visible al iniciar sesión como cliente:** El cliente ya puede entrar al sistema, pero por ahora la pantalla muestra su nombre de usuario en lugar de su nombre real. Ese detalle corresponde a la parte de Gustavo y se ajustará en la Fase 3

# 4. Avance funcional

- **Ya funciona:** Las 26 pruebas hechas con CURL en este módulo responden justo como se esperaba. Entre las cosas que ya hacen bien el sistema están:
  - Generar el número de expediente de forma automática.
  - Rellenar la fecha de cierre en cuanto un caso termina.
  - Evitar que se le asigne un caso a un cliente que pertenece a otro abogado.
  - Usar la dirección web como título si al guardar una liga no se le pone nombre.
- **Pendiente para la Fase 3:**
  - Las opciones para editar y borrar clientes, casos, carpetas, ligas y notas.
  - Los filtros de búsqueda y la vista del archivo histórico.
  - Validar que no se pueda borrar a un cliente si todavía tiene casos registrados.
  - Enviar el aviso al administrador cada vez que se registra un nuevo cliente.

# 5. Uso de IA generativa

- **Herramienta utilizada:** Claude
- **Actividad para la que se utilizó:** Apoyo en la programación de las clases, la lógica de negocio y las rutas de mi módulo, además de preparar los datos de prueba y los comandos CURL.
- **Código o contenido generado:** Las clases para manejar clientes, casos, carpetas, ligas y notas, los datos iniciales de prueba y la lista de comandos CURL.
- **Modificaciones realizadas por el equipo:** Ajustamos la lógica de validación para asegurarnos de que cada abogado solo pueda ver y asignar casos a sus propios clientes, además de limpiar los datos repetidos para que todas las pruebas corrieran en orden.
- **Forma en que se verificó el resultado:** Ejecutamos el proyecto conectado a MySQL y corrimos una por una las pruebas con CURL, revisando que los datos se guardaran bien, que los errores saltaran cuando debían y que nunca se mostrara la contraseña en las respuestas

# 6. Participación individual

- **Danna Gabriela García Castillo:** En esta fase me encargué de programar la base del funcionamiento de mi módulo (clientes, casos, carpetas, ligas y notas). Definí las reglas para crear expedientes en automático, validar materias y proteger el acceso para que cada abogado gestione únicamente a sus clientes. También preparé datos de ejemplo y verifiqué el comportamiento de cada ruta mediante pruebas con CURL

# 7. Próximas actividades

- Probar la conexión directa a MySQL desde mi computadora.
- Programar las funciones para editar y eliminar los registros de mi módulo.
- Implementar los filtros de búsqueda para clientes y casos, además del archivo histórico.
- Terminar de afinar las validaciones y mensajes de error de mi parte.
- Comprobar que toda la información guardada se conserve intacta después de reiniciar el servidor.

## Observaciones adicionales

- Se mantiene el acuerdo de no usar una tabla para documentos; todo se consulta directamente con su enlace a Google Drive.
- El estado "Cancelado" ya está activo en los casos y se toma en cuenta como un caso cerrado.
- El release correspondiente a esta fase se llama **V2** y queda a mi cargo crearlo