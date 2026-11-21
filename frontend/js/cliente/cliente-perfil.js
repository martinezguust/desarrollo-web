// =====================================================================
// cliente-perfil.js — Perfil del cliente en el celular.
// GET /clientes/{id} para sus datos y GET /casos?clienteId= para su caso.
// La contraseña nunca se muestra (siempre ••••••••).
// =====================================================================

let sesion = exigirSesion("CLIENTE")

// Un renglón con etiqueta y valor
function campoMovil(etiqueta, valor) {
    return `
        <div style="background:rgba(255,255,255,.7);border:1px solid var(--gb);border-radius:10px;padding:10px 12px">
            <div style="font-size:10px;color:var(--body);text-transform:uppercase;letter-spacing:.5px;font-weight:600;margin-bottom:2px">${etiqueta}</div>
            <span style="font-size:13px;font-weight:500;color:var(--dark)">${escaparHtml(valor || "—")}</span>
        </div>`
}

function pintarPerfil(cliente, casos) {
    let claseEstado = cliente.estado === "Activo" ? "badge-green" : "badge-red"

    let bloqueCaso = ""

    if (casos.length > 0) {
        let caso = casos[0]

        bloqueCaso = `
            <div style="margin-top:8px;padding:12px;background:rgba(212,175,55,.06);border:1px solid var(--gb);border-radius:10px">
                <div style="font-size:11px;color:var(--body);text-transform:uppercase;letter-spacing:.5px;font-weight:600;margin-bottom:8px">Mi Caso</div>
                ${campoMovil("Expediente", caso.numeroExpediente)}
                ${campoMovil("Materia", caso.materia)}
                ${campoMovil("Estado", caso.estado)}
                ${campoMovil("Abogado", caso.abogadoNombre)}
            </div>`
    }

    $("mob-perfil-content").innerHTML = `
        <div style="display:flex;flex-direction:column;align-items:center;gap:8px;padding:16px 0 20px;border-bottom:1px solid var(--gb)">
            <div style="width:72px;height:72px;border-radius:50%;background:linear-gradient(135deg,var(--gold),var(--gold-dark));display:flex;align-items:center;justify-content:center;font-size:28px;font-weight:700;color:var(--dark)">${escaparHtml(cliente.nombre[0])}</div>
            <div style="font-size:17px;font-weight:700;color:var(--dark)">${escaparHtml(cliente.nombre)}</div>
            <span class="badge ${claseEstado}">${cliente.estado}</span>
        </div>
        <div style="display:flex;flex-direction:column;gap:10px;margin-top:12px">
            ${campoMovil("Correo electrónico", cliente.email)}
            ${campoMovil("Teléfono", cliente.telefono)}
            ${campoMovil("Usuario", cliente.username)}
            ${campoMovil("Contraseña", "••••••••")}
            ${bloqueCaso}
        </div>`
}

function cargarPerfil() {
    let cliente = null

    apiGet("/clientes/" + sesion.perfilId)
        .then(respuesta => {
            cliente = respuesta
            return apiGet("/casos", { clienteId: cliente.id })
        })
        .then(casos => {
            pintarPerfil(cliente, casos)
        })
        .catch(error => {
            mostrarError("No se pudo cargar tu perfil: " + error.message)
        })
}

if (sesion) {
    cargarPerfil()
    revisarChatPendiente("cliente-" + sesion.perfilId)
}
