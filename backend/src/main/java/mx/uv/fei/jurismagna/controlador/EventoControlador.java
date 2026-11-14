package mx.uv.fei.jurismagna.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.uv.fei.jurismagna.modelo.Evento;
import mx.uv.fei.jurismagna.servicio.EventoServicio;

@RestController
public class EventoControlador {

    @Autowired
    EventoServicio eventoServicio;

    // /eventos?alcance=admin  o  /eventos?alcance=abogado-1
    @GetMapping("/eventos")
    public List<Evento> listar(@RequestParam(required = false) String alcance) {
        return eventoServicio.listarPorAlcance(alcance);
    }

    @PostMapping("/eventos")
    public Evento crear(@RequestBody Evento evento) {
        return eventoServicio.crear(evento);
    }
}
