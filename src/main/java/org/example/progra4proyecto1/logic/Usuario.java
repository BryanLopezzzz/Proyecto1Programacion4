package org.example.progra4proyecto1.logic;

import jakarta.persistence.*;
import lombok.Data;



@Data
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String correo;

    @Column(nullable = false)
    private String clave;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.PENDIENTE;

    public enum Rol { EMPRESA, OFERENTE, ADMIN }
    public enum Estado { PENDIENTE, APROBADO, RECHAZADO }
}
