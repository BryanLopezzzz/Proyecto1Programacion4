package org.example.progra4proyecto1.logic;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "empresa")
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @NotBlank(message = "La localización es requerida")
    private String localizacion;

    @NotBlank(message = "El teléfono es requerido")
    private String telefono;

    @NotBlank(message = "La descripción es requerida")
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
    private List<Puesto> puestos;
}
