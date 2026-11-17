package mx.uv.fei.jurismagna.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mx.uv.fei.jurismagna.modelo.Cliente;

public interface ClienteRepositorio extends JpaRepository<Cliente, Integer> {

    // Cliente de un usuario (se usa en el login). El "_" indica usuario.id
    Cliente findByUsuario_Id(Integer usuarioId);

    // Clientes de un abogado (indicadores del abogado)
    long countByAbogado_Id(Integer abogadoId);

    // Filtros opcionales: si un parámetro llega en null, esa condición no se aplica.
    // q busca en el nombre sin importar mayúsculas.
    @Query("SELECT c FROM Cliente c "
            + "WHERE (:abogadoId IS NULL OR c.abogado.id = :abogadoId) "
            + "AND (:q IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :q, '%'))) "
            + "ORDER BY c.nombre")
    List<Cliente> buscar(@Param("abogadoId") Integer abogadoId, @Param("q") String q);
}
