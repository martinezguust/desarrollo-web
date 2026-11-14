package mx.uv.fei.jurismagna.servicio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import mx.uv.fei.jurismagna.modelo.Notificacion;
import mx.uv.fei.jurismagna.modelo.Usuario;
import mx.uv.fei.jurismagna.repositorio.NotificacionRepositorio;
import mx.uv.fei.jurismagna.repositorio.UsuarioRepositorio;

@Service
public class NotificacionServicio {

    @Autowired
    NotificacionRepositorio notificacionRepositorio;

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    public List<Notificacion> listarPorUsuario(Integer usuarioId) {
        if (usuarioId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el usuarioId");
        }

        return notificacionRepositorio.findByUsuario_IdOrderByFechaHoraDescIdDesc(usuarioId);
    }

    public Notificacion crear(Notificacion notificacion) {
        if (notificacion.getUsuarioId() == null || notificacion.getMensaje() == null || notificacion.getMensaje().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan el usuarioId o el mensaje");
        }

        Usuario usuario = usuarioRepositorio.findById(notificacion.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el usuario " + notificacion.getUsuarioId()));

        return avisar(usuario, notificacion.getMensaje());
    }

    // La usan otros servicios (por ejemplo, al recibir un mensaje)
    public Notificacion avisar(Usuario usuario, String mensaje) {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setMensaje(mensaje);
        notificacion.setLeida(false);
        notificacion.setFechaHora(LocalDateTime.now());

        return notificacionRepositorio.save(notificacion);
    }
}
