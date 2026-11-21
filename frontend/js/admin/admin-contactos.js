// =====================================================================
// admin-contactos.js — Contactos del administrador.
// - Los abogados del despacho, con chat (hilo "admin-abogado-{id}").
// - Sus contactos propios (GET/POST/PUT/DELETE /contactos), sin chat.
// =====================================================================

let sesion = exigirSesion("ADMIN")

let listaContactos = []
let listaAbogadosChat = []

let ICONO_BASURA = `<svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/></svg>`
let PUNTO_ROJO = `<span style="position:absolute;top:-3px;right:-3px;width:8px;height:8px;border-radius:50%;background:#C0392B;border:1.5px solid #fff"></span>`

function cargarContactos() {
    let abogados = []

    apiGet("/abogados")
        .then(respuesta => {
            abogados = respuesta
            return apiGet("/contactos", { propietarioTipo: "ADMIN", propietarioId: sesion.usuarioId })
        })
        .then(contactos => {
            listaAbogadosChat = abogados
            listaContactos = contactos
            pintarContactos(abogados, contactos)
            revisarPendientes(abogados)
        })
        .catch(error => {
            mostrarError("No se pudieron cargar los contactos: " + error.message)
        })
}

function pintarContactos(abogados, contactos) {
    let contenedor = $("admin-contactos-grid")

    if (abogados.length === 0 && contactos.length === 0) {
        contenedor.innerHTML = `<div class="empty-state">Sin contactos.</div>`
        return
    }

    contenedor.innerHTML = ""

    abogados.forEach(abogado => {
        let foto = iniciales(abogado.nombre)

        if (abogado.fotoUrl) {
            foto = `<img src="${abogado.fotoUrl}" style="width:100%;height:100%;border-radius:50%;object-fit:cover">`
        }

        contenedor.innerHTML += `
            <div class="contacto-card glass">
                <div class="contacto-avatar" style="font-size:16px">${foto}</div>
                <div class="contacto-info">
                    <h4>${escaparHtml(abogado.nombre)}</h4>
                    <p>${escaparHtml(abogado.telefono || "—")}</p>
                    <p>${escaparHtml(abogado.email || "—")}</p>
                    <p style="color:var(--gold-dark);font-size:11px;margin-top:2px">${escaparHtml(abogado.especialidad || "—")}</p>
                </div>
                <button class="btn btn-blue btn-sm" style="align-self:center;margin-left:auto;white-space:nowrap;position:relative" onclick="abrirChatAbogado(${abogado.id})">
                    <span id="punto-abogado-${abogado.id}"></span>Chat
                </button>
            </div>`
    })

    // Contactos propios: sin chat (acuerdo de la Fase 1)
    contactos.forEach(contacto => {
        contenedor.innerHTML += `
            <div class="contacto-card glass">
                <div class="contacto-avatar" style="background:linear-gradient(135deg,var(--blue),#1a6fa8)">${escaparHtml((contacto.nombre || "?")[0].toUpperCase())}</div>
                <div class="contacto-info">
                    <h4>${escaparHtml(contacto.nombre)}</h4>
                    <p>${escaparHtml(contacto.telefono || "—")}</p>
                    <p>${escaparHtml(contacto.email || "—")}</p>
                    ${contacto.notas ? `<p style="font-size:11px;margin-top:2px">${escaparHtml(contacto.notas)}</p>` : ""}
                </div>
                <div style="display:flex;gap:6px;align-items:center;margin-left:auto">
                    <button class="btn btn-outline btn-sm" onclick="abrirEditarContacto(${contacto.id})">✏️</button>
                    <button class="btn btn-danger btn-sm" onclick="eliminarContacto(${contacto.id})">${ICONO_BASURA}</button>
                </div>
            </div>`
    })
}

// Pone el punto rojo en el botón Chat de los abogados que escribieron
function revisarPendientes(abogados) {
    abogados.forEach(abogado => {
        hayMensajesSinLeer("admin-abogado-" + abogado.id, "admin")
            .then(pendiente => {
                $("punto-abogado-" + abogado.id).innerHTML = pendiente ? PUNTO_ROJO : ""
            })
            .catch(error => {
                console.log(error.message)
            })
    })
}

// chat.js la llama al cerrar el chat
function despuesDeCerrarChat() {
    revisarPendientes(listaAbogadosChat)
}

function abrirChatAbogado(id) {
    let abogado = listaAbogadosChat.find(elemento => elemento.id === id)

    abrirChat("admin-abogado-" + abogado.id, "Chat con " + abogado.nombre, abogado.email || "", "admin")
}

// ---------- Contactos propios ----------

function abrirNuevoContacto() {
    $("nco-titulo").textContent = "Nuevo Contacto"
    $("nco-id").value = ""
    $("nco-nombre").value = ""
    $("nco-tel").value = ""
    $("nco-email").value = ""
    $("nco-notas").value = ""
    abrirModal("ov-nuevo-contacto-admin")
}

function abrirEditarContacto(id) {
    let contacto = listaContactos.find(elemento => elemento.id === id)

    $("nco-titulo").textContent = "Editar Contacto"
    $("nco-id").value = contacto.id
    $("nco-nombre").value = contacto.nombre
    $("nco-tel").value = contacto.telefono || ""
    $("nco-email").value = contacto.email || ""
    $("nco-notas").value = contacto.notas || ""
    abrirModal("ov-nuevo-contacto-admin")
}

function guardarContacto() {
    let id = $("nco-id").value

    let datos = {
        propietarioTipo: "ADMIN",
        propietarioId: sesion.usuarioId,
        nombre: $("nco-nombre").value.trim(),
        telefono: $("nco-tel").value.trim(),
        email: $("nco-email").value.trim(),
        notas: $("nco-notas").value.trim()
    }

    let peticion = null

    if (id) {
        peticion = apiPut("/contactos/" + id, datos)
    } else {
        peticion = apiPost("/contactos", datos)
    }

    peticion
        .then(() => {
            cerrarModal("ov-nuevo-contacto-admin")
            cargarContactos()
            mostrarExito(id ? "Contacto actualizado" : "Contacto agregado")
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

function eliminarContacto(id) {
    if (!confirm("¿Eliminar este contacto?")) {
        return
    }

    apiDelete("/contactos/" + id)
        .then(() => {
            cargarContactos()
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

if (sesion) {
    cargarContactos()
}
