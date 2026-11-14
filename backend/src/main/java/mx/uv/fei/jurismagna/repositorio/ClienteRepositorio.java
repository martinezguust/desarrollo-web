package mx.uv.fei.jurismagna.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.uv.fei.jurismagna.modelo.Cliente;

public interface ClienteRepositorio extends JpaRepository<Cliente, Integer> {
}
