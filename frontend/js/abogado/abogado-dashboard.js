// =====================================================================
// abogado-dashboard.js — Dashboard del abogado.
// Usa GET /abogados/{id} (nombre y especialidad) y
// GET /dashboards/abogados/{id} (sus indicadores).
// =====================================================================

let sesion = exigirSesion("ABOGADO")

let graficaDonaAbogado = null

let ICONO_MALETIN = `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg>`
let ICONO_RELOJ = `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>`
let ICONO_TROFEO = `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/><path d="M4 22h16M9.09 8.09 12 5l2.91 3.09"/><path d="M12 5v13"/></svg>`
let ICONO_PALOMA = `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M9 12l2 2 4-4"/></svg>`

function pintarBienvenida(abogado) {
    $("ab-banner").innerHTML = `
        <div>
            <h2>Bienvenido, <span class="gold">${escaparHtml(abogado.nombre)}</span> 👋</h2>
            <p>${escaparHtml(abogado.especialidad || "Sin especialidad")} · ${fechaHoy()}</p>
        </div>
        <div style="width:64px;height:64px;border-radius:50%;background:linear-gradient(135deg,var(--gold),var(--gold-dark));display:flex;align-items:center;justify-content:center;font-size:24px;font-weight:700;color:var(--dark)">${iniciales(abogado.nombre)}</div>`
}

function pintarIndicadores(datos) {
    let casos = datos.casos

    let tarjetas = [
        { icono: ICONO_MALETIN, valor: casos.total, nombre: "Mis Casos", etiqueta: "total", fondo: "rgba(212,175,55,.15)", color: "var(--gold-dark)" },
        { icono: ICONO_RELOJ, valor: casos.enProceso, nombre: "En Proceso", etiqueta: "activos", fondo: "rgba(41,128,185,.12)", color: "#2980B9" },
        { icono: ICONO_TROFEO, valor: casos.ganados, nombre: "Casos Ganados", etiqueta: datos.efectividad + "% éxito", fondo: "rgba(39,174,96,.12)", color: "var(--green)" },
        { icono: ICONO_PALOMA, valor: datos.efectividad + "%", nombre: "Tasa de Éxito", etiqueta: "en mejora", fondo: "rgba(212,175,55,.12)", color: "var(--gold-dark)" }
    ]

    let html = ""

    tarjetas.forEach(tarjeta => {
        html += `
            <div class="ab-kpi-card">
                <span class="ab-kpi-tag" style="background:${tarjeta.fondo};color:${tarjeta.color}">${tarjeta.etiqueta}</span>
                <div class="ab-kpi-icon" style="color:var(--gold-dark)">${tarjeta.icono}</div>
                <div class="ab-kpi-val">${tarjeta.valor}</div>
                <div class="ab-kpi-lbl">${tarjeta.nombre}</div>
            </div>`
    })

    $("ab-kpi-row").innerHTML = html
}

function pintarDonaAbogado(datos) {
    let casos = datos.casos

    $("ab-chart-sub").textContent = "Solo mis casos · " + casos.total + " total"
    $("abPct").textContent = datos.efectividadCerrados + "%"

    if (graficaDonaAbogado) {
        graficaDonaAbogado.destroy()
    }

    graficaDonaAbogado = new Chart($("abDonut"), {
        type: "doughnut",
        data: {
            datasets: [{
                data: [casos.ganados, casos.enProceso, casos.perdidos, casos.activos],
                backgroundColor: ["#27AE60", "#F39C12", "#C0392B", "#2980B9"],
                borderWidth: 0,
                borderRadius: 4,
                spacing: 3
            }]
        },
        options: {
            cutout: "70%",
            plugins: { legend: { display: false } },
            animation: { duration: 800 }
        }
    })

    // Etiquetas debajo de la dona
    let total = casos.ganados + casos.enProceso + casos.perdidos + casos.activos || 1

    let elementos = [
        { nombre: "Ganados", cantidad: casos.ganados, color: "#27AE60", clase: "pct-green" },
        { nombre: "En Proceso", cantidad: casos.enProceso, color: "#F39C12", clase: "pct-yellow" },
        { nombre: "Perdidos", cantidad: casos.perdidos, color: "#C0392B", clase: "pct-red" },
        { nombre: "Activos", cantidad: casos.activos, color: "#2980B9", clase: "pct-blue" }
    ]

    let html = ""

    elementos.forEach(elemento => {
        let plural = elemento.cantidad !== 1 ? "s" : ""

        html += `
            <div class="donut-label-item">
                <div class="dli-left"><div class="dli-dot" style="background:${elemento.color}"></div><span class="dli-name">${elemento.nombre}</span></div>
                <div class="dli-right"><span class="dli-count">${elemento.cantidad} caso${plural}</span><span class="dli-pct ${elemento.clase}">${Math.round(elemento.cantidad / total * 100)}%</span></div>
            </div>`
    })

    $("ab-donut-labels").innerHTML = html
}

// Una barra de rendimiento: etiqueta, valor y relleno de color
function barraRendimiento(valor, maximo, color, etiqueta, texto) {
    let ancho = Math.round(valor / maximo * 100)

    return `
        <div class="rend-bar-row">
            <div class="rend-bar-top">
                <span class="rend-bar-label"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="${color}" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/></svg>${etiqueta}</span>
                <span class="rend-bar-value" style="color:${color}">${texto}</span>
            </div>
            <div class="rend-bar-track"><div class="rend-bar-fill" style="width:${ancho}%;background:${color}"></div></div>
        </div>`
}

function pintarRendimiento(datos) {
    let casos = datos.casos
    let activos = casos.activos + casos.enProceso
    let total = casos.total

    $("ab-rend-grid").innerHTML =
        barraRendimiento(datos.efectividad, 100, "#C0392B", "Tasa de éxito", datos.efectividad + "%") +
        barraRendimiento(activos, Math.max(total, 1), "#2980B9", "Casos activos", activos + " / " + total) +
        barraRendimiento(datos.satisfaccion, 100, "var(--gold)", "Satisfacción del cliente", (datos.satisfaccion / 20).toFixed(1) + " / 5.0")
}

function cargarDashboardAbogado() {
    apiGet("/abogados/" + sesion.perfilId)
        .then(abogado => {
            pintarBienvenida(abogado)
            return apiGet("/dashboards/abogados/" + sesion.perfilId)
        })
        .then(datos => {
            pintarIndicadores(datos)
            pintarDonaAbogado(datos)
            pintarRendimiento(datos)
        })
        .catch(error => {
            mostrarError("No se pudo cargar tu dashboard: " + error.message)
        })
}

if (sesion) {
    cargarDashboardAbogado()
}
