package org.example.progra4proyecto1.service;

import org.example.progra4proyecto1.data.*;
import org.example.progra4proyecto1.logic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;


@Service("puestoService")
public class PuestoService {
    
    @Autowired private PuestoRepository puesRepo;
    @Autowired private OferenteRepository ofeRepo;
    @Autowired private OferenteHabilidadRepository habiRepo;
    @Autowired private CaracteristicaRepository cararepo;
    @Autowired private MonedaRepository moneRepo;

    @Transactional
    public void publicar(Puesto puesto, Empresa empresa, List<Integer> caraID, List<Integer> niveles, Integer moneID) {
        if (puesRepo.existsByEmpresaAndDescripcionIgnoreCaseAndActivoTrue(empresa, puesto.getDescripcion()))
            throw new IllegalArgumentException("Ya existe un puesto activo con esa descripción");
        Moneda moneda = moneRepo.findById(moneID).orElseThrow(() -> new IllegalArgumentException("Moneda no válida"));
        puesto.setEmpresa(empresa);
        puesto.setMoneda(moneda);
        puesto.setActivo(true);
        puesto.setFechaRegistro(java.time.LocalDateTime.now());
        Puesto saved = puesRepo.save(puesto);
        if (caraID != null) {
            List<PuestoCaracteristica> reqs = new ArrayList<>();
            for (int i = 0; i < caraID.size(); i++) {
                Caracteristica c = cararepo.findById(caraID.get(i)).orElseThrow();
                PuestoCaracteristica pc = new PuestoCaracteristica();
                PuestoCaracteristica.PuestoCaracteristicaId pcId = new PuestoCaracteristica.PuestoCaracteristicaId();
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
            puesRepo.save(p); });
    }

    public List<Puesto> buscarPublicos(List<Integer> ids, boolean modoTodos, Integer monedaId) {
        List<Puesto> todos = puesRepo.findByTipoAndActivoTrueOrderByFechaRegistroDesc(Puesto.TipoPuesto.PUBLICO);
        if (monedaId != null) {
            todos = todos.stream().filter(p -> p.getMoneda().getId().equals(monedaId)).collect(Collectors.toList());
        }
        if (ids == null || ids.isEmpty()) return todos;
        return filtrar(todos, ids, modoTodos);
    }

    public List<Puesto> buscarTodos(List<Integer> ids, boolean modoTodos) {
        // List<Puesto> todos = new ArrayList<>();
        // puesRepo.findAll().forEach(todos::add);
        List<Puesto> todos = puesRepo.findByActivoTrueOrderByFechaRegistroDesc();
        if (ids == null || ids.isEmpty()) return todos;
        return filtrar(todos, ids, modoTodos);
    }

    private List<Puesto> filtrar(List<Puesto> puestos, List<Integer> ids, boolean modoTodos) {
        return puestos.stream().filter(p -> {
            Set<Integer> set = p.getCaracteristicas().stream().map(pc -> pc.getCaracteristica().getId()).collect(Collectors.toSet());
            return modoTodos ? set.containsAll(ids) : ids.stream().anyMatch(set::contains); }).collect(Collectors.toList());
    }

    public List<CandidatoResult> buscarCandidatos(Puesto puesto) {
        List<CandidatoResult> resu = new ArrayList<>();
        List<PuestoCaracteristica> reqs = puesto.getCaracteristicas();

        if (reqs == null || reqs.isEmpty()) return resu;

        // Puntaje máximo posible = suma de todos los niveles requeridos al cuadrado
        // Usamos nivel^2 para dar más peso a niveles altos
        double puntajeMaxGlobal = reqs.stream()
                .mapToDouble(r -> Math.pow(r.getNivelRequerido(), 2))
                .sum();

        ofeRepo.findAll().forEach(oferente -> {
            if (oferente.getUsuario().getEstado() != Usuario.Estado.APROBADO) return;

            List<OferenteHabilidad> habs = habiRepo.findByOferente(oferente);
            int cumplidos = 0;
            double puntajeObtenido = 0;
            List<CandidatoResult.DetalleRequisito> detalle = new ArrayList<>();

            for (PuestoCaracteristica req : reqs) {
                int nivelReq = req.getNivelRequerido();
                double puntajeMaxReq = Math.pow(nivelReq, 2);

                // Buscar si el oferente tiene esta habilidad
                OferenteHabilidad habEncontrada = habs.stream()
                        .filter(h -> h.getCaracteristica().getId()
                                .equals(req.getCaracteristica().getId()))
                        .findFirst()
                        .orElse(null);

                int nivelOferente = habEncontrada != null ? habEncontrada.getNivel() : 0;
                boolean cumple    = nivelOferente >= nivelReq;

                // Puntaje obtenido:
                // - Si no tiene la habilidad: 0
                // - Si tiene pero menor al requerido: puntaje parcial proporcional
                // - Si cumple exacto o supera: puntaje completo del requisito
                double puntajeReq;
                if (nivelOferente == 0) {
                    puntajeReq = 0;
                } else if (cumple) {
                    // Cumple: puntaje completo. Si supera, pequeño bonus (máx 10%)
                    double bonus = Math.min((nivelOferente - nivelReq) * 0.05, 0.10);
                    puntajeReq = puntajeMaxReq * (1.0 + bonus);
                } else {
                    // No cumple pero tiene algo: puntaje parcial
                    // nivel 1 de 3 requerido = (1/3)^2 * puntajeMax → penaliza más la brecha grande
                    puntajeReq = puntajeMaxReq * Math.pow((double) nivelOferente / nivelReq, 2);
                }

                puntajeObtenido += puntajeReq;
                if (cumple) cumplidos++;

                detalle.add(new CandidatoResult.DetalleRequisito(
                        req.getCaracteristica().getNombre(),
                        nivelReq,
                        nivelOferente,
                        cumple,
                        puntajeReq,
                        puntajeMaxReq
                ));
            }

            double pctBinario    = (double) cumplidos / reqs.size() * 100.0;
            double pctPonderado  = (puntajeObtenido / puntajeMaxGlobal) * 100.0;

            resu.add(new CandidatoResult(
                    oferente,
                    reqs.size(),
                    cumplidos,
                    pctBinario,
                    puntajeObtenido,
                    puntajeMaxGlobal,
                    pctPonderado,
                    detalle
            ));
        });

        // Ordenar por puntaje ponderado de mayor a menor
        resu.sort((a, b) -> Double.compare(b.getPorcentajePonderado(),
                a.getPorcentajePonderado()));
        return resu;
    }

    public List<Puesto> findByEmpresa(Empresa empresa) { return puesRepo.findByEmpresaOrderByFechaRegistroDesc(empresa); }

    public Optional<Puesto> findById(Integer id) { return puesRepo.findById(id);
    }

    public List<Puesto> findTop5Publicos() {
        return puesRepo.findTop5ByTipoAndActivoTrueOrderByFechaRegistroDesc(Puesto.TipoPuesto.PUBLICO);
    }

    public List<Puesto> findByMesYAnio(int mes, int anio) {
        return puesRepo.findByActivoTrueOrderByFechaRegistroDesc().stream().
                filter(p -> p.getFechaRegistro().getMonthValue() == mes
                        && p.getFechaRegistro().getYear() == anio).collect(Collectors.toList());
    }}
