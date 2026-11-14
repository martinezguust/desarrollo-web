package mx.uv.fei.jurismagna.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public Notificacion crear(@RequestBody Notificacion notificacion) {
        return notificacionServicio.crear(notificacion);
    }
}
