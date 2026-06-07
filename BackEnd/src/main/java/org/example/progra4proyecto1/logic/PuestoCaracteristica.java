package org.example.progra4proyecto1.logic;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "puesto_caracteristica")
public class PuestoCaracteristica {
    @EmbeddedId
    private PuestoCaracteristicaId id = new PuestoCaracteristicaId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("puestoId")
    @JoinColumn(name = "puesto_id")
    private Puesto puesto;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("caracteristicaId")
    @JoinColumn(name = "caracteristica_id")
    private Caracteristica caracteristica;

    @Column(name = "nivel_requerido", nullable = false)
    private Integer nivelRequerido;

    @Embeddable
    @Data
    public static class PuestoCaracteristicaId implements java.io.Serializable {
        @Column(name = "puesto_id") private Integer puestoId;
        @Column(name = "caracteristica_id") private Integer caracteristicaId;
    }
}