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

//Bryan hizo un cambio en el html de registro, los cambios AQUÍ fueron
//quitar @Valid, BindingResult y @ModelAttribute, reemplazarlos por @RequestParam individuales
//para cada campo, construir el objeto manualmente antes de llamar al service, y agregar la
//validación del teléfono. El GET ya no necesita mandar objeto al model porque el HTML no tiene th:object.

@Controller
public class Controller {

    @Autowired private PuestoService puestoService;
    @Autowired private EmpresaService empresaService;
    @Autowired private OferenteService oferenteService;
    @Autowired private CaracteristicaRepository caracteristicaRepository;

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("puestos", puestoService.findTop5Publicos());
        return "/presentation/publico/inicio";
    }

    @GetMapping("/buscar")
    public String buscarForm(Model model) {
        model.addAttribute("raices", caracteristicaRepository.findByPadreIsNull());
        model.addAttribute("resultados", List.of());
        return "/presentation/publico/buscar";
    }

    @PostMapping("/buscar")
    public String buscarResultados(
            @RequestParam(value = "caracteristicas", required = false) List<Integer> ids,
            @RequestParam(value = "modoTodos", defaultValue = "false") boolean modoTodos,
            Model model) {
        model.addAttribute("raices", caracteristicaRepository.findByPadreIsNull());
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

    // ---- EMPRESA ----

    @GetMapping("/registro/empresa")
    public String regEmpresaForm() {
        return "/presentation/publico/registro-empresa";
    }

    @PostMapping("/registro/empresa")
    public String regEmpresaGuardar(
            @RequestParam String nombre,
            @RequestParam String correo,
            @RequestParam String clave,
            @RequestParam String clave2,
            @RequestParam String localizacion,
            @RequestParam String telefono,
            @RequestParam String descripcion,
            Model model) {

        if (!clave.equals(clave2)) {
            model.addAttribute("errorClave", "Las claves no coinciden");
            return "/presentation/publico/registro-empresa";
        }
        if (!telefono.matches("^\\+506 \\d{4} \\d{4}$")) {
            model.addAttribute("errorGeneral", "El teléfono debe tener el formato +506 XXXX XXXX");
            return "/presentation/publico/registro-empresa";
        }

        Empresa empresa = new Empresa();
        empresa.setNombre(nombre);
        empresa.setLocalizacion(localizacion);
        empresa.setTelefono(telefono);
        empresa.setDescripcion(descripcion);

        try {
            empresaService.registrar(empresa, correo, clave);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorGeneral", e.getMessage());
            return "/presentation/publico/registro-empresa";
        }
        return "redirect:/login?registrado=true";
    }

    // ---- OFERENTE ----

    @GetMapping("/registro/oferente")
    public String regOferenteForm() {
        return "/presentation/publico/registro-oferente";
    }

    @PostMapping("/registro/oferente")
    public String regOferenteGuardar(
            @RequestParam String identificacion,
            @RequestParam String nombre,
            @RequestParam String primerApellido,
            @RequestParam String nacionalidad,
            @RequestParam String correo,
            @RequestParam String clave,
            @RequestParam String clave2,
            @RequestParam String telefono,
            @RequestParam String residencia,
            Model model) {

        if (!clave.equals(clave2)) {
            model.addAttribute("errorClave", "Las claves no coinciden");
            return "/presentation/publico/registro-oferente";
        }
        if (!telefono.matches("^\\+506 \\d{4} \\d{4}$")) {
            model.addAttribute("errorGeneral", "El teléfono debe tener el formato +506 XXXX XXXX");
            return "/presentation/publico/registro-oferente";
        }

        Oferente oferente = new Oferente();
        oferente.setIdentificacion(identificacion);
        oferente.setNombre(nombre);
        oferente.setPrimerApellido(primerApellido);
        oferente.setNacionalidad(nacionalidad);
        oferente.setTelefono(telefono);
        oferente.setResidencia(residencia);

        try {
            oferenteService.registrar(oferente, correo, clave);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorGeneral", e.getMessage());
            return "/presentation/publico/registro-oferente";
        }
        return "redirect:/login?registrado=true";
    }
} */

