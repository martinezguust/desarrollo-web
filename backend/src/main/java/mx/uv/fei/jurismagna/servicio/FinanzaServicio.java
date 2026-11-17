package mx.uv.fei.jurismagna.servicio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import mx.uv.fei.jurismagna.modelo.Movimiento;
import mx.uv.fei.jurismagna.repositorio.MovimientoRepositorio;

@Service
public class FinanzaServicio {

    // Conceptos permitidos según el tipo; "Otros" puede ser de los dos
    static final List<String> CONCEPTOS_INGRESO = List.of("Honorarios", "Consultas y asesorías", "Otros");
    static final List<String> CONCEPTOS_EGRESO = List.of("Nómina", "Renta", "Servicios", "Retención fiscal", "Otros");

    // Conceptos de la dona de gastos (la retención fiscal no entra)
    static final List<String> CONCEPTOS_GASTO = List.of("Nómina", "Renta", "Servicios", "Otros");

    static final String RETENCION = "Retención fiscal";

    static final String[] NOMBRES_MESES = {
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    @Autowired
    MovimientoRepositorio movimientoRepositorio;

    // ---------- Movimientos (CRUD) ----------

    // Sin anio y mes devuelve todos; con los dos, solo los de ese mes
    public List<Movimiento> listarMovimientos(Integer anio, Integer mes) {
        if (anio == null && mes == null) {
            return movimientoRepositorio.findAllByOrderByFechaAscIdAsc();
        }

        YearMonth periodo = mesValido(anio, mes);

        return movimientoRepositorio.findByFechaBetweenOrderByFechaAscIdAsc(periodo.atDay(1), periodo.atEndOfMonth());
    }

    public Movimiento crearMovimiento(Movimiento movimiento) {
        validarConcepto(movimiento);
        movimiento.setId(null);

        return movimientoRepositorio.save(movimiento);
    }

    public Movimiento editarMovimiento(Integer id, Movimiento datos) {
        Movimiento movimiento = buscarMovimiento(id);
        validarConcepto(datos);

        movimiento.setTipo(datos.getTipo());
        movimiento.setConcepto(datos.getConcepto());
        movimiento.setMonto(datos.getMonto());
        movimiento.setFecha(datos.getFecha());

        return movimientoRepositorio.save(movimiento);
    }

    public void eliminarMovimiento(Integer id) {
        movimientoRepositorio.delete(buscarMovimiento(id));
    }

    private Movimiento buscarMovimiento(Integer id) {
        return movimientoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el movimiento " + id));
    }

    // Los campos vacíos y el monto los revisa @Valid; aquí se revisa que el concepto vaya con el tipo
    private void validarConcepto(Movimiento movimiento) {
        List<String> permitidos;

        if (movimiento.getTipo().equals("INGRESO")) {
            permitidos = CONCEPTOS_INGRESO;
        } else if (movimiento.getTipo().equals("EGRESO")) {
            permitidos = CONCEPTOS_EGRESO;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo debe ser INGRESO o EGRESO");
        }

        if (!permitidos.contains(movimiento.getConcepto())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El concepto " + movimiento.getConcepto() + " no corresponde a un " + movimiento.getTipo());
        }
    }

    // anio y mes deben llegar juntos y el mes debe estar entre 1 y 12
    private YearMonth mesValido(Integer anio, Integer mes) {
        if (anio == null || mes == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indica anio y mes juntos");
        }

        if (mes < 1 || mes > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El mes debe estar entre 1 y 12");
        }

        return YearMonth.of(anio, mes);
    }

    // ---------- Reporte de finanzas: GET /finanzas?meses=3 ----------

    public Map<String, Object> reporte(Integer meses) {
        if (meses < 1 || meses > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El parámetro meses debe estar entre 1 y 12");
        }

        List<Movimiento> todos = movimientoRepositorio.findAllByOrderByFechaAscIdAsc();
        YearMonth ultimo = mesActual();
        YearMonth primero = ultimo.minusMonths(meses - 1);

        // Ingresos y egresos de cada mes (para las barras)
        List<Map<String, Object>> listaMeses = new ArrayList<>();

        for (YearMonth mes = primero; !mes.isAfter(ultimo); mes = mes.plusMonths(1)) {
            Map<String, Object> datosMes = new LinkedHashMap<>();
            datosMes.put("mes", mes.toString());
            datosMes.put("nombre", nombreMes(mes));
            datosMes.put("ingresos", ingresosDelMes(todos, mes));
            datosMes.put("egresos", egresosDelMes(todos, mes, false));
            listaMeses.add(datosMes);
        }

        // Totales del periodo y gastos por concepto (para la dona)
        Map<String, BigDecimal> gastosPorConcepto = new LinkedHashMap<>();

        for (String concepto : CONCEPTOS_GASTO) {
            gastosPorConcepto.put(concepto, BigDecimal.ZERO);
        }

        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal retencionFiscal = BigDecimal.ZERO;
        BigDecimal totalEgresos = BigDecimal.ZERO;
        List<Movimiento> registroIngresos = new ArrayList<>();
        List<Movimiento> registroEgresos = new ArrayList<>();

        for (Movimiento movimiento : todos) {
            YearMonth mes = YearMonth.from(movimiento.getFecha());

            if (mes.isBefore(primero) || mes.isAfter(ultimo)) {
                continue;
            }

            if (movimiento.getTipo().equals("INGRESO")) {
                totalIngresos = totalIngresos.add(movimiento.getMonto());
                registroIngresos.add(movimiento);
            } else if (movimiento.getConcepto().equals(RETENCION)) {
                // La retención se muestra con "−" en la tabla de ingresos
                retencionFiscal = retencionFiscal.add(movimiento.getMonto());
                registroIngresos.add(movimiento);
            } else {
                totalEgresos = totalEgresos.add(movimiento.getMonto());
                gastosPorConcepto.put(movimiento.getConcepto(), gastosPorConcepto.get(movimiento.getConcepto()).add(movimiento.getMonto()));
                registroEgresos.add(movimiento);
            }
        }

        BigDecimal ingresosNetos = totalIngresos.subtract(retencionFiscal);

        Map<String, Object> reporte = new LinkedHashMap<>();
        reporte.put("meses", listaMeses);
        reporte.put("gastosPorConcepto", gastosPorConcepto);
        reporte.put("totalIngresos", totalIngresos);
        reporte.put("retencionFiscal", retencionFiscal);
        reporte.put("ingresosNetos", ingresosNetos);
        reporte.put("totalEgresos", totalEgresos);
        reporte.put("neto", ingresosNetos.subtract(totalEgresos));
        reporte.put("fondosDisponibles", capitalAlCierre(todos, ultimo));
        reporte.put("rentabilidad", rentabilidad(todos, ultimo));
        reporte.put("rentabilidadTendencia", redondear(rentabilidad(todos, ultimo) - rentabilidad(todos, ultimo.minusMonths(1))));
        reporte.put("registroIngresos", registroIngresos);
        reporte.put("registroEgresos", registroEgresos);

        return reporte;
    }

    // ---------- Indicadores del mes para el dashboard ----------

    // honorariosTotales es la suma de los honorarios de todos los clientes
    public Map<String, Object> resumenDelMes(BigDecimal honorariosTotales) {
        List<Movimiento> todos = movimientoRepositorio.findAllByOrderByFechaAscIdAsc();
        YearMonth mes = mesActual();
        YearMonth anterior = mes.minusMonths(1);

        BigDecimal capital = capitalAlCierre(todos, mes);
        BigDecimal capitalAnterior = capitalAlCierre(todos, anterior);
        BigDecimal facturacion = ingresosDelMes(todos, mes);
        BigDecimal facturacionAnterior = ingresosDelMes(todos, anterior);
        BigDecimal cobrado = honorariosDelMes(todos, mes);
        BigDecimal pendientes = cobrosPendientes(todos, mes, honorariosTotales);
        BigDecimal pendientesAnterior = cobrosPendientes(todos, anterior, honorariosTotales);

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("mes", mes.toString());
        resumen.put("nombreMes", nombreMes(mes));
        resumen.put("nombreMesAnterior", nombreMes(anterior));
        resumen.put("capitalOperativo", capital);
        resumen.put("capitalTendencia", porcentajeDeCambio(capital, capitalAnterior));
        resumen.put("rentabilidad", rentabilidad(todos, mes));
        resumen.put("rentabilidadTendencia", redondear(rentabilidad(todos, mes) - rentabilidad(todos, anterior)));
        resumen.put("facturacionMes", facturacion);
        resumen.put("facturacionTendencia", porcentajeDeCambio(facturacion, facturacionAnterior));
        resumen.put("cobradoMes", cobrado);
        resumen.put("porcentajeCobrado", porcentaje(cobrado, facturacion));
        resumen.put("cobrosPendientes", pendientes);
        resumen.put("cobrosPendientesCambio", pendientes.subtract(pendientesAnterior));

        return resumen;
    }

    // ---------- Cálculos de apoyo ----------

    // Último mes con movimientos; si no hay ninguno, el mes de hoy
    private YearMonth mesActual() {
        Movimiento ultimo = movimientoRepositorio.findTopByOrderByFechaDesc();

        if (ultimo == null) {
            return YearMonth.now();
        }

        return YearMonth.from(ultimo.getFecha());
    }

    private String nombreMes(YearMonth mes) {
        return NOMBRES_MESES[mes.getMonthValue() - 1];
    }

    private BigDecimal ingresosDelMes(List<Movimiento> todos, YearMonth mes) {
        BigDecimal suma = BigDecimal.ZERO;

        for (Movimiento movimiento : todos) {
            if (YearMonth.from(movimiento.getFecha()).equals(mes) && movimiento.getTipo().equals("INGRESO")) {
                suma = suma.add(movimiento.getMonto());
            }
        }

        return suma;
    }

    // conRetencion = false da los gastos operativos de las gráficas
    private BigDecimal egresosDelMes(List<Movimiento> todos, YearMonth mes, boolean conRetencion) {
        BigDecimal suma = BigDecimal.ZERO;

        for (Movimiento movimiento : todos) {
            boolean esDelMes = YearMonth.from(movimiento.getFecha()).equals(mes);
            boolean esEgreso = movimiento.getTipo().equals("EGRESO");
            boolean cuenta = conRetencion || !movimiento.getConcepto().equals(RETENCION);

            if (esDelMes && esEgreso && cuenta) {
                suma = suma.add(movimiento.getMonto());
            }
        }

        return suma;
    }

    private BigDecimal honorariosDelMes(List<Movimiento> todos, YearMonth mes) {
        BigDecimal suma = BigDecimal.ZERO;

        for (Movimiento movimiento : todos) {
            if (YearMonth.from(movimiento.getFecha()).equals(mes) && movimiento.getConcepto().equals("Honorarios")) {
                suma = suma.add(movimiento.getMonto());
            }
        }

        return suma;
    }

    // Ingresos menos egresos de todos los movimientos hasta el cierre del mes
    private BigDecimal capitalAlCierre(List<Movimiento> todos, YearMonth mes) {
        BigDecimal capital = BigDecimal.ZERO;

        for (Movimiento movimiento : todos) {
            if (YearMonth.from(movimiento.getFecha()).isAfter(mes)) {
                continue;
            }

            if (movimiento.getTipo().equals("INGRESO")) {
                capital = capital.add(movimiento.getMonto());
            } else {
                capital = capital.subtract(movimiento.getMonto());
            }
        }

        return capital;
    }

    // Honorarios de los clientes que todavía no se han cobrado (nunca negativo)
    private BigDecimal cobrosPendientes(List<Movimiento> todos, YearMonth mes, BigDecimal honorariosTotales) {
        BigDecimal cobrado = BigDecimal.ZERO;

        for (Movimiento movimiento : todos) {
            boolean hastaElMes = !YearMonth.from(movimiento.getFecha()).isAfter(mes);

            if (hastaElMes && movimiento.getTipo().equals("INGRESO") && movimiento.getConcepto().equals("Honorarios")) {
                cobrado = cobrado.add(movimiento.getMonto());
            }
        }

        return honorariosTotales.subtract(cobrado).max(BigDecimal.ZERO);
    }

    // (ingresos − egresos) / ingresos del mes × 100
    private double rentabilidad(List<Movimiento> todos, YearMonth mes) {
        BigDecimal ingresos = ingresosDelMes(todos, mes);
        BigDecimal egresos = egresosDelMes(todos, mes, true);

        return porcentaje(ingresos.subtract(egresos), ingresos);
    }

    private double porcentaje(BigDecimal parte, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }

        return redondear(parte.multiply(BigDecimal.valueOf(100)).divide(total, 4, RoundingMode.HALF_UP).doubleValue());
    }

    private double porcentajeDeCambio(BigDecimal actual, BigDecimal anterior) {
        if (anterior.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }

        return porcentaje(actual.subtract(anterior), anterior);
    }

    // Un decimal: 48.78 -> 48.8
    private double redondear(double valor) {
        return Math.round(valor * 10) / 10.0;
    }
}
