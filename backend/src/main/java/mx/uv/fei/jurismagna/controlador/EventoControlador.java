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
import mx.uv.fei.jurismagna.modelo.Evento;
import mx.uv.fei.jurismagna.servicio.EventoServicio;

@RestController
public class EventoControlador {

    @Autowired
    EventoServicio eventoServicio;

    // /eventos?alcance=abogado-1  o, solo un mes,  /eventos?alcance=admin&anio=2026&mes=11
    @GetMapping("/eventos")
    public List<Evento> listar(@RequestParam(required = false) String alcance,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes) {
        return eventoServicio.listar(alcance, anio, mes);
    }

    @PostMapping("/eventos")
    public Evento crear(@Valid @RequestBody Evento evento) {
        return eventoServicio.crear(evento);
    }

    @PutMapping("/eventos/{id}")
    public Evento editar(@PathVariable Integer id, @Valid @RequestBody Evento evento) {
        return eventoServicio.editar(id, evento);
    }

    @DeleteMapping("/eventos/{id}")
    public Map<String, String> eliminar(@PathVariable Integer id) {
        eventoServicio.eliminar(id);

        return Map.of("mensaje", "Evento eliminado");
    }
}
