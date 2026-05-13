package org.example.progra4proyecto1.logic;

import lombok.Data;
import java.util.List;

/**
 * PuestoResult — espejo de CandidatoResult pero desde la perspectiva del OFERENTE.
 *
 * ─────────────────────────────────────────────────────────────────────────────
 * JUSTIFICACIÓN DE LA FÓRMULA (misma base que CandidatoResult, perspectiva inversa)
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * El enunciado dice: "La aplicación permitirá a unos y OTROS hacer búsquedas
 * diversas que muestren el grado de coincidencia entre lo solicitado y lo ofrecido."
 *
 * "Unos y otros" = empresa Y oferente.
 *
 * CandidatoResult responde: "¿Qué tan bien encaja este oferente en mi puesto?"
 * PuestoResult   responde:  "¿Qué tan bien encaja MI perfil en este puesto?"
 *
 * La fórmula es simétrica:
 *
 *   peso_i = nivelRequerido_i / Σ(todos los niveles requeridos del puesto)
 *
 *   aporte_i:
 *     Si tengo la habilidad y mi nivel >= nivelRequerido  → peso_i × 1.0
 *     Si tengo la habilidad y mi nivel <  nivelRequerido  → peso_i × max(0, 1 - brecha × 0.30)
 *     Si NO tengo la habilidad                           → 0
 *
 *   scoreTotal = Σ(aporte_i)  ∈ [0, 1]
 *
 * Por qué pesos y no conteo simple:
 *   Un puesto que exige Java nivel 5 y Git nivel 1 no puede ponderar igual
 *   ambas habilidades. Si el oferente tiene Git pero no Java, el score debe
 *   reflejar que falta lo más importante.
 *
 * Por qué penalización y no binario:
 *   "Al menos en el grado indicado" (enunciado) implica que niveles parciales
 *   tienen valor — un nivel 4 en Java cuando se pide 5 no es lo mismo que no
 *   tener Java. La penalización del 30 % por nivel de brecha captura eso.
 *
 * Tie-breakers (desempates):
 *   1. porcentajeCumplidos → quien cumple más reqs completos gana
 *   2. nivelExcedenteTotal → quien tiene más experiencia extra gana
 *   3. nombre del puesto   → estabilidad determinista
 */
@Data
public class PuestoResult {

    private Puesto puesto;

    // ── Métrica 1: score ponderado principal ──────────────────────────────────
    private double scoreTotal;          // [0, 1]

    // ── Métrica 2 (tie-breaker #1) ────────────────────────────────────────────
    private double porcentajeCumplidos; // [0, 1] — % de reqs que cumple exacto

    // ── Métrica 3 (tie-breaker #2) ────────────────────────────────────────────
    private int nivelExcedenteTotal;    // suma de niveles extra sobre el requerido

    // ── Conteo simple (compatibilidad UI) ────────────────────────────────────
    private int requisitosTotal;
    private int requisitosCumplidos;

    // ── Detalle: qué coincidió y qué no ──────────────────────────────────────
    private List<DetalleCoincidencia> coincidencias;

    /** Porcentaje legible: scoreTotal × 100 */
    public double getPorcentajeCoincidencia() {
        return scoreTotal * 100.0;
    }

    public PuestoResult(Puesto puesto,
                        double scoreTotal,
                        double porcentajeCumplidos,
                        int nivelExcedenteTotal,
                        int requisitosTotal,
                        int requisitosCumplidos,
                        List<DetalleCoincidencia> coincidencias) {
        this.puesto               = puesto;
        this.scoreTotal           = scoreTotal;
        this.porcentajeCumplidos  = porcentajeCumplidos;
        this.nivelExcedenteTotal  = nivelExcedenteTotal;
        this.requisitosTotal      = requisitosTotal;
        this.requisitosCumplidos  = requisitosCumplidos;
        this.coincidencias        = coincidencias;
    }

    // ── Clase interna: detalle por característica ─────────────────────────────
    @Data
    public static class DetalleCoincidencia {
        private String  nombreCaracteristica;
        private int     nivelRequerido;
        private Integer nivelOferente;   // null = el oferente no tiene esta habilidad
        private double  peso;
        private double  aporte;
        private EstadoCoincidencia estado;

        public enum EstadoCoincidencia {
            CUMPLE,        // nivelOferente >= nivelRequerido
            PARCIAL,       // nivelOferente <  nivelRequerido, aporte > 0
            INSUFICIENTE,  // brecha tan grande que aporte = 0
            AUSENTE        // el oferente no tiene la habilidad
        }

        public DetalleCoincidencia(String nombreCaracteristica,
                                   int nivelRequerido,
                                   Integer nivelOferente,
                                   double peso,
                                   double aporte,
                                   EstadoCoincidencia estado) {
            this.nombreCaracteristica = nombreCaracteristica;
            this.nivelRequerido       = nivelRequerido;
            this.nivelOferente        = nivelOferente;
            this.peso                 = peso;
            this.aporte               = aporte;
            this.estado               = estado;
        }
    }
}