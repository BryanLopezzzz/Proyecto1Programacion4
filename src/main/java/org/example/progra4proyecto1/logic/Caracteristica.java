package org.example.progra4proyecto1.logic;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "caracteristica")
public class Caracteristica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "padre_id")
    private Caracteristica padre;

    @OneToMany(mappedBy = "padre", fetch = FetchType.LAZY)
    private List<Caracteristica> hijos;

    public boolean isRaiz() { return padre == null; }
    public boolean isHoja() { return hijos == null || hijos.isEmpty(); }
}