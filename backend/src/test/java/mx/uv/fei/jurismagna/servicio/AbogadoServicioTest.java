package mx.uv.fei.jurismagna.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import mx.uv.fei.jurismagna.modelo.Abogado;
import mx.uv.fei.jurismagna.modelo.Usuario;

// Pruebas unitarias de AbogadoServicio.
// Usan la base H2 en memoria de src/test/resources; con @Transactional
// cada prueba deshace sus cambios al terminar.
@SpringBootTest
@Transactional
class AbogadoServicioTest {

    @Autowired
    AbogadoServicio abogadoServicio;

    // Arma un abogado nuevo con todos sus datos
    private Abogado nuevoAbogado(String username) {
        Abogado abogado = new Abogado();
        abogado.setNombre("Lic. Prueba");
        abogado.setEdad(30);
        abogado.setEspecialidad("Derecho Civil");
        abogado.setEstudios("Licenciatura UV");
        abogado.setExperiencia("5 años");
        abogado.setCedula("11223344");
        abogado.setEmail("prueba@jurismagna.mx");
        abogado.setTelefono("228 123 4567");
        abogado.setUsername(username);
        abogado.setPassword("abogado123");

        return abogado;
    }

    // Revisa el código HTTP que trae la excepción
    private void revisarCodigo(int esperado, ResponseStatusException error) {
        assertEquals(esperado, error.getStatusCode().value());
    }

    @Test
    void crearAbogadoCorrecto() {
        Abogado guardado = abogadoServicio.crear(nuevoAbogado("aprueba"));

        assertNotNull(guardado.getId());
        assertEquals("Lic. Prueba", guardado.getNombre());
        // Sin estado, el abogado se crea como Activo
        assertEquals("Activo", guardado.getEstado());
        // Un abogado nuevo no tiene casos
        assertEquals(0, guardado.getTotalCasos());
    }

    @Test
    void crearAbogadoTambienCreaSuUsuario() {
        Abogado guardado = abogadoServicio.crear(nuevoAbogado("conusuario"));
        Usuario usuario = guardado.getUsuario();

        assertNotNull(usuario.getId());
        assertEquals("conusuario", usuario.getUsername());
        assertEquals("ABOGADO", usuario.getRol());
        // La contraseña se guarda encriptada, nunca como texto normal
        assertNotEquals("abogado123", usuario.getPasswordHash());
    }

    @Test
    void crearAbogadoConUsuarioRepetido() {
        abogadoServicio.crear(nuevoAbogado("repetido"));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> abogadoServicio.crear(nuevoAbogado("repetido")));

        revisarCodigo(409, error);
    }

    @Test
    void crearAbogadoSinContrasena() {
        Abogado abogado = nuevoAbogado("sinpassword");
        abogado.setPassword("");

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> abogadoServicio.crear(abogado));

        revisarCodigo(400, error);
    }

    @Test
    void crearAbogadoConEstadoInvalido() {
        Abogado abogado = nuevoAbogado("estadomalo");
        abogado.setEstado("Vacaciones");

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> abogadoServicio.crear(abogado));

        revisarCodigo(400, error);
    }

    @Test
    void buscarAbogadoQueNoExiste() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> abogadoServicio.buscar(999));

        revisarCodigo(404, error);
    }

    @Test
    void editarAbogado() {
        Abogado guardado = abogadoServicio.crear(nuevoAbogado("editar"));

        Abogado datos = nuevoAbogado("editar");
        datos.setNombre("Lic. Nombre Nuevo");
        datos.setEstado("Inactivo");

        Abogado editado = abogadoServicio.editar(guardado.getId(), datos);

        assertEquals("Lic. Nombre Nuevo", editado.getNombre());
        assertEquals("Inactivo", editado.getEstado());
        // Un abogado Inactivo ya no puede iniciar sesión
        assertFalse(editado.getUsuario().getActivo());
    }

    @Test
    void eliminarAbogadoSinCasos() {
        Abogado guardado = abogadoServicio.crear(nuevoAbogado("eliminar"));
        Integer id = guardado.getId();

        abogadoServicio.eliminar(id);

        // Después de eliminarlo ya no se encuentra
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> abogadoServicio.buscar(id));

        revisarCodigo(404, error);
    }
}
