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
USUARIO_ID=$(echo "$CUERPO" | grep -o '"usuarioId":[0-9]*' | cut -d: -f2)

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

# La foto de perfil de /usuarios se prueba con el usuario del abogado de prueba,
# así la foto del admin no cambia; se borra al eliminar al abogado
probar "POST /usuarios/$USUARIO_ID/foto  subir foto de perfil" 200 -X POST "$URL/usuarios/$USUARIO_ID/foto" \
    -F "foto=@$TEMPORAL/foto.png"
FOTO_USUARIO_URL=$(echo "$CUERPO" | grep -o '/uploads/[^"]*')

probar "DELETE /abogados/1  abogado con clientes y casos" 409 -X DELETE "$URL/abogados/1"

probar "DELETE /abogados/$ABOGADO_ID  eliminar abogado y su usuario" 200 -X DELETE "$URL/abogados/$ABOGADO_ID"

probar "DELETE /abogados/$ABOGADO_ID  ya no existe" 404 -X DELETE "$URL/abogados/$ABOGADO_ID"

probar "GET $FOTO_URL  la foto del abogado eliminado ya se borró" 404 -o /dev/null "$URL$FOTO_URL"

probar "GET $FOTO_USUARIO_URL  la foto de su usuario también se borró" 404 -o /dev/null "$URL$FOTO_USUARIO_URL"

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
# Pruebas de Danna: clientes, casos, histórico, carpetas, ligas y notas
# =====================================================================

# ---------- Clientes ----------

probar "GET /clientes  listar clientes" 200 "$URL/clientes"

probar "GET /clientes?abogadoId=1  clientes del Dr. Juan Ramírez" 200 "$URL/clientes?abogadoId=1"

probar "GET /clientes?q=vega  buscar por nombre" 200 "$URL/clientes?q=vega"

probar "GET /clientes?abogadoId=abc  filtro con tipo incorrecto" 400 "$URL/clientes?abogadoId=abc"

probar "GET /clientes/1  detalle de un cliente" 200 "$URL/clientes/1"

probar "GET /clientes/999  cliente que no existe" 404 "$URL/clientes/999"

probar "POST /clientes  crear cliente y su usuario" 200 -X POST "$URL/clientes" -H "$JSON" \
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
CLIENTE_ID=$(id_creado)

probar "GET /notificaciones?usuarioId=1  el admin recibe el aviso del cliente nuevo" 200 "$URL/notificaciones?usuarioId=1"

if echo "$CUERPO" | grep -q "agregó a Laura Prueba"; then
    echo "Aviso encontrado: Dr. Juan Ramírez agregó a Laura Prueba"
else
    echo "FALLA: no apareció el aviso del cliente nuevo"
    FALLAS=$((FALLAS + 1))
fi

probar "POST /sesiones  login del cliente nuevo (nombre real)" 200 -X POST "$URL/sesiones" -H "$JSON" \
    -d '{"username": "lprueba", "password": "cliente123", "rol": "CLIENTE"}'

probar "POST /clientes  usuario repetido" 409 -X POST "$URL/clientes" -H "$JSON" \
    -d '{"abogadoId": 1, "nombre": "Otro", "username": "cvega", "password": "cliente123"}'

probar "POST /clientes  avance fuera de rango (@Valid)" 400 -X POST "$URL/clientes" -H "$JSON" \
    -d '{"abogadoId": 1, "nombre": "Avance malo", "username": "avmalo", "password": "cliente123", "avance": 150}'

probar "POST /clientes  sin nombre (@Valid)" 400 -X POST "$URL/clientes" -H "$JSON" \
    -d '{"abogadoId": 1, "username": "sinnombre", "password": "cliente123"}'

probar "POST /clientes  abogado que no existe" 404 -X POST "$URL/clientes" -H "$JSON" \
    -d '{"abogadoId": 999, "nombre": "Sin abogado", "username": "sinabogado", "password": "cliente123"}'

probar "PUT /clientes/$CLIENTE_ID  editar avance y comentario (sin cambiar contraseña)" 200 -X PUT "$URL/clientes/$CLIENTE_ID" -H "$JSON" \
    -d '{
        "abogadoId": 1,
        "nombre": "Laura Prueba",
        "edad": 29,
        "estudios": "Arquitectura",
        "ine": "PRUL970101",
        "email": "laura@mail.com",
        "telefono": "228 765 4321",
        "username": "lprueba",
        "password": "",
        "descripcion": "Revisión de contrato de compraventa",
        "avance": 40,
        "honorarios": 22000,
        "driveUrl": "https://drive.google.com/laura",
        "comentario": "Laura, ya revisamos el contrato; falta tu firma.",
        "estado": "Activo"
    }'

probar "POST /sesiones  la contraseña no cambió" 200 -X POST "$URL/sesiones" -H "$JSON" \
    -d '{"username": "lprueba", "password": "cliente123", "rol": "CLIENTE"}'

probar "PUT /clientes/$CLIENTE_ID  honorarios negativos (@Valid)" 400 -X PUT "$URL/clientes/$CLIENTE_ID" -H "$JSON" \
    -d '{"abogadoId": 1, "nombre": "Laura Prueba", "honorarios": -5}'

probar "PUT /clientes/$CLIENTE_ID  usuario que ya usa otro cliente" 409 -X PUT "$URL/clientes/$CLIENTE_ID" -H "$JSON" \
    -d '{"abogadoId": 1, "nombre": "Laura Prueba", "username": "cvega"}'

probar "PUT /clientes/999  cliente que no existe" 404 -X PUT "$URL/clientes/999" -H "$JSON" \
    -d '{"abogadoId": 1, "nombre": "Nadie"}'

# ---------- Casos ----------

probar "GET /casos  listar casos" 200 "$URL/casos"

probar "GET /casos?abogadoId=2&materia=Penal  filtrar por abogado y materia" 200 "$URL/casos?abogadoId=2&materia=Penal"

probar "GET /casos?estado=Activo&q=vega  filtrar por estado y buscar" 200 "$URL/casos?estado=Activo&q=vega"

probar "GET /casos/3  detalle de un caso" 200 "$URL/casos/3"

probar "GET /casos/999  caso que no existe" 404 "$URL/casos/999"

probar "POST /casos  crear caso; genera el siguiente expediente" 200 -X POST "$URL/casos" -H "$JSON" \
    -d "{\"clienteId\": $CLIENTE_ID, \"abogadoId\": 1, \"materia\": \"Inmobiliario\", \"estado\": \"Activo\", \"driveUrl\": \"\"}"
CASO_ID=$(id_creado)

probar "POST /casos  materia no válida" 400 -X POST "$URL/casos" -H "$JSON" \
    -d '{"clienteId": 1, "abogadoId": 1, "materia": "Espacial"}'

probar "POST /casos  sin materia (@Valid)" 400 -X POST "$URL/casos" -H "$JSON" \
    -d '{"clienteId": 1, "abogadoId": 1}'

probar "POST /casos  el cliente no es de ese abogado" 400 -X POST "$URL/casos" -H "$JSON" \
    -d '{"clienteId": 3, "abogadoId": 1, "materia": "Civil"}'

probar "POST /casos  estado no válido" 400 -X POST "$URL/casos" -H "$JSON" \
    -d '{"clienteId": 1, "abogadoId": 1, "materia": "Civil", "estado": "Pausado"}'

probar "PUT /casos/$CASO_ID  cerrar el caso como Ganado (llena la fecha de fin)" 200 -X PUT "$URL/casos/$CASO_ID" -H "$JSON" \
    -d "{\"clienteId\": $CLIENTE_ID, \"abogadoId\": 1, \"materia\": \"Inmobiliario\", \"estado\": \"Ganado\", \"driveUrl\": \"https://drive.google.com/laura-caso\"}"

probar "PUT /casos/999  caso que no existe" 404 -X PUT "$URL/casos/999" -H "$JSON" \
    -d '{"clienteId": 1, "abogadoId": 1, "materia": "Civil"}'

# ---------- Archivo histórico ----------

probar "GET /casos/historico  casos cerrados" 200 "$URL/casos/historico"

probar "GET /casos/historico?estado=Ganado&q=laura  ganados de Laura" 200 "$URL/casos/historico?estado=Ganado&q=laura"

probar "GET /casos/historico?estado=Activo  estado que no es cerrado" 400 "$URL/casos/historico?estado=Activo"

# ---------- Carpetas y ligas ----------

probar "GET /casos/1/carpetas  carpetas del caso" 200 "$URL/casos/1/carpetas"

probar "GET /casos/999/carpetas  caso que no existe" 404 "$URL/casos/999/carpetas"

probar "POST /casos/$CASO_ID/carpetas  crear carpeta" 200 -X POST "$URL/casos/$CASO_ID/carpetas" -H "$JSON" \
    -d '{"numero": "001", "materia": "Inmobiliario", "proyecto": "Compraventa", "estado": "Activo", "ligaUrl": "https://drive.google.com/compraventa"}'
CARPETA_ID=$(id_creado)

probar "PUT /carpetas/$CARPETA_ID  editar carpeta" 200 -X PUT "$URL/carpetas/$CARPETA_ID" -H "$JSON" \
    -d '{"numero": "001", "materia": "Inmobiliario", "proyecto": "Compraventa casa", "estado": "Cerrado", "ligaUrl": "https://drive.google.com/compraventa"}'

probar "PUT /carpetas/999  carpeta que no existe" 404 -X PUT "$URL/carpetas/999" -H "$JSON" \
    -d '{"numero": "001"}'

probar "POST /carpetas/$CARPETA_ID/ligas  liga sin nombre (usa la URL)" 200 -X POST "$URL/carpetas/$CARPETA_ID/ligas" -H "$JSON" \
    -d '{"url": "https://drive.google.com/escritura"}'
LIGA_ID=$(id_creado)

probar "POST /carpetas/$CARPETA_ID/ligas  liga con nombre" 200 -X POST "$URL/carpetas/$CARPETA_ID/ligas" -H "$JSON" \
    -d '{"nombre": "Avalúo", "url": "https://drive.google.com/avaluo"}'

probar "POST /carpetas/$CARPETA_ID/ligas  liga sin URL (@Valid)" 400 -X POST "$URL/carpetas/$CARPETA_ID/ligas" -H "$JSON" \
    -d '{"nombre": "Sin URL"}'

probar "GET /carpetas/$CARPETA_ID/ligas  ligas de la carpeta" 200 "$URL/carpetas/$CARPETA_ID/ligas"

probar "DELETE /ligas/$LIGA_ID  eliminar liga" 200 -X DELETE "$URL/ligas/$LIGA_ID"

probar "DELETE /ligas/$LIGA_ID  ya no existe" 404 -X DELETE "$URL/ligas/$LIGA_ID"

probar "DELETE /carpetas/$CARPETA_ID  eliminar carpeta (y su liga)" 200 -X DELETE "$URL/carpetas/$CARPETA_ID"

probar "GET /carpetas/$CARPETA_ID/ligas  la carpeta ya no existe" 404 "$URL/carpetas/$CARPETA_ID/ligas"

# ---------- Eliminar cliente y caso ----------

probar "DELETE /clientes/$CLIENTE_ID  cliente con casos" 409 -X DELETE "$URL/clientes/$CLIENTE_ID"

probar "POST /casos/$CASO_ID/carpetas  otra carpeta para probar el borrado en cascada" 200 -X POST "$URL/casos/$CASO_ID/carpetas" -H "$JSON" \
    -d '{"numero": "002", "proyecto": "Escrituras"}'

probar "DELETE /casos/$CASO_ID  eliminar caso (y sus carpetas)" 200 -X DELETE "$URL/casos/$CASO_ID"

probar "GET /casos/$CASO_ID/carpetas  el caso ya no existe" 404 "$URL/casos/$CASO_ID/carpetas"

probar "DELETE /clientes/$CLIENTE_ID  eliminar cliente sin casos (y su usuario)" 200 -X DELETE "$URL/clientes/$CLIENTE_ID"

probar "DELETE /clientes/$CLIENTE_ID  ya no existe" 404 -X DELETE "$URL/clientes/$CLIENTE_ID"

probar "POST /sesiones  el usuario del cliente eliminado ya no entra" 401 -X POST "$URL/sesiones" -H "$JSON" \
    -d '{"username": "lprueba", "password": "cliente123", "rol": "CLIENTE"}'

# ---------- Notas ----------

probar "POST /notas  crear nota" 200 -X POST "$URL/notas" -H "$JSON" \
    -d '{"abogadoId": 1, "titulo": "Preparar audiencia", "contenido": "Revisar contrato de arrendamiento"}'
NOTA_ID=$(id_creado)

probar "POST /notas  sin título (@Valid)" 400 -X POST "$URL/notas" -H "$JSON" \
    -d '{"abogadoId": 1, "contenido": "Nota sin título"}'

probar "POST /notas  abogado que no existe" 404 -X POST "$URL/notas" -H "$JSON" \
    -d '{"abogadoId": 999, "titulo": "Nota"}'

probar "GET /notas?abogadoId=1  notas del abogado" 200 "$URL/notas?abogadoId=1"

probar "GET /notas  sin abogadoId" 400 "$URL/notas"

probar "PUT /notas/$NOTA_ID  marcar como completada" 200 -X PUT "$URL/notas/$NOTA_ID" -H "$JSON" \
    -d '{"titulo": "Preparar audiencia", "contenido": "Revisar contrato de arrendamiento", "completada": true}'

probar "PUT /notas/$NOTA_ID  sin título (@Valid)" 400 -X PUT "$URL/notas/$NOTA_ID" -H "$JSON" \
    -d '{"titulo": "", "completada": true}'

probar "PUT /notas/999  nota que no existe" 404 -X PUT "$URL/notas/999" -H "$JSON" \
    -d '{"titulo": "Nada"}'

probar "DELETE /notas/$NOTA_ID  eliminar nota" 200 -X DELETE "$URL/notas/$NOTA_ID"

probar "DELETE /notas/$NOTA_ID  ya no existe" 404 -X DELETE "$URL/notas/$NOTA_ID"


# =====================================================================
# Pruebas de Luisa: mensajes (texto y archivos), notificaciones,
# eventos y contactos
# =====================================================================

# Archivos de prueba para el chat: una nota de voz falsa (webm) y un documento
echo "audio de prueba" > "$TEMPORAL/audio.webm"
echo "documento de prueba" > "$TEMPORAL/documento.pdf"

# ---------- Mensajes de texto ----------

probar "GET /mensajes?hilo=cliente-1  conversación de Carlos Vega" 200 "$URL/mensajes?hilo=cliente-1"

probar "GET /mensajes  sin hilo" 400 "$URL/mensajes"

probar "POST /mensajes  el cliente escribe (avisa a su abogado)" 200 -X POST "$URL/mensajes" -H "$JSON" \
    -d '{"hilo": "cliente-1", "remitente": "cliente", "texto": "Ya firmé los documentos."}'

probar "POST /mensajes  el admin escribe a un abogado" 200 -X POST "$URL/mensajes" -H "$JSON" \
    -d '{"hilo": "admin-abogado-1", "remitente": "admin", "texto": "Juan, revisa el expediente EXP-0001 por favor."}'

probar "POST /mensajes  remitente que no pertenece al hilo" 400 -X POST "$URL/mensajes" -H "$JSON" \
    -d '{"hilo": "cliente-1", "remitente": "admin", "texto": "Hola"}'

probar "POST /mensajes  hilo con formato incorrecto" 400 -X POST "$URL/mensajes" -H "$JSON" \
    -d '{"hilo": "grupo-1", "remitente": "cliente", "texto": "Hola"}'

probar "POST /mensajes  sin texto" 400 -X POST "$URL/mensajes" -H "$JSON" \
    -d '{"hilo": "cliente-1", "remitente": "cliente"}'

probar "POST /mensajes  cliente que no existe" 404 -X POST "$URL/mensajes" -H "$JSON" \
    -d '{"hilo": "cliente-999", "remitente": "cliente", "texto": "Hola"}'

# ---------- Mensajes con archivos (multipart) ----------

probar "POST /mensajes/archivos  nota de voz (audio webm)" 200 -X POST "$URL/mensajes/archivos" \
    -F "hilo=cliente-1" -F "remitente=cliente" -F "tipo=audio" -F "archivo=@$TEMPORAL/audio.webm"
AUDIO_ID=$(id_creado)
AUDIO_URL=$(echo "$CUERPO" | grep -o '/uploads/[^"]*')

probar "GET $AUDIO_URL  el audio se puede descargar" 200 -o /dev/null "$URL$AUDIO_URL"

probar "POST /mensajes/archivos  imagen del abogado" 200 -X POST "$URL/mensajes/archivos" \
    -F "hilo=cliente-1" -F "remitente=abogado" -F "tipo=img" -F "archivo=@$TEMPORAL/foto.png"

probar "POST /mensajes/archivos  documento PDF" 200 -X POST "$URL/mensajes/archivos" \
    -F "hilo=admin-abogado-1" -F "remitente=admin" -F "tipo=file" -F "archivo=@$TEMPORAL/documento.pdf"

probar "POST /mensajes/archivos  un .txt enviado como imagen" 400 -X POST "$URL/mensajes/archivos" \
    -F "hilo=cliente-1" -F "remitente=cliente" -F "tipo=img" -F "archivo=@$TEMPORAL/nota.txt"

probar "POST /mensajes/archivos  tipo no válido" 400 -X POST "$URL/mensajes/archivos" \
    -F "hilo=cliente-1" -F "remitente=cliente" -F "tipo=video" -F "archivo=@$TEMPORAL/audio.webm"

probar "POST /mensajes/archivos  sin archivo" 400 -X POST "$URL/mensajes/archivos" \
    -F "hilo=cliente-1" -F "remitente=cliente" -F "tipo=audio"

probar "POST /mensajes/archivos  remitente que no pertenece al hilo" 400 -X POST "$URL/mensajes/archivos" \
    -F "hilo=admin-abogado-1" -F "remitente=cliente" -F "tipo=audio" -F "archivo=@$TEMPORAL/audio.webm"

# ---------- Leídos y eliminar mensajes ----------

probar "PUT /mensajes/leidos?hilo=cliente-1&lector=abogado  el abogado abre el chat" 200 -X PUT "$URL/mensajes/leidos?hilo=cliente-1&lector=abogado"

probar "PUT /mensajes/leidos?hilo=cliente-1&lector=juez  lector no válido" 400 -X PUT "$URL/mensajes/leidos?hilo=cliente-1&lector=juez"

probar "PUT /mensajes/leidos  sin hilo" 400 -X PUT "$URL/mensajes/leidos?lector=abogado"

probar "DELETE /mensajes/$AUDIO_ID  eliminar la nota de voz (y su archivo)" 200 -X DELETE "$URL/mensajes/$AUDIO_ID"

probar "GET $AUDIO_URL  el archivo ya se borró" 404 "$URL$AUDIO_URL"

probar "DELETE /mensajes/$AUDIO_ID  ya no existe" 404 -X DELETE "$URL/mensajes/$AUDIO_ID"

# ---------- Notificaciones ----------

probar "GET /notificaciones?usuarioId=2  avisos del Dr. Juan Ramírez" 200 "$URL/notificaciones?usuarioId=2"

probar "GET /notificaciones  sin usuarioId" 400 "$URL/notificaciones"

probar "POST /notificaciones  crear notificación" 200 -X POST "$URL/notificaciones" -H "$JSON" \
    -d '{"usuarioId": 1, "mensaje": "Prueba de notificación"}'
NOTIFICACION_ID=$(id_creado)

probar "POST /notificaciones  sin mensaje (@Valid)" 400 -X POST "$URL/notificaciones" -H "$JSON" \
    -d '{"usuarioId": 1, "mensaje": ""}'

probar "POST /notificaciones  usuario que no existe" 404 -X POST "$URL/notificaciones" -H "$JSON" \
    -d '{"usuarioId": 999, "mensaje": "No llega"}'

probar "PUT /notificaciones/leidas?usuarioId=2  marcar todas como leídas" 200 -X PUT "$URL/notificaciones/leidas?usuarioId=2"

probar "PUT /notificaciones/leidas?usuarioId=999  usuario que no existe" 404 -X PUT "$URL/notificaciones/leidas?usuarioId=999"

probar "DELETE /notificaciones/$NOTIFICACION_ID  eliminar notificación" 200 -X DELETE "$URL/notificaciones/$NOTIFICACION_ID"

probar "DELETE /notificaciones/$NOTIFICACION_ID  ya no existe" 404 -X DELETE "$URL/notificaciones/$NOTIFICACION_ID"

# ---------- Eventos ----------

probar "POST /eventos  crear evento" 200 -X POST "$URL/eventos" -H "$JSON" \
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
EVENTO_ID=$(id_creado)

probar "POST /eventos  sin título (se guarda como Evento)" 200 -X POST "$URL/eventos" -H "$JSON" \
    -d '{"fecha": "2026-12-03", "alcance": "abogado-1", "usuarioId": 2}'

probar "POST /eventos  color no válido" 400 -X POST "$URL/eventos" -H "$JSON" \
    -d '{"titulo": "Junta", "fecha": "2026-11-21", "color": "#000000", "alcance": "admin", "usuarioId": 1}'

probar "POST /eventos  alcance no válido" 400 -X POST "$URL/eventos" -H "$JSON" \
    -d '{"titulo": "Junta", "fecha": "2026-11-21", "alcance": "todos", "usuarioId": 1}'

probar "POST /eventos  hora con formato incorrecto" 400 -X POST "$URL/eventos" -H "$JSON" \
    -d '{"titulo": "Junta", "fecha": "2026-11-21", "hora": "25:99", "alcance": "admin", "usuarioId": 1}'

probar "POST /eventos  sin fecha (@Valid)" 400 -X POST "$URL/eventos" -H "$JSON" \
    -d '{"titulo": "Junta", "alcance": "admin", "usuarioId": 1}'

probar "POST /eventos  usuario que no existe" 404 -X POST "$URL/eventos" -H "$JSON" \
    -d '{"titulo": "Junta", "fecha": "2026-11-21", "alcance": "admin", "usuarioId": 999}'

probar "GET /eventos?alcance=abogado-1  agenda completa del Dr. Juan Ramírez" 200 "$URL/eventos?alcance=abogado-1"

probar "GET /eventos?alcance=abogado-1&anio=2026&mes=11  solo noviembre" 200 "$URL/eventos?alcance=abogado-1&anio=2026&mes=11"

probar "GET /eventos?alcance=admin&anio=2026  falta el mes" 400 "$URL/eventos?alcance=admin&anio=2026"

probar "GET /eventos?alcance=admin&anio=2026&mes=13  mes no válido" 400 "$URL/eventos?alcance=admin&anio=2026&mes=13"

probar "GET /eventos  sin alcance" 400 "$URL/eventos"

probar "PUT /eventos/$EVENTO_ID  cambiar hora y color" 200 -X PUT "$URL/eventos/$EVENTO_ID" -H "$JSON" \
    -d '{
        "titulo": "Audiencia Carlos Vega",
        "fecha": "2026-11-28",
        "hora": "12:30",
        "lugar": "Juzgado 5",
        "descripcion": "Se movió la hora",
        "color": "#C0392B",
        "alcance": "abogado-1"
    }'

probar "PUT /eventos/$EVENTO_ID  color no válido" 400 -X PUT "$URL/eventos/$EVENTO_ID" -H "$JSON" \
    -d '{"fecha": "2026-11-28", "color": "rojo", "alcance": "abogado-1"}'

probar "PUT /eventos/999  evento que no existe" 404 -X PUT "$URL/eventos/999" -H "$JSON" \
    -d '{"fecha": "2026-11-28", "alcance": "admin"}'

probar "DELETE /eventos/$EVENTO_ID  eliminar evento" 200 -X DELETE "$URL/eventos/$EVENTO_ID"

probar "DELETE /eventos/$EVENTO_ID  ya no existe" 404 -X DELETE "$URL/eventos/$EVENTO_ID"

# ---------- Contactos ----------

probar "POST /contactos  contacto del admin" 200 -X POST "$URL/contactos" -H "$JSON" \
    -d '{
        "propietarioTipo": "ADMIN",
        "propietarioId": 1,
        "nombre": "Notaría 12",
        "telefono": "55 2222 3333",
        "email": "contacto@notaria12.mx",
        "notas": "Escrituras"
    }'
CONTACTO_ID=$(id_creado)

probar "POST /contactos  contacto de un abogado (usuario 2)" 200 -X POST "$URL/contactos" -H "$JSON" \
    -d '{"propietarioTipo": "ABOGADO", "propietarioId": 2, "nombre": "Perito Gómez", "telefono": "55 4444 5555"}'

probar "POST /contactos  el usuario 1 no es ABOGADO" 400 -X POST "$URL/contactos" -H "$JSON" \
    -d '{"propietarioTipo": "ABOGADO", "propietarioId": 1, "nombre": "Mal propietario"}'

probar "POST /contactos  propietario que no existe" 404 -X POST "$URL/contactos" -H "$JSON" \
    -d '{"propietarioTipo": "ADMIN", "propietarioId": 999, "nombre": "Sin dueño"}'

probar "POST /contactos  propietarioTipo no válido" 400 -X POST "$URL/contactos" -H "$JSON" \
    -d '{"propietarioTipo": "CLIENTE", "propietarioId": 4, "nombre": "No permitido"}'

probar "POST /contactos  sin nombre (@Valid)" 400 -X POST "$URL/contactos" -H "$JSON" \
    -d '{"propietarioTipo": "ADMIN", "propietarioId": 1}'

probar "GET /contactos  todos los contactos" 200 "$URL/contactos"

probar "GET /contactos?propietarioTipo=ADMIN&propietarioId=1  contactos del admin" 200 "$URL/contactos?propietarioTipo=ADMIN&propietarioId=1"

probar "GET /contactos?propietarioTipo=OTRO  tipo no válido" 400 "$URL/contactos?propietarioTipo=OTRO"

probar "PUT /contactos/$CONTACTO_ID  editar contacto" 200 -X PUT "$URL/contactos/$CONTACTO_ID" -H "$JSON" \
    -d '{
        "propietarioTipo": "ADMIN",
        "propietarioId": 1,
        "nombre": "Notaría 12 (Lic. Pérez)",
        "telefono": "55 2222 3333",
        "email": "contacto@notaria12.mx",
        "notas": "Escrituras y poderes"
    }'

probar "PUT /contactos/999  contacto que no existe" 404 -X PUT "$URL/contactos/999" -H "$JSON" \
    -d '{"propietarioTipo": "ADMIN", "propietarioId": 1, "nombre": "Nadie"}'

probar "DELETE /contactos/$CONTACTO_ID  eliminar contacto" 200 -X DELETE "$URL/contactos/$CONTACTO_ID"

probar "DELETE /contactos/$CONTACTO_ID  ya no existe" 404 -X DELETE "$URL/contactos/$CONTACTO_ID"


# =====================================================================
# Resumen
# =====================================================================

echo ""
echo "====================================================================="
echo "Pruebas revisadas: $TOTAL    Fallas: $FALLAS"
echo "====================================================================="
