package org.example.progra4proyecto1.logic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class CandidatoResult {
    private Oferente oferente;
    private int requisitosTotal;
    private int requisitosCumplidos;
    private double porcentajeCoincidencia;  // % binario (cumplió o no)
    private double puntajePonderado;        // puntaje con pesos por nivel
    private double puntajeMaximoPosible;    // máximo que podría haber obtenido
    private double porcentajePonderado;     // puntajePonderado / puntajeMaximo * 100
    private List<DetalleRequisito> detalle; // qué cumplió y qué no

    @Data
    public static class DetalleRequisito {
        private String caracteristica;
        private int nivelRequerido;
        private int nivelOferente;   // 0 si no tiene la habilidad
        private boolean cumple;
        private double puntajeObtenido;
        private double puntajeMaximo;

        public DetalleRequisito(String caracteristica, int nivelRequerido,
                                int nivelOferente, boolean cumple,
                                double puntajeObtenido, double puntajeMaximo) {
            this.caracteristica   = caracteristica;
            this.nivelRequerido   = nivelRequerido;
            this.nivelOferente    = nivelOferente;
            this.cumple           = cumple;
            this.puntajeObtenido  = puntajeObtenido;
            this.puntajeMaximo    = puntajeMaximo;
        }
    }

    public CandidatoResult(Oferente oferente, int requisitosTotal,
                           int requisitosCumplidos, double porcentajeCoincidencia,
                           double puntajePonderado, double puntajeMaximoPosible,
                           double porcentajePonderado, List<DetalleRequisito> detalle) {
        this.oferente               = oferente;
        this.requisitosTotal        = requisitosTotal;
        this.requisitosCumplidos    = requisitosCumplidos;
        this.porcentajeCoincidencia = porcentajeCoincidencia;
        this.puntajePonderado       = puntajePonderado;
        this.puntajeMaximoPosible   = puntajeMaximoPosible;
        this.porcentajePonderado    = porcentajePonderado;
        this.detalle                = detalle;
    }
}