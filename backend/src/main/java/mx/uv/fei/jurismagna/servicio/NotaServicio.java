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

    // El título lo revisa @Valid
    public Nota crear(Nota nota) {
        if (nota.getAbogadoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el abogadoId");
        }

        Abogado abogado = abogadoRepositorio.findById(nota.getAbogadoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el abogado " + nota.getAbogadoId()));

        nota.setId(null);
        nota.setAbogado(abogado);
        nota.setCompletada(false);
        nota.setFecha(LocalDate.now());

        return notaRepositorio.save(nota);
    }

    // Edita el título y el contenido o la marca como completada; el abogado y la fecha no cambian
    public Nota editar(Integer id, Nota datos) {
        Nota nota = buscar(id);

        nota.setTitulo(datos.getTitulo());
        nota.setContenido(datos.getContenido());

        if (datos.getCompletada() != null) {
            nota.setCompletada(datos.getCompletada());
        }

        return notaRepositorio.save(nota);
    }

    public void eliminar(Integer id) {
        notaRepositorio.delete(buscar(id));
    }

    private Nota buscar(Integer id) {
        return notaRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la nota " + id));
    }
}
