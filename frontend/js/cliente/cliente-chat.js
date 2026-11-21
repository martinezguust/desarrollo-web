// =====================================================================
// cliente-chat.js — Chat del cliente con su abogado (hilo "cliente-{id}").
// Los mensajes, audios y archivos se manejan en js/compartidos/chat.js.
// =====================================================================

let sesion = exigirSesion("CLIENTE")

if (sesion) {
    iniciarChatCliente("cliente-" + sesion.perfilId)
}
