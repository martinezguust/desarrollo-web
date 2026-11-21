// =====================================================================
// abogado-contactos.js — Contactos del abogado: el administrador
// (hilo "admin-abogado-{id}") y sus clientes (hilo "cliente-{id}").
// El punto rojo indica mensajes sin leer.
// =====================================================================

let sesion = exigirSesion("ABOGADO")

let listaClientesChat = []

let PUNTO_ROJO = `<span style="position:absolute;top:-3px;right:-3px;width:8px;height:8px;border-radius:50%;background:#C0392B;border:1.5px solid #fff"></span>`

function cargarContactos() {
    apiGet("/clientes", { abogadoId: sesion.perfilId })
        .then(clientes => {
            listaClientesChat = clientes
            pintarContactos(clientes)
            revisarPendientes()
        })
        .catch(error => {
            mostrarError("No se pudieron cargar los contactos: " + error.message)
        })
}

function pintarContactos(clientes) {
    let contenedor = $("ab-contactos-grid")

    // El administrador siempre va primero
    contenedor.innerHTML = `
        <div class="contacto-card glass" style="border-left:3px solid var(--gold)">
            <div class="contacto-avatar" style="background:linear-gradient(135deg,var(--dark),#2D2E35);color:var(--gold);font-size:12px;font-weight:700">AD</div>
            <div class="contacto-info">
                <h4>Administrador</h4>
                <p>Juris Magna</p>
                <p style="font-size:11px;color:var(--body)">Despacho de Élite</p>
            </div>
            <button class="btn btn-blue btn-sm" style="align-self:center;margin-left:auto;white-space:nowrap;position:relative" onclick="abrirChatAdmin()">
                <span id="punto-admin"></span>Chat
            </button>
        </div>`

    clientes.forEach(cliente => {
        contenedor.innerHTML += `
            <div class="contacto-card glass">
                <div class="contacto-avatar">${escaparHtml(cliente.nombre[0])}</div>
                <div class="contacto-info">
                    <h4>${escaparHtml(cliente.nombre)}</h4>
                    <p>${escaparHtml(cliente.telefono || "—")}</p>
                    <p>${escaparHtml(cliente.email || "—")}</p>
                    <p style="font-size:11px;color:var(--body);margin-top:2px">${cliente.estado}</p>
                </div>
                <button class="btn btn-blue btn-sm" style="align-self:center;margin-left:auto;white-space:nowrap;position:relative" onclick="abrirChatCliente(${cliente.id})">
                    <span id="punto-cliente-${cliente.id}"></span>Chat
                </button>
            </div>`
    })
}

function revisarPendientes() {
    hayMensajesSinLeer("admin-abogado-" + sesion.perfilId, "abogado")
        .then(pendiente => {
            $("punto-admin").innerHTML = pendiente ? PUNTO_ROJO : ""
        })
        .catch(error => {
            console.log(error.message)
        })

    listaClientesChat.forEach(cliente => {
        hayMensajesSinLeer("cliente-" + cliente.id, "abogado")
            .then(pendiente => {
                $("punto-cliente-" + cliente.id).innerHTML = pendiente ? PUNTO_ROJO : ""
            })
            .catch(error => {
                console.log(error.message)
            })
    })
}

// chat.js la llama al cerrar el chat
function despuesDeCerrarChat() {
    revisarPendientes()
}

function abrirChatAdmin() {
    abrirChat("admin-abogado-" + sesion.perfilId, "Chat con Administrador", "Juris Magna · Despacho de Élite", "abogado")
}

function abrirChatCliente(id) {
    let cliente = listaClientesChat.find(elemento => elemento.id === id)
    let subtitulo = (cliente.email || "") + " · " + (cliente.telefono || "")

    abrirChat("cliente-" + cliente.id, "Chat con " + cliente.nombre, subtitulo, "abogado")
}

if (sesion) {
    cargarContactos()
}
