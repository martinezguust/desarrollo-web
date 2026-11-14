package mx.uv.fei.jurismagna.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.uv.fei.jurismagna.modelo.Evento;

public interface EventoRepositorio extends JpaRepository<Evento, Integer> {

    // Eventos de una agenda ordenados por fecha y hora
    List<Evento> findByAlcanceOrderByFechaAscHoraAsc(String alcance);
}
