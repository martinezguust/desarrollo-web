package mx.uv.fei.jurismagna.servicio;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import mx.uv.fei.jurismagna.modelo.Abogado;
import mx.uv.fei.jurismagna.modelo.Usuario;
import mx.uv.fei.jurismagna.repositorio.AbogadoRepositorio;
import mx.uv.fei.jurismagna.repositorio.UsuarioRepositorio;

@Service
public class SesionServicio {

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    @Autowired
    AbogadoRepositorio abogadoRepositorio;

    BCryptPasswordEncoder encriptador = new BCryptPasswordEncoder();

    public Map<String, Object> iniciarSesion(String username, String password, String rol) {
        if (username == null || password == null || rol == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan el usuario, la contraseña o el rol");
        }

        Usuario usuario = usuarioRepositorio.findByUsername(username);

        // Mismo mensaje para todos los casos, para no dar pistas de qué falló
        boolean valido = usuario != null
                && usuario.getActivo()
                && usuario.getRol().equals(rol)
                && encriptador.matches(password, usuario.getPasswordHash());

        if (!valido) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario, contraseña o rol incorrectos");
        }

        // perfilId es el id en la tabla abogado o cliente; el admin no tiene
        String nombre = usuario.getNombre();
        Integer perfilId = null;

        if (rol.equals("ABOGADO")) {
            Abogado abogado = abogadoRepositorio.findByUsuario_Id(usuario.getId());
            nombre = abogado.getNombre();
            perfilId = abogado.getId();
        }

        // Pendiente Fase 2.Danna: nombre y perfilId del CLIENTE salen de la tabla cliente
        if (nombre == null) {
            nombre = usuario.getUsername();
        }

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("usuarioId", usuario.getId());
        respuesta.put("rol", usuario.getRol());
        respuesta.put("nombre", nombre);
        respuesta.put("perfilId", perfilId);

        return respuesta;
    }
}
