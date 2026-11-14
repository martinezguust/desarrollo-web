package mx.uv.fei.jurismagna.servicio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import mx.uv.fei.jurismagna.modelo.Abogado;
import mx.uv.fei.jurismagna.modelo.Caso;
import mx.uv.fei.jurismagna.modelo.Cliente;
import mx.uv.fei.jurismagna.repositorio.AbogadoRepositorio;
import mx.uv.fei.jurismagna.repositorio.CasoRepositorio;
import mx.uv.fei.jurismagna.repositorio.ClienteRepositorio;

@Service
public class CasoServicio {

    // Las 18 materias del formulario original
    static final List<String> MATERIAS = List.of(
            "Civil", "Penal", "Mercantil", "Familiar", "Laboral", "Administrativo",
            "Agrario", "Constitucional", "Fiscal / Tributario", "Ambiental", "Internacional",
            "Corporativo", "Inmobiliario", "Migratorio", "Seguridad Social",
            "Propiedad Intelectual", "Concursal / Quiebras", "Electoral");

    static final List<String> ESTADOS = List.of("Activo", "EnProceso", "Ganado", "Perdido", "Cancelado");

    // Estados que cierran el caso y lo mandan al archivo histórico
    static final List<String> ESTADOS_CERRADOS = List.of("Ganado", "Perdido", "Cancelado");

    @Autowired
    CasoRepositorio casoRepositorio;

    @Autowired
    ClienteRepositorio clienteRepositorio;

    @Autowired
    AbogadoRepositorio abogadoRepositorio;

    public List<Caso> listar() {
        return casoRepositorio.findAll();
    }

    public Caso buscar(Integer id) {
        return casoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el caso " + id));
    }

    public Caso crear(Caso caso) {
        if (caso.getClienteId() == null || caso.getAbogadoId() == null || caso.getMateria() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan el cliente, el abogado o la materia");
        }

        if (!MATERIAS.contains(caso.getMateria())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La materia " + caso.getMateria() + " no es válida");
        }

        if (caso.getEstado() == null || caso.getEstado().isBlank()) {
            caso.setEstado("Activo");
        }

        if (!ESTADOS.contains(caso.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado " + caso.getEstado() + " no es válido");
        }

        Cliente cliente = clienteRepositorio.findById(caso.getClienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el cliente " + caso.getClienteId()));

        Abogado abogado = abogadoRepositorio.findById(caso.getAbogadoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el abogado " + caso.getAbogadoId()));

        if (!cliente.getAbogado().getId().equals(abogado.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cliente no pertenece a ese abogado");
        }

        caso.setId(null);
        caso.setCliente(cliente);
        caso.setAbogado(abogado);
        caso.setNumeroExpediente(siguienteExpediente());
        caso.setFechaInicio(LocalDate.now());

        if (ESTADOS_CERRADOS.contains(caso.getEstado()) && caso.getFechaFin() == null) {
            caso.setFechaFin(LocalDate.now());
        }

        return casoRepositorio.save(caso);
    }

    // EXP-0005 -> EXP-0006; si no hay casos empieza en EXP-0001
    private String siguienteExpediente() {
        Caso ultimo = casoRepositorio.findTopByOrderByNumeroExpedienteDesc();
        int numero = 1;

        if (ultimo != null) {
            numero = Integer.parseInt(ultimo.getNumeroExpediente().substring(4)) + 1;
        }

        return String.format("EXP-%04d", numero);
    }
}
