/*package org.example.progra4proyecto1.presentation.publico;

//import org.example.progra4proyecto1.data.CaracteristicaRepository;
import org.example.progra4proyecto1.data.MonedaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class Controller {

    @Autowired private PuestoService puestoService;
    @Autowired private EmpresaService empresaService;
    @Autowired private OferenteService oferenteService;
    @Autowired private CaracteristicaRepository caracteristicaRepository;
    @Autowired private MonedaRepository monedaRepository;

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("puestos", puestoService.findTop5Publicos());
        return "/presentation/publico/inicio";
    }

    @GetMapping("/buscar")
    public String buscarForm(Model model) {
        model.addAttribute("raices", caracteristicaRepository.findByPadreIsNull());
        model.addAttribute("monedas", monedaRepository.findAll());
        model.addAttribute("resultados", List.of());
        return "/presentation/publico/buscar";
    }

    @PostMapping("/buscar")
    public String buscarResultados(
            @RequestParam(value = "caracteristicas", required = false) List<Integer> ids,
            @RequestParam(value = "modoTodos", defaultValue = "false") boolean modoTodos,
            Model model) {
        model.addAttribute("raices", caracteristicaRepository.findByPadreIsNull());
        model.addAttribute("monedas", monedaRepository.findAll());
        model.addAttribute("resultados", puestoService.buscarPublicos(ids, modoTodos));
        model.addAttribute("seleccionadas", ids);
        model.addAttribute("modoTodos", modoTodos);
        return "/presentation/publico/buscar";
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error, Model model) {
        if (error != null) model.addAttribute("error", "Credenciales incorrectas o cuenta no aprobada.");
        return "/presentation/publico/login";
    }

    @GetMapping("/registro/empresa")
    public String regEmpresaForm(Model model) {
        model.addAttribute("empresa", new Empresa());
        return "/presentation/publico/registro-empresa";
    }

    @PostMapping("/registro/empresa")
    public String regEmpresaGuardar(
            @Valid @ModelAttribute("empresa") Empresa empresa, BindingResult result,
            @RequestParam String correo, @RequestParam String clave, @RequestParam String clave2, Model model) {
        if (result.hasErrors()) return "/presentation/publico/registro-empresa";
        if (!clave.equals(clave2)) { model.addAttribute("errorClave", "Las claves no coinciden"); return "/presentation/publico/registro-empresa"; }
        try { empresaService.registrar(empresa, correo, clave); }
        catch (IllegalArgumentException e) { model.addAttribute("errorGeneral", e.getMessage()); return "/presentation/publico/registro-empresa"; }
        return "redirect:/login?registrado=true";
    }

    @GetMapping("/registro/oferente")
    public String regOferenteForm(Model model) {
        model.addAttribute("oferente", new Oferente());
        return "/presentation/publico/registro-oferente";
    }

    @PostMapping("/registro/oferente")
    public String regOferenteGuardar(
            @Valid @ModelAttribute("oferente") Oferente oferente, BindingResult result,
            @RequestParam String correo, @RequestParam String clave, @RequestParam String clave2, Model model) {
        if (result.hasErrors()) return "/presentation/publico/registro-oferente";
        if (!clave.equals(clave2)) { model.addAttribute("errorClave", "Las claves no coinciden"); return "/presentation/publico/registro-oferente"; }
        try { oferenteService.registrar(oferente, correo, clave); }
        catch (IllegalArgumentException e) { model.addAttribute("errorGeneral", e.getMessage()); return "/presentation/publico/registro-oferente"; }
        return "redirect:/login?registrado=true";
    }
} */