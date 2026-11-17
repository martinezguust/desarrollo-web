package mx.uv.fei.jurismagna.servicio;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import mx.uv.fei.jurismagna.modelo.Abogado;
import mx.uv.fei.jurismagna.modelo.Cliente;
import mx.uv.fei.jurismagna.modelo.Usuario;
import mx.uv.fei.jurismagna.repositorio.AbogadoRepositorio;
import mx.uv.fei.jurismagna.repositorio.CasoRepositorio;
import mx.uv.fei.jurismagna.repositorio.ClienteRepositorio;
import mx.uv.fei.jurismagna.repositorio.UsuarioRepositorio;

@Service
public class ClienteServicio {

    static final List<String> ESTADOS = List.of("Activo", "Inactivo");

    @Autowired
    ClienteRepositorio clienteRepositorio;

    @Autowired
    AbogadoRepositorio abogadoRepositorio;

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    @Autowired
    CasoRepositorio casoRepositorio;

    @Autowired
    NotificacionServicio notificacionServicio;

    BCryptPasswordEncoder encriptador = new BCryptPasswordEncoder();

    // Filtros opcionales: /clientes?abogadoId=1&q=vega
    public List<Cliente> listar(Integer abogadoId, String q) {
        return clienteRepositorio.buscar(abogadoId, textoONull(q));
    }

    public Cliente buscar(Integer id) {
        return clienteRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el cliente " + id));
    }

    // Crea el usuario (rol CLIENTE) y el cliente juntos: si algo falla no se guarda nada.
    // El nombre, el avance y los honorarios los revisa @Valid.
    @Transactional
    public Cliente crear(Cliente cliente) {
        if (cliente.getAbogadoId() == null || estaVacio(cliente.getUsername()) || estaVacio(cliente.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan el abogado, el usuario o la contraseña");
        }

        completarValores(cliente);
        Abogado abogado = buscarAbogado(cliente.getAbogadoId());

        if (usuarioRepositorio.existsByUsername(cliente.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario " + cliente.getUsername() + " ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(cliente.getUsername());
        usuario.setPasswordHash(encriptador.encode(cliente.getPassword()));
        usuario.setRol("CLIENTE");
        usuario.setActivo(cliente.getEstado().equals("Activo"));
        usuarioRepositorio.save(usuario);

        cliente.setId(null);
        cliente.setUsuario(usuario);
        cliente.setAbogado(abogado);

        Cliente guardado = clienteRepositorio.save(cliente);

        // Aviso en la campana del administrador
        for (Usuario admin : usuarioRepositorio.findByRol("ADMIN")) {
            notificacionServicio.avisar(admin, abogado.getNombre() + " agregó a " + guardado.getNombre());
        }

        return guardado;
    }

    // Mismo cuerpo que el POST. Si password llega vacío, la contraseña no cambia.
    @Transactional
    public Cliente editar(Integer id, Cliente datos) {
        Cliente cliente = buscar(id);
        Usuario usuario = cliente.getUsuario();

        if (datos.getAbogadoId() != null) {
            cliente.setAbogado(buscarAbogado(datos.getAbogadoId()));
        }

        // Cambio de nombre de usuario: no puede repetirse con otro
        if (!estaVacio(datos.getUsername()) && !datos.getUsername().equals(usuario.getUsername())) {
            if (usuarioRepositorio.existsByUsername(datos.getUsername())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario " + datos.getUsername() + " ya existe");
            }

            usuario.setUsername(datos.getUsername());
        }

        if (!estaVacio(datos.getPassword())) {
            usuario.setPasswordHash(encriptador.encode(datos.getPassword()));
        }

        completarValores(datos);

        cliente.setNombre(datos.getNombre());
        cliente.setEdad(datos.getEdad());
        cliente.setEstudios(datos.getEstudios());
        cliente.setIne(datos.getIne());
        cliente.setEmail(datos.getEmail());
        cliente.setTelefono(datos.getTelefono());
        cliente.setDescripcion(datos.getDescripcion());
        cliente.setAvance(datos.getAvance());
        cliente.setHonorarios(datos.getHonorarios());
        cliente.setDriveUrl(datos.getDriveUrl());
        cliente.setComentario(datos.getComentario());
        cliente.setEstado(datos.getEstado());

        // Un cliente Inactivo ya no puede iniciar sesión
        usuario.setActivo(datos.getEstado().equals("Activo"));
        usuarioRepositorio.save(usuario);

        return clienteRepositorio.save(cliente);
    }

    // Regla de Danna: no se elimina un cliente que todavía tiene casos (409)
    @Transactional
    public void eliminar(Integer id) {
        Cliente cliente = buscar(id);

        if (casoRepositorio.existsByCliente_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: el cliente todavía tiene casos");
        }

        Usuario usuario = cliente.getUsuario();
        clienteRepositorio.delete(cliente);
        usuarioRepositorio.delete(usuario);
    }

    // Valores por omisión (avance 0, honorarios 0, estado Activo) y estado válido
    private void completarValores(Cliente cliente) {
        if (cliente.getAvance() == null) {
            cliente.setAvance(0);
        }

        if (cliente.getHonorarios() == null) {
            cliente.setHonorarios(BigDecimal.ZERO);
        }

        if (estaVacio(cliente.getEstado())) {
            cliente.setEstado("Activo");
        }

        if (!ESTADOS.contains(cliente.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado debe ser Activo o Inactivo");
        }
    }

    private Abogado buscarAbogado(Integer abogadoId) {
        return abogadoRepositorio.findById(abogadoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el abogado " + abogadoId));
    }

    // "" o "   " cuentan como "sin filtro"
    private String textoONull(String texto) {
        if (estaVacio(texto)) {
            return null;
        }

        return texto.trim();
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
