package mx.uv.fei.jurismagna.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import mx.uv.fei.jurismagna.modelo.Abogado;
import mx.uv.fei.jurismagna.servicio.AbogadoServicio;

@RestController
public class AbogadoControlador {

    @Autowired
    AbogadoServicio abogadoServicio;

    @GetMapping("/abogados")
    public List<Abogado> listar() {
        return abogadoServicio.listar();
    }

    @GetMapping("/abogados/{id}")
    public Abogado buscar(@PathVariable Integer id) {
        return abogadoServicio.buscar(id);
    }

    @PostMapping("/abogados")
    public Abogado crear(@RequestBody Abogado abogado) {
        return abogadoServicio.crear(abogado);
    }
}
