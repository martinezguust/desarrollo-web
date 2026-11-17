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
import mx.uv.fei.jurismagna.modelo.Caso;
import mx.uv.fei.jurismagna.servicio.CasoServicio;

@RestController
public class CasoControlador {

    @Autowired
    CasoServicio casoServicio;

    // Filtros por query string (todos opcionales)
    @GetMapping("/casos")
    public List<Caso> listar(@RequestParam(required = false) Integer abogadoId,
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) String materia,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String q) {
        return casoServicio.listar(abogadoId, clienteId, materia, estado, q);
    }

    // Archivo histórico: /casos/historico?estado=Ganado&q=vega
    @GetMapping("/casos/historico")
    public List<Caso> historico(@RequestParam(required = false) String estado,
            @RequestParam(required = false) String q) {
        return casoServicio.historico(estado, q);
    }

    @GetMapping("/casos/{id}")
    public Caso buscar(@PathVariable Integer id) {
        return casoServicio.buscar(id);
    }

    @PostMapping("/casos")
    public Caso crear(@Valid @RequestBody Caso caso) {
        return casoServicio.crear(caso);
    }

    @PutMapping("/casos/{id}")
    public Caso editar(@PathVariable Integer id, @Valid @RequestBody Caso caso) {
        return casoServicio.editar(id, caso);
    }

    @DeleteMapping("/casos/{id}")
    public Map<String, String> eliminar(@PathVariable Integer id) {
        casoServicio.eliminar(id);

        return Map.of("mensaje", "Caso eliminado");
    }
}
