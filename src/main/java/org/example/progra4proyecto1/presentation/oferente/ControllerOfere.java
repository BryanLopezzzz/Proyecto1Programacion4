package org.example.progra4proyecto1.presentation.oferente;

import org.example.progra4proyecto1.data.CaracteristicaRepository;
import org.example.progra4proyecto1.logic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/oferente")
public class ControllerOfere {

    @Autowired private OferenteService oferenteService;
    @Autowired private PuestoService puestoService;
    @Autowired private CaracteristicaRepository caracteristicaRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    private Oferente getOferente(Principal principal) {
        return oferenteService.findByCorreo(principal.getName()).orElseThrow();
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("oferente", getOferente(principal));
        return "presentation/oferente/dashboard";
    }

    @GetMapping("/habilidades")
    public String habilidades(Model model, Principal principal) {
        Oferente oferente = getOferente(principal);
        model.addAttribute("oferente", oferente);
        model.addAttribute("habilidades", oferenteService.getHabilidades(oferente));
        model.addAttribute("raices", caracteristicaRepository.findByPadreIsNull());
        return "presentation/oferente/habilidades";
    }

    @PostMapping("/habilidades/agregar")
    public String agregar(@RequestParam Integer caracteristicaId, @RequestParam Integer nivel, Principal principal) {
        oferenteService.agregarHabilidad(getOferente(principal), caracteristicaId, nivel);
        return "redirect:/oferente/habilidades";
    }

    @PostMapping("/habilidades/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, Principal principal) {
        oferenteService.eliminarHabilidad(getOferente(principal), id);
        return "redirect:/oferente/habilidades";
    }

    @GetMapping("/cv")
    public String cvForm(Model model, Principal principal) {
        model.addAttribute("oferente", getOferente(principal));
        return "presentation/oferente/cv";
    }

    @PostMapping("/cv/subir")
    public String subirCv(@RequestParam MultipartFile archivo, Principal principal, Model model) {
        Oferente oferente = getOferente(principal);

        if (archivo.isEmpty() || !archivo.getOriginalFilename().endsWith(".pdf")) {
            model.addAttribute("error", "Solo se permiten archivos PDF");
            model.addAttribute("oferente", oferente);
            return "presentation/oferente/cv";
        }
        try {
            // Ruta absoluta en el proyecto
            String dirAbsoluto = System.getProperty("user.dir") + "/uploads/cv";
            File dir = new File(dirAbsoluto);
            if (!dir.exists()) dir.mkdirs();

            String nombre = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
            File destino = new File(dir, nombre);
            archivo.transferTo(destino.getAbsoluteFile());

            oferente.setCurriculumPdf(nombre);
            oferenteService.guardar(oferente);

        } catch (IOException e) {
            model.addAttribute("error", "Error al subir archivo: " + e.getMessage());
            model.addAttribute("oferente", oferente);
            return "presentation/oferente/cv";
        }
        return "redirect:/oferente/dashboard";
    }

    @GetMapping("/puestos")
    public String buscarPuestos(
            @RequestParam(required = false) List<Integer> caracteristicas,
            @RequestParam(defaultValue = "false") boolean modoTodos, Model model) {
        model.addAttribute("resultados", puestoService.buscarTodos(caracteristicas, modoTodos));
        model.addAttribute("raices", caracteristicaRepository.findByPadreIsNull());
        model.addAttribute("seleccionadas", caracteristicas);
        model.addAttribute("modoTodos", modoTodos);
        return "presentation/oferente/buscar-puestos";
    }
}