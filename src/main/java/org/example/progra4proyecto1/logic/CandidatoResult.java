package org.example.progra4proyecto1.logic;

import lombok.Data;
import java.util.List;

@Data
public class CandidatoResult {

    private Oferente oferente;

    // ── Métrica 1: score ponderado principal ──────────────────────────────────
    // scoreTotal ∈ [0,1]
    // peso_i = nivelRequerido_i / Σ(todos los niveles requeridos)
    // Si cumple:   aporte_i = peso_i × 1.0
    // Si parcial:  aporte_i = peso_i × max(0, 1 − brecha × 0.30)
    // Si ausente:  aporte_i = 0
    private double scoreTotal;

    // ── Métrica 2 (tie-breaker #1): % de requisitos cumplidos completamente ──
    // porcentajeCumplidos ∈ [0,1]
    // Diferencia entre alguien que cumple 3/5 exacto vs 2/5 exacto + parciales
    private double porcentajeCumplidos;

    // ── Métrica 3 (tie-breaker #2): suma de niveles excedentes ───────────────
    // Si nivelOferente > nivelRequerido → excedente += (nivelOferente - nivelRequerido)
    // Desempata a favor de quien tiene más experiencia extra
    private int nivelExcedenteTotal;

    // ── Desglose por característica (el "qué coincidió") ─────────────────────
    private List<DetalleCoincidencia> coincidencias;

    // ── Campos heredados (compatibilidad con código existente) ────────────────
    private int requisitosTotal;
    private int requisitosCumplidos;

    // Porcentaje legible para la UI (scoreTotal × 100)
    public double getPorcentajeCoincidencia() {
        return scoreTotal * 100.0;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Constructor completo
    // ─────────────────────────────────────────────────────────────────────────
    public CandidatoResult(Oferente oferente,
                           double scoreTotal,
                           double porcentajeCumplidos,
                           int nivelExcedenteTotal,
                           int requisitosTotal,
                           int requisitosCumplidos,
                           List<DetalleCoincidencia> coincidencias) {
        this.oferente             = oferente;
        this.scoreTotal           = scoreTotal;
        this.porcentajeCumplidos  = porcentajeCumplidos;
        this.nivelExcedenteTotal  = nivelExcedenteTotal;
        this.requisitosTotal      = requisitosTotal;
        this.requisitosCumplidos  = requisitosCumplidos;
        this.coincidencias        = coincidencias;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Clase interna: detalle de cada característica ("qué coincidió")
    // ─────────────────────────────────────────────────────────────────────────
    @Data
    public static class DetalleCoincidencia {

        private String nombreCaracteristica;
        private int    nivelRequerido;
        private Integer nivelOferente;   // null = no tiene la habilidad
        private double peso;             // peso_i = nivelReq / sumaTotal
        private double aporte;           // contribución al scoreTotal
        private EstadoCoincidencia estado;

        public enum EstadoCoincidencia {
            CUMPLE,        // nivelOferente >= nivelRequerido
            PARCIAL,       // nivelOferente < nivelRequerido pero aporte > 0
            INSUFICIENTE,  // brecha tan grande que aporte = 0
            AUSENTE        // el oferente no tiene esa habilidad
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