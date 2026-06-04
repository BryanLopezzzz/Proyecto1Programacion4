package org.example.progra4proyecto1.service;

import org.example.progra4proyecto1.data.*;
import org.example.progra4proyecto1.logic.*;
import org.example.progra4proyecto1.logic.CandidatoResult.DetalleCoincidencia;
import org.example.progra4proyecto1.logic.CandidatoResult.DetalleCoincidencia.EstadoCoincidencia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("puestoService")
public class PuestoService {

    @Autowired private PuestoRepository       puesRepo;
    @Autowired private OferenteRepository     ofeRepo;
    @Autowired private OferenteHabilidadRepository habiRepo;
    @Autowired private CaracteristicaRepository    cararepo;
    @Autowired private MonedaRepository        moneRepo;

    private static final double FACTOR_PENALIZACION = 0.30;

    public List<CandidatoResult> buscarCandidatos(Puesto puesto) {

        List<PuestoCaracteristica> requerimientos = puesto.getCaracteristicas();
        List<CandidatoResult> resultados = new ArrayList<>();

        if (requerimientos == null || requerimientos.isEmpty()) {
            ofeRepo.findAll().forEach(oferente -> {
                if (oferente.getUsuario().getEstado() != Usuario.Estado.APROBADO) return;
                resultados.add(new CandidatoResult(
                        oferente, 0.0, 0.0, 0, 0, 0, List.of()
                ));
            });
            resultados.sort(Comparator.comparing(c -> c.getOferente().getNombre()));
            return resultados;
        }

        double sumaWeights = requerimientos.stream()
                .mapToDouble(PuestoCaracteristica::getNivelRequerido)
                .sum();

        ofeRepo.findAll().forEach(oferente -> {

            if (oferente.getUsuario().getEstado() != Usuario.Estado.APROBADO) return;

            List<OferenteHabilidad> habilidades = habiRepo.findByOferente(oferente);

            Map<Integer, Integer> mapaHabilidades = new HashMap<>();
            habilidades.forEach(h ->
                    mapaHabilidades.put(h.getCaracteristica().getId(), h.getNivel()));

            double scoreTotal          = 0.0;
            int    cumpleCompletos     = 0;
            int    nivelExcedenteTotal = 0;
            List<DetalleCoincidencia> coincidencias = new ArrayList<>();

            for (PuestoCaracteristica req : requerimientos) {

                int     nivelReq = req.getNivelRequerido();
                double  peso     = nivelReq / sumaWeights;
                Integer nivelOfe = mapaHabilidades.get(req.getCaracteristica().getId());

                double aporte;
                EstadoCoincidencia estado;

                if (nivelOfe == null) {
                    aporte = 0.0;
                    estado = EstadoCoincidencia.AUSENTE;
                } else if (nivelOfe >= nivelReq) {
                    aporte = peso * 1.0;
                    estado = EstadoCoincidencia.CUMPLE;
                    cumpleCompletos++;
                    nivelExcedenteTotal += (nivelOfe - nivelReq);
                } else {
                    int    brecha = nivelReq - nivelOfe;
                    double factor = Math.max(0.0, 1.0 - brecha * FACTOR_PENALIZACION);
                    aporte = peso * factor;
                    estado = (aporte > 0) ? EstadoCoincidencia.PARCIAL
                            : EstadoCoincidencia.INSUFICIENTE;
                }

                scoreTotal += aporte;

                coincidencias.add(new DetalleCoincidencia(
                        req.getCaracteristica().getNombre(),
                        nivelReq, nivelOfe, peso, aporte, estado
                ));
            }

            double porcentajeCumplidos = (double) cumpleCompletos / requerimientos.size();

            resultados.add(new CandidatoResult(
                    oferente, scoreTotal, porcentajeCumplidos,
                    nivelExcedenteTotal, requerimientos.size(),
                    cumpleCompletos, coincidencias
            ));
        });

        resultados.sort(
                Comparator
                        .comparingDouble(CandidatoResult::getScoreTotal).reversed()
                        .thenComparingDouble(CandidatoResult::getPorcentajeCumplidos).reversed()
                        .thenComparingInt(CandidatoResult::getNivelExcedenteTotal).reversed()
                        .thenComparing(c -> c.getOferente().getNombre())
        );

        return resultados;
    }

    @Transactional
    public void publicar(Puesto puesto, Empresa empresa, List<Integer> caraID,
                         List<Integer> niveles, Integer moneID) {
        if (puesRepo.existsByEmpresaAndDescripcionIgnoreCaseAndActivoTrue(empresa, puesto.getDescripcion()))
            throw new IllegalArgumentException("Ya existe un puesto activo con esa descripción");

        Moneda moneda = moneRepo.findById(moneID)
                .orElseThrow(() -> new IllegalArgumentException("Moneda no válida"));
        puesto.setEmpresa(empresa);
        puesto.setMoneda(moneda);
        puesto.setActivo(true);
        puesto.setFechaRegistro(java.time.LocalDateTime.now());
        Puesto saved = puesRepo.save(puesto);

        if (caraID != null) {
            List<PuestoCaracteristica> reqs = new ArrayList<>();
            for (int i = 0; i < caraID.size(); i++) {
                Caracteristica c = cararepo.findById(caraID.get(i)).orElseThrow();
                PuestoCaracteristica pc  = new PuestoCaracteristica();
                PuestoCaracteristica.PuestoCaracteristicaId pcId =
                        new PuestoCaracteristica.PuestoCaracteristicaId();
                pcId.setPuestoId(saved.getId());
                pcId.setCaracteristicaId(c.getId());
                pc.setId(pcId);
                pc.setPuesto(saved);
                pc.setCaracteristica(c);
                pc.setNivelRequerido(niveles.get(i));
                reqs.add(pc);
            }
            saved.setCaracteristicas(reqs);
            puesRepo.save(saved);
        }
    }

    public void desactivar(Integer id) {
        puesRepo.findById(id).ifPresent(p -> {
            p.setActivo(false);
            puesRepo.save(p);
        });
    }

    public List<Puesto> buscarPublicos(List<Integer> ids, boolean modoTodos, Integer monedaId) {
        List<Puesto> todos = puesRepo
                .findByTipoAndActivoTrueOrderByFechaRegistroDesc(Puesto.TipoPuesto.PUBLICO);
        if (monedaId != null)
            todos = todos.stream()
                    .filter(p -> p.getMoneda().getId().equals(monedaId))
                    .collect(Collectors.toList());
        if (ids == null || ids.isEmpty()) return todos;
        return filtrar(todos, ids, modoTodos);
    }

    public List<Puesto> buscarTodos(List<Integer> ids, boolean modoTodos) {
        List<Puesto> todos = puesRepo.findByActivoTrueOrderByFechaRegistroDesc();
        if (ids == null || ids.isEmpty()) return todos;
        return filtrar(todos, ids, modoTodos);
    }

    private List<Puesto> filtrar(List<Puesto> puestos, List<Integer> ids, boolean modoTodos) {
        return puestos.stream().filter(p -> {
            Set<Integer> set = p.getCaracteristicas().stream()
                    .map(pc -> pc.getCaracteristica().getId())
                    .collect(Collectors.toSet());
            return modoTodos ? set.containsAll(ids) : ids.stream().anyMatch(set::contains);
        }).collect(Collectors.toList());
    }

    public List<Puesto> findByEmpresa(Empresa empresa) {
        return puesRepo.findByEmpresaOrderByFechaRegistroDesc(empresa);
    }

    public Optional<Puesto> findById(Integer id) {
        return puesRepo.findById(id);
    }

    public List<Puesto> findTop5Publicos() {
        return puesRepo.findTop5ByTipoAndActivoTrueOrderByFechaRegistroDesc(Puesto.TipoPuesto.PUBLICO);
    }

    public List<Puesto> findByMesYAnio(int mes, int anio) {
        return puesRepo.findByActivoTrueOrderByFechaRegistroDesc().stream()
                .filter(p -> p.getFechaRegistro().getMonthValue() == mes
                        && p.getFechaRegistro().getYear()       == anio)
                .collect(Collectors.toList());
    }

    public List<PuestoResult> buscarPuestosConScore(Oferente oferente,
                                                    List<Integer> caracteristicaIds,
                                                    boolean modoTodos) {

        Map<Integer, Integer> mapaHabilidades = new HashMap<>();
        habiRepo.findByOferente(oferente)
                .forEach(h -> mapaHabilidades.put(h.getCaracteristica().getId(), h.getNivel()));

        List<Puesto> puestos = puesRepo.findByActivoTrueOrderByFechaRegistroDesc();

        if (caracteristicaIds != null && !caracteristicaIds.isEmpty()) {
            puestos = filtrar(puestos, caracteristicaIds, modoTodos);
        }

        List<PuestoResult> resultados = new ArrayList<>();

        for (Puesto puesto : puestos) {
            List<PuestoCaracteristica> reqs = puesto.getCaracteristicas();
            if (reqs == null) reqs = new ArrayList<>();

            double sumaWeights = reqs.stream()
                    .mapToDouble(PuestoCaracteristica::getNivelRequerido)
                    .sum();

            double scoreTotal          = 0.0;
            int    cumpleCompletos     = 0;
            int    nivelExcedenteTotal = 0;
            List<PuestoResult.DetalleCoincidencia> coincidencias = new ArrayList<>();

            for (PuestoCaracteristica req : reqs) {
                int     nivelReq = req.getNivelRequerido();
                double  peso     = (sumaWeights > 0) ? nivelReq / sumaWeights : 0.0;
                Integer nivelOfe = mapaHabilidades.get(req.getCaracteristica().getId());

                double aporte;
                PuestoResult.DetalleCoincidencia.EstadoCoincidencia estado;

                if (nivelOfe == null) {
                    aporte = 0.0;
                    estado = PuestoResult.DetalleCoincidencia.EstadoCoincidencia.AUSENTE;
                } else if (nivelOfe >= nivelReq) {
                    aporte = peso * 1.0;
                    estado = PuestoResult.DetalleCoincidencia.EstadoCoincidencia.CUMPLE;
                    cumpleCompletos++;
                    nivelExcedenteTotal += (nivelOfe - nivelReq);
                } else {
                    int    brecha = nivelReq - nivelOfe;
                    double factor = Math.max(0.0, 1.0 - brecha * FACTOR_PENALIZACION);
                    aporte = peso * factor;
                    estado = (aporte > 0)
                            ? PuestoResult.DetalleCoincidencia.EstadoCoincidencia.PARCIAL
                            : PuestoResult.DetalleCoincidencia.EstadoCoincidencia.INSUFICIENTE;
                }

                scoreTotal += aporte;
                coincidencias.add(new PuestoResult.DetalleCoincidencia(
                        req.getCaracteristica().getNombre(),
                        nivelReq, nivelOfe, peso, aporte, estado));
            }

            double porcentajeCumplidos = reqs.isEmpty()
                    ? 0.0 : (double) cumpleCompletos / reqs.size();

            resultados.add(new PuestoResult(
                    puesto, scoreTotal, porcentajeCumplidos,
                    nivelExcedenteTotal, reqs.size(), cumpleCompletos, coincidencias));
        }

        resultados.sort(
                Comparator.comparingDouble(PuestoResult::getScoreTotal).reversed()
                        .thenComparingDouble(PuestoResult::getPorcentajeCumplidos).reversed()
                        .thenComparingInt(PuestoResult::getNivelExcedenteTotal).reversed()
                        .thenComparing(r -> r.getPuesto().getDescripcion())
        );

        return resultados;
    }
}