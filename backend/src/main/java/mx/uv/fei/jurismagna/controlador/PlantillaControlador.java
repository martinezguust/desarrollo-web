package mx.uv.fei.jurismagna.controlador;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import mx.uv.fei.jurismagna.repositorio.AbogadoRepositorio;
import mx.uv.fei.jurismagna.repositorio.CasoRepositorio;
import mx.uv.fei.jurismagna.repositorio.ClienteRepositorio;
import mx.uv.fei.jurismagna.repositorio.MensajeRepositorio;

// Página de estado del backend hecha con Thymeleaf (templates/inicio.html)
@Controller
public class PlantillaControlador {

    @Autowired
    AbogadoRepositorio abogadoRepositorio;

    @Autowired
    ClienteRepositorio clienteRepositorio;

    @Autowired
    CasoRepositorio casoRepositorio;

    @Autowired
    MensajeRepositorio mensajeRepositorio;

    // Spring guarda aquí todas las rutas de los controladores
    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    RequestMappingHandlerMapping rutasDeSpring;

    @GetMapping("/")
    public String inicio(Model modelo) {
        long casosActivos = casoRepositorio.findAll().stream()
                .filter(caso -> caso.getEstado().equals("Activo") || caso.getEstado().equals("EnProceso"))
                .count();

        modelo.addAttribute("sistema", "Juris Magna");
        modelo.addAttribute("estado", "Backend funcionando");
        modelo.addAttribute("abogados", abogadoRepositorio.count());
        modelo.addAttribute("clientes", clienteRepositorio.count());
        modelo.addAttribute("casosActivos", casosActivos);
        modelo.addAttribute("mensajes", mensajeRepositorio.count());
        modelo.addAttribute("rutas", listarRutas());

        return "inicio";
    }

    // Lista { metodo, ruta } de la API, ordenada por ruta (sin "/" ni "/error")
    private List<Map<String, String>> listarRutas() {
        List<Map<String, String>> rutas = new ArrayList<>();

        for (RequestMappingInfo info : rutasDeSpring.getHandlerMethods().keySet()) {
            for (String ruta : info.getPatternValues()) {
                if (ruta.equals("/") || ruta.equals("/error")) {
                    continue;
                }

                for (RequestMethod metodo : info.getMethodsCondition().getMethods()) {
                    Map<String, String> fila = new LinkedHashMap<>();
                    fila.put("metodo", metodo.name());
                    fila.put("ruta", ruta);
                    rutas.add(fila);
                }
            }
        }

        rutas.sort(Comparator.comparing((Map<String, String> fila) -> fila.get("ruta"))
                .thenComparing(fila -> fila.get("metodo")));

        return rutas;
    }
}
