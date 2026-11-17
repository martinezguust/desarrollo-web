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
import mx.uv.fei.jurismagna.modelo.Notificacion;
import mx.uv.fei.jurismagna.servicio.NotificacionServicio;

@RestController
public class NotificacionControlador {

    @Autowired
    NotificacionServicio notificacionServicio;

    // /notificaciones?usuarioId=1
    @GetMapping("/notificaciones")
    public List<Notificacion> listar(@RequestParam(required = false) Integer usuarioId) {
        return notificacionServicio.listarPorUsuario(usuarioId);
    }

    @PostMapping("/notificaciones")
    public Notificacion crear(@Valid @RequestBody Notificacion notificacion) {
        return notificacionServicio.crear(notificacion);
    }

    // /notificaciones/leidas?usuarioId=1
    @PutMapping("/notificaciones/leidas")
    public Map<String, Object> marcarLeidas(@RequestParam(required = false) Integer usuarioId) {
        return notificacionServicio.marcarLeidas(usuarioId);
    }

    @DeleteMapping("/notificaciones/{id}")
    public Map<String, String> eliminar(@PathVariable Integer id) {
        notificacionServicio.eliminar(id);

        return Map.of("mensaje", "Notificación eliminada");
    }
}
