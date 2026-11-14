package mx.uv.fei.jurismagna.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import mx.uv.fei.jurismagna.modelo.Carpeta;
import mx.uv.fei.jurismagna.modelo.Liga;
import mx.uv.fei.jurismagna.servicio.CarpetaServicio;

@RestController
public class CarpetaControlador {

    @Autowired
    CarpetaServicio carpetaServicio;

    @GetMapping("/casos/{id}/carpetas")
    public List<Carpeta> listar(@PathVariable Integer id) {
        return carpetaServicio.listarPorCaso(id);
    }

    @PostMapping("/casos/{id}/carpetas")
    public Carpeta crear(@PathVariable Integer id, @RequestBody Carpeta carpeta) {
        return carpetaServicio.crear(id, carpeta);
    }

    @GetMapping("/carpetas/{id}/ligas")
    public List<Liga> listarLigas(@PathVariable Integer id) {
        return carpetaServicio.listarLigas(id);
    }

    @PostMapping("/carpetas/{id}/ligas")
    public Liga crearLiga(@PathVariable Integer id, @RequestBody Liga liga) {
        return carpetaServicio.crearLiga(id, liga);
    }
}
