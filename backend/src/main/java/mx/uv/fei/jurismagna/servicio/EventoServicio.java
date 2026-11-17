package mx.uv.fei.jurismagna.servicio;

import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import mx.uv.fei.jurismagna.modelo.Evento;
import mx.uv.fei.jurismagna.repositorio.EventoRepositorio;
import mx.uv.fei.jurismagna.repositorio.UsuarioRepositorio;

@Service
public class EventoServicio {

    // Los 6 colores del selector
    static final List<String> COLORES = List.of("#D4AF37", "#27AE60", "#2980B9", "#C0392B", "#8E44AD", "#E67E22");

    @Autowired
    EventoRepositorio eventoRepositorio;

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    // Cada agenda solo ve sus eventos; con anio y mes, solo los de ese mes
    public List<Evento> listar(String alcance, Integer anio, Integer mes) {
        if (alcance == null || alcance.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el alcance");
        }

        if (anio == null && mes == null) {
            return eventoRepositorio.findByAlcanceOrderByFechaAscHoraAsc(alcance);
        }

        if (anio == null || mes == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indica anio y mes juntos");
        }

        if (mes < 1 || mes > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El mes debe estar entre 1 y 12");
        }

        YearMonth periodo = YearMonth.of(anio, mes);

        return eventoRepositorio.findByAlcanceAndFechaBetweenOrderByFechaAscHoraAsc(alcance, periodo.atDay(1), periodo.atEndOfMonth());
    }

    public Evento crear(Evento evento) {
        if (evento.getUsuarioId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el usuarioId");
        }

        validar(evento);

        evento.setId(null);
        evento.setUsuario(usuarioRepositorio.findById(evento.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el usuario " + evento.getUsuarioId())));

        return eventoRepositorio.save(evento);
    }

    // Mismo cuerpo que el POST; si no llega usuarioId se conserva el que lo creó
    public Evento editar(Integer id, Evento datos) {
        Evento evento = buscar(id);
        validar(datos);

        evento.setTitulo(datos.getTitulo());
        evento.setFecha(datos.getFecha());
        evento.setHora(datos.getHora());
        evento.setLugar(datos.getLugar());
        evento.setDescripcion(datos.getDescripcion());
        evento.setColor(datos.getColor());
        evento.setAlcance(datos.getAlcance());

        if (datos.getUsuarioId() != null) {
            evento.setUsuario(usuarioRepositorio.findById(datos.getUsuarioId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el usuario " + datos.getUsuarioId())));
        }

        return eventoRepositorio.save(evento);
    }

    public void eliminar(Integer id) {
        eventoRepositorio.delete(buscar(id));
    }

    private Evento buscar(Integer id) {
        return eventoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el evento " + id));
    }

    // La fecha y el alcance vacíos los revisa @Valid; la hora HH:MM la revisa el formato del JSON
    private void validar(Evento evento) {
        if (!evento.getAlcance().equals("admin") && !evento.getAlcance().matches("abogado-\\d+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El alcance debe ser admin o abogado-{id}");
        }

        if (evento.getColor() == null || evento.getColor().isBlank()) {
            evento.setColor("#D4AF37");
        }

        if (!COLORES.contains(evento.getColor())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El color " + evento.getColor() + " no es válido");
        }

        // Sin título se guarda como "Evento"
        if (evento.getTitulo() == null || evento.getTitulo().isBlank()) {
            evento.setTitulo("Evento");
        }
    }
}
