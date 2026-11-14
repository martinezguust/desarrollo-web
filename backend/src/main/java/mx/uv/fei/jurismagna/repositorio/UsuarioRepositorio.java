package mx.uv.fei.jurismagna.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.uv.fei.jurismagna.modelo.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {

    // Spring arma la consulta a partir del nombre del método
    Usuario findByUsername(String username);

    boolean existsByUsername(String username);
}
