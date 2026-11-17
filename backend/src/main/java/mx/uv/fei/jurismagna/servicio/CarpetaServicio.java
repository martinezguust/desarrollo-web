package mx.uv.fei.jurismagna.servicio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import mx.uv.fei.jurismagna.modelo.Carpeta;
import mx.uv.fei.jurismagna.modelo.Caso;
import mx.uv.fei.jurismagna.modelo.Liga;
import mx.uv.fei.jurismagna.repositorio.CarpetaRepositorio;
import mx.uv.fei.jurismagna.repositorio.CasoRepositorio;
import mx.uv.fei.jurismagna.repositorio.LigaRepositorio;

// Carpetas del drive de cada caso y las ligas dentro de cada carpeta
@Service
public class CarpetaServicio {

    @Autowired
    CarpetaRepositorio carpetaRepositorio;

    @Autowired
    LigaRepositorio ligaRepositorio;

    @Autowired
    CasoRepositorio casoRepositorio;

    public List<Carpeta> listarPorCaso(Integer casoId) {
        buscarCaso(casoId);

        return carpetaRepositorio.findByCaso_IdOrderByIdAsc(casoId);
    }

    public Carpeta crear(Integer casoId, Carpeta carpeta) {
        Caso caso = buscarCaso(casoId);

        carpeta.setId(null);
        carpeta.setCaso(caso);
        carpeta.setFecha(LocalDate.now());

        return carpetaRepositorio.save(carpeta);
    }

    // El caso y la fecha de creación no cambian
    public Carpeta editar(Integer id, Carpeta datos) {
        Carpeta carpeta = buscarCarpeta(id);

        carpeta.setNumero(datos.getNumero());
        carpeta.setMateria(datos.getMateria());
        carpeta.setProyecto(datos.getProyecto());
        carpeta.setEstado(datos.getEstado());
        carpeta.setLigaUrl(datos.getLigaUrl());

        return carpetaRepositorio.save(carpeta);
    }

    // Al borrar la carpeta, la base de datos borra sus ligas (ON DELETE CASCADE)
    public void eliminar(Integer id) {
        carpetaRepositorio.delete(buscarCarpeta(id));
    }

    public List<Liga> listarLigas(Integer carpetaId) {
        buscarCarpeta(carpetaId);

        return ligaRepositorio.findByCarpeta_IdOrderByIdAsc(carpetaId);
    }

    // La URL la revisa @Valid
    public Liga crearLiga(Integer carpetaId, Liga liga) {
        Carpeta carpeta = buscarCarpeta(carpetaId);

        // Sin nombre se usa la URL
        if (liga.getNombre() == null || liga.getNombre().isBlank()) {
            liga.setNombre(liga.getUrl());
        }

        liga.setId(null);
        liga.setCarpeta(carpeta);

        return ligaRepositorio.save(liga);
    }

    public void eliminarLiga(Integer id) {
        Liga liga = ligaRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la liga " + id));

        ligaRepositorio.delete(liga);
    }

    private Caso buscarCaso(Integer casoId) {
        return casoRepositorio.findById(casoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el caso " + casoId));
    }

    private Carpeta buscarCarpeta(Integer carpetaId) {
        return carpetaRepositorio.findById(carpetaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la carpeta " + carpetaId));
    }
}
