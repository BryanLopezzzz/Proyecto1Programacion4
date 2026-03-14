package org.example.progra4proyecto1.logic;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CandidatoResult {
    private Oferente oferente;
    private int requisitosTotal;
    private int requisitosCumplidos;
    private double porcentajeCoincidencia;
}