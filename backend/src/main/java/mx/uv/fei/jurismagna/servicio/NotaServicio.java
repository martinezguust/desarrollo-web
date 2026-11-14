package mx.uv.fei.jurismagna.servicio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import mx.uv.fei.jurismagna.modelo.Abogado;
import mx.uv.fei.jurismagna.modelo.Nota;
import mx.uv.fei.jurismagna.repositorio.AbogadoRepositorio;
import mx.uv.fei.jurismagna.repositorio.NotaRepositorio;

@Service
public class NotaServicio {

    @Autowired
    NotaRepositorio notaRepositorio;

    @Autowired
    AbogadoRepositorio abogadoRepositorio;

    // Cada abogado solo ve sus notas
    public List<Nota> listarPorAbogado(Integer abogadoId) {
        if (abogadoId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el abogadoId");
        }

        return notaRepositorio.findByAbogado_IdOrderByFechaDescIdDesc(abogadoId);
    }

    public Nota crear(Nota nota) {
        if (nota.getAbogadoId() == null || nota.getTitulo() == null || nota.getTitulo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan el abogadoId o el título");
        }

        Abogado abogado = abogadoRepositorio.findById(nota.getAbogadoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el abogado " + nota.getAbogadoId()));

        nota.setId(null);
        nota.setAbogado(abogado);
        nota.setCompletada(false);
        nota.setFecha(LocalDate.now());

        return notaRepositorio.save(nota);
    }
}
