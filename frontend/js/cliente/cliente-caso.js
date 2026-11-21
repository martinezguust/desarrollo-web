// =====================================================================
// cliente-caso.js — Inicio del cliente en el celular (RF-12, RF-13, RF-14).
// - GET /clientes/{id}: avance, estado, honorarios, comentario y Drive.
// - GET /casos?clienteId=: color del anillo según el estado del caso.
// - GET /abogados/{id}: perfil de su abogado.
// - El comentario se escucha con síntesis de voz en español.
// =====================================================================

let sesion = exigirSesion("CLIENTE")

let clienteActual = null
let leyendo = false

// Color del anillo según el estado del caso (RF-12)
let COLOR_POR_ESTADO = {
    Activo: "#27AE60",
    EnProceso: "#F39C12",
    Ganado: "#27AE60",
    Perdido: "#C0392B",
    Cancelado: "#C0392B",
    Inactivo: "#9CA3AF"
}

let CIRCUNFERENCIA = 351.86

function cargarCaso() {
    apiGet("/clientes/" + sesion.perfilId)
        .then(cliente => {
            clienteActual = cliente
            pintarCliente(cliente)
            return apiGet("/casos", { clienteId: cliente.id })
        })
        .then(casos => {
            pintarAnillo(casos)
        })
        .catch(error => {
            mostrarError("No se pudo cargar tu caso: " + error.message)
        })
}

function pintarCliente(cliente) {
    $("mob-nombre").textContent = cliente.nombre
    $("mob-inicial").textContent = cliente.nombre[0]
    $("mob-avance").textContent = cliente.avance + "%"
    $("mob-estado").textContent = cliente.estado
    $("mob-hon").textContent = formatoMoneda(cliente.honorarios)
    $("mob-desc").textContent = cliente.descripcion || "—"
    $("mob-abogado").textContent = cliente.abogadoNombre || "—"
    $("mob-chart-pct").textContent = cliente.avance + "%"

    if (cliente.comentario) {
        $("mob-comments-box").innerHTML = `
            <div style="display:flex;gap:8px">
                <div>
                    <div style="font-size:12px;background:rgba(212,175,55,.1);border-radius:10px;padding:8px 10px;color:var(--dark);line-height:1.5">💬 ${escaparHtml(cliente.comentario)}</div>
                    <div style="font-size:10px;color:var(--body);margin-top:3px">Tu abogado</div>
                </div>
            </div>`
    } else {
        $("mob-comments-box").innerHTML = `<p style="font-size:13px;color:var(--body);padding:4px">Sin comentarios de tu abogado.</p>`
    }
}

// El anillo se llena según el avance; el color sale del caso abierto
function pintarAnillo(casos) {
    let cliente = clienteActual
    let arco = $("mob-ring-arc")

    let caso = casos.find(elemento => elemento.estado === "Activo")

    if (!caso) {
        caso = casos.find(elemento => elemento.estado === "EnProceso")
    }

    if (!caso && casos.length > 0) {
        caso = casos[casos.length - 1]
    }

    let estado = caso ? caso.estado : cliente.estado
    arco.style.stroke = COLOR_POR_ESTADO[estado] || "#27AE60"

    // Pequeña pausa para que se vea la animación
    setTimeout(() => {
        arco.style.strokeDashoffset = CIRCUNFERENCIA - (cliente.avance / 100 * CIRCUNFERENCIA)
    }, 80)

    // Botón de Drive: primero el enlace del cliente; si no hay, el del caso
    let drive = cliente.driveUrl || (caso && caso.driveUrl) || ""
    let boton = $("mob-drive-btn")

    if (drive) {
        boton.href = drive
        boton.style.display = "flex"
    } else {
        boton.style.display = "none"
    }
}

// Lee el comentario del abogado en voz alta (RF-13)
function escucharComentario() {
    if (!clienteActual || !clienteActual.comentario) {
        mostrarError("No hay comentario de tu abogado.")
        return
    }

    if (!("speechSynthesis" in window)) {
        mostrarError("Tu navegador no permite la síntesis de voz.")
        return
    }

    if (leyendo) {
        window.speechSynthesis.cancel()
        leyendo = false
        return
    }

    let voz = new SpeechSynthesisUtterance(clienteActual.comentario)
    voz.lang = "es-MX"
    voz.rate = 0.95

    voz.onend = () => {
        leyendo = false
    }

    window.speechSynthesis.speak(voz)
    leyendo = true
}

// ---------- Perfil del abogado (ov-ab-cliente, RF-14) ----------

function crearModalAbogado() {
    if ($("ov-ab-cliente")) {
        return
    }

    let modal = document.createElement("div")
    modal.className = "overlay-bg"
    modal.id = "ov-ab-cliente"
    modal.style.zIndex = "2000"
    modal.innerHTML = `<div id="ov-ab-cliente-body" style="background:var(--bg);border-radius:20px;width:340px;max-width:92vw;overflow:hidden;box-shadow:0 20px 60px rgba(0,0,0,.4)"></div>`

    activarCierreConFondo(modal)
    document.body.appendChild(modal)
}

function verPerfilAbogadoCliente() {
    if (!clienteActual) {
        return
    }

    apiGet("/abogados/" + clienteActual.abogadoId)
        .then(abogado => {
            crearModalAbogado()
            pintarPerfilAbogado(abogado)
            abrirModal("ov-ab-cliente")
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

function pintarPerfilAbogado(abogado) {
    let porcentaje = abogado.efectividad
    let circulo = 163.36
    let desfase = circulo - (porcentaje / 100 * circulo)

    let foto = `<span style="font-size:22px;font-weight:700;color:var(--dark)">${iniciales(abogado.nombre)}</span>`

    if (abogado.fotoUrl) {
        foto = `<img src="${abogado.fotoUrl}" style="width:100%;height:100%;object-fit:cover;border-radius:50%">`
    }

    $("ov-ab-cliente-body").innerHTML = `
        <div style="background:var(--dark);padding:16px 20px;display:flex;justify-content:space-between;align-items:center">
            <h3 style="color:#fff;font-size:16px">Perfil del Abogado</h3>
            <button onclick="cerrarModal('ov-ab-cliente')" style="background:none;border:none;color:rgba(255,255,255,.6);font-size:18px;cursor:pointer">✕</button>
        </div>
        <div style="padding:20px;display:flex;flex-direction:column;align-items:center;gap:12px">
            <div style="width:72px;height:72px;border-radius:50%;background:linear-gradient(135deg,var(--gold),var(--gold-dark));display:flex;align-items:center;justify-content:center;overflow:hidden">${foto}</div>
            <div style="text-align:center">
                <div style="font-size:16px;font-weight:700;color:var(--dark)">${escaparHtml(abogado.nombre)}</div>
                <div style="font-size:12px;color:var(--body)">${escaparHtml(abogado.especialidad || "Abogado")}</div>
            </div>
            <div style="display:flex;align-items:center;gap:16px;width:100%;background:rgba(212,175,55,.05);border-radius:12px;padding:12px">
                <div style="position:relative;width:70px;height:70px;flex-shrink:0">
                    <svg width="70" height="70" viewBox="0 0 70 70">
                        <circle cx="35" cy="35" r="26" fill="none" stroke="#E5E7EB" stroke-width="7"/>
                        <circle cx="35" cy="35" r="26" fill="none" stroke="#27AE60" stroke-width="7"
                            stroke-linecap="round" stroke-dasharray="163.36"
                            stroke-dashoffset="${desfase}" transform="rotate(-90 35 35)"
                            style="transition:stroke-dashoffset .8s ease"/>
                    </svg>
                    <div style="position:absolute;inset:0;display:flex;align-items:center;justify-content:center;font-size:13px;font-weight:800;color:var(--dark)">${porcentaje}%</div>
                </div>
                <div>
                    <div style="font-size:11px;color:var(--body);margin-bottom:3px">Efectividad</div>
                    <div style="font-size:15px;font-weight:700;color:var(--green)">${porcentaje}%</div>
                    <div style="font-size:11px;color:var(--body);margin-top:2px">${abogado.totalCasos} casos · ${abogado.casosGanados} ganados</div>
                </div>
            </div>
            <div style="width:100%;display:flex;flex-direction:column;gap:8px">
                <div style="background:#fff;border:1px solid var(--gb);border-radius:10px;padding:10px 12px">
                    <div style="font-size:10px;color:var(--body);text-transform:uppercase;letter-spacing:.5px;font-weight:600;margin-bottom:2px">Estudios</div>
                    <span style="font-size:13px;font-weight:500;color:var(--dark)">${escaparHtml(abogado.estudios || "—")}</span>
                </div>
                <div style="background:#fff;border:1px solid var(--gb);border-radius:10px;padding:10px 12px">
                    <div style="font-size:10px;color:var(--body);text-transform:uppercase;letter-spacing:.5px;font-weight:600;margin-bottom:2px">Especialidad</div>
                    <span style="font-size:13px;font-weight:500;color:var(--dark)">${escaparHtml(abogado.especialidad || "—")}</span>
                </div>
            </div>
        </div>`
}

if (sesion) {
    cargarCaso()
    revisarChatPendiente("cliente-" + sesion.perfilId)
}
