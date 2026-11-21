// =====================================================================
// calendario.js — Calendario mensual con eventos y días festivos
// mexicanos (RF-08, RF-09). Lo usan admin-calendario y abogado-calendario.
//
// Cada agenda tiene su "alcance": "admin" o "abogado-{id}".
// Eventos: GET /eventos?alcance=&anio=&mes=, POST, PUT y DELETE /eventos.
// =====================================================================

let COLORES_EVENTO = ["#D4AF37", "#27AE60", "#2980B9", "#C0392B", "#8E44AD", "#E67E22"]
let DIAS_SEMANA = ["Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"]

let calContenedor = null
let calAlcance = null
let calAnio = new Date().getFullYear()
let calMes = new Date().getMonth()
let calEventos = []
let calDiaSeleccionado = null
let colorElegido = COLORES_EVENTO[0]

// ---------- Días festivos (Ley Federal del Trabajo, art. 74) ----------

// Día del mes del n-ésimo lunes (n = 1, 2, 3...) de un mes (0 = enero)
function enesimoLunes(anio, mes, n) {
    let primero = new Date(anio, mes, 1).getDay()
    let primerLunes = 1 + ((8 - primero) % 7)

    return primerLunes + (n - 1) * 7
}

function textoFecha(anio, mes, dia) {
    return anio + "-" + String(mes + 1).padStart(2, "0") + "-" + String(dia).padStart(2, "0")
}

// Regresa { "2026-01-01": "Año Nuevo", ... } para un año
function festivosDelAnio(anio) {
    let festivos = {}

    festivos[textoFecha(anio, 0, 1)] = "Año Nuevo"
    festivos[textoFecha(anio, 1, enesimoLunes(anio, 1, 1))] = "Día de la Constitución"
    festivos[textoFecha(anio, 2, enesimoLunes(anio, 2, 3))] = "Natalicio de Benito Juárez"
    festivos[textoFecha(anio, 4, 1)] = "Día del Trabajo"
    festivos[textoFecha(anio, 8, 16)] = "Día de la Independencia"
    festivos[textoFecha(anio, 10, enesimoLunes(anio, 10, 3))] = "Día de la Revolución"
    festivos[textoFecha(anio, 11, 25)] = "Navidad"

    // Cambio de Poder Ejecutivo Federal, cada 6 años (2024, 2030...)
    if ((anio - 2024) % 6 === 0) {
        festivos[textoFecha(anio, 9, 1)] = "Transmisión del Poder Ejecutivo"
    }

    return festivos
}

// ---------- Pintar el mes ----------

function iniciarCalendario(idContenedor, alcance) {
    calContenedor = idContenedor
    calAlcance = alcance
    cargarEventosMes()
}

function cargarEventosMes() {
    apiGet("/eventos", { alcance: calAlcance, anio: calAnio, mes: calMes + 1 })
        .then(eventos => {
            calEventos = eventos
            pintarCalendario()

            if (calDiaSeleccionado) {
                seleccionarDia(calDiaSeleccionado)
            }
        })
        .catch(error => {
            mostrarError("No se pudieron cargar los eventos: " + error.message)
        })
}

function eventosDelDia(fecha) {
    return calEventos.filter(evento => evento.fecha === fecha)
}

function pintarCalendario() {
    let primerDia = new Date(calAnio, calMes, 1).getDay()
    let diasDelMes = new Date(calAnio, calMes + 1, 0).getDate()
    let hoy = new Date()
    let festivos = festivosDelAnio(calAnio)

    let html = `
        <div class="cal-wrap">
            <div class="cal-header">
                <button class="cal-nav" onclick="cambiarMes(-1)">‹</button>
                <div><span class="cal-month">${NOMBRES_MESES[calMes]}</span><span class="cal-year">${calAnio}</span></div>
                <button class="cal-nav" onclick="cambiarMes(1)">›</button>
            </div>
            <div class="cal-grid">`

    DIAS_SEMANA.forEach(dia => {
        html += `<div class="cal-dh">${dia}</div>`
    })

    for (let i = 0; i < primerDia; i++) {
        html += `<div class="cal-cell"></div>`
    }

    for (let dia = 1; dia <= diasDelMes; dia++) {
        let fecha = textoFecha(calAnio, calMes, dia)
        let esHoy = hoy.getFullYear() === calAnio && hoy.getMonth() === calMes && hoy.getDate() === dia

        let chips = ""

        if (festivos[fecha]) {
            chips += `<div class="cal-chip" style="background:#7F8C8D" title="Día festivo">🇲🇽 ${festivos[fecha]}</div>`
        }

        eventosDelDia(fecha).forEach(evento => {
            chips += `<div class="cal-chip" style="background:${evento.color};cursor:pointer" onclick="event.stopPropagation(); abrirEditarEvento(${evento.id})">${escaparHtml(evento.titulo)}</div>`
        })

        html += `
            <div class="cal-cell active ${esHoy ? "today" : ""}" onclick="seleccionarDia('${fecha}')">
                <span class="cal-num">${dia}</span>
                ${chips}
            </div>`
    }

    html += `
            </div>
        </div>
        <div class="cal-day-panel" id="cal-day-panel" style="display:none">
            <div class="cal-day-panel-hdr">
                <h4 id="cal-day-title">Selecciona un día</h4>
                <button class="btn btn-primary btn-sm" onclick="abrirNuevoEvento()">+ Agregar evento</button>
            </div>
            <div class="cal-day-panel-body" id="cal-day-body"></div>
        </div>`

    $(calContenedor).innerHTML = html
}

function cambiarMes(direccion) {
    calMes = calMes + direccion

    if (calMes > 11) {
        calMes = 0
        calAnio++
    }

    if (calMes < 0) {
        calMes = 11
        calAnio--
    }

    calDiaSeleccionado = null
    cargarEventosMes()
}

// Panel de abajo con los eventos del día elegido
function seleccionarDia(fecha) {
    calDiaSeleccionado = fecha

    let partes = fecha.split("-")
    let dia = new Date(Number(partes[0]), Number(partes[1]) - 1, Number(partes[2]))
    let titulo = dia.toLocaleDateString("es-MX", { weekday: "long", day: "numeric", month: "long" })

    $("cal-day-panel").style.display = "block"
    $("cal-day-title").textContent = titulo.charAt(0).toUpperCase() + titulo.slice(1)

    let festivo = festivosDelAnio(Number(partes[0]))[fecha]
    let html = ""

    if (festivo) {
        html += `<p style="color:var(--body);font-size:13px">🇲🇽 Día festivo: ${festivo}</p>`
    }

    let eventos = eventosDelDia(fecha)

    if (eventos.length === 0) {
        html += `<p style="color:var(--body);font-size:13px;text-align:center;padding:8px 0">Sin eventos para este día</p>`
    }

    eventos.forEach(evento => {
        let detalle = evento.hora || ""

        if (evento.lugar) {
            detalle += " · " + evento.lugar
        }

        if (evento.descripcion) {
            let corta = evento.descripcion.length > 30 ? evento.descripcion.slice(0, 30) + "..." : evento.descripcion
            detalle += " · " + corta
        }

        html += `
            <div class="cal-ev-item" onclick="abrirEditarEvento(${evento.id})">
                <div class="cal-ev-dot" style="background:${evento.color}"></div>
                <div class="cal-ev-info">
                    <div class="cal-ev-title">${escaparHtml(evento.titulo)}</div>
                    <div class="cal-ev-meta">${escaparHtml(detalle)}</div>
                </div>
                <span class="cal-ev-arrow">›</span>
            </div>`
    })

    $("cal-day-body").innerHTML = html
}

// ---------- Modal de evento ----------

function pintarColores() {
    let html = ""

    COLORES_EVENTO.forEach(color => {
        let clase = color === colorElegido ? "sel" : ""
        html += `<button class="color-dot-btn ${clase}" style="background:${color}" onclick="elegirColor('${color}', this)"></button>`
    })

    $("ev-colors").innerHTML = html
}

function elegirColor(color, boton) {
    colorElegido = color

    document.querySelectorAll(".color-dot-btn").forEach(otro => {
        otro.classList.remove("sel")
    })

    boton.classList.add("sel")
}

function abrirNuevoEvento() {
    $("ev-overlay-title").textContent = "Agregar Evento"
    $("ev-fecha").value = calDiaSeleccionado
    $("ev-edit-id").value = ""
    $("ev-btn-delete").style.display = "none"
    $("ev-titulo").value = ""
    $("ev-hora").value = "09:00"
    $("ev-lugar").value = ""
    $("ev-desc").value = ""

    colorElegido = COLORES_EVENTO[0]
    pintarColores()
    abrirModal("ov-evento")
}

function abrirEditarEvento(id) {
    let evento = calEventos.find(elemento => elemento.id === id)

    $("ev-overlay-title").textContent = "Editar Evento"
    $("ev-fecha").value = evento.fecha
    $("ev-edit-id").value = evento.id
    $("ev-btn-delete").style.display = "inline-flex"
    $("ev-titulo").value = evento.titulo
    $("ev-hora").value = evento.hora || ""
    $("ev-lugar").value = evento.lugar || ""
    $("ev-desc").value = evento.descripcion || ""

    colorElegido = evento.color
    pintarColores()
    abrirModal("ov-evento")
}

function guardarEvento() {
    let id = $("ev-edit-id").value
    let sesionCalendario = obtenerSesion()

    let datos = {
        titulo: $("ev-titulo").value.trim() || "Evento",
        fecha: $("ev-fecha").value,
        hora: $("ev-hora").value.trim() || null,
        lugar: $("ev-lugar").value.trim(),
        descripcion: $("ev-desc").value.trim(),
        color: colorElegido,
        alcance: calAlcance,
        usuarioId: sesionCalendario.usuarioId
    }

    // La hora es opcional, pero si se escribe debe ser HH:MM (00:00 a 23:59)
    if (datos.hora && !/^([01][0-9]|2[0-3]):[0-5][0-9]$/.test(datos.hora)) {
        mostrarError("La hora debe tener el formato HH:MM, por ejemplo 09:30.")
        return
    }

    let peticion = null

    if (id) {
        peticion = apiPut("/eventos/" + id, datos)
    } else {
        peticion = apiPost("/eventos", datos)
    }

    peticion
        .then(() => {
            cerrarModal("ov-evento")
            calDiaSeleccionado = datos.fecha
            cargarEventosMes()
            mostrarExito(id ? "Evento actualizado" : "Evento agregado")
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

function eliminarEvento() {
    let id = $("ev-edit-id").value

    apiDelete("/eventos/" + id)
        .then(() => {
            cerrarModal("ov-evento")
            cargarEventosMes()
        })
        .catch(error => {
            mostrarError(error.message)
        })
}
