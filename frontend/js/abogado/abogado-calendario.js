// =====================================================================
// abogado-calendario.js — Agenda propia del abogado (alcance "abogado-{id}").
// Todo el calendario está en js/compartidos/calendario.js.
// =====================================================================

let sesion = exigirSesion("ABOGADO")

if (sesion) {
    iniciarCalendario("cal-abogado", "abogado-" + sesion.perfilId)
}
