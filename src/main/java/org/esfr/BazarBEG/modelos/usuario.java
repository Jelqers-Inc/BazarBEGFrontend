package org.esfr.BazarBEG.modelos;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    private String email; //Hay que validar como Email
    private String contraseña;
    private String rol;

    @Temporal(TemporalType.DATE)
    private Date fechaRegistro;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Pedido> pedidos;

    // Getters y Setters
}
