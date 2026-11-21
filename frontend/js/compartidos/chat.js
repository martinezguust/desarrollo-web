// =====================================================================
// chat.js — Mensajes entre abogado y cliente, y entre admin y abogado
// (RF-11): texto, notas de voz (MediaRecorder), imágenes y archivos.
//
// Hilos: "cliente-{id}" (abogado y cliente) y "admin-abogado-{id}".
// - En escritorio el chat vive en el modal ov-mensajes (abrirChat).
// - En el celular del cliente vive en la página cliente-chat (iniciarChatCliente).
// Mientras el chat está abierto, se revisa si hay mensajes nuevos cada 5 s.
// =====================================================================

let chatHilo = null
let chatRemitente = null
let chatContenedor = null
let chatEstilo = null
let chatRevision = null

// Grabación de audio en curso
let grabadora = null
let pedazosAudio = []

// ---------- Cargar y pintar ----------

function cargarMensajes() {
    return apiGet("/mensajes", { hilo: chatHilo })
        .then(mensajes => {
            pintarMensajes(mensajes)
        })
        .catch(error => {
            mostrarError("No se pudieron cargar los mensajes: " + error.message)
        })
}

// Marca como leídos los mensajes que me mandaron en este hilo
function marcarHiloLeido() {
    return apiPut("/mensajes/leidos?hilo=" + chatHilo + "&lector=" + chatRemitente)
        .catch(error => {
            console.log("Error al marcar leídos: " + error.message)
        })
}

function nombreRemitente(remitente) {
    let nombres = {
        cliente: "Cliente",
        abogado: "Abogado",
        admin: "Admin"
    }

    return nombres[remitente] || remitente
}

function pintarMensajes(mensajes) {
    let contenedor = $(chatContenedor)

    if (mensajes.length === 0) {
        contenedor.innerHTML = `<div style="text-align:center;color:var(--body);font-size:13px;padding:20px">Sin mensajes aún.</div>`
        return
    }

    contenedor.innerHTML = ""

    mensajes.forEach(mensaje => {
        let mio = mensaje.remitente === chatRemitente
        let hora = formatoHora(mensaje.fechaHora)

        if (chatEstilo === "movil") {
            let quien = mio ? "Tú" : nombreRemitente(mensaje.remitente)

            contenedor.innerHTML += `
                <div class="bubble-wrap ${mio ? "mine" : "theirs"}">
                    <div class="bubble ${mio ? "bubble-gold" : "bubble-white"}">${contenidoBurbuja(mensaje)}</div>
                    <span class="chat-time">${quien} · ${hora}</span>
                </div>`
        } else {
            contenedor.innerHTML += `
                <div class="msg-bubble-wrap ${mio ? "right" : "left"}">
                    <div class="msg-bubble ${mio ? "msg-gold" : "msg-white"}">${contenidoBurbuja(mensaje)}</div>
                    <span class="msg-time">${nombreRemitente(mensaje.remitente)} · ${hora}</span>
                </div>`
        }
    })

    contenedor.scrollTop = contenedor.scrollHeight
}

// Lo que va dentro de la burbuja según el tipo de mensaje
function contenidoBurbuja(mensaje) {
    let url = escaparHtml(mensaje.archivoUrl)
    let nombre = escaparHtml(mensaje.nombreArchivo || "archivo")

    let iconoVer = `<svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>`
    let iconoBajar = `<svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>`
    let estiloBoton = "flex:1;padding:5px 8px;border-radius:8px;border:1px solid currentColor;background:transparent;font-size:11px;font-weight:600;cursor:pointer;display:flex;align-items:center;justify-content:center;gap:4px;color:inherit;text-decoration:none;opacity:.8"

    if (mensaje.tipo === "audio") {
        return `
            <div style="display:flex;flex-direction:column;gap:6px;min-width:180px">
                <div style="display:flex;align-items:center;gap:8px">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z"/><path d="M19 10v2a7 7 0 0 1-14 0v-2"/><line x1="12" y1="19" x2="12" y2="23"/><line x1="8" y1="23" x2="16" y2="23"/></svg>
                    <span style="font-size:11px;opacity:.7">Audio</span>
                </div>
                <audio controls style="width:180px;height:32px;outline:none" src="${url}"></audio>
            </div>`
    }

    if (mensaje.tipo === "img") {
        return `
            <div style="display:flex;flex-direction:column;gap:6px">
                <img src="${url}" onclick="verImagen('${url}', '${nombre}')" style="max-width:180px;max-height:140px;border-radius:8px;display:block;cursor:pointer" title="Clic para ampliar"/>
                <div style="display:flex;gap:6px">
                    <button onclick="verImagen('${url}', '${nombre}')" style="${estiloBoton}">${iconoVer}Ver</button>
                    <a href="${url}" download="${nombre}" style="${estiloBoton}">${iconoBajar}Descargar</a>
                </div>
            </div>`
    }

    if (mensaje.tipo === "file") {
        return `
            <div style="display:flex;flex-direction:column;gap:6px;min-width:160px">
                <div style="display:flex;align-items:center;gap:7px">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                    <span style="font-size:12px;font-weight:500;word-break:break-all">${nombre}</span>
                </div>
                <div style="display:flex;gap:6px">
                    <a href="${url}" target="_blank" style="${estiloBoton}">${iconoVer}Ver</a>
                    <a href="${url}" download="${nombre}" style="${estiloBoton}">${iconoBajar}Descargar</a>
                </div>
            </div>`
    }

    return escaparHtml(mensaje.texto)
}

// Imagen en grande con botón de descarga (ov-img-preview)
function verImagen(url, nombre) {
    let visor = $("ov-img-preview")

    if (!visor) {
        visor = document.createElement("div")
        visor.id = "ov-img-preview"
        visor.style.cssText = "position:fixed;inset:0;background:rgba(0,0,0,.85);z-index:9000;display:flex;align-items:center;justify-content:center;flex-direction:column;gap:14px"
        visor.innerHTML = `
            <button onclick="$('ov-img-preview').style.display='none'" style="position:absolute;top:16px;right:20px;background:none;border:none;color:#fff;font-size:28px;cursor:pointer;line-height:1">✕</button>
            <img id="ov-img-el" style="max-width:90vw;max-height:80vh;border-radius:12px;object-fit:contain"/>
            <a id="ov-img-dl" style="color:var(--gold);font-size:13px;font-weight:600;padding:8px 20px;border:1px solid var(--gold);border-radius:50px;text-decoration:none" download>Descargar</a>`

        visor.addEventListener("click", evento => {
            if (evento.target === visor) {
                visor.style.display = "none"
            }
        })

        document.body.appendChild(visor)
    }

    $("ov-img-el").src = url
    $("ov-img-dl").href = url
    $("ov-img-dl").download = nombre
    visor.style.display = "flex"
}

// ---------- Enviar ----------

function enviarTexto(campo) {
    let texto = $(campo).value.trim()

    if (!texto) {
        return
    }

    let datos = {
        hilo: chatHilo,
        remitente: chatRemitente,
        texto: texto
    }

    apiPost("/mensajes", datos)
        .then(() => {
            $(campo).value = ""
            cargarMensajes()
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

// Sube audio, imagen o archivo con POST /mensajes/archivos (multipart)
function enviarArchivo(archivo, tipo, nombre) {
    let formulario = new FormData()
    formulario.append("hilo", chatHilo)
    formulario.append("remitente", chatRemitente)
    formulario.append("tipo", tipo)
    formulario.append("archivo", archivo, nombre)

    apiSubirArchivo("/mensajes/archivos", formulario)
        .then(() => {
            cargarMensajes()
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

// Abre el selector de archivos; las imágenes se mandan como "img"
function elegirArchivo() {
    let selector = document.createElement("input")
    selector.type = "file"
    selector.accept = "image/*,.pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.txt"

    selector.onchange = evento => {
        let archivo = evento.target.files[0]

        if (!archivo) {
            return
        }

        let tipo = archivo.type.startsWith("image/") ? "img" : "file"
        enviarArchivo(archivo, tipo, archivo.name)
    }

    selector.click()
}

// ---------- Notas de voz ----------

// Indicador rojo "Grabando..." con el tiempo
let relojGrabacion = null
let segundosGrabacion = 0

function mostrarIndicadorGrabacion(mostrar) {
    let indicador = $("rec-indicator")

    if (!mostrar) {
        if (indicador) {
            indicador.style.display = "none"
        }

        clearInterval(relojGrabacion)
        return
    }

    if (!indicador) {
        indicador = document.createElement("div")
        indicador.id = "rec-indicator"
        indicador.style.cssText = "position:fixed;bottom:80px;left:50%;transform:translateX(-50%);background:rgba(192,57,43,.95);color:#fff;padding:8px 18px;border-radius:50px;font-size:13px;font-weight:600;display:flex;align-items:center;gap:8px;z-index:9999;box-shadow:0 4px 16px rgba(0,0,0,.3);backdrop-filter:blur(10px)"
        indicador.innerHTML = `<span style="width:8px;height:8px;border-radius:50%;background:#fff" id="rec-dot"></span><span id="rec-timer">0:00</span><span style="opacity:.8;font-size:11px">· Grabando... presiona el micrófono para enviar</span>`
        document.body.appendChild(indicador)
    }

    indicador.style.display = "flex"
    segundosGrabacion = 0
    $("rec-timer").textContent = "0:00"

    relojGrabacion = setInterval(() => {
        segundosGrabacion++

        let minutos = Math.floor(segundosGrabacion / 60)
        let segundos = segundosGrabacion % 60

        $("rec-timer").textContent = minutos + ":" + (segundos < 10 ? "0" : "") + segundos
    }, 1000)
}

// Primer clic: empieza a grabar. Segundo clic: detiene y envía.
function alternarGrabacion() {
    if (grabadora && grabadora.state === "recording") {
        grabadora.stop()
        return
    }

    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        mostrarError("Tu navegador no permite grabar audio.")
        return
    }

    navigator.mediaDevices.getUserMedia({ audio: true })
        .then(microfono => {
            pedazosAudio = []
            grabadora = new MediaRecorder(microfono)

            grabadora.ondataavailable = evento => {
                if (evento.data.size > 0) {
                    pedazosAudio.push(evento.data)
                }
            }

            grabadora.onstop = () => {
                mostrarIndicadorGrabacion(false)
                microfono.getTracks().forEach(pista => pista.stop())

                let audio = new Blob(pedazosAudio, { type: "audio/webm" })
                enviarArchivo(audio, "audio", "audio_" + Date.now() + ".webm")
            }

            grabadora.start()
            mostrarIndicadorGrabacion(true)

            // Se detiene sola a los 2 minutos
            setTimeout(() => {
                if (grabadora && grabadora.state === "recording") {
                    grabadora.stop()
                }
            }, 120000)
        })
        .catch(error => {
            console.log(error)
            mostrarError("No se pudo usar el micrófono. Revisa los permisos del navegador.")
        })
}

// ---------- Chat en el modal (admin y abogado) ----------

function abrirChat(hilo, titulo, subtitulo, remitente) {
    chatHilo = hilo
    chatRemitente = remitente
    chatContenedor = "msg-thread"
    chatEstilo = "escritorio"

    $("msg-titulo").textContent = titulo
    $("msg-subtitulo").textContent = subtitulo
    $("msg-input").value = ""
    $("msg-thread").innerHTML = ""

    abrirModal("ov-mensajes")

    cargarMensajes().then(marcarHiloLeido)

    clearInterval(chatRevision)
    chatRevision = setInterval(cargarMensajes, 5000)
}

function cerrarChat() {
    clearInterval(chatRevision)
    marcarHiloLeido()
    cerrarModal("ov-mensajes")

    // Las páginas de contactos definen esta función para quitar el punto rojo
    if (typeof despuesDeCerrarChat === "function") {
        despuesDeCerrarChat()
    }
}

function enviarMensajeModal() {
    enviarTexto("msg-input")
}

function grabarAudioModal() {
    alternarGrabacion()
}

function adjuntarArchivoModal() {
    elegirArchivo()
}

// ---------- Chat del cliente (vista móvil) ----------

function iniciarChatCliente(hilo) {
    chatHilo = hilo
    chatRemitente = "cliente"
    chatContenedor = "chat-msgs"
    chatEstilo = "movil"

    cargarMensajes().then(marcarHiloLeido)

    chatRevision = setInterval(() => {
        cargarMensajes().then(marcarHiloLeido)
    }, 5000)
}

function enviarMensajeCliente() {
    enviarTexto("chat-input")
}

function grabarAudioCliente() {
    alternarGrabacion()
}

function adjuntarArchivoCliente() {
    elegirArchivo()
}

// ---------- Mensajes sin leer ----------

// Regresa una promesa con true si en el hilo hay mensajes sin leer para "lector"
function hayMensajesSinLeer(hilo, lector) {
    return apiGet("/mensajes", { hilo: hilo })
        .then(mensajes => {
            return mensajes.some(mensaje => !mensaje.leido && mensaje.remitente !== lector)
        })
}

// Punto rojo del botón Chat en la barra del celular
function revisarChatPendiente(hilo) {
    hayMensajesSinLeer(hilo, "cliente")
        .then(pendiente => {
            $("mob-chat-badge").style.display = pendiente ? "block" : "none"
        })
        .catch(error => {
            console.log("Error al revisar el chat: " + error.message)
        })
}
