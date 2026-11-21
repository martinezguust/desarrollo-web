// =====================================================================
// admin-historico.js — Archivo histórico de casos cerrados (RF-05).
// Usa GET /casos/historico?estado=&q= (endpoint de Danna).
// =====================================================================

let sesion = exigirSesion("ADMIN")

let estadoHistorico = ""
let textoHistorico = ""

// Espera a que el usuario deje de escribir para no llamar al backend en cada tecla
let esperaBusqueda = null

function cargarHistorico() {
    apiGet("/casos/historico", { estado: estadoHistorico, q: textoHistorico })
        .then(casos => {
            pintarHistorico(casos)
        })
        .catch(error => {
            mostrarError("No se pudo cargar el histórico: " + error.message)
        })
}

function pintarHistorico(casos) {
    let cuerpo = $("tabla-historico")

    if (casos.length === 0) {
        cuerpo.innerHTML = `<tr><td colspan="8" class="empty-state">No hay casos en el archivo histórico.</td></tr>`
        return
    }

    cuerpo.innerHTML = ""

    casos.forEach(caso => {
        let drive = `<span style="color:var(--body);font-size:12px">Sin enlace</span>`

        if (caso.driveUrl) {
            drive = `<a href="${escaparHtml(caso.driveUrl)}" target="_blank" class="btn btn-blue btn-sm">Ver en Drive</a>`
        }

        cuerpo.innerHTML += `
            <tr>
                <td><span class="exp-id">#${caso.id}</span></td>
                <td>${caso.numeroExpediente}</td>
                <td>${escaparHtml(caso.clienteNombre || "—")}</td>
                <td>${escaparHtml(caso.abogadoNombre || "—")}</td>
                <td><span class="badge badge-blue">${escaparHtml(caso.materia)}</span></td>
                <td><span class="badge ${claseEstado(caso.estado)}">${caso.estado}</span></td>
                <td>${formatoFecha(caso.fechaFin || caso.fechaInicio)}</td>
                <td>${drive}</td>
            </tr>`
    })
}

function buscarHistorico(texto) {
    textoHistorico = texto.trim()

    clearTimeout(esperaBusqueda)
    esperaBusqueda = setTimeout(cargarHistorico, 300)
}

function filtrarHistorico(estado, boton) {
    estadoHistorico = estado

    document.querySelectorAll(".hist-chip").forEach(chip => {
        chip.classList.remove("active")
    })

    boton.classList.add("active")
    cargarHistorico()
}

if (sesion) {
    cargarHistorico()
}
