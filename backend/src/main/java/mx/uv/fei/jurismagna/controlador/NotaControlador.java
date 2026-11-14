package mx.uv.fei.jurismagna.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public Nota crear(@RequestBody Nota nota) {
        return notaServicio.crear(nota);
    }
}
