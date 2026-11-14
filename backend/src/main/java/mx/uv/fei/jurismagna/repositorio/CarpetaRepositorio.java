package mx.uv.fei.jurismagna.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.uv.fei.jurismagna.modelo.Carpeta;

public interface CarpetaRepositorio extends JpaRepository<Carpeta, Integer> {

    // El "_" indica que se busca por caso.id
    List<Carpeta> findByCaso_IdOrderByIdAsc(Integer casoId);
}
