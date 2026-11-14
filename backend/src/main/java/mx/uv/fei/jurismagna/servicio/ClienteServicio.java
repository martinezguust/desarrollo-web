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
import mx.uv.fei.jurismagna.repositorio.ClienteRepositorio;
import mx.uv.fei.jurismagna.repositorio.UsuarioRepositorio;

@Service
public class ClienteServicio {

    @Autowired
    ClienteRepositorio clienteRepositorio;

    @Autowired
    AbogadoRepositorio abogadoRepositorio;

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    BCryptPasswordEncoder encriptador = new BCryptPasswordEncoder();

    public List<Cliente> listar() {
        return clienteRepositorio.findAll();
    }

    public Cliente buscar(Integer id) {
        return clienteRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el cliente " + id));
    }

    // Crea el usuario (rol CLIENTE) y el cliente juntos: si algo falla no se guarda nada
    @Transactional
    public Cliente crear(Cliente cliente) {
        if (estaVacio(cliente.getNombre()) || cliente.getAbogadoId() == null
                || estaVacio(cliente.getUsername()) || estaVacio(cliente.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan el nombre, el abogado, el usuario o la contraseña");
        }

        if (cliente.getAvance() == null) {
            cliente.setAvance(0);
        }

        if (cliente.getHonorarios() == null) {
            cliente.setHonorarios(BigDecimal.ZERO);
        }

        if (cliente.getAvance() < 0 || cliente.getAvance() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El avance debe estar entre 0 y 100");
        }

        if (cliente.getHonorarios().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los honorarios no pueden ser negativos");
        }

        Abogado abogado = abogadoRepositorio.findById(cliente.getAbogadoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el abogado " + cliente.getAbogadoId()));

        if (usuarioRepositorio.existsByUsername(cliente.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario " + cliente.getUsername() + " ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(cliente.getUsername());
        usuario.setPasswordHash(encriptador.encode(cliente.getPassword()));
        usuario.setRol("CLIENTE");
        usuario.setActivo(true);
        usuarioRepositorio.save(usuario);

        cliente.setId(null);
        cliente.setUsuario(usuario);
        cliente.setAbogado(abogado);

        if (estaVacio(cliente.getEstado())) {
            cliente.setEstado("Activo");
        }

        return clienteRepositorio.save(cliente);
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
