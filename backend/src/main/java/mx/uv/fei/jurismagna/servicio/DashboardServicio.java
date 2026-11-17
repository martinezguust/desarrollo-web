package mx.uv.fei.jurismagna.servicio;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.uv.fei.jurismagna.modelo.Abogado;
import mx.uv.fei.jurismagna.modelo.Caso;
import mx.uv.fei.jurismagna.modelo.Cliente;
import mx.uv.fei.jurismagna.repositorio.AbogadoRepositorio;
import mx.uv.fei.jurismagna.repositorio.CasoRepositorio;
import mx.uv.fei.jurismagna.repositorio.ClienteRepositorio;

// Indicadores (KPIs) del dashboard del administrador y del abogado
@Service
public class DashboardServicio {

    @Autowired
    AbogadoRepositorio abogadoRepositorio;

    @Autowired
    ClienteRepositorio clienteRepositorio;

    @Autowired
    CasoRepositorio casoRepositorio;

    @Autowired
    AbogadoServicio abogadoServicio;

    @Autowired
    FinanzaServicio finanzaServicio;

    public Map<String, Object> dashboardAdmin() {
        List<Cliente> clientes = clienteRepositorio.findAll();
        List<Caso> casos = casoRepositorio.findAll();
        long abogados = abogadoRepositorio.count();

        int clientesActivos = 0;
        BigDecimal honorariosTotales = BigDecimal.ZERO;

        for (Cliente cliente : clientes) {
            if (cliente.getEstado().equals("Activo")) {
                clientesActivos++;
            }

            honorariosTotales = honorariosTotales.add(cliente.getHonorarios());
        }

        Map<String, Integer> conteo = contarCasos(casos);

        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("personal", abogados + clientes.size());
        dashboard.put("abogados", abogados);
        dashboard.put("casos", conteo);
        dashboard.put("efectividadCerrados", porcentaje(conteo.get("ganados"), conteo.get("ganados") + conteo.get("perdidos")));
        dashboard.put("clientesActivos", clientesActivos);
        dashboard.put("totalClientes", clientes.size());
        dashboard.put("honorariosTotales", honorariosTotales);
        dashboard.put("finanzas", finanzaServicio.resumenDelMes(honorariosTotales));

        return dashboard;
    }

    public Map<String, Object> dashboardAbogado(Integer abogadoId) {
        // buscar() responde 404 si no existe y ya trae sus indicadores
        Abogado abogado = abogadoServicio.buscar(abogadoId);
        Map<String, Integer> conteo = contarCasos(casoRepositorio.findByAbogado_Id(abogadoId));

        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("abogadoId", abogadoId);
        dashboard.put("casos", conteo);
        dashboard.put("efectividad", abogado.getEfectividad());
        dashboard.put("efectividadCerrados", porcentaje(conteo.get("ganados"), conteo.get("ganados") + conteo.get("perdidos")));
        dashboard.put("clientes", abogado.getClientes());
        dashboard.put("satisfaccion", Math.min(abogado.getEfectividad() + 20, 100));

        return dashboard;
    }

    // { total, activos, enProceso, ganados, perdidos }
    private Map<String, Integer> contarCasos(List<Caso> casos) {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        conteo.put("total", casos.size());
        conteo.put("activos", 0);
        conteo.put("enProceso", 0);
        conteo.put("ganados", 0);
        conteo.put("perdidos", 0);

        for (Caso caso : casos) {
            String clave = switch (caso.getEstado()) {
                case "Activo" -> "activos";
                case "EnProceso" -> "enProceso";
                case "Ganado" -> "ganados";
                case "Perdido" -> "perdidos";
                default -> null;
            };

            if (clave != null) {
                conteo.put(clave, conteo.get(clave) + 1);
            }
        }

        return conteo;
    }

    private int porcentaje(int parte, int total) {
        if (total == 0) {
            return 0;
        }

        return Math.round(parte * 100f / total);
    }
}
