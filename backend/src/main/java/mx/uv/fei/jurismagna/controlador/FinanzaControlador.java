package mx.uv.fei.jurismagna.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import mx.uv.fei.jurismagna.modelo.Movimiento;
import mx.uv.fei.jurismagna.servicio.FinanzaServicio;

@RestController
public class FinanzaControlador {

    @Autowired
    FinanzaServicio finanzaServicio;

    @GetMapping("/movimientos")
    public List<Movimiento> listarMovimientos() {
        return finanzaServicio.listarMovimientos();
    }

    @PostMapping("/movimientos")
    public Movimiento crearMovimiento(@RequestBody Movimiento movimiento) {
        return finanzaServicio.crearMovimiento(movimiento);
    }
}
