#!/bin/bash
# =====================================================================
# Juris Magna — Pruebas de la API con CURL
#
# Uso (con el backend corriendo):
#     bash curl/pruebas.sh
#
# Llama directo a Spring (puerto 8081). Con nginx sería
# http://localhost/api/...
# Cada integrante agrega sus pruebas en su propia sección.
# Al final se muestra cuántas pruebas pasaron y cuántas fallaron.
# =====================================================================

URL="http://localhost:8081"

TOTAL=0
FALLAS=0

# Carpeta temporal para los archivos de prueba (fotos, audios, documentos)
TEMPORAL=$(mktemp -d)
trap 'rm -rf "$TEMPORAL"' EXIT

# Imprime un título antes de cada prueba (pruebas que no revisan el código)
function titulo() {
    echo ""
    echo "=== $1 ==="
}

# Hace una prueba y revisa el código HTTP:
#     probar "descripción" código_esperado argumentos_de_curl...
# Deja la respuesta en CUERPO para poder sacar el id de lo creado.
function probar() {
    local descripcion="$1"
    local esperado="$2"
    shift 2

    echo ""
    echo "=== $descripcion (esperado $esperado) ==="

    local respuesta
    respuesta=$(curl -s -w "\n%{http_code}" "$@")
    CODIGO=$(echo "$respuesta" | tail -n 1)
    CUERPO=$(echo "$respuesta" | sed '$d')

    # Respuestas largas se recortan para que la salida se pueda leer
    if [ ${#CUERPO} -gt 600 ]; then
        echo "${CUERPO:0:600} ..."
    else
        echo "$CUERPO"
    fi

    TOTAL=$((TOTAL + 1))

    if [ "$CODIGO" == "$esperado" ]; then
        echo "OK (HTTP $CODIGO)"
    else
        echo "FALLA: se esperaba HTTP $esperado y llegó HTTP $CODIGO"
        FALLAS=$((FALLAS + 1))
    fi
}

# Saca el "id" de la última respuesta (por ejemplo, del registro recién creado)
function id_creado() {
    echo "$CUERPO" | grep -o '"id":[0-9]*' | head -n 1 | cut -d: -f2
}

# Imagen PNG de 1x1 píxel y un texto para probar las subidas de archivos
echo "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=" | base64 -d > "$TEMPORAL/foto.png"
echo "esto no es una imagen" > "$TEMPORAL/nota.txt"

JSON="Content-Type: application/json"


# =====================================================================
# Pruebas de Gustavo: página de estado, sesiones, usuarios, abogados,
# dashboards, finanzas y movimientos
# =====================================================================

# ---------- Página de estado (Thymeleaf) y errores generales ----------

probar "GET /  página de estado del backend" 200 "$URL/"

if echo "$CUERPO" | grep -q "Backend funcionando"; then
    echo "La página muestra: Backend funcionando"
fi

probar "GET /no-existe  ruta que no existe" 404 "$URL/no-existe"

probar "DELETE /finanzas  método no permitido" 405 -X DELETE "$URL/finanzas"

# ---------- Sesiones ----------

probar "POST /sesiones  login del admin" 200 -X POST "$URL/sesiones" -H "$JSON" \
    -d '{"username": "admin", "password": "admin123", "rol": "ADMIN"}'

probar "POST /sesiones  login de un abogado" 200 -X POST "$URL/sesiones" -H "$JSON" \
    -d '{"username": "jramirez", "password": "abogado123", "rol": "ABOGADO"}'

probar "POST /sesiones  login de un cliente (devuelve su nombre real)" 200 -X POST "$URL/sesiones" -H "$JSON" \
    -d '{"username": "cvega", "password": "cliente123", "rol": "CLIENTE"}'

probar "POST /sesiones  contraseña incorrecta" 401 -X POST "$URL/sesiones" -H "$JSON" \
    -d '{"username": "jramirez", "password": "malaclave", "rol": "ABOGADO"}'

probar "POST /sesiones  rol que no corresponde" 401 -X POST "$URL/sesiones" -H "$JSON" \
    -d '{"username": "jramirez", "password": "abogado123", "rol": "ADMIN"}'

probar "POST /sesiones  JSON mal escrito" 400 -X POST "$URL/sesiones" -H "$JSON" \
    -d '{"username": "admin", '

# ---------- Usuarios (perfil del administrador) ----------

probar "GET /usuarios/1  perfil del admin" 200 "$URL/usuarios/1"

probar "GET /usuarios/999  usuario que no existe" 404 "$URL/usuarios/999"

probar "PUT /usuarios/1  editar perfil del admin" 200 -X PUT "$URL/usuarios/1" -H "$JSON" \
    -d '{"nombre": "Administrador", "email": "admin@jurismagna.mx", "telefono": "55 0000 0000"}'

probar "POST /usuarios/1/foto  subir foto del admin" 200 -X POST "$URL/usuarios/1/foto" \
    -F "foto=@$TEMPORAL/foto.png"

# ---------- Abogados ----------

probar "GET /abogados  listar abogados con sus indicadores" 200 "$URL/abogados"

probar "GET /abogados/1  detalle de un abogado" 200 "$URL/abogados/1"

probar "GET /abogados/999  abogado que no existe" 404 "$URL/abogados/999"

probar "GET /abogados/abc  id que no es número" 400 "$URL/abogados/abc"

probar "POST /abogados  crear abogado y su usuario" 200 -X POST "$URL/abogados" -H "$JSON" \
    -d '{
        "nombre": "Lic. Andrea Prueba",
        "edad": 30,
        "especialidad": "Derecho Laboral",
        "estudios": "Licenciatura UV",
        "experiencia": "5 años",
        "cedula": "11223344",
        "email": "andrea@jurismagna.mx",
        "telefono": "228 123 4567",
        "username": "aprueba",
        "password": "abogado123"
    }'
ABOGADO_ID=$(id_creado)

probar "POST /abogados  usuario repetido" 409 -X POST "$URL/abogados" -H "$JSON" \
    -d '{"nombre": "Otro", "username": "jramirez", "password": "abogado123"}'

probar "POST /abogados  sin contraseña" 400 -X POST "$URL/abogados" -H "$JSON" \
    -d '{"nombre": "Sin clave", "username": "sinclave"}'

probar "POST /abogados  sin nombre (@Valid)" 400 -X POST "$URL/abogados" -H "$JSON" \
    -d '{"username": "sinnombre", "password": "abogado123"}'

probar "PUT /abogados/$ABOGADO_ID  editar abogado" 200 -X PUT "$URL/abogados/$ABOGADO_ID" -H "$JSON" \
    -d '{
        "nombre": "Lic. Andrea Prueba Ruiz",
        "edad": 31,
        "especialidad": "Derecho Laboral",
        "estudios": "Maestría UV",
        "experiencia": "6 años",
        "cedula": "11223344",
        "email": "andrea@jurismagna.mx",
        "telefono": "228 123 4567",
        "estado": "Activo"
    }'

probar "PUT /abogados/$ABOGADO_ID  estado no válido" 400 -X PUT "$URL/abogados/$ABOGADO_ID" -H "$JSON" \
    -d '{"nombre": "Lic. Andrea Prueba Ruiz", "estado": "Vacaciones"}'

probar "PUT /abogados/999  abogado que no existe" 404 -X PUT "$URL/abogados/999" -H "$JSON" \
    -d '{"nombre": "Nadie"}'

probar "PUT /abogados/$ABOGADO_ID/password  cambiar contraseña" 200 -X PUT "$URL/abogados/$ABOGADO_ID/password" -H "$JSON" \
    -d '{"password": "nueva1234"}'

probar "PUT /abogados/$ABOGADO_ID/password  contraseña muy corta" 400 -X PUT "$URL/abogados/$ABOGADO_ID/password" -H "$JSON" \
    -d '{"password": "12"}'

probar "POST /sesiones  login con la contraseña nueva" 200 -X POST "$URL/sesiones" -H "$JSON" \
    -d '{"username": "aprueba", "password": "nueva1234", "rol": "ABOGADO"}'

probar "POST /abogados/$ABOGADO_ID/foto  subir foto" 200 -X POST "$URL/abogados/$ABOGADO_ID/foto" \
    -F "foto=@$TEMPORAL/foto.png"
FOTO_URL=$(echo "$CUERPO" | grep -o '/uploads/[^"]*')

probar "GET $FOTO_URL  la foto se puede ver" 200 -o /dev/null "$URL$FOTO_URL"

probar "POST /abogados/$ABOGADO_ID/foto  archivo que no es imagen" 400 -X POST "$URL/abogados/$ABOGADO_ID/foto" \
    -F "foto=@$TEMPORAL/nota.txt"

probar "POST /abogados/$ABOGADO_ID/foto  sin el campo foto" 400 -X POST "$URL/abogados/$ABOGADO_ID/foto" \
    -F "otro=@$TEMPORAL/foto.png"

probar "DELETE /abogados/1  abogado con clientes y casos" 409 -X DELETE "$URL/abogados/1"

probar "DELETE /abogados/$ABOGADO_ID  eliminar abogado y su usuario" 200 -X DELETE "$URL/abogados/$ABOGADO_ID"

probar "DELETE /abogados/$ABOGADO_ID  ya no existe" 404 -X DELETE "$URL/abogados/$ABOGADO_ID"

# ---------- Dashboards ----------

probar "GET /dashboards/admin  indicadores del administrador" 200 "$URL/dashboards/admin"

probar "GET /dashboards/abogados/1  indicadores del abogado" 200 "$URL/dashboards/abogados/1"

probar "GET /dashboards/abogados/999  abogado que no existe" 404 "$URL/dashboards/abogados/999"

# ---------- Finanzas y movimientos ----------

probar "GET /finanzas?meses=3  reporte de los últimos 3 meses" 200 "$URL/finanzas?meses=3"

probar "GET /finanzas?meses=13  meses fuera de rango" 400 "$URL/finanzas?meses=13"

probar "GET /finanzas?meses=abc  meses no es número" 400 "$URL/finanzas?meses=abc"

probar "GET /movimientos  listar movimientos" 200 "$URL/movimientos"

probar "GET /movimientos?anio=2026&mes=10  movimientos de octubre" 200 "$URL/movimientos?anio=2026&mes=10"

probar "GET /movimientos?mes=10  falta el año" 400 "$URL/movimientos?mes=10"

probar "GET /movimientos?anio=2026&mes=13  mes no válido" 400 "$URL/movimientos?anio=2026&mes=13"

probar "POST /movimientos  crear un ingreso" 200 -X POST "$URL/movimientos" -H "$JSON" \
    -d '{"tipo": "INGRESO", "concepto": "Honorarios", "monto": 15000, "fecha": "2026-10-12"}'
MOVIMIENTO_ID=$(id_creado)

probar "POST /movimientos  concepto que no corresponde al tipo" 400 -X POST "$URL/movimientos" -H "$JSON" \
    -d '{"tipo": "INGRESO", "concepto": "Nómina", "monto": 1000, "fecha": "2026-10-12"}'

probar "POST /movimientos  monto negativo (@Valid)" 400 -X POST "$URL/movimientos" -H "$JSON" \
    -d '{"tipo": "EGRESO", "concepto": "Renta", "monto": -500, "fecha": "2026-10-12"}'

probar "POST /movimientos  faltan campos (@Valid)" 400 -X POST "$URL/movimientos" -H "$JSON" \
    -d '{"tipo": "EGRESO"}'

probar "POST /movimientos  fecha con formato incorrecto" 400 -X POST "$URL/movimientos" -H "$JSON" \
    -d '{"tipo": "EGRESO", "concepto": "Renta", "monto": 500, "fecha": "12/10/2026"}'

probar "PUT /movimientos/$MOVIMIENTO_ID  editar movimiento" 200 -X PUT "$URL/movimientos/$MOVIMIENTO_ID" -H "$JSON" \
    -d '{"tipo": "INGRESO", "concepto": "Consultas y asesorías", "monto": 18000, "fecha": "2026-10-13"}'

probar "PUT /movimientos/999  movimiento que no existe" 404 -X PUT "$URL/movimientos/999" -H "$JSON" \
    -d '{"tipo": "INGRESO", "concepto": "Honorarios", "monto": 100, "fecha": "2026-10-13"}'

probar "DELETE /movimientos/$MOVIMIENTO_ID  eliminar movimiento" 200 -X DELETE "$URL/movimientos/$MOVIMIENTO_ID"

probar "DELETE /movimientos/$MOVIMIENTO_ID  ya no existe" 404 -X DELETE "$URL/movimientos/$MOVIMIENTO_ID"


# =====================================================================
# Pruebas de Danna: clientes, casos, carpetas, ligas, notas
# =====================================================================

titulo "GET /clientes  listar clientes (200)"
curl -s "$URL/clientes"
echo ""

titulo "GET /clientes/1  detalle de un cliente (200)"
curl -s "$URL/clientes/1"
echo ""

titulo "GET /clientes/999  cliente que no existe (404)"
curl -s -w "\nHTTP %{http_code}" "$URL/clientes/999"
echo ""

titulo "POST /clientes  crear cliente y su usuario (200)"
curl -s -X POST "$URL/clientes" \
    -H "Content-Type: application/json" \
    -d '{
        "abogadoId": 1,
        "nombre": "Laura Prueba",
        "edad": 29,
        "estudios": "Arquitectura",
        "ine": "PRUL970101",
        "email": "laura@mail.com",
        "telefono": "228 765 4321",
        "username": "lprueba",
        "password": "cliente123",
        "descripcion": "Revisión de contrato de compraventa",
        "avance": 10,
        "honorarios": 20000,
        "driveUrl": "",
        "comentario": "Hola Laura, ya estamos revisando tu contrato."
    }'
echo ""

titulo "POST /clientes  usuario repetido (409)"
curl -s -w "\nHTTP %{http_code}" -X POST "$URL/clientes" \
    -H "Content-Type: application/json" \
    -d '{"abogadoId": 1, "nombre": "Otro", "username": "cvega", "password": "cliente123"}'
echo ""

titulo "POST /clientes  avance fuera de rango (400)"
curl -s -w "\nHTTP %{http_code}" -X POST "$URL/clientes" \
    -H "Content-Type: application/json" \
    -d '{"abogadoId": 1, "nombre": "Avance malo", "username": "avmalo", "password": "cliente123", "avance": 150}'
echo ""

titulo "POST /clientes  abogado que no existe (404)"
curl -s -w "\nHTTP %{http_code}" -X POST "$URL/clientes" \
    -H "Content-Type: application/json" \
    -d '{"abogadoId": 999, "nombre": "Sin abogado", "username": "sinabogado", "password": "cliente123"}'
echo ""

titulo "POST /sesiones  login del cliente recién creado (200)"
curl -s -X POST "$URL/sesiones" \
    -H "Content-Type: application/json" \
    -d '{"username": "lprueba", "password": "cliente123", "rol": "CLIENTE"}'
echo ""

titulo "GET /casos  listar casos (200)"
curl -s "$URL/casos"
echo ""

titulo "GET /casos/3  detalle de un caso (200)"
curl -s "$URL/casos/3"
echo ""

titulo "POST /casos  crear caso; genera el siguiente expediente (200)"
curl -s -X POST "$URL/casos" \
    -H "Content-Type: application/json" \
    -d '{"clienteId": 2, "abogadoId": 1, "materia": "Familiar", "estado": "Activo", "driveUrl": ""}'
echo ""

titulo "POST /casos  caso cerrado; llena la fecha de fin (200)"
curl -s -X POST "$URL/casos" \
    -H "Content-Type: application/json" \
    -d '{"clienteId": 3, "abogadoId": 2, "materia": "Penal", "estado": "Cancelado"}'
echo ""

titulo "POST /casos  materia no válida (400)"
curl -s -w "\nHTTP %{http_code}" -X POST "$URL/casos" \
    -H "Content-Type: application/json" \
    -d '{"clienteId": 1, "abogadoId": 1, "materia": "Espacial"}'
echo ""

titulo "POST /casos  el cliente no es de ese abogado (400)"
curl -s -w "\nHTTP %{http_code}" -X POST "$URL/casos" \
    -H "Content-Type: application/json" \
    -d '{"clienteId": 3, "abogadoId": 1, "materia": "Civil"}'
echo ""

titulo "GET /casos/1/carpetas  carpetas del caso (200)"
curl -s "$URL/casos/1/carpetas"
echo ""

titulo "POST /casos/1/carpetas  crear carpeta (200)"
curl -s -X POST "$URL/casos/1/carpetas" \
    -H "Content-Type: application/json" \
    -d '{"numero": "002", "materia": "Civil", "proyecto": "Pruebas periciales", "estado": "Activo", "ligaUrl": "https://drive.google.com/periciales"}'
echo ""

titulo "GET /casos/999/carpetas  caso que no existe (404)"
curl -s -w "\nHTTP %{http_code}" "$URL/casos/999/carpetas"
echo ""

titulo "POST /carpetas/1/ligas  agregar liga sin nombre; usa la URL (200)"
curl -s -X POST "$URL/carpetas/1/ligas" \
    -H "Content-Type: application/json" \
    -d '{"url": "https://drive.google.com/escritura"}'
echo ""

titulo "POST /carpetas/1/ligas  liga sin URL (400)"
curl -s -w "\nHTTP %{http_code}" -X POST "$URL/carpetas/1/ligas" \
    -H "Content-Type: application/json" \
    -d '{"nombre": "Sin URL"}'
echo ""

titulo "GET /carpetas/1/ligas  ligas de la carpeta (200)"
curl -s "$URL/carpetas/1/ligas"
echo ""

titulo "POST /notas  crear nota (200)"
curl -s -X POST "$URL/notas" \
    -H "Content-Type: application/json" \
    -d '{"abogadoId": 1, "titulo": "Preparar audiencia", "contenido": "Revisar contrato de arrendamiento"}'
echo ""

titulo "POST /notas  sin título (400)"
curl -s -w "\nHTTP %{http_code}" -X POST "$URL/notas" \
    -H "Content-Type: application/json" \
    -d '{"abogadoId": 1, "contenido": "Nota sin título"}'
echo ""

titulo "GET /notas?abogadoId=1  notas del abogado (200)"
curl -s "$URL/notas?abogadoId=1"
echo ""

titulo "GET /notas  sin abogadoId (400)"
curl -s -w "\nHTTP %{http_code}" "$URL/notas"
echo ""



# =====================================================================
# Pruebas de Luisa: mensajes, notificaciones, eventos, contactos
# =====================================================================

titulo "GET /mensajes?hilo=cliente-1  conversación de Carlos Vega (200)"
curl -s "$URL/mensajes?hilo=cliente-1"
echo ""

titulo "GET /mensajes  sin hilo (400)"
curl -s -w "\nHTTP %{http_code}" "$URL/mensajes"
echo ""

titulo "POST /mensajes  el cliente escribe; avisa a su abogado (200)"
curl -s -X POST "$URL/mensajes" \
    -H "Content-Type: application/json" \
    -d '{"hilo": "cliente-1", "remitente": "cliente", "texto": "Ya firmé los documentos."}'
echo ""

titulo "POST /mensajes  el admin escribe a un abogado (200)"
curl -s -X POST "$URL/mensajes" \
    -H "Content-Type: application/json" \
    -d '{"hilo": "admin-abogado-1", "remitente": "admin", "texto": "Juan, revisa el expediente EXP-0001 por favor."}'
echo ""

titulo "POST /mensajes  remitente que no pertenece al hilo (400)"
curl -s -w "\nHTTP %{http_code}" -X POST "$URL/mensajes" \
    -H "Content-Type: application/json" \
    -d '{"hilo": "cliente-1", "remitente": "admin", "texto": "Hola"}'
echo ""

titulo "POST /mensajes  cliente que no existe (404)"
curl -s -w "\nHTTP %{http_code}" -X POST "$URL/mensajes" \
    -H "Content-Type: application/json" \
    -d '{"hilo": "cliente-999", "remitente": "cliente", "texto": "Hola"}'
echo ""

titulo "GET /notificaciones?usuarioId=2  avisos del Dr. Juan Ramírez (200)"
curl -s "$URL/notificaciones?usuarioId=2"
echo ""

titulo "POST /notificaciones  crear notificación (200)"
curl -s -X POST "$URL/notificaciones" \
    -H "Content-Type: application/json" \
    -d '{"usuarioId": 1, "mensaje": "Prueba de notificación"}'
echo ""

titulo "POST /notificaciones  usuario que no existe (404)"
curl -s -w "\nHTTP %{http_code}" -X POST "$URL/notificaciones" \
    -H "Content-Type: application/json" \
    -d '{"usuarioId": 999, "mensaje": "No llega"}'
echo ""

titulo "GET /notificaciones?usuarioId=1  avisos del admin (200)"
curl -s "$URL/notificaciones?usuarioId=1"
echo ""

titulo "POST /eventos  crear evento (200)"
curl -s -X POST "$URL/eventos" \
    -H "Content-Type: application/json" \
    -d '{
        "titulo": "Audiencia Carlos Vega",
        "fecha": "2026-11-28",
        "hora": "10:00",
        "lugar": "Juzgado 5",
        "descripcion": "Llevar contrato y acta notarial",
        "color": "#D4AF37",
        "alcance": "abogado-1",
        "usuarioId": 2
    }'
echo ""

titulo "POST /eventos  sin título; se guarda como Evento (200)"
curl -s -X POST "$URL/eventos" \
    -H "Content-Type: application/json" \
    -d '{"fecha": "2026-11-20", "alcance": "admin", "usuarioId": 1}'
echo ""

titulo "POST /eventos  color no válido (400)"
curl -s -w "\nHTTP %{http_code}" -X POST "$URL/eventos" \
    -H "Content-Type: application/json" \
    -d '{"titulo": "Junta", "fecha": "2026-11-21", "color": "#000000", "alcance": "admin", "usuarioId": 1}'
echo ""

titulo "POST /eventos  alcance no válido (400)"
curl -s -w "\nHTTP %{http_code}" -X POST "$URL/eventos" \
    -H "Content-Type: application/json" \
    -d '{"titulo": "Junta", "fecha": "2026-11-21", "alcance": "todos", "usuarioId": 1}'
echo ""

titulo "GET /eventos?alcance=abogado-1  agenda del Dr. Juan Ramírez (200)"
curl -s "$URL/eventos?alcance=abogado-1"
echo ""

titulo "GET /eventos  sin alcance (400)"
curl -s -w "\nHTTP %{http_code}" "$URL/eventos"
echo ""

titulo "POST /contactos  crear contacto del admin (200)"
curl -s -X POST "$URL/contactos" \
    -H "Content-Type: application/json" \
    -d '{
        "propietarioTipo": "ADMIN",
        "propietarioId": 1,
        "nombre": "Notaría 12",
        "telefono": "55 2222 3333",
        "email": "contacto@notaria12.mx",
        "notas": "Escrituras"
    }'
echo ""

titulo "POST /contactos  sin nombre (400)"
curl -s -w "\nHTTP %{http_code}" -X POST "$URL/contactos" \
    -H "Content-Type: application/json" \
    -d '{"propietarioTipo": "ADMIN", "propietarioId": 1}'
echo ""

titulo "GET /contactos  listar contactos (200)"
curl -s "$URL/contactos"
echo ""


# =====================================================================
# Resumen
# =====================================================================

echo ""
echo "====================================================================="
echo "Pruebas revisadas: $TOTAL    Fallas: $FALLAS"
echo "====================================================================="
