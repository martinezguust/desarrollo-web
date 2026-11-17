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

    // Las 18 materias del formulario
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

    // Filtros opcionales: /casos?abogadoId=1&clienteId=2&materia=Civil&estado=Activo&q=EXP-0001
    public List<Caso> listar(Integer abogadoId, Integer clienteId, String materia, String estado, String q) {
        return casoRepositorio.buscar(abogadoId, clienteId, textoONull(materia), textoONull(estado), textoONull(q));
    }

    // Archivo histórico: estado vacío = los tres estados cerrados
    public List<Caso> historico(String estado, String q) {
        List<String> estados = ESTADOS_CERRADOS;

        if (textoONull(estado) != null) {
            if (!ESTADOS_CERRADOS.contains(estado)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "En el histórico el estado debe ser Ganado, Perdido o Cancelado");
            }

            estados = List.of(estado);
        }

        return casoRepositorio.historico(estados, textoONull(q));
    }

    public Caso buscar(Integer id) {
        return casoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el caso " + id));
    }

    public Caso crear(Caso caso) {
        caso.setId(null);
        caso.setNumeroExpediente(siguienteExpediente());
        caso.setFechaInicio(LocalDate.now());

        aplicarDatos(caso, caso);

        return casoRepositorio.save(caso);
    }

    // Mismo cuerpo que el POST; el número de expediente y la fecha de inicio no cambian
    public Caso editar(Integer id, Caso datos) {
        Caso caso = buscar(id);
        aplicarDatos(caso, datos);

        return casoRepositorio.save(caso);
    }

    // Al borrar el caso, la base de datos borra sus carpetas y ligas (ON DELETE CASCADE)
    public void eliminar(Integer id) {
        casoRepositorio.delete(buscar(id));
    }

    // Valida los datos y los copia al caso. La materia vacía la revisa @Valid.
    private void aplicarDatos(Caso caso, Caso datos) {
        if (datos.getClienteId() == null || datos.getAbogadoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan el cliente o el abogado");
        }

        if (!MATERIAS.contains(datos.getMateria())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La materia " + datos.getMateria() + " no es válida");
        }

        String estado = datos.getEstado();

        if (estado == null || estado.isBlank()) {
            estado = "Activo";
        }

        if (!ESTADOS.contains(estado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado " + estado + " no es válido");
        }

        Cliente cliente = clienteRepositorio.findById(datos.getClienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el cliente " + datos.getClienteId()));

        Abogado abogado = abogadoRepositorio.findById(datos.getAbogadoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el abogado " + datos.getAbogadoId()));

        if (!cliente.getAbogado().getId().equals(abogado.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cliente no pertenece a ese abogado");
        }

        caso.setCliente(cliente);
        caso.setAbogado(abogado);
        caso.setMateria(datos.getMateria());
        caso.setDriveUrl(datos.getDriveUrl());

        // Fecha de fin: al cerrar se llena (si no venía, con la de hoy); al reabrir se quita
        if (ESTADOS_CERRADOS.contains(estado)) {
            if (datos.getFechaFin() != null) {
                caso.setFechaFin(datos.getFechaFin());
            } else if (caso.getFechaFin() == null) {
                caso.setFechaFin(LocalDate.now());
            }
        } else {
            caso.setFechaFin(null);
        }

        caso.setEstado(estado);
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

    private String textoONull(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }
}
