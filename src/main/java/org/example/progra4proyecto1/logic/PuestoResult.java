package org.example.progra4proyecto1.logic;

import lombok.Data;
import java.util.List;

@Data
public class PuestoResult {

    private Puesto puesto;
    private double scoreTotal;
    private double porcentajeCumplidos;
    private int nivelExcedenteTotal;
    private int requisitosTotal;
    private int requisitosCumplidos;
    private List<DetalleCoincidencia> coincidencias;
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

    @Data
    public static class DetalleCoincidencia {
        private String  nombreCaracteristica;
        private int     nivelRequerido;
        private Integer nivelOferente;
        private double  peso;
        private double  aporte;
        private EstadoCoincidencia estado;

        public enum EstadoCoincidencia {
            CUMPLE,
            PARCIAL,
            INSUFICIENTE,
            AUSENTE
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