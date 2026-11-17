package mx.uv.fei.jurismagna.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.uv.fei.jurismagna.modelo.Caso;

public interface CasoRepositorio extends JpaRepository<Caso, Integer> {

    // El expediente más alto (EXP-0005) para calcular el siguiente
    Caso findTopByOrderByNumeroExpedienteDesc();

    // Casos de un abogado (indicadores y dashboard del abogado)
    List<Caso> findByAbogado_Id(Integer abogadoId);
}
