package mx.uv.fei.jurismagna.repositorio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.uv.fei.jurismagna.modelo.Movimiento;

public interface MovimientoRepositorio extends JpaRepository<Movimiento, Integer> {

    List<Movimiento> findAllByOrderByFechaAscIdAsc();

    // Movimientos entre dos fechas (incluidas)
    List<Movimiento> findByFechaBetweenOrderByFechaAscIdAsc(LocalDate inicio, LocalDate fin);

    // El movimiento más reciente: de aquí sale el "mes actual" de finanzas
    Movimiento findTopByOrderByFechaDesc();
}
