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
# =====================================================================

URL="http://localhost:8081"

# Imprime un título antes de cada prueba
function titulo() {
    echo ""
    echo "=== $1 ==="
}


# =====================================================================
# Pruebas de Gustavo: sesiones, abogados, movimientos
# =====================================================================

titulo "POST /sesiones  login del admin (200)"
curl -s -X POST "$URL/sesiones" \
    -H "Content-Type: application/json" \
    -d '{"username": "admin", "password": "admin123", "rol": "ADMIN"}'
echo ""

titulo "POST /sesiones  login de un abogado (200)"
curl -s -X POST "$URL/sesiones" \
    -H "Content-Type: application/json" \
    -d '{"username": "jramirez", "password": "abogado123", "rol": "ABOGADO"}'
echo ""

titulo "POST /sesiones  contraseña incorrecta (401)"
curl -s -w "\nHTTP %{http_code}" -X POST "$URL/sesiones" \
    -H "Content-Type: application/json" \
    -d '{"username": "jramirez", "password": "malaclave", "rol": "ABOGADO"}'
echo ""

titulo "POST /sesiones  rol que no corresponde (401)"
curl -s -w "\nHTTP %{http_code}" -X POST "$URL/sesiones" \
    -H "Content-Type: application/json" \
    -d '{"username": "jramirez", "password": "abogado123", "rol": "ADMIN"}'
echo ""

titulo "GET /abogados  listar abogados (200)"
curl -s "$URL/abogados"
echo ""

titulo "GET /abogados/1  detalle de un abogado (200)"
curl -s "$URL/abogados/1"
echo ""

titulo "GET /abogados/999  abogado que no existe (404)"
curl -s -w "\nHTTP %{http_code}" "$URL/abogados/999"
echo ""

titulo "POST /abogados  crear abogado y su usuario (200)"
curl -s -X POST "$URL/abogados" \
    -H "Content-Type: application/json" \
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
echo ""

titulo "POST /abogados  usuario repetido (409)"
curl -s -w "\nHTTP %{http_code}" -X POST "$URL/abogados" \
    -H "Content-Type: application/json" \
    -d '{"nombre": "Otro", "username": "jramirez", "password": "abogado123"}'
echo ""

titulo "POST /abogados  sin contraseña (400)"
curl -s -w "\nHTTP %{http_code}" -X POST "$URL/abogados" \
    -H "Content-Type: application/json" \
    -d '{"nombre": "Sin clave", "username": "sinclave"}'
echo ""

titulo "POST /sesiones  login del abogado recién creado (200)"
curl -s -X POST "$URL/sesiones" \
    -H "Content-Type: application/json" \
    -d '{"username": "aprueba", "password": "abogado123", "rol": "ABOGADO"}'
echo ""

titulo "GET /movimientos  listar movimientos (200)"
curl -s "$URL/movimientos"
echo ""

titulo "POST /movimientos  crear un ingreso (200)"
curl -s -X POST "$URL/movimientos" \
    -H "Content-Type: application/json" \
    -d '{"tipo": "INGRESO", "concepto": "Honorarios", "monto": 15000, "fecha": "2026-11-12"}'
echo ""

titulo "POST /movimientos  concepto que no corresponde al tipo (400)"
curl -s -w "\nHTTP %{http_code}" -X POST "$URL/movimientos" \
    -H "Content-Type: application/json" \
    -d '{"tipo": "INGRESO", "concepto": "Nómina", "monto": 1000, "fecha": "2026-11-12"}'
echo ""

titulo "POST /movimientos  monto negativo (400)"
curl -s -w "\nHTTP %{http_code}" -X POST "$URL/movimientos" \
    -H "Content-Type: application/json" \
    -d '{"tipo": "EGRESO", "concepto": "Renta", "monto": -500, "fecha": "2026-11-12"}'
echo ""


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
