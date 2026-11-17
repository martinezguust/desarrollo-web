package mx.uv.fei.jurismagna.servicio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    // El mensaje vacío lo revisa @Valid
    public Notificacion crear(Notificacion notificacion) {
        if (notificacion.getUsuarioId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el usuarioId");
        }

        return avisar(buscarUsuario(notificacion.getUsuarioId()), notificacion.getMensaje());
    }

    // La usan otros servicios (al recibir un mensaje, al crear un cliente)
    public Notificacion avisar(Usuario usuario, String mensaje) {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setMensaje(mensaje);
        notificacion.setLeida(false);
        notificacion.setFechaHora(LocalDateTime.now());

        return notificacionRepositorio.save(notificacion);
    }

    // Marca como leídas todas las notificaciones del usuario (al abrir la campana)
    @Transactional
    public Map<String, Object> marcarLeidas(Integer usuarioId) {
        if (usuarioId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el usuarioId");
        }

        buscarUsuario(usuarioId);

        List<Notificacion> sinLeer = notificacionRepositorio.findByUsuario_IdAndLeidaFalse(usuarioId);

        for (Notificacion notificacion : sinLeer) {
            notificacion.setLeida(true);
        }

        notificacionRepositorio.saveAll(sinLeer);

        return Map.of("mensaje", "Notificaciones marcadas como leídas", "actualizadas", sinLeer.size());
    }

    public void eliminar(Integer id) {
        Notificacion notificacion = notificacionRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la notificación " + id));

        notificacionRepositorio.delete(notificacion);
    }

    private Usuario buscarUsuario(Integer usuarioId) {
        return usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el usuario " + usuarioId));
    }
}
