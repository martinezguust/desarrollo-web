package mx.uv.fei.jurismagna.controlador;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import mx.uv.fei.jurismagna.servicio.SesionServicio;

@RestController
public class SesionControlador {

    @Autowired
    SesionServicio sesionServicio;

    // Login: {username, password, rol} -> {usuarioId, rol, nombre, perfilId}
    @PostMapping("/sesiones")
    public Map<String, Object> iniciarSesion(@RequestBody Map<String, String> datos) {
        return sesionServicio.iniciarSesion(datos.get("username"), datos.get("password"), datos.get("rol"));
    }
}
