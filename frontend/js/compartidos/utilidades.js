// =====================================================================
// utilidades.js — Funciones pequeñas que usan todas las páginas:
// buscar elementos, dar formato a dinero y fechas, abrir y cerrar
// modales y mostrar confirmaciones o errores (RF-15, RNF-08).
// =====================================================================

let NOMBRES_MESES = ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio",
    "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"]

// Atajo para document.getElementById
function $(id) {
    return document.getElementById(id)
}

// Evita que el texto que escribe el usuario se interprete como HTML
function escaparHtml(texto) {
    if (texto === null || texto === undefined) {
        return ""
    }

    return String(texto)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;")
}

// 45000 -> "$45,000"
function formatoMoneda(numero) {
    let valor = Number(numero) || 0

    return valor.toLocaleString("es-MX", {
        style: "currency",
        currency: "MXN",
        maximumFractionDigits: 0
    })
}

// "2026-10-31" -> "31/10/2026"
function formatoFecha(fecha) {
    if (!fecha) {
        return "—"
    }

    let partes = String(fecha).slice(0, 10).split("-")
    let dia = new Date(Number(partes[0]), Number(partes[1]) - 1, Number(partes[2]))

    return dia.toLocaleDateString("es-MX")
}

// "2026-11-20T09:05:00" -> "09:05"
function formatoHora(fechaHora) {
    if (!fechaHora) {
        return ""
    }

    return String(fechaHora).slice(11, 16)
}

// "viernes, 20 de noviembre de 2026"
function fechaHoy() {
    return new Date().toLocaleDateString("es-MX", {
        weekday: "long",
        day: "numeric",
        month: "long",
        year: "numeric"
    })
}

// "Dr. Juan Ramírez" -> "DJ"
function iniciales(nombre) {
    if (!nombre) {
        return "?"
    }

    return nombre.split(" ")
        .filter(palabra => palabra !== "")
        .map(palabra => palabra[0])
        .join("")
        .slice(0, 2)
        .toUpperCase()
}

// Color de la etiqueta según el estado del caso
function claseEstado(estado) {
    let clases = {
        Activo: "badge-gold",
        EnProceso: "badge-blue",
        Ganado: "badge-green",
        Perdido: "badge-red",
        Cancelado: "badge-gray"
    }

    return clases[estado] || "badge-blue"
}

// Texto del estado del caso para mostrarlo en pantalla ("EnProceso" -> "En Proceso")
function nombreEstado(estado) {
    if (estado === "EnProceso") {
        return "En Proceso"
    }

    return estado
}

// Muestra u oculta la contraseña de un campo (el "ojo")
function alternarPassword(id) {
    let campo = $(id)

    if (!campo) {
        return
    }

    if (campo.type === "password") {
        campo.type = "text"
    } else {
        campo.type = "password"
    }
}

// ---------- Modales ----------

function abrirModal(id) {
    $(id).classList.add("open")
}

function cerrarModal(id) {
    $(id).classList.remove("open")
}

// Un clic en el fondo oscuro cierra el modal
function activarCierreConFondo(modal) {
    modal.addEventListener("click", evento => {
        if (evento.target === modal) {
            modal.classList.remove("open")
        }
    })
}

document.querySelectorAll(".overlay-bg").forEach(modal => {
    activarCierreConFondo(modal)
})

// Crea un modal sencillo (éxito o error) si la página no lo tiene
function crearModalAviso(id, claseIcono, icono, idMensaje, idBotones) {
    let modal = $(id)

    if (modal) {
        return modal
    }

    modal = document.createElement("div")
    modal.className = "overlay-bg"
    modal.id = id
    modal.style.zIndex = "3000"
    modal.innerHTML = `
        <div class="success-modal glass">
            <div class="${claseIcono}">${icono}</div>
            <h3 id="${idMensaje}"></h3>
            <div id="${idBotones}"></div>
        </div>`

    activarCierreConFondo(modal)
    document.body.appendChild(modal)

    return modal
}

// Modal de confirmación tras una operación exitosa (RF-15)
function mostrarExito(mensaje, botones) {
    crearModalAviso("ov-exito", "success-check", "✓", "exito-msg", "exito-btns")

    $("exito-msg").textContent = mensaje

    if (botones) {
        $("exito-btns").innerHTML = botones
    } else {
        $("exito-btns").innerHTML = `<button class="btn btn-primary" onclick="cerrarModal('ov-exito')">Aceptar</button>`
    }

    abrirModal("ov-exito")
}

// Modal de error con el mensaje que manda el backend (RNF-08)
function mostrarError(mensaje) {
    crearModalAviso("ov-error", "error-check", "!", "error-msg-texto", "error-btns")

    $("error-msg-texto").textContent = mensaje
    $("error-btns").innerHTML = `<button class="btn btn-primary" onclick="cerrarModal('ov-error')">Entendido</button>`

    abrirModal("ov-error")
}
