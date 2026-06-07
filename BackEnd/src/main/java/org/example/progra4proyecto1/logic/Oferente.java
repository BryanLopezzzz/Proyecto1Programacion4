package org.example.progra4proyecto1.logic;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "oferente")
public class Oferente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @NotBlank(message = "La identificación es requerida")
    @Column(nullable = false, unique = true)
    private String identificacion;

    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @NotBlank(message = "El primer apellido es requerido")
    @Column(name = "primer_apellido")
    private String primerApellido;

    @NotBlank(message = "La nacionalidad es requerida")
    private String nacionalidad;

    @NotBlank(message = "El teléfono es requerido")
    private String telefono;

    @NotBlank(message = "La residencia es requerida")
    private String residencia;

    @Column(name = "curriculum_pdf")
    private String curriculumPdf;

    @OneToMany(mappedBy = "oferente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OferenteHabilidad> habilidades;
}