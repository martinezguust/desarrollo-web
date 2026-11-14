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


# =====================================================================
# Pruebas de Luisa: mensajes, notificaciones, eventos, contactos
# =====================================================================
