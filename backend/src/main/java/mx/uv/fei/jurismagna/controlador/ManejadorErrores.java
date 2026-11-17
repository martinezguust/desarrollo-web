package mx.uv.fei.jurismagna.controlador;

import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.ConstraintViolationException;

// Convierte cualquier error de la API en { "error": "mensaje" } con su código HTTP.
// Lo usan los controladores de las tres integrantes.
@RestControllerAdvice
public class ManejadorErrores {

    private static final Logger log = LoggerFactory.getLogger(ManejadorErrores.class);

    // Errores que lanzan los servicios: 400, 401, 404, 409
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> errorDeServicio(ResponseStatusException e) {
        return respuesta(e.getStatusCode(), e.getReason());
    }

    // Validaciones @Valid de las entidades (@NotBlank, @Positive, ...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> datosInvalidos(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return respuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> restriccionInvalida(ConstraintViolationException e) {
        String mensaje = e.getConstraintViolations().stream()
                .map(violacion -> violacion.getMessage())
                .collect(Collectors.joining(", "));

        return respuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    // JSON mal escrito o con un dato de formato incorrecto (fecha, número, hora)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> jsonInvalido(HttpMessageNotReadableException e) {
        return respuesta(HttpStatus.BAD_REQUEST, "El JSON no es válido o tiene un dato con formato incorrecto");
    }

    // Parámetro de la URL con tipo incorrecto, por ejemplo /abogados/abc
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> parametroInvalido(MethodArgumentTypeMismatchException e) {
        return respuesta(HttpStatus.BAD_REQUEST, "El valor de " + e.getName() + " no es válido");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> faltaParametro(MissingServletRequestParameterException e) {
        return respuesta(HttpStatus.BAD_REQUEST, "Falta el parámetro " + e.getParameterName());
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Map<String, String>> faltaArchivo(MissingServletRequestPartException e) {
        return respuesta(HttpStatus.BAD_REQUEST, "Falta el campo " + e.getRequestPartName());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> archivoGrande(MaxUploadSizeExceededException e) {
        return respuesta(HttpStatus.BAD_REQUEST, "El archivo pesa más de 10 MB");
    }

    // La base de datos rechazó el cambio (registro repetido o con datos ligados)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> conflicto(DataIntegrityViolationException e) {
        return respuesta(HttpStatus.CONFLICT, "El registro está repetido o tiene datos ligados");
    }

    // Cualquier otro error: los de Spring (404, 405, 415) conservan su código; el resto es 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> otroError(Exception e) {
        if (e instanceof ErrorResponse errorSpring) {
            HttpStatusCode codigo = errorSpring.getStatusCode();

            return respuesta(codigo, mensajeGeneral(codigo.value()));
        }

        log.error("Error no controlado", e);

        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }

    private String mensajeGeneral(int codigo) {
        if (codigo == 404) {
            return "No existe la ruta";
        }

        if (codigo == 405) {
            return "Método HTTP no permitido en esta ruta";
        }

        if (codigo == 415) {
            return "Tipo de contenido no soportado; envía JSON";
        }

        return "La petición no es válida";
    }

    private ResponseEntity<Map<String, String>> respuesta(HttpStatusCode codigo, String mensaje) {
        return ResponseEntity.status(codigo).body(Map.of("error", mensaje == null ? "Error" : mensaje));
    }
}
