package mx.uv.fei.jurismagna.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.uv.fei.jurismagna.modelo.Notificacion;

public interface NotificacionRepositorio extends JpaRepository<Notificacion, Integer> {

    // Notificaciones de un usuario, de la más reciente a la más antigua.
    // El "_" indica que se busca por usuario.id
    List<Notificacion> findByUsuario_IdOrderByFechaHoraDescIdDesc(Integer usuarioId);
}
