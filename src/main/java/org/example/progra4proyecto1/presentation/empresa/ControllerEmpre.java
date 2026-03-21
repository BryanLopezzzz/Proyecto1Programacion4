package org.example.progra4proyecto1.presentation.empresa;

import org.example.progra4proyecto1.data.CaracteristicaRepository;
import org.example.progra4proyecto1.data.MonedaRepository;
import org.example.progra4proyecto1.logic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/empresa")
public class ControllerEmpre {

    @Autowired private EmpresaService empresaService;
    @Autowired private PuestoService puestoService;
    @Autowired private OferenteService oferenteService;
    @Autowired private CaracteristicaRepository caracteristicaRepository;
    @Autowired private MonedaRepository monedaRepository;

    private Empresa getEmpresa(Principal principal) {
        return empresaService.findByCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("empresa", getEmpresa(principal));
        return "presentation/empresa/dashboard";
    }

    @GetMapping("/puestos")
    public String misPuestos(Model model, Principal principal) {
        model.addAttribute("puestos", puestoService.findByEmpresa(getEmpresa(principal)));
        return "presentation/empresa/puestos";
    }

    @GetMapping("/puestos/nuevo")
    public String nuevoPuestoForm(Model model) {
        model.addAttribute("puesto", new Puesto());
        model.addAttribute("raices", caracteristicaRepository.findByPadreIsNull());
        model.addAttribute("monedas", monedaRepository.findAll());
        return "presentation/empresa/publicar-puesto";
    }

    @PostMapping("/puestos/nuevo")
    public String nuevoPuestoGuardar(
            @RequestParam String descripcion,
            @RequestParam java.math.BigDecimal salario,
            @RequestParam String tipo,
            @RequestParam Integer monedaId,
            @RequestParam Map<String, String> todosLosParams,
            Principal principal,
            Model model) {
        try {
            /*
             * CORRECCIÓN CRÍTICA:
             * El formulario ya no envía dos arrays paralelos (caracteristicas[] y niveles[])
             * que se desacoplan cuando no todos los checkboxes están marcados.
             *
             * Ahora envía:
             *   sel_<id> = "true"   → checkbox marcado
             *   nivel_<id> = "1..5" → nivel correspondiente a ese ID
             *
             * Aquí recorremos todos los parámetros buscando los que empiezan con "sel_",
             * extraemos el ID y buscamos su nivel en nivel_<id>.
             * Así el emparejamiento es siempre correcto.
             */
            List<Integer> caracIds = new ArrayList<>();
            List<Integer> niveles  = new ArrayList<>();

            for (Map.Entry<String, String> entry : todosLosParams.entrySet()) {
                if (entry.getKey().startsWith("sel_")) {
                    String idStr = entry.getKey().substring(4); // quita "sel_"
                    Integer caracteristicaId = Integer.parseInt(idStr);
                    String nivelStr = todosLosParams.get("nivel_" + idStr);
                    int nivel = (nivelStr != null) ? Integer.parseInt(nivelStr) : 3;
                    caracIds.add(caracteristicaId);
                    niveles.add(nivel);
                }
            }

            Puesto puesto = new Puesto();
            puesto.setDescripcion(descripcion);
            puesto.setSalario(salario);
            puesto.setTipo(Puesto.TipoPuesto.valueOf(tipo));

            puestoService.publicar(puesto, getEmpresa(principal), caracIds, niveles, monedaId);

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("raices", caracteristicaRepository.findByPadreIsNull());
            model.addAttribute("monedas", monedaRepository.findAll());
            return "presentation/empresa/publicar-puesto";
        }
        return "redirect:/empresa/puestos";
    }

    @PostMapping("/puestos/desactivar/{id}")
    public String desactivar(@PathVariable Integer id, Principal principal) {
        Empresa empresa = getEmpresa(principal);
        puestoService.findById(id).ifPresent(p -> {
            if (p.getEmpresa().getId().equals(empresa.getId()))
                puestoService.desactivar(id);
        });
        return "redirect:/empresa/puestos";
    }

    @GetMapping("/candidatos/buscar")
    public String buscarCandidatos(@RequestParam Integer puestoId, Model model, Principal principal) {
        Empresa empresa = getEmpresa(principal);
        Puesto puesto = puestoService.findById(puestoId).orElseThrow();
        if (!puesto.getEmpresa().getId().equals(empresa.getId()))
            return "redirect:/empresa/puestos";
        model.addAttribute("puesto", puesto);
        model.addAttribute("candidatos", puestoService.buscarCandidatos(puesto));
        return "presentation/empresa/candidatos";
    }

    @GetMapping("/candidatos/{id}")
    public String detalleCandidato(@PathVariable Integer id, Model model) {
        Oferente oferente = oferenteService.findById(id).orElseThrow();
        model.addAttribute("oferente", oferente);
        model.addAttribute("habilidades", oferenteService.getHabilidades(oferente));
        return "presentation/empresa/detalle-candidato";
    }
}