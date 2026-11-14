package mx.uv.fei.jurismagna.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.uv.fei.jurismagna.modelo.Nota;

public interface NotaRepositorio extends JpaRepository<Nota, Integer> {

    // Notas de un abogado, de la más reciente a la más antigua
    List<Nota> findByAbogado_IdOrderByFechaDescIdDesc(Integer abogadoId);
}
