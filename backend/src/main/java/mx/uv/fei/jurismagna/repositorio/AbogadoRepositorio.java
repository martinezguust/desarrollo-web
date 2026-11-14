package mx.uv.fei.jurismagna.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.uv.fei.jurismagna.modelo.Abogado;

public interface AbogadoRepositorio extends JpaRepository<Abogado, Integer> {

    // Busca el abogado de un usuario (se usa en el login); el "_" indica usuario.id
    Abogado findByUsuario_Id(Integer usuarioId);
}
