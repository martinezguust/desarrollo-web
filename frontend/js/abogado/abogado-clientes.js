// =====================================================================
// abogado-clientes.js — Clientes del abogado (RF-06).
// GET /clientes?abogadoId=&q= para la cuadrícula y la tabla;
// POST, PUT y DELETE /clientes para crear, editar y eliminar.
// El botón 💬 abre el chat con el cliente (chat.js de Luisa).
// =====================================================================

let sesion = exigirSesion("ABOGADO")

let listaClientes = []
let clientePorEliminar = null
let textoBusqueda = ""
let esperaBusqueda = null

function cargarClientes() {
    apiGet("/clientes", { abogadoId: sesion.perfilId, q: textoBusqueda })
        .then(clientes => {
            listaClientes = clientes
            pintarClientes(clientes)
        })
        .catch(error => {
            mostrarError("No se pudieron cargar los clientes: " + error.message)
        })
}

function botonesCliente(cliente, textoEditar) {
    return `
        <div class="action-btns">
            <button class="btn btn-outline btn-sm" onclick="abrirEditarCliente(${cliente.id})">${textoEditar}</button>
            <button class="btn btn-blue btn-sm" onclick="abrirMensajesCliente(${cliente.id})">💬</button>
            <button class="btn btn-danger btn-sm" onclick="confirmarEliminarCliente(${cliente.id})">🗑️</button>
        </div>`
}

function pintarClientes(clientes) {
    let cuadricula = $("clientes-grid")
    let tabla = $("tabla-clientes")

    if (clientes.length === 0) {
        cuadricula.innerHTML = `<div class="empty-state">No hay clientes.</div>`
        tabla.innerHTML = `<tr><td colspan="5" class="empty-state">No hay clientes.</td></tr>`
        return
    }

    cuadricula.innerHTML = ""
    tabla.innerHTML = ""

    clientes.forEach(cliente => {
        let claseEstado = cliente.estado === "Activo" ? "badge-green" : "badge-red"

        cuadricula.innerHTML += `
            <div class="client-card glass">
                <div class="client-card-top">
                    <div class="client-avatar-big">${escaparHtml(cliente.nombre[0])}</div>
                    <span class="badge ${claseEstado}">${cliente.estado}</span>
                </div>
                <h4>${escaparHtml(cliente.nombre)}</h4>
                <p style="font-size:13px;color:var(--body)">${escaparHtml(cliente.descripcion || "")}</p>
                <div style="font-size:12px;display:flex;flex-direction:column;gap:3px">
                    <span>📧 ${escaparHtml(cliente.email || "—")}</span>
                    <span>📱 ${escaparHtml(cliente.telefono || "—")}</span>
                </div>
                <div style="margin-top:4px">${botonesCliente(cliente, "✏️ Editar")}</div>
            </div>`

        tabla.innerHTML += `
            <tr>
                <td>${escaparHtml(cliente.nombre)}</td>
                <td>${escaparHtml(cliente.email || "—")}</td>
                <td>${formatoMoneda(cliente.honorarios)}</td>
                <td><span class="badge ${claseEstado}">${cliente.estado}</span></td>
                <td>${botonesCliente(cliente, "✏️")}</td>
            </tr>`
    })
}

function buscarClientes(texto) {
    textoBusqueda = texto.trim()

    clearTimeout(esperaBusqueda)
    esperaBusqueda = setTimeout(cargarClientes, 300)
}

function cambiarVistaClientes(vista) {
    $("clientes-grid").style.display = vista === "grid" ? "grid" : "none"
    $("clientes-table").style.display = vista === "list" ? "block" : "none"
    $("vt-grid").classList.toggle("active", vista === "grid")
    $("vt-list").classList.toggle("active", vista === "list")
}

// ---------- Nuevo y editar ----------

let CAMPOS_CLIENTE = ["ncl-nombre", "ncl-edad", "ncl-estudios", "ncl-ine", "ncl-email", "ncl-tel",
    "ncl-user", "ncl-pass", "ncl-desc", "ncl-avance", "ncl-hon", "ncl-drive", "ncl-comentario"]

function abrirNuevoCliente() {
    $("ncl-title").textContent = "Nuevo Cliente"
    $("ncl-edit-id").value = ""

    CAMPOS_CLIENTE.forEach(id => {
        $(id).value = ""
    })

    $("ncl-avance").value = "0"
    $("ncl-pass").placeholder = ""
    $("ncl-toggle").classList.add("on")

    abrirModal("ov-nuevo-cliente")
}

function abrirEditarCliente(id) {
    let cliente = listaClientes.find(elemento => elemento.id === id)

    $("ncl-title").textContent = "Editar Cliente"
    $("ncl-edit-id").value = cliente.id
    $("ncl-nombre").value = cliente.nombre
    $("ncl-edad").value = cliente.edad || ""
    $("ncl-estudios").value = cliente.estudios || ""
    $("ncl-ine").value = cliente.ine || ""
    $("ncl-email").value = cliente.email || ""
    $("ncl-tel").value = cliente.telefono || ""
    $("ncl-user").value = cliente.username || ""

    // La contraseña nunca llega del backend; vacía = no se cambia
    $("ncl-pass").value = ""
    $("ncl-pass").placeholder = "Sin cambios"

    $("ncl-desc").value = cliente.descripcion || ""
    $("ncl-avance").value = cliente.avance
    $("ncl-hon").value = Number(cliente.honorarios)
    $("ncl-drive").value = cliente.driveUrl || ""
    $("ncl-comentario").value = cliente.comentario || ""
    $("ncl-toggle").classList.toggle("on", cliente.estado === "Activo")

    abrirModal("ov-nuevo-cliente")
}

function guardarCliente() {
    let id = $("ncl-edit-id").value
    let activo = $("ncl-toggle").classList.contains("on")

    let datos = {
        abogadoId: sesion.perfilId,
        nombre: $("ncl-nombre").value.trim(),
        edad: Number($("ncl-edad").value) || null,
        estudios: $("ncl-estudios").value.trim(),
        ine: $("ncl-ine").value.trim(),
        email: $("ncl-email").value.trim(),
        telefono: $("ncl-tel").value.trim(),
        username: $("ncl-user").value.trim(),
        password: $("ncl-pass").value,
        descripcion: $("ncl-desc").value.trim(),
        avance: Number($("ncl-avance").value) || 0,
        honorarios: Number($("ncl-hon").value) || 0,
        driveUrl: $("ncl-drive").value.trim(),
        comentario: $("ncl-comentario").value.trim(),
        estado: activo ? "Activo" : "Inactivo"
    }

    let peticion = null

    if (id) {
        peticion = apiPut("/clientes/" + id, datos)
    } else {
        peticion = apiPost("/clientes", datos)
    }

    peticion
        .then(() => {
            cerrarModal("ov-nuevo-cliente")
            cargarClientes()
            mostrarExito(id ? "Cliente actualizado" : "Cliente guardado correctamente")
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

// ---------- Eliminar ----------

function confirmarEliminarCliente(id) {
    let cliente = listaClientes.find(elemento => elemento.id === id)

    clientePorEliminar = id
    $("confirm-cl-nombre").textContent = cliente.nombre
    abrirModal("ov-confirm-cl")
}

function eliminarCliente() {
    apiDelete("/clientes/" + clientePorEliminar)
        .then(() => {
            cerrarModal("ov-confirm-cl")
            cargarClientes()
            mostrarExito("Cliente eliminado")
        })
        .catch(error => {
            cerrarModal("ov-confirm-cl")
            mostrarError(error.message)
        })
}

// ---------- Chat con el cliente ----------

function abrirMensajesCliente(id) {
    let cliente = listaClientes.find(elemento => elemento.id === id)
    let subtitulo = (cliente.email || "") + " · " + (cliente.telefono || "")

    // abrirChat está en chat.js
    abrirChat("cliente-" + cliente.id, "Chat con " + cliente.nombre, subtitulo, "abogado")
}

if (sesion) {
    cargarClientes()
}
