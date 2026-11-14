package mx.uv.fei.jurismagna.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.uv.fei.jurismagna.modelo.Movimiento;

public interface MovimientoRepositorio extends JpaRepository<Movimiento, Integer> {

    List<Movimiento> findAllByOrderByFechaAscIdAsc();
}
