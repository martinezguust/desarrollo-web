package mx.uv.fei.jurismagna.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import mx.uv.fei.jurismagna.modelo.Abogado;
import mx.uv.fei.jurismagna.modelo.Caso;
import mx.uv.fei.jurismagna.modelo.Usuario;
import mx.uv.fei.jurismagna.repositorio.AbogadoRepositorio;
import mx.uv.fei.jurismagna.repositorio.CasoRepositorio;
import mx.uv.fei.jurismagna.repositorio.ClienteRepositorio;
import mx.uv.fei.jurismagna.repositorio.NotaRepositorio;
import mx.uv.fei.jurismagna.repositorio.UsuarioRepositorio;

@Service
public class AbogadoServicio {

    static final List<String> ESTADOS = List.of("Activo", "Inactivo");

    @Autowired
    AbogadoRepositorio abogadoRepositorio;

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    @Autowired
    CasoRepositorio casoRepositorio;

    @Autowired
    ClienteRepositorio clienteRepositorio;

    @Autowired
    NotaRepositorio notaRepositorio;

    @Autowired
    UsuarioServicio usuarioServicio;

    @Autowired
    ArchivoServicio archivoServicio;

    BCryptPasswordEncoder encriptador = new BCryptPasswordEncoder();

    public List<Abogado> listar() {
        List<Abogado> abogados = abogadoRepositorio.findAll();

        for (Abogado abogado : abogados) {
            calcularIndicadores(abogado);
        }

        return abogados;
    }

    public Abogado buscar(Integer id) {
        Abogado abogado = abogadoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el abogado " + id));

        calcularIndicadores(abogado);

        return abogado;
    }

    // Crea el usuario (rol ABOGADO) y el abogado juntos: si algo falla no se guarda nada.
    // El nombre lo revisa @Valid; aquí se revisan el usuario y la contraseña.
    @Transactional
    public Abogado crear(Abogado abogado) {
        if (estaVacio(abogado.getUsername()) || estaVacio(abogado.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan el usuario o la contraseña");
        }

        if (usuarioRepositorio.existsByUsername(abogado.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario " + abogado.getUsername() + " ya existe");
        }

        if (estaVacio(abogado.getEstado())) {
            abogado.setEstado("Activo");
        }

        validarEstado(abogado.getEstado());

        Usuario usuario = new Usuario();
        usuario.setUsername(abogado.getUsername());
        usuario.setPasswordHash(encriptador.encode(abogado.getPassword()));
        usuario.setRol("ABOGADO");
        usuario.setActivo(abogado.getEstado().equals("Activo"));
        usuarioRepositorio.save(usuario);

        abogado.setId(null);
        abogado.setUsuario(usuario);
        abogado.setFotoUrl(null);

        Abogado guardado = abogadoRepositorio.save(abogado);
        calcularIndicadores(guardado);

        return guardado;
    }

    // Edita los datos del abogado; el usuario, la contraseña y la foto tienen sus propias rutas
    @Transactional
    public Abogado editar(Integer id, Abogado datos) {
        Abogado abogado = buscar(id);

        if (estaVacio(datos.getEstado())) {
            datos.setEstado(abogado.getEstado());
        }

        validarEstado(datos.getEstado());

        abogado.setNombre(datos.getNombre());
        abogado.setEdad(datos.getEdad());
        abogado.setEspecialidad(datos.getEspecialidad());
        abogado.setEstudios(datos.getEstudios());
        abogado.setExperiencia(datos.getExperiencia());
        abogado.setCedula(datos.getCedula());
        abogado.setEmail(datos.getEmail());
        abogado.setTelefono(datos.getTelefono());
        abogado.setEstado(datos.getEstado());

        // Un abogado Inactivo ya no puede iniciar sesión
        abogado.getUsuario().setActivo(datos.getEstado().equals("Activo"));

        Abogado guardado = abogadoRepositorio.save(abogado);
        calcularIndicadores(guardado);

        return guardado;
    }

    // No se elimina si todavía tiene clientes o casos; sus notas y sus fotos se borran con él
    @Transactional
    public void eliminar(Integer id) {
        Abogado abogado = buscar(id);

        if (abogado.getClientes() > 0 || abogado.getTotalCasos() > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: el abogado todavía tiene clientes o casos asignados");
        }

        notaRepositorio.deleteAll(notaRepositorio.findByAbogado_IdOrderByFechaDescIdDesc(id));
        archivoServicio.eliminar(abogado.getFotoUrl());

        Usuario usuario = abogado.getUsuario();
        archivoServicio.eliminar(usuario.getFotoUrl());

        abogadoRepositorio.delete(abogado);
        usuarioRepositorio.delete(usuario);
    }

    public void cambiarPassword(Integer id, String password) {
        Abogado abogado = buscar(id);
        usuarioServicio.cambiarPassword(abogado.getUsuario().getId(), password);
    }

    public String subirFoto(Integer id, MultipartFile foto) {
        Abogado abogado = buscar(id);
        String fotoUrl = archivoServicio.guardar(foto, "abogados", String.valueOf(id), UsuarioServicio.EXTENSIONES_FOTO);

        abogado.setFotoUrl(fotoUrl);
        abogadoRepositorio.save(abogado);

        return fotoUrl;
    }

    // Activos (Activo + EnProceso), total, ganados, clientes y efectividad = ganados / total × 100
    private void calcularIndicadores(Abogado abogado) {
        List<Caso> casos = casoRepositorio.findByAbogado_Id(abogado.getId());
        int activos = 0;
        int ganados = 0;

        for (Caso caso : casos) {
            if (caso.getEstado().equals("Activo") || caso.getEstado().equals("EnProceso")) {
                activos++;
            }

            if (caso.getEstado().equals("Ganado")) {
                ganados++;
            }
        }

        int clientes = (int) clienteRepositorio.countByAbogado_Id(abogado.getId());

        abogado.ponerIndicadores(activos, casos.size(), ganados, clientes, porcentaje(ganados, casos.size()));
    }

    private int porcentaje(int parte, int total) {
        if (total == 0) {
            return 0;
        }

        return Math.round(parte * 100f / total);
    }

    private void validarEstado(String estado) {
        if (!ESTADOS.contains(estado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado debe ser Activo o Inactivo");
        }
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
