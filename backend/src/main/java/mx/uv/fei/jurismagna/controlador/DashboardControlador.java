package mx.uv.fei.jurismagna.controlador;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import mx.uv.fei.jurismagna.servicio.DashboardServicio;

@RestController
public class DashboardControlador {

    @Autowired
    DashboardServicio dashboardServicio;

    @GetMapping("/dashboards/admin")
    public Map<String, Object> dashboardAdmin() {
        return dashboardServicio.dashboardAdmin();
    }

    @GetMapping("/dashboards/abogados/{id}")
    public Map<String, Object> dashboardAbogado(@PathVariable Integer id) {
        return dashboardServicio.dashboardAbogado(id);
    }
}
