// =====================================================================
// login.js — Inicio de sesión con los tres roles (RF-01).
// Manda { username, password, rol } a POST /sesiones; si el backend
// responde bien, guarda la sesión y abre la página de inicio del rol.
// =====================================================================

let rolSeleccionado = "ADMIN"

// Si ya hay una sesión abierta, se entra directo
let sesionAbierta = obtenerSesion()

if (sesionAbierta) {
    window.location.href = INICIO_POR_ROL[sesionAbierta.rol]
}

function seleccionarRol(rol, boton) {
    rolSeleccionado = rol

    document.querySelectorAll(".role-btn").forEach(otroBoton => {
        otroBoton.classList.remove("active")
    })

    boton.classList.add("active")
    ocultarErrorLogin()
}

// El botón se pinta de dorado cuando ya hay usuario y contraseña
function revisarLogin() {
    let usuario = $("login-user").value.trim()
    let password = $("login-pass").value
    let boton = document.querySelector(".login-submit")

    if (usuario && password) {
        boton.classList.add("ready")
    } else {
        boton.classList.remove("ready")
    }
}

function mostrarErrorLogin(mensaje) {
    $("login-error").textContent = mensaje
    $("login-error").style.display = "block"
}

function ocultarErrorLogin() {
    $("login-error").style.display = "none"
}

function iniciarSesion() {
    let usuario = $("login-user").value.trim()
    let password = $("login-pass").value

    ocultarErrorLogin()

    if (!usuario || !password) {
        mostrarErrorLogin("Escribe tu usuario y tu contraseña.")
        return
    }

    let datos = {
        username: usuario,
        password: password,
        rol: rolSeleccionado
    }

    apiPost("/sesiones", datos)
        .then(sesion => {
            guardarSesion(sesion)
            window.location.href = INICIO_POR_ROL[sesion.rol]
        })
        .catch(error => {
            mostrarErrorLogin(error.message)
        })
}
