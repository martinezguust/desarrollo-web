package mx.uv.fei.jurismagna.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mx.uv.fei.jurismagna.modelo.Contacto;

public interface ContactoRepositorio extends JpaRepository<Contacto, Integer> {

    // Filtros opcionales: si un parámetro llega en null, esa condición no se aplica
    @Query("SELECT c FROM Contacto c "
            + "WHERE (:propietarioTipo IS NULL OR c.propietarioTipo = :propietarioTipo) "
            + "AND (:propietarioId IS NULL OR c.propietarioId = :propietarioId) "
            + "ORDER BY c.nombre")
    List<Contacto> buscar(@Param("propietarioTipo") String propietarioTipo, @Param("propietarioId") Integer propietarioId);
}
