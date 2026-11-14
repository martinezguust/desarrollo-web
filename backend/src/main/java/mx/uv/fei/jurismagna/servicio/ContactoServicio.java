package mx.uv.fei.jurismagna.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import mx.uv.fei.jurismagna.modelo.Contacto;
import mx.uv.fei.jurismagna.repositorio.ContactoRepositorio;

@Service
public class ContactoServicio {

    @Autowired
    ContactoRepositorio contactoRepositorio;

    public List<Contacto> listar() {
        return contactoRepositorio.findAll();
    }

    public Contacto crear(Contacto contacto) {
        if (contacto.getNombre() == null || contacto.getNombre().isBlank()
                || contacto.getPropietarioTipo() == null || contacto.getPropietarioId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan el nombre o el propietario");
        }

        if (!contacto.getPropietarioTipo().equals("ADMIN") && !contacto.getPropietarioTipo().equals("ABOGADO")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El propietarioTipo debe ser ADMIN o ABOGADO");
        }

        contacto.setId(null);

        return contactoRepositorio.save(contacto);
    }
}
