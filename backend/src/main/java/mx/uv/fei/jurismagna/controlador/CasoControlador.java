package mx.uv.fei.jurismagna.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import mx.uv.fei.jurismagna.modelo.Caso;
import mx.uv.fei.jurismagna.servicio.CasoServicio;

@RestController
public class CasoControlador {

    @Autowired
    CasoServicio casoServicio;

    @GetMapping("/casos")
    public List<Caso> listar() {
        return casoServicio.listar();
    }

    @GetMapping("/casos/{id}")
    public Caso buscar(@PathVariable Integer id) {
        return casoServicio.buscar(id);
    }

    @PostMapping("/casos")
    public Caso crear(@RequestBody Caso caso) {
        return casoServicio.crear(caso);
    }
}
