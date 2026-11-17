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
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
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
    public Abogado crear(@Valid @RequestBody Abogado abogado) {
        return abogadoServicio.crear(abogado);
    }

    @PutMapping("/abogados/{id}")
    public Abogado editar(@PathVariable Integer id, @Valid @RequestBody Abogado abogado) {
        return abogadoServicio.editar(id, abogado);
    }

    @DeleteMapping("/abogados/{id}")
    public Map<String, String> eliminar(@PathVariable Integer id) {
        abogadoServicio.eliminar(id);

        return Map.of("mensaje", "Abogado eliminado");
    }

    // Cuerpo: { "password": "nuevaClave" }
    @PutMapping("/abogados/{id}/password")
    public Map<String, String> cambiarPassword(@PathVariable Integer id, @RequestBody Map<String, String> datos) {
        abogadoServicio.cambiarPassword(id, datos.get("password"));

        return Map.of("mensaje", "Contraseña actualizada");
    }

    // Multipart con el campo "foto" (JPG, PNG o WEBP)
    @PostMapping("/abogados/{id}/foto")
    public Map<String, String> subirFoto(@PathVariable Integer id, @RequestParam("foto") MultipartFile foto) {
        return Map.of("fotoUrl", abogadoServicio.subirFoto(id, foto));
    }
}
