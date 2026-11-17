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
import mx.uv.fei.jurismagna.modelo.Cliente;
import mx.uv.fei.jurismagna.servicio.ClienteServicio;

@RestController
public class ClienteControlador {

    @Autowired
    ClienteServicio clienteServicio;

    // Filtros por query string (opcionales): /clientes?abogadoId=1&q=vega
    @GetMapping("/clientes")
    public List<Cliente> listar(@RequestParam(required = false) Integer abogadoId,
            @RequestParam(required = false) String q) {
        return clienteServicio.listar(abogadoId, q);
    }

    @GetMapping("/clientes/{id}")
    public Cliente buscar(@PathVariable Integer id) {
        return clienteServicio.buscar(id);
    }

    @PostMapping("/clientes")
    public Cliente crear(@Valid @RequestBody Cliente cliente) {
        return clienteServicio.crear(cliente);
    }

    @PutMapping("/clientes/{id}")
    public Cliente editar(@PathVariable Integer id, @Valid @RequestBody Cliente cliente) {
        return clienteServicio.editar(id, cliente);
    }

    @DeleteMapping("/clientes/{id}")
    public Map<String, String> eliminar(@PathVariable Integer id) {
        clienteServicio.eliminar(id);

        return Map.of("mensaje", "Cliente eliminado");
    }
}
