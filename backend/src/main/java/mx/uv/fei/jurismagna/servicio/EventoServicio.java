package mx.uv.fei.jurismagna.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import mx.uv.fei.jurismagna.modelo.Evento;
import mx.uv.fei.jurismagna.modelo.Usuario;
import mx.uv.fei.jurismagna.repositorio.EventoRepositorio;
import mx.uv.fei.jurismagna.repositorio.UsuarioRepositorio;

@Service
public class EventoServicio {

    // Los 6 colores del selector original
    static final List<String> COLORES = List.of("#D4AF37", "#27AE60", "#2980B9", "#C0392B", "#8E44AD", "#E67E22");

    @Autowired
    EventoRepositorio eventoRepositorio;

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    // Cada agenda solo ve sus eventos
    public List<Evento> listarPorAlcance(String alcance) {
        if (alcance == null || alcance.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el alcance");
        }

        return eventoRepositorio.findByAlcanceOrderByFechaAscHoraAsc(alcance);
    }

    public Evento crear(Evento evento) {
        if (evento.getFecha() == null || evento.getAlcance() == null || evento.getUsuarioId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan la fecha, el alcance o el usuarioId");
        }

        if (!evento.getAlcance().equals("admin") && !evento.getAlcance().matches("abogado-\\d+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El alcance debe ser admin o abogado-{id}");
        }

        if (evento.getColor() == null || evento.getColor().isBlank()) {
            evento.setColor("#D4AF37");
        }

        if (!COLORES.contains(evento.getColor())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El color " + evento.getColor() + " no es válido");
        }

        // Sin título se guarda como "Evento", como en el diseño original
        if (evento.getTitulo() == null || evento.getTitulo().isBlank()) {
            evento.setTitulo("Evento");
        }

        Usuario usuario = usuarioRepositorio.findById(evento.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el usuario " + evento.getUsuarioId()));

        evento.setId(null);
        evento.setUsuario(usuario);

        return eventoRepositorio.save(evento);
    }
}
