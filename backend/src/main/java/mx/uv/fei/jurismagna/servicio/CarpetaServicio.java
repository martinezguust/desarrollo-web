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

    public List<Liga> listarLigas(Integer carpetaId) {
        buscarCarpeta(carpetaId);

        return ligaRepositorio.findByCarpeta_IdOrderByIdAsc(carpetaId);
    }

    public Liga crearLiga(Integer carpetaId, Liga liga) {
        Carpeta carpeta = buscarCarpeta(carpetaId);

        if (liga.getUrl() == null || liga.getUrl().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta la URL de la liga");
        }

        // Sin nombre se usa la URL, como en el diseño original
        if (liga.getNombre() == null || liga.getNombre().isBlank()) {
            liga.setNombre(liga.getUrl());
        }

        liga.setId(null);
        liga.setCarpeta(carpeta);

        return ligaRepositorio.save(liga);
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
