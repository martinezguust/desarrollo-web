// =====================================================================
// sesion.js — Guarda quién inició sesión y protege cada página por rol.
// La sesión vive en sessionStorage: se borra al cerrar la pestaña.
// Datos guardados: { usuarioId, rol, nombre, perfilId }
// =====================================================================

let CLAVE_SESION = "sesionJurisMagna"

// Página de inicio de cada rol después del login
let INICIO_POR_ROL = {
    ADMIN: "/admin/admin-dashboard.html",
    ABOGADO: "/abogado/abogado-dashboard.html",
    CLIENTE: "/cliente/cliente-caso.html"
}

function guardarSesion(datos) {
    sessionStorage.setItem(CLAVE_SESION, JSON.stringify(datos))
}

function obtenerSesion() {
    let texto = sessionStorage.getItem(CLAVE_SESION)

    if (!texto) {
        return null
    }

    return JSON.parse(texto)
}

// Se llama al inicio de cada página. Si no hay sesión o el rol no
// corresponde, regresa al login. Devuelve la sesión si todo está bien.
function exigirSesion(rol) {
    let sesion = obtenerSesion()

    if (!sesion || sesion.rol !== rol) {
        window.location.href = "/index.html"
        return null
    }

    return sesion
}

function cerrarSesion() {
    sessionStorage.removeItem(CLAVE_SESION)
    window.location.href = "/index.html"
}
