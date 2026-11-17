package mx.uv.fei.jurismagna.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import mx.uv.fei.jurismagna.modelo.Usuario;
import mx.uv.fei.jurismagna.repositorio.UsuarioRepositorio;

// Perfil de usuario (lo usa el administrador), contraseñas y fotos
@Service
public class UsuarioServicio {

    static final List<String> EXTENSIONES_FOTO = List.of("jpg", "jpeg", "png", "webp");

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    @Autowired
    ArchivoServicio archivoServicio;

    BCryptPasswordEncoder encriptador = new BCryptPasswordEncoder();

    public Usuario buscar(Integer id) {
        return usuarioRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el usuario " + id));
    }

    // Solo cambia los datos del perfil; el usuario, el rol y la contraseña no se tocan aquí
    public Usuario editarPerfil(Integer id, Usuario datos) {
        Usuario usuario = buscar(id);
        usuario.setNombre(datos.getNombre());
        usuario.setEmail(datos.getEmail());
        usuario.setTelefono(datos.getTelefono());

        return usuarioRepositorio.save(usuario);
    }

    public void cambiarPassword(Integer id, String password) {
        Usuario usuario = buscar(id);

        if (password == null || password.length() < 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 4 caracteres");
        }

        usuario.setPasswordHash(encriptador.encode(password));
        usuarioRepositorio.save(usuario);
    }

    public String subirFoto(Integer id, MultipartFile foto) {
        Usuario usuario = buscar(id);
        String fotoUrl = archivoServicio.guardar(foto, "usuarios", String.valueOf(id), EXTENSIONES_FOTO);

        usuario.setFotoUrl(fotoUrl);
        usuarioRepositorio.save(usuario);

        return fotoUrl;
    }
}
