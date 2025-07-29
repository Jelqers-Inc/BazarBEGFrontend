package org.esfr.BazarBEG.modelos;

import jakarta.persistence.*;

@Entity
public class DetallePedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int cantidad;
    private float precioUnitario;

    @ManyToOne
    @JoinColumn(name = "pedidoid")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "productoid")
    private Producto producto;

    // Getters y Setters
}
