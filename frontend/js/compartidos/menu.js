// =====================================================================
// menu.js — Barra lateral y barra superior del admin y del abogado:
// sección activa, fecha, campana de notificaciones, perfil del usuario
// de la barra superior y cerrar sesión.
// Necesita sesion.js, utilidades.js y api.js.
// =====================================================================

let sesionMenu = obtenerSesion()

// ---------- Barra lateral ----------

// Marca como activa la sección de la página que está abierta
function marcarSeccionActiva() {
    let paginaActual = window.location.pathname.split("/").pop()

    document.querySelectorAll(".sidebar-nav .nav-item").forEach(enlace => {
        let destino = enlace.getAttribute("href").split("/").pop()

        if (destino === paginaActual) {
            enlace.classList.add("active")
        } else {
            enlace.classList.remove("active")
        }
    })
}

// ---------- Notificaciones (campana) ----------

function cargarNotificaciones() {
    if (!sesionMenu || !$("notif-list")) {
        return
    }

    apiGet("/notificaciones", { usuarioId: sesionMenu.usuarioId })
        .then(notificaciones => {
            pintarNotificaciones(notificaciones)
        })
        .catch(error => {
            console.log("Error al cargar notificaciones: " + error.message)
        })
}

function pintarNotificaciones(notificaciones) {
    let sinLeer = notificaciones.filter(notificacion => !notificacion.leida).length

    let globo = $("notif-count")
    globo.textContent = sinLeer

    if (sinLeer > 0) {
        globo.style.display = "flex"
    } else {
        globo.style.display = "none"
    }

    let lista = $("notif-list")

    if (notificaciones.length === 0) {
        lista.innerHTML = `<div class="notif-item"><div><p>Sin notificaciones.</p></div></div>`
        return
    }

    lista.innerHTML = ""

    notificaciones.slice(0, 10).forEach(notificacion => {
        let clase = notificacion.leida ? "" : "unread"

        lista.innerHTML += `
            <div class="notif-item ${clase}">
                <span class="notif-dot">●</span>
                <div>
                    <p>${escaparHtml(notificacion.mensaje)}</p>
                    <small>${formatoFecha(notificacion.fechaHora)} · ${formatoHora(notificacion.fechaHora)}</small>
                </div>
            </div>`
    })
}

function alternarNotificaciones() {
    $("notif-panel").classList.toggle("open")
}

function marcarNotificacionesLeidas() {
    apiPut("/notificaciones/leidas?usuarioId=" + sesionMenu.usuarioId)
        .then(() => {
            cargarNotificaciones()
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

// ---------- Foto o iniciales en un círculo ----------

function htmlFoto(fotoUrl, nombre) {
    if (fotoUrl) {
        return `<img src="${fotoUrl}" style="width:100%;height:100%;object-fit:cover;border-radius:50%">`
    }

    return `<span>${iniciales(nombre)}</span>`
}

// Muestra una vista previa de la foto elegida en un formulario
function previsualizarFoto(campo, idVista) {
    let archivo = campo.files[0]

    if (!archivo) {
        return
    }

    let lector = new FileReader()

    lector.onload = evento => {
        $(idVista).innerHTML = `<img src="${evento.target.result}" style="width:100%;height:100%;object-fit:cover;border-radius:50%">`
    }

    lector.readAsDataURL(archivo)
}

// ---------- Perfil del administrador ----------

function cargarDatosAdmin() {
    apiGet("/usuarios/" + sesionMenu.usuarioId)
        .then(usuario => {
            if (usuario.fotoUrl) {
                $("admin-topbar-foto").innerHTML = htmlFoto(usuario.fotoUrl, "AD")
            }
        })
        .catch(error => {
            console.log("Error al cargar el perfil: " + error.message)
        })
}

// El modal se crea la primera vez que se abre
function crearModalPerfilAdmin() {
    if ($("ov-perfil-admin")) {
        return
    }

    let modal = document.createElement("div")
    modal.className = "overlay-bg"
    modal.id = "ov-perfil-admin"
    modal.innerHTML = `
        <div class="admin-perfil-overlay glass">
            <div class="perfil-header-bar">
                <h3>Panel — Perfil Administrador</h3>
                <button class="close-btn" onclick="cerrarModal('ov-perfil-admin')">✕</button>
            </div>
            <div style="padding:24px;display:flex;flex-direction:column;gap:16px" id="admin-perfil-body"></div>
        </div>`

    activarCierreConFondo(modal)
    document.body.appendChild(modal)
}

function abrirPerfilAdmin() {
    apiGet("/usuarios/" + sesionMenu.usuarioId)
        .then(usuario => {
            crearModalPerfilAdmin()
            pintarPerfilAdmin(usuario)
            abrirModal("ov-perfil-admin")
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

function pintarPerfilAdmin(usuario) {
    let foto = usuario.fotoUrl ? htmlFoto(usuario.fotoUrl, "AD") : "AD"

    $("admin-perfil-body").innerHTML = `
        <div style="display:flex;align-items:center;gap:16px;padding-bottom:16px;border-bottom:1px solid var(--gb)">
            <div style="width:72px;height:72px;border-radius:50%;background:linear-gradient(135deg,var(--gold),var(--gold-dark));display:flex;align-items:center;justify-content:center;font-size:24px;font-weight:700;color:var(--dark);overflow:hidden;flex-shrink:0" id="admin-perfil-foto-prev">${foto}</div>
            <div>
                <div style="font-size:16px;font-weight:700;color:var(--dark)">${escaparHtml(usuario.nombre || "Administrador")}</div>
                <div style="font-size:12px;color:var(--body)">Juris Magna · Despacho de Élite</div>
                <input type="file" id="admin-foto-file" accept="image/*" onchange="previsualizarFoto(this, 'admin-perfil-foto-prev')" style="display:none"/>
                <button class="btn btn-outline btn-sm" style="margin-top:6px" onclick="$('admin-foto-file').click()">Cambiar foto</button>
            </div>
        </div>
        <div class="form-grid">
            <div class="form-group">
                <label>Email</label>
                <input class="input" id="admin-p-email" value="${escaparHtml(usuario.email)}"/>
            </div>
            <div class="form-group">
                <label>Teléfono</label>
                <input class="input" id="admin-p-tel" value="${escaparHtml(usuario.telefono)}"/>
            </div>
            <div class="form-group">
                <label>Usuario</label>
                <input class="input" id="admin-p-user" value="${escaparHtml(usuario.username)}" disabled/>
            </div>
            <div class="form-group">
                <label>Contraseña</label>
                <div class="pw-wrap">
                    <input class="input" type="password" id="admin-p-pass" placeholder="Nueva contraseña"/>
                    <button type="button" class="pw-eye" onclick="alternarPassword('admin-p-pass')" tabindex="-1">
                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                    </button>
                </div>
            </div>
        </div>
        <div style="display:flex;gap:10px;justify-content:flex-end">
            <button class="btn btn-outline" onclick="cerrarModal('ov-perfil-admin')">Cancelar</button>
            <button class="btn btn-primary" onclick="guardarPerfilAdmin()">Guardar cambios</button>
        </div>`
}

// Guarda en orden: datos, contraseña (si se escribió) y foto (si se eligió)
function guardarPerfilAdmin() {
    let id = sesionMenu.usuarioId
    let password = $("admin-p-pass").value
    let archivo = $("admin-foto-file").files[0]

    let datos = {
        nombre: sesionMenu.nombre,
        email: $("admin-p-email").value.trim(),
        telefono: $("admin-p-tel").value.trim()
    }

    apiPut("/usuarios/" + id, datos)
        .then(() => {
            if (password) {
                return apiPut("/usuarios/" + id + "/password", { password: password })
            }
        })
        .then(() => {
            if (archivo) {
                let formulario = new FormData()
                formulario.append("foto", archivo)

                return apiSubirArchivo("/usuarios/" + id + "/foto", formulario)
            }
        })
        .then(() => {
            cerrarModal("ov-perfil-admin")
            cargarDatosAdmin()
            mostrarExito("Perfil actualizado")
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

// ---------- Perfil del abogado (lo usan admin-abogados y las páginas del abogado) ----------

// Nombre y foto del abogado en la barra lateral y en la barra superior
function cargarDatosAbogado() {
    apiGet("/abogados/" + sesionMenu.perfilId)
        .then(abogado => {
            let foto = htmlFoto(abogado.fotoUrl, abogado.nombre)

            $("ab-chip-foto").innerHTML = foto
            $("ab-topbar-foto").innerHTML = foto
            $("ab-chip-nombre").textContent = abogado.nombre
            $("ab-topbar-nombre").textContent = abogado.nombre
            $("ab-sidebar-nombre").textContent = abogado.nombre
        })
        .catch(error => {
            console.log("Error al cargar el abogado: " + error.message)
        })
}

function abrirPerfilPropio() {
    verPerfilAbogado(sesionMenu.perfilId)
}

function verPerfilAbogado(id) {
    apiGet("/abogados/" + id)
        .then(abogado => {
            pintarPerfilAbogado(abogado)
            abrirModal("ov-perfil-abogado")
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

function pintarPerfilAbogado(abogado) {
    let esPropio = sesionMenu.rol === "ABOGADO" && sesionMenu.perfilId === abogado.id
    let puedeEditar = esPropio || sesionMenu.rol === "ADMIN"

    let foto = `<span style="font-size:42px;color:rgba(255,255,255,.35);font-weight:700">${iniciales(abogado.nombre)}</span>`

    if (abogado.fotoUrl) {
        foto = `<img src="${abogado.fotoUrl}" alt="${escaparHtml(abogado.nombre)}" style="width:100%;height:100%;object-fit:cover">`
    }

    let lapiz = ""

    if (puedeEditar) {
        lapiz = `
            <button type="button" onclick="cambiarPasswordAbogado(${abogado.id})" title="Cambiar contraseña" style="border:none;background:none;cursor:pointer;color:var(--gold-dark);padding:3px;display:flex;align-items:center;border-radius:5px">
                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
            </button>`
    }

    $("perfil-ab-body").innerHTML = `
        <div class="perfil-body">
            <div class="perfil-left">
                <div class="perfil-photo">${foto}</div>
                <div class="perfil-name-box">
                    <h4>${escaparHtml(abogado.nombre)}</h4>
                    <span>${escaparHtml(abogado.especialidad || "Sin especialidad")}</span>
                </div>
                <div class="perfil-age-box">
                    <label>Edad</label>
                    <span>${abogado.edad || "—"} años</span>
                </div>
            </div>
            <div class="perfil-right">
                <div class="perfil-section">
                    <div class="perfil-section-title">Datos Personales y Académicos</div>
                    <div class="perfil-field-grid">
                        <div class="perfil-field"><label>Estudios</label><span>${escaparHtml(abogado.estudios || "—")}</span></div>
                        <div class="perfil-field"><label>Experiencia</label><span>${escaparHtml(abogado.experiencia || "—")}</span></div>
                        <div class="perfil-field"><label>Cédula Profesional</label><span>${escaparHtml(abogado.cedula || "—")}</span></div>
                        <div class="perfil-field"><label>Casos activos</label><span>${abogado.casosActivos}</span></div>
                    </div>
                </div>
                <div class="perfil-section">
                    <div class="perfil-section-title">Datos de Contacto</div>
                    <div class="perfil-field-grid">
                        <div class="perfil-field"><label>Número</label><span>${escaparHtml(abogado.telefono || "—")}</span></div>
                        <div class="perfil-field"><label>Correo</label><span>${escaparHtml(abogado.email || "—")}</span></div>
                    </div>
                </div>
                <div class="perfil-section">
                    <div class="perfil-section-title">Datos de Inicio de Sesión</div>
                    <div class="perfil-field-grid">
                        <div class="perfil-field"><label>Usuario</label><span>${escaparHtml(abogado.username)}</span></div>
                        <div class="perfil-field">
                            <label>Contraseña</label>
                            <div style="display:flex;align-items:center;justify-content:space-between;gap:8px">
                                <span>••••••••</span>
                                ${lapiz}
                            </div>
                        </div>
                    </div>
                </div>
                <div class="perfil-section">
                    <div class="perfil-section-title">Rendimiento</div>
                    <div class="perfil-field-grid">
                        <div class="perfil-field"><label>Tasa de éxito</label><span style="color:var(--green);font-weight:700">${abogado.efectividad}%</span></div>
                        <div class="perfil-field"><label>Clientes</label><span>${abogado.clientes}</span></div>
                    </div>
                </div>
            </div>
        </div>`

    let textoEditar = esPropio ? "Editar mi perfil" : "Editar"

    $("perfil-ab-footer").innerHTML = `
        <button onclick="cerrarModal('ov-perfil-abogado'); abrirEditarAbogado(${abogado.id})">${textoEditar}</button>
        <button onclick="cerrarModal('ov-perfil-abogado')">Cerrar</button>`
}

function abrirEditarAbogado(id) {
    apiGet("/abogados/" + id)
        .then(abogado => {
            $("eab-id").value = abogado.id
            $("eab-nombre").value = abogado.nombre || ""
            $("eab-edad").value = abogado.edad || ""
            $("eab-estudios").value = abogado.estudios || ""
            $("eab-exp").value = abogado.experiencia || ""
            $("eab-esp").value = abogado.especialidad || ""
            $("eab-cedula").value = abogado.cedula || ""
            $("eab-email").value = abogado.email || ""
            $("eab-tel").value = abogado.telefono || ""
            $("eab-foto-file").value = ""

            if (abogado.fotoUrl) {
                $("eab-foto-preview").innerHTML = htmlFoto(abogado.fotoUrl, abogado.nombre)
            } else {
                $("eab-foto-preview").innerHTML = `<svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#9CA3AF" stroke-width="1.5"><circle cx="12" cy="8" r="4"/><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7"/></svg>`
            }

            abrirModal("ov-editar-abogado")
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

// PUT con los datos y, si se eligió una foto, POST multipart de la foto
function guardarEdicionAbogado() {
    let id = $("eab-id").value
    let archivo = $("eab-foto-file").files[0]

    let datos = {
        nombre: $("eab-nombre").value.trim(),
        edad: Number($("eab-edad").value) || null,
        estudios: $("eab-estudios").value.trim(),
        experiencia: $("eab-exp").value.trim(),
        especialidad: $("eab-esp").value.trim(),
        cedula: $("eab-cedula").value.trim(),
        email: $("eab-email").value.trim(),
        telefono: $("eab-tel").value.trim()
    }

    apiPut("/abogados/" + id, datos)
        .then(() => {
            if (archivo) {
                let formulario = new FormData()
                formulario.append("foto", archivo)

                return apiSubirArchivo("/abogados/" + id + "/foto", formulario)
            }
        })
        .then(() => {
            cerrarModal("ov-editar-abogado")
            mostrarExito("Abogado actualizado correctamente")
            actualizarDespuesDeEditar()
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

function cambiarPasswordAbogado(id) {
    let nueva = prompt("Nueva contraseña para el abogado:")

    if (nueva === null) {
        return
    }

    apiPut("/abogados/" + id + "/password", { password: nueva })
        .then(() => {
            mostrarExito("Contraseña actualizada. El administrador también verá el cambio.")
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

// Refresca lo que dependa del abogado editado
function actualizarDespuesDeEditar() {
    if (sesionMenu.rol === "ABOGADO") {
        cargarDatosAbogado()
    }

    // La página de abogados del admin define esta función para recargar su tabla
    if (typeof recargarAbogados === "function") {
        recargarAbogados()
    }
}

// ---------- Al cargar la página ----------

if (sesionMenu) {
    marcarSeccionActiva()

    if ($("topbar-fecha")) {
        $("topbar-fecha").textContent = fechaHoy()
    }

    cargarNotificaciones()

    // Revisa notificaciones nuevas cada 30 segundos
    setInterval(cargarNotificaciones, 30000)

    if (sesionMenu.rol === "ADMIN") {
        cargarDatosAdmin()
    }

    if (sesionMenu.rol === "ABOGADO") {
        cargarDatosAbogado()
    }
}
