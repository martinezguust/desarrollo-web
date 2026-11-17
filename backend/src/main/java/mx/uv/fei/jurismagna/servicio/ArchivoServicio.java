package mx.uv.fei.jurismagna.servicio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

// Guarda y borra los archivos subidos en la carpeta backend/uploads/
@Service
public class ArchivoServicio {

    // Carpeta "uploads" dentro de donde se ejecuta el backend (backend/)
    public static final Path CARPETA_UPLOADS = Paths.get("uploads").toAbsolutePath();

    // Guarda el archivo como uploads/{carpeta}/{nombre}.{extension} y devuelve su URL
    public String guardar(MultipartFile archivo, String carpeta, String nombre, List<String> extensiones) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el archivo");
        }

        String extension = extensionDe(archivo.getOriginalFilename());

        if (!extensiones.contains(extension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tipo de archivo no permitido; se aceptan: " + String.join(", ", extensiones));
        }

        String nombreFinal = nombre + "." + extension;
        Path destino = CARPETA_UPLOADS.resolve(carpeta).resolve(nombreFinal);

        try (InputStream entrada = archivo.getInputStream()) {
            Files.createDirectories(destino.getParent());
            Files.copy(entrada, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo guardar el archivo");
        }

        return "/uploads/" + carpeta + "/" + nombreFinal;
    }

    // Borra el archivo de una URL /uploads/...; si no existe, no pasa nada
    public void eliminar(String url) {
        if (url == null || !url.startsWith("/uploads/")) {
            return;
        }

        try {
            Files.deleteIfExists(CARPETA_UPLOADS.resolve(url.substring("/uploads/".length())));
        } catch (IOException e) {
            // Si no se puede borrar el archivo, el registro igual se elimina
        }
    }

    // "Foto.JPG" -> "jpg"
    public String extensionDe(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "";
        }

        return nombreArchivo.substring(nombreArchivo.lastIndexOf('.') + 1).toLowerCase();
    }
}
