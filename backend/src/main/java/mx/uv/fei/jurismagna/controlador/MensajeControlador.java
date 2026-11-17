package mx.uv.fei.jurismagna.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    // Mensaje de texto (JSON)
    @PostMapping("/mensajes")
    public Mensaje enviar(@RequestBody Mensaje mensaje) {
        return mensajeServicio.enviarTexto(mensaje);
    }

    // Audio, imagen o archivo (multipart): campos hilo, remitente, tipo y archivo
    @PostMapping("/mensajes/archivos")
    public Mensaje enviarArchivo(@RequestParam String hilo,
            @RequestParam String remitente,
            @RequestParam String tipo,
            @RequestParam MultipartFile archivo) {
        return mensajeServicio.enviarArchivo(hilo, remitente, tipo, archivo);
    }

    // /mensajes/leidos?hilo=cliente-1&lector=abogado
    @PutMapping("/mensajes/leidos")
    public Map<String, Object> marcarLeidos(@RequestParam(required = false) String hilo,
            @RequestParam(required = false) String lector) {
        return mensajeServicio.marcarLeidos(hilo, lector);
    }

    @DeleteMapping("/mensajes/{id}")
    public Map<String, String> eliminar(@PathVariable Integer id) {
        mensajeServicio.eliminar(id);

        return Map.of("mensaje", "Mensaje eliminado");
    }
}
