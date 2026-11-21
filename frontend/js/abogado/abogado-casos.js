// =====================================================================
// abogado-casos.js — Archivos y casos del abogado (RF-07).
// Casos: GET /casos?abogadoId=&materia=&q=, POST, PUT y DELETE /casos.
// Drive de cada caso: carpetas (/casos/{id}/carpetas, /carpetas/{id})
// y ligas (/carpetas/{id}/ligas, /ligas/{id}).
// =====================================================================

let sesion = exigirSesion("ABOGADO")

let listaCasos = []
let materiaFiltro = ""
let textoFiltro = ""
let esperaBusqueda = null

// Caso y carpeta que están abiertos en el drive
let casoDrive = null
let carpetaDrive = null
let listaCarpetas = []

// ---------- Tabla y filtros ----------

function cargarCasos() {
    apiGet("/casos", { abogadoId: sesion.perfilId, materia: materiaFiltro, q: textoFiltro })
        .then(casos => {
            listaCasos = casos
            pintarCasos(casos)
        })
        .catch(error => {
            mostrarError("No se pudieron cargar los casos: " + error.message)
        })
}

function pintarCasos(casos) {
    let cuerpo = $("tabla-casos")

    if (casos.length === 0) {
        cuerpo.innerHTML = `<tr><td colspan="6" class="empty-state">No hay casos.</td></tr>`
        return
    }

    cuerpo.innerHTML = ""

    casos.forEach(caso => {
        let drive = `<span style="font-size:11px;color:var(--body)">Sin enlace</span>`

        if (caso.driveUrl) {
            drive = `<a class="btn btn-blue btn-sm" href="${escaparHtml(caso.driveUrl)}" target="_blank">Abrir</a>`
        }

        cuerpo.innerHTML += `
            <tr>
                <td><span class="exp-id">${caso.numeroExpediente}</span></td>
                <td>${escaparHtml(caso.clienteNombre || "-")}</td>
                <td><span class="badge badge-blue">${escaparHtml(caso.materia)}</span></td>
                <td><span class="badge ${claseEstado(caso.estado)}">${caso.estado}</span></td>
                <td>${formatoFecha(caso.fechaInicio)}</td>
                <td>
                    <div class="action-btns">
                        <button class="btn btn-outline btn-sm" onclick="abrirEditarCaso(${caso.id})">✏️ Editar</button>
                        <button class="btn btn-outline btn-sm" onclick="abrirDrive(${caso.id})">📂 Carpetas</button>
                        ${drive}
                    </div>
                </td>
            </tr>`
    })
}

// Los chips salen de las materias de todos los casos del abogado
function cargarFiltros() {
    apiGet("/casos", { abogadoId: sesion.perfilId })
        .then(casos => {
            let materias = []

            casos.forEach(caso => {
                if (!materias.includes(caso.materia)) {
                    materias.push(caso.materia)
                }
            })

            pintarFiltros(materias)
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

function pintarFiltros(materias) {
    let contenedor = $("casos-filters")
    contenedor.innerHTML = ""

    let opciones = [""].concat(materias)

    opciones.forEach(materia => {
        let chip = document.createElement("button")
        chip.className = "filter-chip" + (materia === materiaFiltro ? " active" : "")
        chip.textContent = materia === "" ? "Todos" : materia

        chip.onclick = () => {
            materiaFiltro = materia
            pintarFiltros(materias)
            cargarCasos()
        }

        contenedor.appendChild(chip)
    })

    let buscador = document.createElement("input")
    buscador.className = "input"
    buscador.style.maxWidth = "180px"
    buscador.placeholder = "🔍 Buscar..."
    buscador.value = textoFiltro

    buscador.oninput = evento => {
        textoFiltro = evento.target.value.trim()
        clearTimeout(esperaBusqueda)
        esperaBusqueda = setTimeout(cargarCasos, 300)
    }

    contenedor.appendChild(buscador)
}

function recargarTodo() {
    cargarCasos()
    cargarFiltros()
}

// ---------- Nuevo y editar caso ----------

// Llena el select con los clientes del abogado
function llenarClientes(seleccionado) {
    return apiGet("/clientes", { abogadoId: sesion.perfilId })
        .then(clientes => {
            let select = $("nca-cliente")
            select.innerHTML = ""

            clientes.forEach(cliente => {
                let marcado = cliente.id === seleccionado ? "selected" : ""
                select.innerHTML += `<option value="${cliente.id}" ${marcado}>${escaparHtml(cliente.nombre)}</option>`
            })
        })
}

function prepararModalCaso(titulo) {
    $("nca-title").textContent = titulo
    $("nca-success").style.display = "none"
    $("nca-abogado").value = sesion.nombre
}

function abrirNuevoCaso() {
    prepararModalCaso("Nuevo Caso")
    $("nca-edit-id").value = ""
    $("nca-materia").value = "Civil"
    $("nca-estado").value = "Activo"
    $("nca-drive").value = ""
    $("nca-drive-link").style.display = "none"
    $("nca-btn-eliminar").style.display = "none"

    llenarClientes(null)
        .then(() => {
            abrirModal("ov-nuevo-caso")
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

function mostrarEnlaceDrive(url) {
    let enlace = $("nca-drive-link")

    if (url) {
        enlace.href = url
        enlace.style.display = "inline-flex"
    } else {
        enlace.style.display = "none"
    }
}

function abrirEditarCaso(id) {
    let caso = listaCasos.find(elemento => elemento.id === id)

    prepararModalCaso("✏️ Editar — " + caso.numeroExpediente)
    $("nca-edit-id").value = caso.id
    $("nca-materia").value = caso.materia
    $("nca-estado").value = caso.estado
    $("nca-drive").value = caso.driveUrl || ""
    $("nca-btn-eliminar").style.display = "inline-flex"
    mostrarEnlaceDrive(caso.driveUrl)

    llenarClientes(caso.clienteId)
        .then(() => {
            abrirModal("ov-nuevo-caso")
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

// Al editar, el modal se queda abierto con el aviso verde (como el diseño original)
function guardarCaso() {
    let id = $("nca-edit-id").value

    let datos = {
        clienteId: Number($("nca-cliente").value),
        abogadoId: sesion.perfilId,
        materia: $("nca-materia").value,
        estado: $("nca-estado").value,
        driveUrl: $("nca-drive").value.trim()
    }

    if (id) {
        apiPut("/casos/" + id, datos)
            .then(caso => {
                $("nca-success").style.display = "block"
                mostrarEnlaceDrive(caso.driveUrl)
                recargarTodo()
            })
            .catch(error => {
                mostrarError(error.message)
            })
    } else {
        apiPost("/casos", datos)
            .then(caso => {
                cerrarModal("ov-nuevo-caso")
                recargarTodo()
                mostrarExito("Caso " + caso.numeroExpediente + " creado")
            })
            .catch(error => {
                mostrarError(error.message)
            })
    }
}

function eliminarCaso() {
    let id = $("nca-edit-id").value

    if (!confirm("¿Eliminar este caso con sus carpetas y ligas?")) {
        return
    }

    apiDelete("/casos/" + id)
        .then(() => {
            cerrarModal("ov-nuevo-caso")
            recargarTodo()
            mostrarExito("Caso eliminado")
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

// ---------- Drive: carpetas ----------

function abrirDrive(id) {
    casoDrive = listaCasos.find(elemento => elemento.id === id)
    carpetaDrive = null

    $("drive-title").textContent = "📂 Drive — " + casoDrive.numeroExpediente
    $("drive-sub").textContent = casoDrive.clienteNombre + " · " + casoDrive.materia
    $("drive-path").textContent = "Mi Unidad / " + casoDrive.numeroExpediente
    $("drive-carpetas-view").style.display = "block"
    $("drive-archivos-view").style.display = "none"

    cargarCarpetas()
    abrirModal("ov-drive")
}

function cargarCarpetas() {
    apiGet("/casos/" + casoDrive.id + "/carpetas")
        .then(carpetas => {
            listaCarpetas = carpetas
            pintarCarpetas(carpetas)
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

function pintarCarpetas(carpetas) {
    let cuadricula = $("drive-grid")

    if (carpetas.length === 0) {
        cuadricula.innerHTML = `<div class="empty-state">Sin carpetas.</div>`
        return
    }

    cuadricula.innerHTML = ""

    carpetas.forEach(carpeta => {
        cuadricula.innerHTML += `
            <div class="drive-folder">
                <span style="font-size:30px">📁</span>
                <span style="font-size:13px;font-weight:600">${escaparHtml(carpeta.numero)} — ${escaparHtml(carpeta.proyecto)}</span>
                <span class="badge badge-blue">${escaparHtml(carpeta.materia || "—")}</span>
                <button class="btn btn-outline btn-sm" style="width:100%;margin-top:4px" onclick="abrirCarpeta(${carpeta.id})">Ver archivos</button>
                <div class="action-btns">
                    <button class="btn btn-outline btn-sm" onclick="abrirEditarCarpeta(${carpeta.id})">✏️</button>
                    <button class="btn btn-danger btn-sm" onclick="eliminarCarpeta(${carpeta.id})">🗑️</button>
                </div>
            </div>`
    })
}

function abrirNuevaCarpeta() {
    $("nc-titulo").textContent = "Nueva Carpeta"
    $("nc-btn-guardar").textContent = "Crear"
    $("nc-edit-id").value = ""

    let campos = ["nc-num", "nc-mat", "nc-proy", "nc-est", "nc-liga"]

    campos.forEach(id => {
        $(id).value = ""
    })

    abrirModal("ov-nueva-carpeta")
}

function abrirEditarCarpeta(id) {
    let carpeta = listaCarpetas.find(elemento => elemento.id === id)

    $("nc-titulo").textContent = "Editar Carpeta"
    $("nc-btn-guardar").textContent = "Guardar"
    $("nc-edit-id").value = carpeta.id
    $("nc-num").value = carpeta.numero || ""
    $("nc-mat").value = carpeta.materia || ""
    $("nc-proy").value = carpeta.proyecto || ""
    $("nc-est").value = carpeta.estado || ""
    $("nc-liga").value = carpeta.ligaUrl || ""

    abrirModal("ov-nueva-carpeta")
}

function guardarCarpeta() {
    let id = $("nc-edit-id").value

    let datos = {
        numero: $("nc-num").value.trim(),
        materia: $("nc-mat").value.trim(),
        proyecto: $("nc-proy").value.trim(),
        estado: $("nc-est").value.trim(),
        ligaUrl: $("nc-liga").value.trim()
    }

    let peticion = null

    if (id) {
        peticion = apiPut("/carpetas/" + id, datos)
    } else {
        peticion = apiPost("/casos/" + casoDrive.id + "/carpetas", datos)
    }

    peticion
        .then(() => {
            cerrarModal("ov-nueva-carpeta")
            cargarCarpetas()
            mostrarExito(id ? "Carpeta actualizada" : "Carpeta creada con éxito")
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

function eliminarCarpeta(id) {
    if (!confirm("¿Eliminar la carpeta y sus ligas?")) {
        return
    }

    apiDelete("/carpetas/" + id)
        .then(() => {
            cargarCarpetas()
            mostrarExito("Carpeta eliminada")
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

// ---------- Drive: ligas de una carpeta ----------

function abrirCarpeta(id) {
    carpetaDrive = listaCarpetas.find(elemento => elemento.id === id)

    $("drive-arch-path").textContent = casoDrive.numeroExpediente + " / " + carpetaDrive.numero + " — " + carpetaDrive.proyecto
    $("drive-carpetas-view").style.display = "none"
    $("drive-archivos-view").style.display = "block"

    cargarLigas()
}

function volverCarpetas() {
    carpetaDrive = null
    $("drive-carpetas-view").style.display = "block"
    $("drive-archivos-view").style.display = "none"
}

function cargarLigas() {
    apiGet("/carpetas/" + carpetaDrive.id + "/ligas")
        .then(ligas => {
            pintarLigas(ligas)
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

function pintarLigas(ligas) {
    let cuerpo = $("drive-archivos-tbody")

    if (ligas.length === 0) {
        cuerpo.innerHTML = `<tr><td colspan="3" class="empty-state">Sin archivos.</td></tr>`
        return
    }

    cuerpo.innerHTML = ""

    ligas.forEach(liga => {
        let corta = liga.url.length > 40 ? liga.url.slice(0, 40) + "..." : liga.url

        cuerpo.innerHTML += `
            <tr>
                <td>${escaparHtml(liga.nombre)}</td>
                <td class="url-prev">${escaparHtml(corta)}</td>
                <td>
                    <div class="action-btns">
                        <a class="btn btn-blue btn-sm" href="${escaparHtml(liga.url)}" target="_blank">Abrir</a>
                        <button class="btn btn-danger btn-sm" onclick="eliminarLiga(${liga.id})">🗑️</button>
                    </div>
                </td>
            </tr>`
    })
}

function abrirNuevaLiga() {
    $("nl-nombre").value = ""
    $("nl-url").value = ""
    abrirModal("ov-nueva-liga")
}

function guardarLiga() {
    let url = $("nl-url").value.trim()

    let datos = {
        nombre: $("nl-nombre").value.trim() || url,
        url: url
    }

    apiPost("/carpetas/" + carpetaDrive.id + "/ligas", datos)
        .then(liga => {
            cerrarModal("ov-nueva-liga")
            cargarLigas()

            let botones = `
                <a class="btn btn-primary" href="${escaparHtml(liga.url)}" target="_blank">Abrir</a>
                <button class="btn btn-outline" style="margin-left:8px" onclick="cerrarModal('ov-exito')">Cerrar</button>`

            mostrarExito("Liga guardada", botones)
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

function eliminarLiga(id) {
    apiDelete("/ligas/" + id)
        .then(() => {
            cargarLigas()
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

if (sesion) {
    recargarTodo()
}
