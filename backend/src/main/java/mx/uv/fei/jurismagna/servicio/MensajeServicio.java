package mx.uv.fei.jurismagna.servicio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import mx.uv.fei.jurismagna.modelo.Abogado;
import mx.uv.fei.jurismagna.modelo.Cliente;
import mx.uv.fei.jurismagna.modelo.Mensaje;
import mx.uv.fei.jurismagna.repositorio.AbogadoRepositorio;
import mx.uv.fei.jurismagna.repositorio.ClienteRepositorio;
import mx.uv.fei.jurismagna.repositorio.MensajeRepositorio;

@Service
public class MensajeServicio {

    // Extensiones permitidas según el tipo de mensaje
    static final Map<String, List<String>> EXTENSIONES = Map.of(
            "audio", List.of("webm"),
            "img", List.of("jpg", "jpeg", "png", "gif", "webp"),
            "file", List.of("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"));

    static final List<String> LECTORES = List.of("abogado", "cliente", "admin");

    @Autowired
    MensajeRepositorio mensajeRepositorio;

    @Autowired
    ClienteRepositorio clienteRepositorio;

    @Autowired
    AbogadoRepositorio abogadoRepositorio;

    @Autowired
    NotificacionServicio notificacionServicio;

    @Autowired
    ArchivoServicio archivoServicio;

    public List<Mensaje> listarPorHilo(String hilo) {
        if (hilo == null || hilo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el hilo");
        }

        return mensajeRepositorio.findByHiloOrderByFechaHoraAscIdAsc(hilo);
    }

    // Mensaje de texto (JSON)
    @Transactional
    public Mensaje enviarTexto(Mensaje mensaje) {
        if (mensaje.getHilo() == null || mensaje.getRemitente() == null
                || mensaje.getTexto() == null || mensaje.getTexto().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan el hilo, el remitente o el texto");
        }

        validarHiloYAvisar(mensaje.getHilo(), mensaje.getRemitente());

        mensaje.setId(null);
        mensaje.setTipo("texto");
        mensaje.setArchivoUrl(null);
        mensaje.setNombreArchivo(null);
        mensaje.setFechaHora(LocalDateTime.now());
        mensaje.setLeido(false);

        return mensajeRepositorio.save(mensaje);
    }

    // Nota de voz, imagen o archivo (multipart). Se guarda en uploads/mensajes/{id}-{nombre}.
    // Si algo falla, @Transactional deshace el mensaje y el aviso.
    @Transactional
    public Mensaje enviarArchivo(String hilo, String remitente, String tipo, MultipartFile archivo) {
        if (!EXTENSIONES.containsKey(tipo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo debe ser audio, img o file");
        }

        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el archivo");
        }

        validarHiloYAvisar(hilo, remitente);

        String nombreOriginal = archivo.getOriginalFilename();

        Mensaje mensaje = new Mensaje();
        mensaje.setHilo(hilo);
        mensaje.setRemitente(remitente);
        mensaje.setTipo(tipo);
        mensaje.setNombreArchivo(nombreOriginal);
        mensaje.setFechaHora(LocalDateTime.now());
        mensaje.setLeido(false);
        mensajeRepositorio.save(mensaje);

        // Primero se guarda el mensaje para tener su id y usarlo en el nombre del archivo
        String url = archivoServicio.guardar(archivo, "mensajes", mensaje.getId() + "-" + nombreSinExtension(nombreOriginal),
                EXTENSIONES.get(tipo));
        mensaje.setArchivoUrl(url);

        return mensajeRepositorio.save(mensaje);
    }

    // Marca como leídos los mensajes del hilo que recibió el lector (los que no envió él)
    @Transactional
    public Map<String, Object> marcarLeidos(String hilo, String lector) {
        if (hilo == null || hilo.isBlank() || !LECTORES.contains(lector)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan el hilo o el lector (abogado, cliente o admin)");
        }

        List<Mensaje> sinLeer = mensajeRepositorio.findByHiloAndRemitenteNotAndLeidoFalse(hilo, lector);

        for (Mensaje mensaje : sinLeer) {
            mensaje.setLeido(true);
        }

        mensajeRepositorio.saveAll(sinLeer);

        return Map.of("mensaje", "Mensajes marcados como leídos", "actualizados", sinLeer.size());
    }

    // Borra el mensaje y su archivo (si tiene)
    public void eliminar(Integer id) {
        Mensaje mensaje = mensajeRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el mensaje " + id));

        archivoServicio.eliminar(mensaje.getArchivoUrl());
        mensajeRepositorio.delete(mensaje);
    }

    // Revisa que el remitente pertenezca al hilo y avisa al abogado cuando él recibe:
    //   cliente-{clienteId}         escriben abogado y cliente
    //   admin-abogado-{abogadoId}   escriben admin y abogado
    private void validarHiloYAvisar(String hilo, String remitente) {
        if (hilo == null || remitente == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan el hilo o el remitente");
        }

        if (hilo.matches("cliente-\\d+")) {
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
    }

    // "Mi contrato (1).pdf" -> "Mi_contrato__1_" (solo letras, números, "-" y "_")
    private String nombreSinExtension(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "archivo";
        }

        int punto = nombre.lastIndexOf('.');

        if (punto > 0) {
            nombre = nombre.substring(0, punto);
        }

        return nombre.replaceAll("[^A-Za-z0-9_-]", "_");
    }
}
