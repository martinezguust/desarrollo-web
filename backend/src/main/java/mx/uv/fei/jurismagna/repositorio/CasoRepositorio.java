package mx.uv.fei.jurismagna.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mx.uv.fei.jurismagna.modelo.Caso;

public interface CasoRepositorio extends JpaRepository<Caso, Integer> {

    // El expediente más alto (EXP-0005) para calcular el siguiente
    Caso findTopByOrderByNumeroExpedienteDesc();

    // Casos de un abogado (indicadores y dashboard del abogado)
    List<Caso> findByAbogado_Id(Integer abogadoId);

    // ¿El cliente tiene casos? (no se puede eliminar si tiene)
    boolean existsByCliente_Id(Integer clienteId);

    // Filtros opcionales: si un parámetro llega en null, esa condición no se aplica.
    // q busca en el número de expediente y en el nombre del cliente.
    @Query("SELECT c FROM Caso c "
            + "WHERE (:abogadoId IS NULL OR c.abogado.id = :abogadoId) "
            + "AND (:clienteId IS NULL OR c.cliente.id = :clienteId) "
            + "AND (:materia IS NULL OR c.materia = :materia) "
            + "AND (:estado IS NULL OR c.estado = :estado) "
            + "AND (:q IS NULL OR LOWER(c.numeroExpediente) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "     OR LOWER(c.cliente.nombre) LIKE LOWER(CONCAT('%', :q, '%'))) "
            + "ORDER BY c.numeroExpediente")
    List<Caso> buscar(@Param("abogadoId") Integer abogadoId, @Param("clienteId") Integer clienteId,
            @Param("materia") String materia, @Param("estado") String estado, @Param("q") String q);

    // Archivo histórico: casos cerrados, del más reciente al más antiguo.
    // q busca en el expediente, el cliente y el abogado.
    @Query("SELECT c FROM Caso c "
            + "WHERE c.estado IN :estados "
            + "AND (:q IS NULL OR LOWER(c.numeroExpediente) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "     OR LOWER(c.cliente.nombre) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "     OR LOWER(c.abogado.nombre) LIKE LOWER(CONCAT('%', :q, '%'))) "
            + "ORDER BY c.fechaFin DESC, c.id DESC")
    List<Caso> historico(@Param("estados") List<String> estados, @Param("q") String q);
}
