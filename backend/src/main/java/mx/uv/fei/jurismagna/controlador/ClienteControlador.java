package mx.uv.fei.jurismagna.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import mx.uv.fei.jurismagna.modelo.Cliente;
import mx.uv.fei.jurismagna.servicio.ClienteServicio;

@RestController
public class ClienteControlador {

    @Autowired
    ClienteServicio clienteServicio;

    @GetMapping("/clientes")
    public List<Cliente> listar() {
        return clienteServicio.listar();
    }

    @GetMapping("/clientes/{id}")
    public Cliente buscar(@PathVariable Integer id) {
        return clienteServicio.buscar(id);
    }

    @PostMapping("/clientes")
    public Cliente crear(@RequestBody Cliente cliente) {
        return clienteServicio.crear(cliente);
    }
}
