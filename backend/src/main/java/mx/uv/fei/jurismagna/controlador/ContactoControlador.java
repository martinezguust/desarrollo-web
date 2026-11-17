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
import mx.uv.fei.jurismagna.modelo.Contacto;
import mx.uv.fei.jurismagna.servicio.ContactoServicio;

@RestController
public class ContactoControlador {

    @Autowired
    ContactoServicio contactoServicio;

    // Filtros opcionales: /contactos?propietarioTipo=ADMIN&propietarioId=1
    @GetMapping("/contactos")
    public List<Contacto> listar(@RequestParam(required = false) String propietarioTipo,
            @RequestParam(required = false) Integer propietarioId) {
        return contactoServicio.listar(propietarioTipo, propietarioId);
    }

    @PostMapping("/contactos")
    public Contacto crear(@Valid @RequestBody Contacto contacto) {
        return contactoServicio.crear(contacto);
    }

    @PutMapping("/contactos/{id}")
    public Contacto editar(@PathVariable Integer id, @Valid @RequestBody Contacto contacto) {
        return contactoServicio.editar(id, contacto);
    }

    @DeleteMapping("/contactos/{id}")
    public Map<String, String> eliminar(@PathVariable Integer id) {
        contactoServicio.eliminar(id);

        return Map.of("mensaje", "Contacto eliminado");
    }
}
