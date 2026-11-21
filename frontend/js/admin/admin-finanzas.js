// =====================================================================
// admin-finanzas.js — Administración y finanzas (RF-04).
// GET /finanzas?meses=3 trae las tarjetas, las gráficas y los registros.
// Los movimientos se crean, editan y eliminan con /movimientos.
// El reporte PDF se arma con jsPDF y se descarga con un clic.
// =====================================================================

let sesion = exigirSesion("ADMIN")

let MESES_REPORTE = 3

let reporteActual = null
let graficaBarras = null
let graficaPastel = null

// Colores de la dona de gastos, en el orden en que llegan los conceptos
let COLORES_GASTOS = ["#2980B9", "#F39C12", "#8E44AD", "#E67E22", "#16A085", "#7F8C8D"]

function cargarFinanzas() {
    apiGet("/finanzas", { meses: MESES_REPORTE })
        .then(reporte => {
            reporteActual = reporte
            pintarFinanzas(reporte)
        })
        .catch(error => {
            mostrarError("No se pudieron cargar las finanzas: " + error.message)
        })
}

// "Agosto + Septiembre + Octubre 2026"
function textoPeriodo(meses) {
    if (meses.length === 0) {
        return "Sin movimientos"
    }

    let nombres = meses.map(mes => mes.nombre.slice(0, 3))
    let anio = meses[meses.length - 1].mes.slice(0, 4)

    return nombres.join(" + ") + " " + anio
}

function nombreDelMes(fecha) {
    let numero = Number(String(fecha).slice(5, 7))

    return NOMBRES_MESES[numero - 1]
}

function pintarFinanzas(reporte) {
    let ultimoMes = reporte.meses[reporte.meses.length - 1]

    // Tarjeta oscura y tarjetas de resumen
    $("fin-fondos").textContent = formatoMoneda(reporte.fondosDisponibles)
    $("fin-fondos-sub").textContent = "Ingresos netos operativos · " + ultimoMes.nombre + " " + ultimoMes.mes.slice(0, 4)
    $("fin-rentabilidad").textContent = reporte.rentabilidad + "%"

    let tendencia = Number(reporte.rentabilidadTendencia)
    $("fin-rentabilidad-tendencia").textContent = (tendencia >= 0 ? "↑ +" : "↓ ") + tendencia + " pp"

    $("fin-total-ingresos").textContent = formatoMoneda(reporte.totalIngresos)
    $("fin-periodo").textContent = textoPeriodo(reporte.meses)
    $("fin-total-egresos").textContent = formatoMoneda(reporte.totalEgresos)
    $("fin-neto").textContent = formatoMoneda(reporte.neto)
    $("fin-titulo-barras").textContent = "Ingresos vs Egresos — Últimos " + reporte.meses.length + " meses"

    pintarGraficas(reporte)
    pintarRegistros(reporte)
}

function pintarGraficas(reporte) {
    if (graficaBarras) {
        graficaBarras.destroy()
    }

    if (graficaPastel) {
        graficaPastel.destroy()
    }

    graficaBarras = new Chart($("finBar"), {
        type: "bar",
        data: {
            labels: reporte.meses.map(mes => mes.nombre),
            datasets: [
                {
                    label: "Ingresos",
                    data: reporte.meses.map(mes => Number(mes.ingresos)),
                    backgroundColor: "rgba(39,174,96,0.75)",
                    borderRadius: 8,
                    borderSkipped: false
                },
                {
                    label: "Egresos",
                    data: reporte.meses.map(mes => Number(mes.egresos)),
                    backgroundColor: "rgba(192,57,43,0.65)",
                    borderRadius: 8,
                    borderSkipped: false
                }
            ]
        },
        options: {
            responsive: true,
            plugins: { legend: { position: "top" } },
            scales: {
                x: { grid: { display: false }, border: { display: false }, ticks: { color: "#6B7280" } },
                y: {
                    grid: { color: "rgba(212,175,55,0.08)" },
                    border: { display: false },
                    ticks: { color: "#6B7280", callback: valor => "$" + (valor / 1000).toFixed(0) + "k" }
                }
            }
        }
    })

    let conceptos = Object.keys(reporte.gastosPorConcepto)

    graficaPastel = new Chart($("finPie"), {
        type: "doughnut",
        data: {
            labels: conceptos,
            datasets: [{
                data: conceptos.map(concepto => Number(reporte.gastosPorConcepto[concepto])),
                backgroundColor: COLORES_GASTOS,
                borderWidth: 0,
                spacing: 2,
                borderRadius: 3
            }]
        },
        options: {
            cutout: "55%",
            plugins: {
                legend: { position: "bottom", labels: { font: { size: 11 }, padding: 8 } },
                tooltip: {
                    callbacks: {
                        label: contexto => contexto.label + ": $" + (contexto.parsed / 1000).toFixed(0) + "k"
                    }
                }
            },
            animation: { duration: 600 }
        }
    })
}

// Un renglón de la tabla. La retención fiscal es EGRESO aunque vaya en la
// tabla de ingresos: se muestra en rojo y con "-".
function renglonMovimiento(movimiento) {
    let esIngreso = movimiento.tipo === "INGRESO"
    let color = esIngreso ? "var(--green)" : "var(--red)"
    let signo = esIngreso ? "+" : "-"

    return `
        <tr>
            <td>${escaparHtml(movimiento.concepto)}</td>
            <td>${nombreDelMes(movimiento.fecha)}</td>
            <td style="color:${color}">${signo}${formatoMoneda(movimiento.monto)}</td>
            <td><button class="btn btn-outline btn-sm" onclick="abrirEditarMovimiento(${movimiento.id})">✏️</button></td>
        </tr>`
}

function pintarRegistros(reporte) {
    let egresos = ""
    let ingresos = ""

    reporte.registroEgresos.forEach(movimiento => {
        egresos += renglonMovimiento(movimiento)
    })

    reporte.registroIngresos.forEach(movimiento => {
        ingresos += renglonMovimiento(movimiento)
    })

    egresos += `<tr class="total-row"><td colspan="2"><b>Total Egresos</b></td><td style="color:var(--red)"><b>-${formatoMoneda(reporte.totalEgresos)}</b></td><td></td></tr>`
    ingresos += `<tr class="total-row"><td colspan="2"><b>Total Ingresos Netos</b></td><td style="color:var(--green)"><b>+${formatoMoneda(reporte.ingresosNetos)}</b></td><td></td></tr>`

    $("tabla-egresos").innerHTML = egresos
    $("tabla-ingresos").innerHTML = ingresos
}

// ---------- Movimientos: nuevo, editar, eliminar ----------

// Date -> "2026-11-20" con la fecha de México, no la de UTC
function textoFechaLocal(fecha) {
    let mes = String(fecha.getMonth() + 1).padStart(2, "0")
    let dia = String(fecha.getDate()).padStart(2, "0")

    return fecha.getFullYear() + "-" + mes + "-" + dia
}

function abrirNuevoMovimiento() {
    $("mov-titulo").textContent = "Nuevo Movimiento"
    $("mov-id").value = ""
    $("mov-tipo").value = "INGRESO"
    $("mov-concepto").value = "Honorarios"
    $("mov-monto").value = ""
    $("mov-fecha").value = textoFechaLocal(new Date())
    $("mov-btn-eliminar").style.display = "none"

    abrirModal("ov-movimiento")
}

function abrirEditarMovimiento(id) {
    let todos = reporteActual.registroIngresos.concat(reporteActual.registroEgresos)
    let movimiento = todos.find(elemento => elemento.id === id)

    $("mov-titulo").textContent = "Editar Movimiento"
    $("mov-id").value = movimiento.id
    $("mov-tipo").value = movimiento.tipo
    $("mov-concepto").value = movimiento.concepto
    $("mov-monto").value = Number(movimiento.monto)
    $("mov-fecha").value = movimiento.fecha
    $("mov-btn-eliminar").style.display = "inline-flex"

    abrirModal("ov-movimiento")
}

function guardarMovimiento() {
    let id = $("mov-id").value

    let datos = {
        tipo: $("mov-tipo").value,
        concepto: $("mov-concepto").value,
        monto: $("mov-monto").value,
        fecha: $("mov-fecha").value
    }

    let peticion = null

    if (id) {
        peticion = apiPut("/movimientos/" + id, datos)
    } else {
        peticion = apiPost("/movimientos", datos)
    }

    peticion
        .then(() => {
            cerrarModal("ov-movimiento")
            cargarFinanzas()
            mostrarExito(id ? "Movimiento actualizado" : "Movimiento registrado")
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

function eliminarMovimiento() {
    let id = $("mov-id").value

    if (!confirm("¿Eliminar este movimiento?")) {
        return
    }

    apiDelete("/movimientos/" + id)
        .then(() => {
            cerrarModal("ov-movimiento")
            cargarFinanzas()
            mostrarExito("Movimiento eliminado")
        })
        .catch(error => {
            mostrarError(error.message)
        })
}

// ---------- Reporte PDF (jsPDF) ----------

// Escribe una tabla sencilla y regresa la altura donde terminó
function tablaPdf(documento, y, titulo, encabezados, renglones, anchos) {
    documento.setFont("helvetica", "bold")
    documento.setFontSize(12)
    documento.setTextColor(26, 28, 32)
    documento.text(titulo, 15, y)
    y = y + 7

    documento.setFillColor(250, 250, 250)
    documento.rect(15, y - 5, 180, 7, "F")
    documento.setFontSize(8)
    documento.setTextColor(107, 114, 128)

    let x = 17

    encabezados.forEach((encabezado, i) => {
        documento.text(encabezado.toUpperCase(), x, y)
        x = x + anchos[i]
    })

    y = y + 7
    documento.setFontSize(10)

    renglones.forEach(renglon => {
        // Salto de página si ya no cabe
        if (y > 280) {
            documento.addPage()
            y = 20
        }

        x = 17
        documento.setFont("helvetica", renglon.negrita ? "bold" : "normal")

        renglon.celdas.forEach((celda, i) => {
            if (renglon.colores && renglon.colores[i]) {
                documento.setTextColor(...renglon.colores[i])
            } else {
                documento.setTextColor(26, 28, 32)
            }

            documento.text(String(celda), x, y)
            x = x + anchos[i]
        })

        documento.setDrawColor(243, 244, 246)
        documento.line(15, y + 2, 195, y + 2)
        y = y + 7
    })

    return y + 6
}

function exportarPDF() {
    if (!reporteActual) {
        mostrarError("Todavía no se cargan las finanzas.")
        return
    }

    // Los indicadores del despacho salen del dashboard
    apiGet("/dashboards/admin")
        .then(dashboard => {
            crearPDF(reporteActual, dashboard)
            mostrarExito("Reporte PDF descargado")
        })
        .catch(error => {
            mostrarError("No se pudo crear el reporte: " + error.message)
        })
}

function crearPDF(reporte, dashboard) {
    let documento = new window.jspdf.jsPDF()
    let verde = [39, 174, 96]
    let rojo = [192, 57, 43]

    // Encabezado
    documento.setFont("helvetica", "bold")
    documento.setFontSize(20)
    documento.setTextColor(26, 28, 32)
    documento.text("JURIS", 15, 20)
    documento.setTextColor(212, 175, 55)
    documento.text("MAGNA", 41, 20)

    documento.setFont("helvetica", "normal")
    documento.setFontSize(10)
    documento.setTextColor(107, 114, 128)
    documento.text("Reporte de Administración y Finanzas · " + fechaHoy(), 15, 27)

    documento.text("Fondos disponibles", 195, 16, { align: "right" })
    documento.setFont("helvetica", "bold")
    documento.setFontSize(16)
    documento.setTextColor(212, 175, 55)
    documento.text(formatoMoneda(reporte.fondosDisponibles), 195, 24, { align: "right" })

    documento.setDrawColor(212, 175, 55)
    documento.setLineWidth(0.8)
    documento.line(15, 32, 195, 32)
    documento.setLineWidth(0.2)

    // Tarjetas de resumen
    let tarjetas = [
        { titulo: "TOTAL INGRESOS", valor: reporte.totalIngresos, color: verde },
        { titulo: "TOTAL EGRESOS", valor: reporte.totalEgresos, color: rojo },
        { titulo: "NETO", valor: reporte.neto, color: [212, 175, 55] }
    ]

    tarjetas.forEach((tarjeta, i) => {
        let x = 15 + i * 61

        documento.setDrawColor(229, 231, 235)
        documento.rect(x, 38, 57, 22)
        documento.setFillColor(...tarjeta.color)
        documento.rect(x, 38, 1.5, 22, "F")

        documento.setFont("helvetica", "normal")
        documento.setFontSize(8)
        documento.setTextColor(107, 114, 128)
        documento.text(tarjeta.titulo, x + 5, 45)

        documento.setFont("helvetica", "bold")
        documento.setFontSize(13)
        documento.setTextColor(26, 28, 32)
        documento.text(formatoMoneda(tarjeta.valor), x + 5, 53)

        documento.setFont("helvetica", "normal")
        documento.setFontSize(8)
        documento.setTextColor(107, 114, 128)
        documento.text(textoPeriodo(reporte.meses), x + 5, 58)
    })

    let anchos = [90, 45, 45]

    // Registro de egresos
    let renglonesEgresos = reporte.registroEgresos.map(movimiento => {
        return {
            celdas: [movimiento.concepto, nombreDelMes(movimiento.fecha), "-" + formatoMoneda(movimiento.monto)],
            colores: [null, null, rojo]
        }
    })

    renglonesEgresos.push({
        celdas: ["Total Egresos", "", "-" + formatoMoneda(reporte.totalEgresos)],
        colores: [null, null, rojo],
        negrita: true
    })

    let y = tablaPdf(documento, 72, "Registro de Egresos", ["Concepto", "Mes", "Monto"], renglonesEgresos, anchos)

    // Registro de ingresos (la retención va en rojo)
    let renglonesIngresos = reporte.registroIngresos.map(movimiento => {
        let esIngreso = movimiento.tipo === "INGRESO"
        let signo = esIngreso ? "+" : "-"

        return {
            celdas: [movimiento.concepto, nombreDelMes(movimiento.fecha), signo + formatoMoneda(movimiento.monto)],
            colores: [null, null, esIngreso ? verde : rojo]
        }
    })

    renglonesIngresos.push({
        celdas: ["Total Ingresos Netos", "", "+" + formatoMoneda(reporte.ingresosNetos)],
        colores: [null, null, verde],
        negrita: true
    })

    y = tablaPdf(documento, y, "Registro de Ingresos", ["Concepto", "Mes", "Monto"], renglonesIngresos, anchos)

    // Resumen del despacho
    let casos = dashboard.casos

    let renglonesResumen = [
        { celdas: ["Total abogados", dashboard.abogados] },
        { celdas: ["Total clientes", dashboard.totalClientes] },
        { celdas: ["Casos activos", casos.activos + casos.enProceso] },
        { celdas: ["Casos ganados", casos.ganados], colores: [null, verde] },
        { celdas: ["Casos perdidos", casos.perdidos], colores: [null, rojo] },
        { celdas: ["Honorarios totales registrados", formatoMoneda(dashboard.honorariosTotales)], negrita: true }
    ]

    if (y > 220) {
        documento.addPage()
        y = 20
    }

    y = tablaPdf(documento, y, "Resumen del Despacho", ["Indicador", "Valor"], renglonesResumen, [135, 45])

    // Pie de página
    documento.setFont("helvetica", "normal")
    documento.setFontSize(9)
    documento.setTextColor(156, 163, 175)
    documento.text("Juris Magna — Documento Confidencial", 15, 288)
    documento.text(String(new Date().getFullYear()), 195, 288, { align: "right" })

    // Fecha local (toISOString daría la fecha en UTC)
    let hoy = new Date()
    let fecha = textoFechaLocal(hoy)

    documento.save("JurisMagna_Finanzas_" + fecha + ".pdf")
}

if (sesion) {
    cargarFinanzas()
}
