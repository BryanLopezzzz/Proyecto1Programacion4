package org.example.progra4proyecto1.presentation.empresa;

import org.example.progra4proyecto1.data.CaracteristicaRepository;
import org.example.progra4proyecto1.data.MonedaRepository;
import org.example.progra4proyecto1.logic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

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
        return "/presentation/empresa/dashboard";
    }

    @GetMapping("/puestos")
    public String misPuestos(Model model, Principal principal) {
        model.addAttribute("puestos", puestoService.findByEmpresa(getEmpresa(principal)));
        return "/presentation/empresa/puestos";
    }

    @GetMapping("/puestos/nuevo")
    public String nuevoPuestoForm(Model model) {
        model.addAttribute("puesto", new Puesto());
        model.addAttribute("raices", caracteristicaRepository.findByPadreIsNull());
        model.addAttribute("monedas", monedaRepository.findAll());
        return "/presentation/empresa/publicar-puesto";
    }

    @PostMapping("/puestos/nuevo")
    public String nuevoPuestoGuardar(
            @ModelAttribute Puesto puesto,
            @RequestParam(value = "caracteristicas", required = false) List<Integer> caracIds,
            @RequestParam(value = "niveles", required = false) List<Integer> niveles,
            @RequestParam Integer monedaId, Principal principal, Model model) {
        try {
            puestoService.publicar(puesto, getEmpresa(principal), caracIds, niveles, monedaId);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("raices", caracteristicaRepository.findByPadreIsNull());
            model.addAttribute("monedas", monedaRepository.findAll());
            return "/presentation/empresa/publicar-puesto";
        }
        return "redirect:/empresa/puestos";
    }

    @PostMapping("/puestos/desactivar/{id}")
    public String desactivar(@PathVariable Integer id, Principal principal) {
        Empresa empresa = getEmpresa(principal);
        puestoService.findById(id).ifPresent(p -> {
            if (p.getEmpresa().getId().equals(empresa.getId())) puestoService.desactivar(id);
        });
        return "redirect:/empresa/puestos";
    }

    @GetMapping("/candidatos/buscar")
    public String buscarCandidatos(@RequestParam Integer puestoId, Model model, Principal principal) {
        Empresa empresa = getEmpresa(principal);
        Puesto puesto = puestoService.findById(puestoId).orElseThrow();
        if (!puesto.getEmpresa().getId().equals(empresa.getId())) return "redirect:/empresa/puestos";
        model.addAttribute("puesto", puesto);
        model.addAttribute("candidatos", puestoService.buscarCandidatos(puesto));
        return "/presentation/empresa/candidatos";
    }

    @GetMapping("/candidatos/{id}")
    public String detalleCandidato(@PathVariable Integer id, Model model) {
        Oferente oferente = oferenteService.findById(id).orElseThrow();
        model.addAttribute("oferente", oferente);
        model.addAttribute("habilidades", oferenteService.getHabilidades(oferente));
        return "/presentation/empresa/detalle-candidato";
    }
}