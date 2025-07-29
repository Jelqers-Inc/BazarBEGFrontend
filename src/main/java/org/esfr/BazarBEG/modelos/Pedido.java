package org.esfr.BazarBEG.modelos;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Temporal(TemporalType.DATE)
    private Date fechaPedido;

    private String estado;
    private float total;

    @ManyToOne
    @JoinColumn(name = "usuarioid")
    private usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<DetallePedido> detalles;

    // Getters y Setters
}

