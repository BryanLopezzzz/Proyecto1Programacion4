package org.example.progra4proyecto1.presentation.oferente;


import org.example.progra4proyecto1.data.CaracteristicaRepository;
import org.example.progra4proyecto1.logic.*;
import org.example.progra4proyecto1.service.OferenteService;
import org.example.progra4proyecto1.service.PuestoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/oferente")

public class ControllerOfere {
    @Autowired private OferenteService ofeServicio;
    @Autowired private PuestoService puestServicio;
    @Autowired private CaracteristicaRepository caraRepo;

    @Value("${app.upload.dir}")
    private String uploadDir;
    private Oferente getOferente(Principal principal) {
        // para no repetir la busqueda a cada rato
        return ofeServicio.findByCorreo(principal.getName()).orElseThrow();
    }
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("oferente", getOferente(principal)); return "presentation/oferente/dashboard";
    }
    @GetMapping("/habilidades")
    public String habilidades(Model model, Principal principal) {
        Oferente oferente = getOferente(principal);
        model.addAttribute("oferente", oferente);
        model.addAttribute("habilidades", ofeServicio.getHabilidades(oferente));
        model.addAttribute("raices", caraRepo.findByPadreIsNull());
        return "presentation/oferente/habilidades";
    }

    @PostMapping("/habilidades/agregar")
    public String agregar(@RequestParam Integer caracteristicaId, @RequestParam Integer nivel, Principal principal) {
        ofeServicio.agregarHabilidad(getOferente(principal), caracteristicaId, nivel); return "redirect:/oferente/habilidades";
    }

    @PostMapping("/habilidades/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, Principal principal) {
        ofeServicio.eliminarHabilidad(getOferente(principal), id); return "redirect:/oferente/habilidades";
    }

    @GetMapping("/cv")
    public String cvForm(Model model, Principal principal) {
        model.addAttribute("oferente", getOferente(principal));
        return "presentation/oferente/cv";
    }

    @PostMapping("/cv/subir")
    public String subirCv(@RequestParam MultipartFile archivo, Principal principal, Model model) {
        Oferente oferente = getOferente(principal);
        if (archivo.isEmpty()) {
            model.addAttribute("error", "Debe seleccionar un archivo");
            model.addAttribute("oferente", oferente); return "presentation/oferente/cv";
        }
        String originalFilename = archivo.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            model.addAttribute("error", "Solo se permiten archivos PDF");
            model.addAttribute("oferente", oferente); return "presentation/oferente/cv";
        }
        try {
            Path dirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            File dir = dirPath.toFile();
            // si no existe la carpeta la creamos con el dir
            if (!dir.exists()) {
                dir.mkdirs();
            }
            //el uuid es para evitar nombres repetidos
            String nombre = UUID.randomUUID() + "_" + originalFilename;
            File destino = dirPath.resolve(nombre).toFile();
            archivo.transferTo(destino);
            oferente.setCurriculumPdf(nombre);
            ofeServicio.guardar(oferente);
        } catch (IOException e) {
            model.addAttribute("error", "Error al subir archivo: " + e.getMessage());
            model.addAttribute("oferente", oferente); return "presentation/oferente/cv";
        }
        return "redirect:/oferente/dashboard";
    }

    @GetMapping("/puestos")
    public String buscarPuestos(@RequestParam(required = false) List<Integer> caracteristicas, @RequestParam(defaultValue = "false") boolean modoTodos, Model model) {
        model.addAttribute("resultados", puestServicio.buscarTodos(caracteristicas, modoTodos));
        model.addAttribute("raices", caraRepo.findByPadreIsNull());
        model.addAttribute("seleccionadas", caracteristicas);
        model.addAttribute("modoTodos", modoTodos); return "presentation/oferente/buscar-puestos";
    }
}