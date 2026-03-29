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

    @Autowired private PuestoRepository puestoRepository;
    @Autowired private OferenteRepository oferenteRepository;
    @Autowired private OferenteHabilidadRepository habilidadRepository;
    @Autowired private CaracteristicaRepository caracteristicaRepository;
    @Autowired private MonedaRepository monedaRepository;

    @Transactional
    public void publicar(Puesto puesto, Empresa empresa,
                         List<Integer> caracteristicaIds, List<Integer> niveles, Integer monedaId) {
        //Se cambia debido a lo "nuevo" de PuestoRepositorio
        if (puestoRepository.existsByEmpresaAndDescripcionIgnoreCaseAndActivoTrue(empresa, puesto.getDescripcion()))
            throw new IllegalArgumentException("Ya existe un puesto activo con esa descripción");

        Moneda moneda = monedaRepository.findById(monedaId)
                .orElseThrow(() -> new IllegalArgumentException("Moneda no válida"));

        puesto.setEmpresa(empresa);
        puesto.setMoneda(moneda);
        puesto.setActivo(true);
        puesto.setFechaRegistro(java.time.LocalDateTime.now());
        Puesto saved = puestoRepository.save(puesto);

        if (caracteristicaIds != null) {
            List<PuestoCaracteristica> reqs = new ArrayList<>();
            for (int i = 0; i < caracteristicaIds.size(); i++) {
                Caracteristica c = caracteristicaRepository.findById(caracteristicaIds.get(i)).orElseThrow();
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
            puestoRepository.save(saved);
        }
    }

    public void desactivar(Integer id) {
        puestoRepository.findById(id).ifPresent(p -> {
            p.setActivo(false);
            puestoRepository.save(p);
        });
    }

    public List<Puesto> buscarPublicos(List<Integer> ids, boolean modoTodos, Integer monedaId) {
        List<Puesto> todos = puesRepo.findAllPublicosActivos();
        if (monedaId != null) {
            todos = todos.stream().filter(p -> p.getMoneda().getId().equals(monedaId)).collect(Collectors.toList());
        }
        if (ids == null || ids.isEmpty()) return todos;
        return filtrar(todos, ids, modoTodos);
    }

    public List<Puesto> buscarTodos(List<Integer> ids, boolean modoTodos) {
        List<Puesto> todos = puestoRepository.findAllActivos();
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
        ofeRepo.findAll().forEach(oferente -> {
            if (oferente.getUsuario().getEstado() != Usuario.Estado.APROBADO) return;
            List<PuestoCaracteristica> reqs = puesto.getCaracteristicas();
            List<OferenteHabilidad> habs = habiRepo.findByOferente(oferente);
            int cumplidos = 0;
            for (PuestoCaracteristica req : reqs) {
                if (habs.stream().anyMatch(h -> h.getCaracteristica().getId().equals(req.getCaracteristica().getId()) && h.getNivel() >= req.getNivelRequerido())) cumplidos++;
            }
            if (!reqs.isEmpty()) {
                double pct = (double) cumplidos / reqs.size() * 100.0;
                resultados.add(new CandidatoResult(oferente, reqs.size(), cumplidos, pct));
            }
        });
        resultados.sort((a, b) -> Double.compare(b.getPorcentajeCoincidencia(), a.getPorcentajeCoincidencia()));
        return resultados;
    }

    public List<Puesto> findByEmpresa(Empresa empresa) { return puesRepo.findByEmpresaOrderByFechaRegistroDesc(empresa); }

    public Optional<Puesto> findById(Integer id) { return puesRepo.findById(id);
    }

    public List<Puesto> findTop5Publicos() {
        return puestoRepository.findTop5ByTipoAndActivoTrueOrderByFechaRegistroDesc(Puesto.TipoPuesto.PUBLICO);
    }
    public List<Puesto> findByMesYAnio(int mes, int anio) { return puestoRepository.findByMesYAnio(mes, anio); }
}
