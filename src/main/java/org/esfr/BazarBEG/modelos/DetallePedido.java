package org.esfr.BazarBEG.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private int cantidad;

    @Min(value = 0, message = "El precio unitario no puede ser negativo")
    private float precioUnitario;

    // Coloca las anotaciones aquí, justo antes del campo.
    @ManyToOne
    @JoinColumn(name = "pedidoid", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "productoid")
    private Producto producto;

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(float precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
}