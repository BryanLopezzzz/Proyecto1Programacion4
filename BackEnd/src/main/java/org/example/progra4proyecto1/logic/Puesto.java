package org.example.progra4proyecto1.logic;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "puesto")
public class Puesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @NotBlank(message = "La descripción es requerida")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "El salario es requerido")
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false)
    private BigDecimal salario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "moneda_id", nullable = false)
    private Moneda moneda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPuesto tipo;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @OneToMany(mappedBy = "puesto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PuestoCaracteristica> caracteristicas;

    public enum TipoPuesto { PUBLICO, PRIVADO }
}
