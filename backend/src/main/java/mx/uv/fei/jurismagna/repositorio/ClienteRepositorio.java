package mx.uv.fei.jurismagna.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.uv.fei.jurismagna.modelo.Cliente;

public interface ClienteRepositorio extends JpaRepository<Cliente, Integer> {

    // Cliente de un usuario (se usa en el login). El "_" indica usuario.id
    Cliente findByUsuario_Id(Integer usuarioId);

    // Clientes de un abogado (indicadores del abogado)
    long countByAbogado_Id(Integer abogadoId);
}
