// =====================================================================
// abogado-notas.js — Notas privadas del abogado (RF-10).
// GET /notas?abogadoId=, POST, PUT (editar o marcar completada) y DELETE.
// =====================================================================

let sesion = exigirSesion("ABOGADO")

let listaNotas = []

let ICONO_BASURA = `<svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6M14 11v6"/><path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/></svg>`
let ICONO_PALOMITA = `<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>`

function cargarNotas() {
    apiGet("/notas", { abogadoId: sesion.perfilId })
        .then(notas => {
            listaNotas = notas
            pintarNotas(notas)
        })
        .catch(error => {
            mostrarError("No se pudieron cargar las notas: " + error.message)
        })
}

function pintarNotas(notas) {
    let contenedor = $("notas-container")

    if (notas.length === 0) {
        contenedor.innerHTML = `<div class="empty-state">Sin notas. Crea la primera.</div>`
        return
    }

    contenedor.innerHTML = ""

    notas.forEach(nota => {
        let hecha = nota.completada

        let etiqueta = ""

        if (hecha) {
            etiqueta = `<span style="font-size:11px;background:rgba(39,174,96,.1);color:var(--green);padding:2px 8px;border-radius:20px;font-weight:600">Completada</span>`
        }

        contenedor.innerHTML += `
            <div class="nota-card" style="opacity:${hecha ? ".65" : "1"};border-left:3px solid ${hecha ? "var(--green)" : "var(--gold)"}">
                <div style="display:flex;align-items:flex-start;gap:12px">
                    <button onclick="alternarCompletada(${nota.id})" title="${hecha ? "Marcar pendiente" : "Marcar completada"}"
                        style="flex-shrink:0;width:22px;height:22px;border-radius:6px;border:2px solid ${hecha ? "var(--green)" : "var(--gb)"};background:${hecha ? "var(--green)" : "transparent"};cursor:pointer;display:flex;align-items:center;justify-content:center;margin-top:1px;transition:all .2s">
                        ${hecha ? ICONO_PALOMITA : ""}
                    </button>
                    <div style="flex:1;min-width:0">
                        <h4 style="text-decoration:${hecha ? "line-through" : "none"};color:${hecha ? "var(--body)" : "var(--dark)"};font-size:14px;margin-bottom:4px">${escaparHtml(nota.titulo)}</h4>
                        <p style="font-size:13px;color:var(--body);line-height:1.5">${escaparHtml(nota.contenido || "")}</p>
                        <div style="display:flex;align-items:center;gap:8px;margin-top:8px">
                            <span style="font-size:11px;color:var(--body)">${formatoFecha(nota.fecha)}</span>
                            ${etiqueta}
                        </div>
                    </div>
                    <div style="display:flex;gap:6px;flex-shrink:0">
                        <button class="btn btn-outline btn-sm" onclick="abrirEditarNota(${nota.id})" style="padding:5px 10px">Editar</button>
                        <button class="btn btn-danger btn-sm" onclick="eliminarNota(${nota.id})" style="padding:5px 8px">${ICONO_BASURA}</button>
                    </div>
                </div>
            </div>`
    })
}

function abrirNuevaNota() {
    $("nota-title").textContent = "Nueva Nota"
    $("nota-edit-id").value = ""
    $("nota-titulo").value = ""
    $("nota-contenido").value = ""
    abrirModal("ov-nota")
}

function abrirEditarNota(id) {
    let nota = listaNotas.find(elemento => elemento.id === id)

    $("nota-title").textContent = "Editar Nota"
    $("nota-edit-id").value = nota.id
    $("nota-titulo").value = nota.titulo
    $("nota-contenido").value = nota.contenido || ""
    abrirModal("ov-nota")
}

function guardarNota() {
    let id = $("nota-edit-id").value

    let datos = {
        abogadoId: sesion.perfilId,
        titulo: $("nota-titulo").value.trim(),
        contenido: $("nota-contenido").value.trim()
    }

    let peticion = null

    if (id) {
        // Se manda su estado actual; si no, el backend la regresa a pendiente
        let nota = listaNotas.find(elemento => elemento.id === Number(id))
        datos.completada = nota.completada

        peticion = apiPut("/notas/" + id, datos)
    } else {
        peticion = apiPost("/notas", datos)
    }

    peticion
        .then(() => {
            cerrarModal("ov-nota")
            cargarNotas()
            mostrarExito(id ? "Nota actualizada" : "Nota guardada")
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

// El PUT lleva el título y el contenido actuales para no borrarlos
function alternarCompletada(id) {
    let nota = listaNotas.find(elemento => elemento.id === id)

    let datos = {
        titulo: nota.titulo,
        contenido: nota.contenido,
        completada: !nota.completada
    }

    apiPut("/notas/" + id, datos)
        .then(() => {
            cargarNotas()
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

function eliminarNota(id) {
    if (!confirm("¿Eliminar esta nota?")) {
        return
    }

    apiDelete("/notas/" + id)
        .then(() => {
            cargarNotas()
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

if (sesion) {
    cargarNotas()
}
