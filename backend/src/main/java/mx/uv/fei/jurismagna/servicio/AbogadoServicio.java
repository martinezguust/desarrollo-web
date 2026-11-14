package mx.uv.fei.jurismagna.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import mx.uv.fei.jurismagna.modelo.Abogado;
import mx.uv.fei.jurismagna.modelo.Usuario;
import mx.uv.fei.jurismagna.repositorio.AbogadoRepositorio;
import mx.uv.fei.jurismagna.repositorio.UsuarioRepositorio;

@Service
public class AbogadoServicio {

    @Autowired
    AbogadoRepositorio abogadoRepositorio;

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    BCryptPasswordEncoder encriptador = new BCryptPasswordEncoder();

    public List<Abogado> listar() {
        return abogadoRepositorio.findAll();
    }

    public Abogado buscar(Integer id) {
        return abogadoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el abogado " + id));
    }

    // Crea el usuario (rol ABOGADO) y el abogado juntos: si algo falla no se guarda nada
    @Transactional
    public Abogado crear(Abogado abogado) {
        if (estaVacio(abogado.getNombre()) || estaVacio(abogado.getUsername()) || estaVacio(abogado.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan el nombre, el usuario o la contraseña");
        }

        if (usuarioRepositorio.existsByUsername(abogado.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario " + abogado.getUsername() + " ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(abogado.getUsername());
        usuario.setPasswordHash(encriptador.encode(abogado.getPassword()));
        usuario.setRol("ABOGADO");
        usuario.setActivo(true);
        usuarioRepositorio.save(usuario);

        abogado.setId(null);
        abogado.setUsuario(usuario);

        if (estaVacio(abogado.getEstado())) {
            abogado.setEstado("Activo");
        }

        return abogadoRepositorio.save(abogado);
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
