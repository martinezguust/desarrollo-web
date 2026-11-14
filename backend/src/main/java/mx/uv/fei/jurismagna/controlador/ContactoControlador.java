package mx.uv.fei.jurismagna.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import mx.uv.fei.jurismagna.modelo.Contacto;
import mx.uv.fei.jurismagna.servicio.ContactoServicio;

@RestController
public class ContactoControlador {

    @Autowired
    ContactoServicio contactoServicio;

    @GetMapping("/contactos")
    public List<Contacto> listar() {
        return contactoServicio.listar();
    }

    @PostMapping("/contactos")
    public Contacto crear(@RequestBody Contacto contacto) {
        return contactoServicio.crear(contacto);
    }
}
