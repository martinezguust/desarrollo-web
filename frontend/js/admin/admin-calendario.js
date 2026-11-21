// =====================================================================
// admin-calendario.js — Agenda del administrador (alcance "admin").
// Todo el calendario está en js/compartidos/calendario.js.
// =====================================================================

let sesion = exigirSesion("ADMIN")

if (sesion) {
    iniciarCalendario("cal-admin", "admin")
}
