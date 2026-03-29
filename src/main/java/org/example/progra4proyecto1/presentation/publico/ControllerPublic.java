package org.example.progra4proyecto1.presentation.publico;

import org.example.progra4proyecto1.data.CaracteristicaRepository;
import org.example.progra4proyecto1.data.MonedaRepository;
import org.example.progra4proyecto1.service.EmpresaService;
import org.example.progra4proyecto1.service.OferenteService;
import org.example.progra4proyecto1.service.PuestoService;
import org.example.progra4proyecto1.logic.Empresa;
import org.example.progra4proyecto1.logic.Oferente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class ControllerPublic {
    @Autowired private PuestoService pservicio;
    @Autowired private EmpresaService eservicio;
    @Autowired private OferenteService oservicio;
    @Autowired private CaracteristicaRepository caraRepe;
    @Autowired private MonedaRepository repoMone;

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("puestos", pservicio.findTop5Publicos());
        return "presentation/publico/inicio";
    }
    @GetMapping("/buscar")
    public String buscarForm(Model model) {
        model.addAttribute("raices", caraRepe.findByPadreIsNull());
        model.addAttribute("resultados", List.of());
        model.addAttribute("monedas", repoMone.findAll());
        return "presentation/publico/buscar";
    }
    @PostMapping("/buscar")
    public String buscarResultados(@RequestParam(value = "caracteristicas", required = false) List<Integer> ids,
            @RequestParam(value = "modoTodos", defaultValue = "false") boolean modoTodos, @RequestParam(value = "monedaId", required = false) Integer monedaId, Model model) {
        model.addAttribute("raices", caraRepe.findByPadreIsNull());
        model.addAttribute("resultados", pservicio.buscarPublicos(ids, modoTodos, monedaId));
        model.addAttribute("seleccionadas", ids);
        model.addAttribute("modoTodos", modoTodos);
        model.addAttribute("monedas", repoMone.findAll());
        model.addAttribute("monedaSeleccionada", monedaId); return "presentation/publico/buscar";
    }
    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error, Model model) {
        if (error != null) model.addAttribute("error", "Credenciales incorrectas o cuenta no aprobada"); return "presentation/publico/login";
    }
    @GetMapping("/registro/empresa")
    public String regEmpresaForm() {
        return "presentation/publico/registro-empresa";
    }
    @PostMapping("/registro/empresa")
    public String regEmpresaGuardar(@RequestParam String nombre, @RequestParam String correo, @RequestParam String clave,
            @RequestParam String clave2, @RequestParam String localizacion, @RequestParam String telefono, @RequestParam String descripcion, Model model) {
        model.addAttribute("v_correo", correo);
        model.addAttribute("v_localizacion", localizacion);
        model.addAttribute("v_descripcion", descripcion);

        if (nombre == null || nombre.isBlank()) {
            model.addAttribute("errorNombre", "El nombre es requerido");
            model.addAttribute("v_telefono", telefono); return "presentation/publico/registro-empresa";
        }
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ][a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\\s\\.]*$")) {
            model.addAttribute("errorNombre", "El nombre solo puede contener letras, espacios y puntos");
            model.addAttribute("v_telefono", telefono); return "presentation/publico/registro-empresa";
        }

        if (nombre.isBlank()) {
            model.addAttribute("errorNombre", "El nombre no puede ser solo espacios");
            model.addAttribute("v_telefono", telefono); return "presentation/publico/registro-empresa";
        }
        if (nombre.matches(".*\\d.*")) {
            model.addAttribute("errorNombre", "El nombre no puede contener números");
            model.addAttribute("v_telefono", telefono); return "presentation/publico/registro-empresa";
        }
        model.addAttribute("v_nombre", nombre);
        if (clave == null || clave.isBlank() || clave.length() < 6) {
            model.addAttribute("errorClave", "La contraseña debe tener mínimo 6 caracteres");
            model.addAttribute("v_nombre", nombre);
            model.addAttribute("v_telefono", telefono); return "presentation/publico/registro-empresa";
        }
        if (!clave.equals(clave2)) {
            model.addAttribute("errorClave", "Las claves no coinciden");
            model.addAttribute("v_nombre", nombre);
            model.addAttribute("v_telefono", telefono); return "presentation/publico/registro-empresa";
        }
        if (correo == null || correo.isBlank()) {
            model.addAttribute("errorCorreo", "El correo es requerido");
            model.addAttribute("v_nombre", nombre);
            model.addAttribute("v_telefono", telefono);
            model.addAttribute("v_localizacion", localizacion);
            model.addAttribute("v_descripcion", descripcion); return "presentation/publico/registro-empresa";
        }

        if (localizacion == null || localizacion.isBlank()) {
            model.addAttribute("errorLocalizacion", "La localización es requerida");
            model.addAttribute("v_nombre", nombre);
            model.addAttribute("v_correo", correo);
            model.addAttribute("v_telefono", telefono);
            model.addAttribute("v_descripcion", descripcion); return "presentation/publico/registro-empresa";
        }

        if (descripcion == null || descripcion.isBlank()) {
            model.addAttribute("errorDescripcion", "La descripción es requerida");
            model.addAttribute("v_nombre", nombre);
            model.addAttribute("v_correo", correo);
            model.addAttribute("v_telefono", telefono);
            model.addAttribute("v_localizacion", localizacion); return "presentation/publico/registro-empresa";
        }
        String telefonoFinal = "+506 " + telefono.trim();
        if (!telefonoFinal.matches("^\\+506 \\d{4} \\d{4}$")) {
            model.addAttribute("errorGeneral", "El teléfono debe tener el formato XXXX XXXX");
            model.addAttribute("v_nombre", nombre);
            model.addAttribute("v_correo", correo);
            model.addAttribute("v_localizacion", localizacion);
            model.addAttribute("v_descripcion", descripcion);
            return "presentation/publico/registro-empresa";
        }
        Empresa empresa = new Empresa();
        empresa.setNombre(nombre);
        empresa.setLocalizacion(localizacion);
        empresa.setTelefono(telefonoFinal);
        empresa.setDescripcion(descripcion);

        try {
            eservicio.registrar(empresa, correo, clave);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorGeneral", e.getMessage()); return "presentation/publico/registro-empresa";
        }
        return "redirect:/login?registrado=true";
    }

    @GetMapping("/registro/oferente")
    public String regOferenteForm() {
        return "presentation/publico/registro-oferente";
    }

    @PostMapping("/registro/oferente")
    public String regOferenteGuardar(@RequestParam String identificacion, @RequestParam String nombre, @RequestParam String primerApellido, @RequestParam String nacionalidad, @RequestParam String correo, @RequestParam String clave, @RequestParam String clave2, @RequestParam String telefono, @RequestParam String residencia, Model model) {
        model.addAttribute("v_identificacion", identificacion);
        model.addAttribute("v_correo", correo);
        model.addAttribute("v_nacionalidad", nacionalidad);
        model.addAttribute("v_residencia", residencia);

        if (identificacion == null || identificacion.isBlank()) {
            model.addAttribute("errorIdentificacion", "La identificación es requerida");
            model.addAttribute("v_telefono", telefono);
            model.addAttribute("v_primerApellido", primerApellido);
            return "presentation/publico/registro-oferente";
        }
        if (!identificacion.matches("^\\d{1}-\\d{4}-\\d{4}$")) {
            model.addAttribute("errorIdentificacion", "La identificación debe tener el formato X-XXXX-XXXX");
            model.addAttribute("v_nombre", nombre);
            model.addAttribute("v_telefono", telefono);
            model.addAttribute("v_primerApellido", primerApellido);
            return "presentation/publico/registro-oferente";
        }
        model.addAttribute("v_identificacion", identificacion);
        if (nombre == null || nombre.isBlank()) {
            model.addAttribute("errorNombre", "El nombre es requerido");
            model.addAttribute("v_telefono", telefono);
            model.addAttribute("v_primerApellido", primerApellido);
            return "presentation/publico/registro-oferente";
        }
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\\s]+$")) {
            model.addAttribute("errorNombre", "El nombre solo puede contener letras y espacios");
            model.addAttribute("v_telefono", telefono);
            model.addAttribute("v_primerApellido", primerApellido);
            return "presentation/publico/registro-oferente";
        }
        model.addAttribute("v_nombre", nombre);
        if (primerApellido == null || primerApellido.isBlank()) {
            model.addAttribute("errorApellido", "El primer apellido es requerido");
            model.addAttribute("v_telefono", telefono);
            model.addAttribute("v_primerApellido", primerApellido);
            return "presentation/publico/registro-oferente";
        }
        if (!primerApellido.matches("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\\s]+$")) {
            model.addAttribute("errorApellido", "El apellido solo puede contener letras y espacios");
            model.addAttribute("v_telefono", telefono);
            return "presentation/publico/registro-oferente";
        }
        model.addAttribute("v_primerApellido", primerApellido);
        if (nacionalidad == null || nacionalidad.isBlank()) {
            model.addAttribute("errorGeneral", "La nacionalidad es requerida");
            model.addAttribute("v_nombre", nombre);
            model.addAttribute("v_primerApellido", primerApellido);
            model.addAttribute("v_telefono", telefono);
            return "presentation/publico/registro-oferente";
        }
        if (residencia == null || residencia.isBlank()) {
            model.addAttribute("errorGeneral", "La residencia es requerida");
            model.addAttribute("v_nombre", nombre);
            model.addAttribute("v_primerApellido", primerApellido);
            model.addAttribute("v_telefono", telefono);
            return "presentation/publico/registro-oferente";
        }

        if (clave == null || clave.isBlank() || clave.length() < 6) {
            model.addAttribute("errorClave", "La contraseña debe tener mínimo 6 caracteres");
            model.addAttribute("v_telefono", telefono);
            return "presentation/publico/registro-oferente";
        }
        if (!clave.equals(clave2)) {
            model.addAttribute("errorClave", "Las claves no coinciden");
            return "presentation/publico/registro-oferente";
        }
        String telefFinal = "+506 " + telefono.trim();
        if (!telefFinal.matches("^\\+506 \\d{4} \\d{4}$")) {
            model.addAttribute("errorGeneral", "El teléfono debe tener el formato XXXX XXXX");
            model.addAttribute("v_telefono", telefono);
            return "presentation/publico/registro-oferente";
        }
        Oferente oferente = new Oferente();
        oferente.setIdentificacion(identificacion);
        oferente.setNombre(nombre);
        oferente.setPrimerApellido(primerApellido);
        oferente.setNacionalidad(nacionalidad);
        oferente.setTelefono(telefFinal);
        oferente.setResidencia(residencia);

        try {
            oservicio.registrar(oferente, correo, clave);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorGeneral", e.getMessage());
            model.addAttribute("v_telefono", telefono); return "presentation/publico/registro-oferente";
        }
        return "redirect:/login?registrado=true";
    }


}

