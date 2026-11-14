package mx.uv.fei.jurismagna.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.uv.fei.jurismagna.modelo.Mensaje;
import mx.uv.fei.jurismagna.servicio.MensajeServicio;

@RestController
public class MensajeControlador {

    @Autowired
    MensajeServicio mensajeServicio;

    // El hilo llega por query string: /mensajes?hilo=cliente-1
    @GetMapping("/mensajes")
    public List<Mensaje> listar(@RequestParam(required = false) String hilo) {
        return mensajeServicio.listarPorHilo(hilo);
    }

    @PostMapping("/mensajes")
    public Mensaje enviar(@RequestBody Mensaje mensaje) {
        return mensajeServicio.enviarTexto(mensaje);
    }
}
