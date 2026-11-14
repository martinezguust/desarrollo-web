package mx.uv.fei.jurismagna.servicio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import mx.uv.fei.jurismagna.modelo.Abogado;
import mx.uv.fei.jurismagna.modelo.Cliente;
import mx.uv.fei.jurismagna.modelo.Mensaje;
import mx.uv.fei.jurismagna.repositorio.AbogadoRepositorio;
import mx.uv.fei.jurismagna.repositorio.ClienteRepositorio;
import mx.uv.fei.jurismagna.repositorio.MensajeRepositorio;

@Service
public class MensajeServicio {

    @Autowired
    MensajeRepositorio mensajeRepositorio;

    @Autowired
    ClienteRepositorio clienteRepositorio;

    @Autowired
    AbogadoRepositorio abogadoRepositorio;

    @Autowired
    NotificacionServicio notificacionServicio;

    public List<Mensaje> listarPorHilo(String hilo) {
        if (hilo == null || hilo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el hilo");
        }

        return mensajeRepositorio.findByHiloOrderByFechaHoraAscIdAsc(hilo);
    }

    // Envía un mensaje de texto y avisa al abogado si él es quien lo recibe
    @Transactional
    public Mensaje enviarTexto(Mensaje mensaje) {
        if (mensaje.getHilo() == null || mensaje.getRemitente() == null
                || mensaje.getTexto() == null || mensaje.getTexto().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan el hilo, el remitente o el texto");
        }

        String hilo = mensaje.getHilo();
        String remitente = mensaje.getRemitente();

        if (hilo.matches("cliente-\\d+")) {
            // Conversación abogado <-> cliente
            if (!remitente.equals("abogado") && !remitente.equals("cliente")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "En " + hilo + " solo escriben abogado y cliente");
            }

            Integer clienteId = Integer.valueOf(hilo.substring("cliente-".length()));

            Cliente cliente = clienteRepositorio.findById(clienteId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el cliente " + clienteId));

            if (remitente.equals("cliente")) {
                notificacionServicio.avisar(cliente.getAbogado().getUsuario(), "Nuevo mensaje de " + cliente.getNombre());
            }
        } else if (hilo.matches("admin-abogado-\\d+")) {
            // Conversación admin <-> abogado
            if (!remitente.equals("admin") && !remitente.equals("abogado")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "En " + hilo + " solo escriben admin y abogado");
            }

            Integer abogadoId = Integer.valueOf(hilo.substring("admin-abogado-".length()));

            Abogado abogado = abogadoRepositorio.findById(abogadoId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el abogado " + abogadoId));

            if (remitente.equals("admin")) {
                notificacionServicio.avisar(abogado.getUsuario(), "Nuevo mensaje del Administrador");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El hilo debe ser cliente-{id} o admin-abogado-{id}");
        }

        mensaje.setId(null);
        mensaje.setTipo("texto");
        mensaje.setArchivoUrl(null);
        mensaje.setNombreArchivo(null);
        mensaje.setFechaHora(LocalDateTime.now());
        mensaje.setLeido(false);

        return mensajeRepositorio.save(mensaje);
    }
}
