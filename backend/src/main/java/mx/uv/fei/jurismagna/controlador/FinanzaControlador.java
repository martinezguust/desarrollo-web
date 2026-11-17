package mx.uv.fei.jurismagna.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import mx.uv.fei.jurismagna.modelo.Movimiento;
import mx.uv.fei.jurismagna.servicio.FinanzaServicio;

@RestController
public class FinanzaControlador {

    @Autowired
    FinanzaServicio finanzaServicio;

    // Reporte de los últimos meses: /finanzas?meses=3
    @GetMapping("/finanzas")
    public Map<String, Object> reporte(@RequestParam(defaultValue = "3") Integer meses) {
        return finanzaServicio.reporte(meses);
    }

    // Todos o los de un mes: /movimientos?anio=2026&mes=10
    @GetMapping("/movimientos")
    public List<Movimiento> listarMovimientos(@RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes) {
        return finanzaServicio.listarMovimientos(anio, mes);
    }

    @PostMapping("/movimientos")
    public Movimiento crearMovimiento(@Valid @RequestBody Movimiento movimiento) {
        return finanzaServicio.crearMovimiento(movimiento);
    }

    @PutMapping("/movimientos/{id}")
    public Movimiento editarMovimiento(@PathVariable Integer id, @Valid @RequestBody Movimiento movimiento) {
        return finanzaServicio.editarMovimiento(id, movimiento);
    }

    @DeleteMapping("/movimientos/{id}")
    public Map<String, String> eliminarMovimiento(@PathVariable Integer id) {
        finanzaServicio.eliminarMovimiento(id);

        return Map.of("mensaje", "Movimiento eliminado");
    }
}
