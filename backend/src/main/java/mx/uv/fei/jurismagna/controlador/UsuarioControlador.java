package mx.uv.fei.jurismagna.controlador;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import mx.uv.fei.jurismagna.modelo.Usuario;
import mx.uv.fei.jurismagna.servicio.UsuarioServicio;

// Perfil del usuario (lo usa el administrador para su perfil)
@RestController
public class UsuarioControlador {

    @Autowired
    UsuarioServicio usuarioServicio;

    @GetMapping("/usuarios/{id}")
    public Usuario buscar(@PathVariable Integer id) {
        return usuarioServicio.buscar(id);
    }

    // Cuerpo: { "nombre": "...", "email": "...", "telefono": "..." }
    @PutMapping("/usuarios/{id}")
    public Usuario editarPerfil(@PathVariable Integer id, @RequestBody Usuario usuario) {
        return usuarioServicio.editarPerfil(id, usuario);
    }

    @PutMapping("/usuarios/{id}/password")
    public Map<String, String> cambiarPassword(@PathVariable Integer id, @RequestBody Map<String, String> datos) {
        usuarioServicio.cambiarPassword(id, datos.get("password"));

        return Map.of("mensaje", "Contraseña actualizada");
    }

    @PostMapping("/usuarios/{id}/foto")
    public Map<String, String> subirFoto(@PathVariable Integer id, @RequestParam("foto") MultipartFile foto) {
        return Map.of("fotoUrl", usuarioServicio.subirFoto(id, foto));
    }
}
