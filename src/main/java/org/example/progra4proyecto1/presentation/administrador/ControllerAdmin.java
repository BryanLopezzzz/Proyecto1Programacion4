package org.example.progra4proyecto1.presentation.administrador;

import org.example.progra4proyecto1.logic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class ControllerAdmin {

    @Autowired private AdminService adminService;
    @Autowired private PuestoService puestoService;
    //@Autowired private ReporteService reporteService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "presentation/admin/dashboard";
    }

    @GetMapping("/empresas/pendientes")
    public String empresasPendientes(Model model) {
        model.addAttribute("pendientes", adminService.empresasPendientes());
        return "presentation/admin/empresas-pendientes";
    }

    @PostMapping("/empresas/aprobar/{id}")
    public String aprobarEmpresa(@PathVariable Integer id) {
        adminService.aprobarUsuario(id);
        return "redirect:/admin/empresas/pendientes";
    }

    @PostMapping("/empresas/rechazar/{id}")
    public String rechazarEmpresa(@PathVariable Integer id) {
        adminService.rechazarUsuario(id);
        return "redirect:/admin/empresas/pendientes";
    }

    @GetMapping("/oferentes/pendientes")
    public String oferentesPendientes(Model model) {
        model.addAttribute("pendientes", adminService.oferentesPendientes());
        return "presentation/admin/oferentes-pendientes";
    }

    @PostMapping("/oferentes/aprobar/{id}")
    public String aprobarOferente(@PathVariable Integer id) {
        adminService.aprobarUsuario(id);
        return "redirect:/admin/oferentes/pendientes";
    }

    @PostMapping("/oferentes/rechazar/{id}")
    public String rechazarOferente(@PathVariable Integer id) {
        adminService.rechazarUsuario(id);
        return "redirect:/admin/oferentes/pendientes";
    }

    @GetMapping("/caracteristicas")
    public String caracteristicas(@RequestParam(required = false) Integer actualId, Model model) {
        model.addAttribute("raices", adminService.raices());

        /*
         * CORRECCIÓN: se pasa la lista completa de nodos al modelo.
         * El template la usa para el select de "padre al crear",
         * permitiendo elegir cualquier nodo (no solo raíces) como padre.
         */
        model.addAttribute("todosNodos", adminService.todosLosNodos());

        if (actualId != null) {
            adminService.findCaracteristica(actualId).ifPresent(c -> {
                model.addAttribute("actual", c);
                model.addAttribute("hijos", adminService.hijosDe(actualId));
            });
        }
        return "presentation/admin/caracteristicas";
    }

    @PostMapping("/caracteristicas/crear")
    public String crearCaracteristica(
            @RequestParam String nombre,
            @RequestParam(required = false) Integer padreId) {
        adminService.crearCaracteristica(nombre, padreId);
        return padreId != null
                ? "redirect:/admin/caracteristicas?actualId=" + padreId
                : "redirect:/admin/caracteristicas";
    }

    @GetMapping("/reportes")
    public String reportesForm(Model model) {
        model.addAttribute("anioActual", java.time.LocalDate.now().getYear());
        return "presentation/admin/reportes";
    }

    @GetMapping("/reportes/puestos-mes")
    public String reportePuestosMes(@RequestParam int mes, @RequestParam int anio, Model model) {
        String[] meses = {"Enero","Febrero","Marzo","Abril","Mayo","Junio",
                "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
        List<Puesto> puestos = puestoService.findByMesYAnio(mes, anio);
        model.addAttribute("puestos", puestos);
        model.addAttribute("mes", mes);
        model.addAttribute("anio", anio);
        model.addAttribute("nombreMes", meses[mes - 1]);
        return "presentation/admin/reporte-puestos";
    }

    @GetMapping("/reportes/coincidencias")
    public String reporteCoincidencias(@RequestParam int mes, @RequestParam int anio, Model model) {
        String[] meses = {"Enero","Febrero","Marzo","Abril","Mayo","Junio",
                "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
        List<Puesto> puestos = puestoService.findByMesYAnio(mes, anio);
        List<List<CandidatoResult>> candidatosPorPuesto = new ArrayList<>();
        for (Puesto p : puestos) {
            candidatosPorPuesto.add(puestoService.buscarCandidatos(p));
        }
        model.addAttribute("puestos", puestos);
        model.addAttribute("candidatosPorPuesto", candidatosPorPuesto);
        model.addAttribute("mes", mes);
        model.addAttribute("anio", anio);
        model.addAttribute("nombreMes", meses[mes - 1]);
        return "presentation/admin/reporte-coincidencias";
    }
}