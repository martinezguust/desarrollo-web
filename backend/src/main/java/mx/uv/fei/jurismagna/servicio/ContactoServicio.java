package mx.uv.fei.jurismagna.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import mx.uv.fei.jurismagna.modelo.Contacto;
import mx.uv.fei.jurismagna.modelo.Usuario;
import mx.uv.fei.jurismagna.repositorio.ContactoRepositorio;
import mx.uv.fei.jurismagna.repositorio.UsuarioRepositorio;

@Service
public class ContactoServicio {

    static final List<String> TIPOS = List.of("ADMIN", "ABOGADO");

    @Autowired
    ContactoRepositorio contactoRepositorio;

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    // Filtros opcionales: /contactos?propietarioTipo=ADMIN&propietarioId=1
    public List<Contacto> listar(String propietarioTipo, Integer propietarioId) {
        if (propietarioTipo != null && propietarioTipo.isBlank()) {
            propietarioTipo = null;
        }

        if (propietarioTipo != null && !TIPOS.contains(propietarioTipo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El propietarioTipo debe ser ADMIN o ABOGADO");
        }

        return contactoRepositorio.buscar(propietarioTipo, propietarioId);
    }

    // El nombre y el propietario vacíos los revisa @Valid
    public Contacto crear(Contacto contacto) {
        validarPropietario(contacto);
        contacto.setId(null);

        return contactoRepositorio.save(contacto);
    }

    public Contacto editar(Integer id, Contacto datos) {
        Contacto contacto = buscar(id);
        validarPropietario(datos);

        contacto.setPropietarioTipo(datos.getPropietarioTipo());
        contacto.setPropietarioId(datos.getPropietarioId());
        contacto.setNombre(datos.getNombre());
        contacto.setTelefono(datos.getTelefono());
        contacto.setEmail(datos.getEmail());
        contacto.setNotas(datos.getNotas());

        return contactoRepositorio.save(contacto);
    }

    public void eliminar(Integer id) {
        contactoRepositorio.delete(buscar(id));
    }

    private Contacto buscar(Integer id) {
        return contactoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el contacto " + id));
    }

    // El dueño debe ser un usuario que exista y cuyo rol coincida con propietarioTipo
    private void validarPropietario(Contacto contacto) {
        if (!TIPOS.contains(contacto.getPropietarioTipo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El propietarioTipo debe ser ADMIN o ABOGADO");
        }

        Usuario propietario = usuarioRepositorio.findById(contacto.getPropietarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el usuario propietario " + contacto.getPropietarioId()));

        if (!propietario.getRol().equals(contacto.getPropietarioTipo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El usuario " + contacto.getPropietarioId() + " no es " + contacto.getPropietarioTipo());
        }
    }
}
