package mx.uv.fei.jurismagna.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.uv.fei.jurismagna.modelo.Liga;

public interface LigaRepositorio extends JpaRepository<Liga, Integer> {

    // El "_" indica que se busca por carpeta.id
    List<Liga> findByCarpeta_IdOrderByIdAsc(Integer carpetaId);
}
