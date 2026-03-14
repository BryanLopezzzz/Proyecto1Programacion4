package org.example.progra4proyecto1.presentation.admin;

/**
 * import com.una.bolsaempleo.logic.*;
 * import org.springframework.beans.factory.annotation.Autowired;
 * import org.springframework.http.*;
 * import org.springframework.stereotype.Controller;
 * import org.springframework.ui.Model;
 * import org.springframework.web.bind.annotation.*;
 * import java.util.*;
 *
 * @Controller
 * @RequestMapping("/admin")
 * public class Controller {
 *
 *     @Autowired private AdminService adminService;
 *     @Autowired private PuestoService puestoService;
 *     @Autowired private ReporteService reporteService;
 *
 *     @GetMapping("/dashboard")
 *     public String dashboard() { return "/presentation/admin/dashboard"; }
 *
 *     @GetMapping("/empresas/pendientes")
 *     public String empresasPendientes(Model model) {
 *         model.addAttribute("pendientes", adminService.empresasPendientes());
 *         return "/presentation/admin/empresas-pendientes";
 *     }
 *
 *     @PostMapping("/empresas/aprobar/{id}")
 *     public String aprobarEmpresa(@PathVariable Integer id) {
 *         adminService.aprobarUsuario(id);
 *         return "redirect:/admin/empresas/pendientes";
 *     }
 *
 *     @PostMapping("/empresas/rechazar/{id}")
 *     public String rechazarEmpresa(@PathVariable Integer id) {
 *         adminService.rechazarUsuario(id);
 *         return "redirect:/admin/empresas/pendientes";
 *     }
 *
 *     @GetMapping("/oferentes/pendientes")
 *     public String oferentesPendientes(Model model) {
 *         model.addAttribute("pendientes", adminService.oferentesPendientes());
 *         return "/presentation/admin/oferentes-pendientes";
 *     }
 *
 *     @PostMapping("/oferentes/aprobar/{id}")
 *     public String aprobarOferente(@PathVariable Integer id) {
 *         adminService.aprobarUsuario(id);
 *         return "redirect:/admin/oferentes/pendientes";
 *     }
 *
 *     @PostMapping("/oferentes/rechazar/{id}")
 *     public String rechazarOferente(@PathVariable Integer id) {
 *         adminService.rechazarUsuario(id);
 *         return "redirect:/admin/oferentes/pendientes";
 *     }
 *
 *     @GetMapping("/caracteristicas")
 *     public String caracteristicas(@RequestParam(required = false) Integer actualId, Model model) {
 *         model.addAttribute("raices", adminService.raices());
 *         if (actualId != null) {
 *             adminService.findCaracteristica(actualId).ifPresent(c -> {
 *                 model.addAttribute("actual", c);
 *                 model.addAttribute("hijos", adminService.hijosDe(actualId));
 *             });
 *         }
 *         return "/presentation/admin/caracteristicas";
 *     }
 *
 *     @PostMapping("/caracteristicas/crear")
 *     public String crearCaracteristica(@RequestParam String nombre, @RequestParam(required = false) Integer padreId) {
 *         adminService.crearCaracteristica(nombre, padreId);
 *         return padreId != null ? "redirect:/admin/caracteristicas?actualId=" + padreId : "redirect:/admin/caracteristicas";
 *     }
 *
 *     @GetMapping("/reportes")
 *     public String reportesForm(Model model) {
 *         model.addAttribute("anioActual", java.time.LocalDate.now().getYear());
 *         return "/presentation/admin/reportes";
 *     }
 *
 *     @GetMapping("/reportes/puestos-mes")
 *     public ResponseEntity<byte[]> reportePuestosMes(@RequestParam int mes, @RequestParam int anio) {
 *         try {
 *             byte[] pdf = reporteService.reportePuestosPorMes(mes, anio);
 *             return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
 *                 .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=puestos_" + mes + "_" + anio + ".pdf")
 *                 .body(pdf);
 *         } catch (Exception e) { return ResponseEntity.internalServerError().build(); }
 *     }
 *
 *     @GetMapping("/reportes/coincidencias")
 *     public ResponseEntity<byte[]> reporteCoincidencias(@RequestParam int mes, @RequestParam int anio) {
 *         try {
 *             List<Puesto> puestos = puestoService.findByMesYAnio(mes, anio);
 *             List<List<CandidatoResult>> cands = new ArrayList<>();
 *             for (Puesto p : puestos) cands.add(puestoService.buscarCandidatos(p));
 *             byte[] pdf = reporteService.reporteCoincidencias(puestos, cands);
 *             return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
 *                 .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=coincidencias_" + mes + "_" + anio + ".pdf")
 *                 .body(pdf);
 *         } catch (Exception e) { return ResponseEntity.internalServerError().build(); }
 *     }
 * }
 */
