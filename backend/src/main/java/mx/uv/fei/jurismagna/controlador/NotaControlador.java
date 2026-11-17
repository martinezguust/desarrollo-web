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

import jakarta.validation.Valid;
import mx.uv.fei.jurismagna.modelo.Nota;
import mx.uv.fei.jurismagna.servicio.NotaServicio;

@RestController
public class NotaControlador {

    @Autowired
    NotaServicio notaServicio;

    // El abogado llega por query string: /notas?abogadoId=1
    @GetMapping("/notas")
    public List<Nota> listar(@RequestParam(required = false) Integer abogadoId) {
        return notaServicio.listarPorAbogado(abogadoId);
    }

    @PostMapping("/notas")
    public Nota crear(@Valid @RequestBody Nota nota) {
        return notaServicio.crear(nota);
    }

    // Editar título y contenido o marcarla como completada
    @PutMapping("/notas/{id}")
    public Nota editar(@PathVariable Integer id, @Valid @RequestBody Nota nota) {
        return notaServicio.editar(id, nota);
    }

    @DeleteMapping("/notas/{id}")
    public Map<String, String> eliminar(@PathVariable Integer id) {
        notaServicio.eliminar(id);

        return Map.of("mensaje", "Nota eliminada");
    }
}
