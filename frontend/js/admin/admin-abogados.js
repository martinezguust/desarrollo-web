// =====================================================================
// admin-abogados.js — Gestión de abogados (RF-02): tabla, alta y baja.
// Ver perfil, editar y cambiar contraseña están en menu.js porque
// también los usa el abogado desde sus propias páginas.
// =====================================================================

let sesion = exigirSesion("ADMIN")

let abogadoPorEliminar = null

// Última lista recibida del backend
let listaAbogados = []

function cargarAbogados() {
    apiGet("/abogados")
        .then(abogados => {
            pintarTablaAbogados(abogados)
        })
        .catch(error => {
            mostrarError("No se pudieron cargar los abogados: " + error.message)
        })
}

// menu.js la llama después de editar un abogado
function recargarAbogados() {
    cargarAbogados()
}

function pintarTablaAbogados(abogados) {
    listaAbogados = abogados

    let cuerpo = $("tabla-abogados")

    if (abogados.length === 0) {
        cuerpo.innerHTML = `<tr><td colspan="8" class="empty-state">No hay abogados registrados.</td></tr>`
        return
    }

    cuerpo.innerHTML = ""

    abogados.forEach(abogado => {
        let foto = `<span style="display:inline-flex;width:36px;height:36px;border-radius:50%;background:linear-gradient(135deg,var(--gold),var(--gold-dark));align-items:center;justify-content:center;font-weight:700;color:var(--dark);margin-right:8px;font-size:13px;vertical-align:middle;flex-shrink:0">${iniciales(abogado.nombre)}</span>`

        if (abogado.fotoUrl) {
            foto = `<img src="${abogado.fotoUrl}" class="avatar" onerror="this.style.display='none'">`
        }

        let claseEstado = abogado.estado === "Activo" ? "badge-green" : "badge-red"

        cuerpo.innerHTML += `
            <tr>
                <td><div style="display:flex;align-items:center;gap:6px">${foto}<span style="font-weight:500">${escaparHtml(abogado.nombre)}</span></div></td>
                <td>${escaparHtml(abogado.especialidad || "—")}</td>
                <td style="font-family:monospace;font-size:12px;color:var(--gold-dark)">${escaparHtml(abogado.cedula || "—")}</td>
                <td><span class="badge badge-blue">${abogado.casosActivos}</span></td>
                <td>${abogado.clientes}</td>
                <td>
                    <div style="display:flex;align-items:center;gap:8px">
                        <div class="prog-bar"><div class="prog-fill" style="width:${abogado.efectividad}%"></div></div>
                        <span style="font-size:12px;font-weight:600">${abogado.efectividad}%</span>
                    </div>
                </td>
                <td><span class="badge ${claseEstado}">${abogado.estado}</span></td>
                <td>
                    <div class="action-btns">
                        <button class="btn btn-outline btn-sm" onclick="verPerfilAbogado(${abogado.id})">Ver perfil</button>
                        <button class="btn btn-danger btn-sm" onclick="confirmarEliminarAbogado(${abogado.id})">🗑️</button>
                    </div>
                </td>
            </tr>`
    })
}

// ---------- Nuevo abogado ----------

function abrirNuevoAbogado() {
    let campos = ["nab-nombre", "nab-edad", "nab-esp", "nab-estudios", "nab-exp", "nab-cedula",
        "nab-email", "nab-tel", "nab-user", "nab-pass", "nab-foto-file"]

    campos.forEach(id => {
        $(id).value = ""
    })

    $("nab-foto-preview").innerHTML = "👤"
    abrirModal("ov-nuevo-abogado")
}

// POST /abogados crea el abogado y su usuario; después se sube la foto
function guardarAbogado() {
    let archivo = $("nab-foto-file").files[0]

    let datos = {
        nombre: $("nab-nombre").value.trim(),
        edad: Number($("nab-edad").value) || null,
        especialidad: $("nab-esp").value.trim(),
        estudios: $("nab-estudios").value.trim(),
        experiencia: $("nab-exp").value.trim(),
        cedula: $("nab-cedula").value.trim(),
        email: $("nab-email").value.trim(),
        telefono: $("nab-tel").value.trim(),
        username: $("nab-user").value.trim(),
        password: $("nab-pass").value
    }

    apiPost("/abogados", datos)
        .then(abogado => {
            if (archivo) {
                let formulario = new FormData()
                formulario.append("foto", archivo)

                return apiSubirArchivo("/abogados/" + abogado.id + "/foto", formulario)
            }
        })
        .then(() => {
            cerrarModal("ov-nuevo-abogado")
            cargarAbogados()
            mostrarExito("Abogado guardado correctamente")
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

// ---------- Eliminar abogado ----------

function confirmarEliminarAbogado(id) {
    let abogado = listaAbogados.find(elemento => elemento.id === id)

    abogadoPorEliminar = id
    $("confirm-ab-nombre").textContent = abogado.nombre
    abrirModal("ov-confirm-ab")
}

function eliminarAbogado() {
    apiDelete("/abogados/" + abogadoPorEliminar)
        .then(() => {
            cerrarModal("ov-confirm-ab")
            cargarAbogados()
            mostrarExito("Abogado eliminado")
        })
        .catch(error => {
            cerrarModal("ov-confirm-ab")
            mostrarError(error.message)
        })
}

if (sesion) {
    cargarAbogados()
}
