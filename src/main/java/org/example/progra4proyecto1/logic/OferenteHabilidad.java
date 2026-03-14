package org.example.progra4proyecto1.logic;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "oferente_habilidad")
public class OferenteHabilidad {
    @EmbeddedId
    private OferenteHabilidadId id = new OferenteHabilidadId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("oferenteId")
    @JoinColumn(name = "oferente_id")
    private Oferente oferente;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("caracteristicaId")
    @JoinColumn(name = "caracteristica_id")
    private Caracteristica caracteristica;

    @Column(nullable = false)
    private Integer nivel;

    @Embeddable
    @Data
    public static class OferenteHabilidadId implements java.io.Serializable {
        @Column(name = "oferente_id") private Integer oferenteId;
        @Column(name = "caracteristica_id") private Integer caracteristicaId;
    }
}