// =====================================================================
// api.js — Todas las llamadas al backend pasan por aquí.
// El frontend llama a /api/... y nginx le quita el /api antes de
// mandarlo a Spring Boot (puerto 8081).
// Cada función regresa la promesa de fetch: la página decide qué hacer
// con los datos (.then) y con el error (.catch).
// =====================================================================

let URL_API = "/api"

// Revisa la respuesta: si el código HTTP es de error, lanza el mensaje
// que manda el backend en { "error": "..." }
function leerRespuesta(respuesta) {
    return respuesta.text()
        .then(texto => {
            let datos = {}

            if (texto) {
                try {
                    datos = JSON.parse(texto)
                } catch (e) {
                    datos = {}
                }
            }

            if (!respuesta.ok) {
                let mensaje = datos.error

                // 502, 503 y 504 los manda nginx cuando Spring Boot no está corriendo
                if (!mensaje && respuesta.status >= 502 && respuesta.status <= 504) {
                    mensaje = "El backend no está respondiendo (HTTP " + respuesta.status + ")"
                }

                if (!mensaje) {
                    mensaje = "El servidor no respondió bien (HTTP " + respuesta.status + ")"
                }

                throw new Error(mensaje)
            }

            return datos
        })
}

// Cuando fetch ni siquiera llega al servidor, el error es de conexión
function errorDeConexion(error) {
    if (error instanceof TypeError) {
        throw new Error("No se pudo conectar con el servidor")
    }

    throw error
}

// GET con query string opcional: apiGet("/clientes", { abogadoId: 1, q: "car" })
function apiGet(ruta, filtros) {
    let url = URL_API + ruta

    if (filtros) {
        let consulta = new URLSearchParams()

        for (let clave in filtros) {
            if (filtros[clave] !== undefined && filtros[clave] !== null && filtros[clave] !== "") {
                consulta.append(clave, filtros[clave])
            }
        }

        if (consulta.toString() !== "") {
            url = url + "?" + consulta.toString()
        }
    }

    let parametros = {
        method: "GET"
    }

    return fetch(url, parametros)
        .catch(error => {
            return errorDeConexion(error)
        })
        .then(respuesta => {
            return leerRespuesta(respuesta)
        })
}

// POST con cuerpo JSON
function apiPost(ruta, objeto) {
    let url = URL_API + ruta

    let datos = JSON.stringify(objeto)

    let parametros = {
        method: "POST",
        body: datos,
        headers: { "Content-Type": "application/json" }
    }

    return fetch(url, parametros)
        .catch(error => {
            return errorDeConexion(error)
        })
        .then(respuesta => {
            return leerRespuesta(respuesta)
        })
}

// PUT con cuerpo JSON (o sin cuerpo, por ejemplo para "marcar leídas")
function apiPut(ruta, objeto) {
    let url = URL_API + ruta

    let parametros = {
        method: "PUT",
        headers: { "Content-Type": "application/json" }
    }

    if (objeto) {
        parametros.body = JSON.stringify(objeto)
    }

    return fetch(url, parametros)
        .catch(error => {
            return errorDeConexion(error)
        })
        .then(respuesta => {
            return leerRespuesta(respuesta)
        })
}

// DELETE
function apiDelete(ruta) {
    let url = URL_API + ruta

    let parametros = {
        method: "DELETE"
    }

    return fetch(url, parametros)
        .catch(error => {
            return errorDeConexion(error)
        })
        .then(respuesta => {
            return leerRespuesta(respuesta)
        })
}

// POST multipart para subir archivos (fotos, audios, documentos).
// No se pone Content-Type: el navegador lo arma con el FormData.
function apiSubirArchivo(ruta, formulario) {
    let url = URL_API + ruta

    let parametros = {
        method: "POST",
        body: formulario
    }

    return fetch(url, parametros)
        .catch(error => {
            return errorDeConexion(error)
        })
        .then(respuesta => {
            return leerRespuesta(respuesta)
        })
}
