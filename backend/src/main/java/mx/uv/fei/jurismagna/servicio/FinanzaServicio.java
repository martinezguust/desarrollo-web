package mx.uv.fei.jurismagna.servicio;

import java.math.BigDecimal;
import java.util.List;

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

    @Autowired
    MovimientoRepositorio movimientoRepositorio;

    public List<Movimiento> listarMovimientos() {
        return movimientoRepositorio.findAllByOrderByFechaAscIdAsc();
    }

    public Movimiento crearMovimiento(Movimiento movimiento) {
        validar(movimiento);
        movimiento.setId(null);

        return movimientoRepositorio.save(movimiento);
    }

    private void validar(Movimiento movimiento) {
        if (movimiento.getTipo() == null || movimiento.getConcepto() == null
                || movimiento.getMonto() == null || movimiento.getFecha() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan el tipo, el concepto, el monto o la fecha");
        }

        if (movimiento.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El monto debe ser mayor que 0");
        }

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
}
