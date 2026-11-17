package mx.uv.fei.jurismagna.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.uv.fei.jurismagna.modelo.Mensaje;

public interface MensajeRepositorio extends JpaRepository<Mensaje, Integer> {

    // Mensajes de una conversación en orden cronológico
    List<Mensaje> findByHiloOrderByFechaHoraAscIdAsc(String hilo);

    // Mensajes sin leer del hilo que NO envió el lector (los que él recibió)
    List<Mensaje> findByHiloAndRemitenteNotAndLeidoFalse(String hilo, String lector);
}
