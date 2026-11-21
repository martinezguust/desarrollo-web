// =====================================================================
// admin-dashboard.js — Panel ejecutivo del administrador (RF-03).
// Todos los números salen de GET /dashboards/admin.
// =====================================================================

let sesion = exigirSesion("ADMIN")

let graficaDona = null

// "↑ +14.2% vs mes anterior" en verde, o "↓ -3% ..." en rojo
function pintarTendencia(id, valor, sufijo, comparacion) {
    let elemento = $(id)

    if (valor === null || valor === undefined) {
        elemento.textContent = ""
        return
    }

    let numero = Number(valor)

    if (numero >= 0) {
        elemento.textContent = "↑ +" + numero + sufijo + " " + comparacion
        elemento.className = "fin-kpi-trend trend-up"
    } else {
        elemento.textContent = "↓ " + numero + sufijo + " " + comparacion
        elemento.className = "fin-kpi-trend trend-dn"
    }
}

// Lista de etiquetas debajo de la dona (ganados, en proceso, perdidos, activos)
function pintarEtiquetasDona(idContenedor, casos) {
    let total = casos.ganados + casos.enProceso + casos.perdidos + casos.activos

    if (total === 0) {
        total = 1
    }

    let elementos = [
        { nombre: "Ganados", cantidad: casos.ganados, color: "#27AE60", clase: "pct-green" },
        { nombre: "En Proceso", cantidad: casos.enProceso, color: "#F39C12", clase: "pct-yellow" },
        { nombre: "Perdidos", cantidad: casos.perdidos, color: "#C0392B", clase: "pct-red" },
        { nombre: "Activos", cantidad: casos.activos, color: "#2980B9", clase: "pct-blue" }
    ]

    let html = ""

    elementos.forEach(elemento => {
        let plural = elemento.cantidad !== 1 ? "s" : ""
        let porcentaje = Math.round(elemento.cantidad / total * 100)

        html += `
            <div class="donut-label-item">
                <div class="dli-left">
                    <div class="dli-dot" style="background:${elemento.color}"></div>
                    <span class="dli-name">${elemento.nombre}</span>
                </div>
                <div class="dli-right">
                    <span class="dli-count">${elemento.cantidad} caso${plural}</span>
                    <span class="dli-pct ${elemento.clase}">${porcentaje}%</span>
                </div>
            </div>`
    })

    $(idContenedor).innerHTML = html
}

function pintarDona(casos) {
    if (graficaDona) {
        graficaDona.destroy()
    }

    let nombres = ["Ganados", "En Proceso", "Perdidos", "Activos"]

    graficaDona = new Chart($("adminDonut"), {
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
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: contexto => nombres[contexto.dataIndex] + ": " + contexto.parsed
                    }
                }
            },
            animation: { duration: 800 }
        }
    })
}

function pintarDashboard(datos) {
    let casos = datos.casos
    let finanzas = datos.finanzas

    // Tarjetas financieras
    $("exec-mes").textContent = finanzas.nombreMes + " " + finanzas.mes.slice(0, 4)
    $("kpi-capital").textContent = formatoMoneda(finanzas.capitalOperativo)
    pintarTendencia("kpi-capital-tendencia", finanzas.capitalTendencia, "%", "vs mes anterior")

    $("kpi-rentabilidad").textContent = finanzas.rentabilidad + "%"
    pintarTendencia("kpi-rentabilidad-tendencia", finanzas.rentabilidadTendencia, " pp", "vs mes anterior")

    $("kpi-facturacion-titulo").textContent = "Facturación " + finanzas.nombreMes
    $("kpi-facturacion").textContent = formatoMoneda(finanzas.facturacionMes)
    pintarTendencia("kpi-facturacion-tendencia", finanzas.facturacionTendencia, "%", "vs " + finanzas.nombreMesAnterior)

    // Personal y casos
    $("s-personal").textContent = datos.personal
    $("s-abogados").textContent = datos.abogados
    $("s-activos").textContent = casos.activos + casos.enProceso
    $("s-ganados").textContent = casos.ganados
    $("s-perdidos").textContent = casos.perdidos

    // Dona de efectividad
    $("admin-total-casos").textContent = casos.total
    $("adminPct").textContent = datos.efectividadCerrados + "%"
    pintarDona(casos)
    pintarEtiquetasDona("admin-donut-labels", casos)

    // Resumen financiero
    let cambio = Number(finanzas.cobrosPendientesCambio)
    let signo = cambio >= 0 ? "+" : "-"

    $("kpi-pendientes").textContent = formatoMoneda(finanzas.cobrosPendientes)
    $("kpi-pendientes-cambio").textContent = "↗ " + signo + formatoMoneda(Math.abs(cambio)) + " este mes"

    $("kpi-cobrado-titulo").textContent = "Cobrado " + finanzas.nombreMes
    $("kpi-cobrado").textContent = formatoMoneda(finanzas.cobradoMes)
    $("kpi-cobrado-pct").textContent = "↑ " + finanzas.porcentajeCobrado + "% cobrado"

    $("kpi-honorarios").textContent = formatoMoneda(datos.honorariosTotales)
    $("kpi-clientes-activos").textContent = datos.clientesActivos
    $("kpi-total-clientes").textContent = datos.totalClientes
}

function cargarDashboard() {
    apiGet("/dashboards/admin")
        .then(datos => {
            pintarDashboard(datos)
        })
        .catch(error => {
            mostrarError("No se pudo cargar el panel: " + error.message)
        })
}

if (sesion) {
    cargarDashboard()
}
