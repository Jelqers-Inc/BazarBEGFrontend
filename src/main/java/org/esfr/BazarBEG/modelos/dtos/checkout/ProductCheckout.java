package org.esfr.BazarBEG.modelos.dtos.checkout;

import java.math.BigDecimal;

public class ProductCheckout {
    private Integer id_producto;
    private Integer cantidad;
    private BigDecimal precio;

    public Integer getId_producto() {
        return id_producto;
    }

    public void setId_producto(Integer id_producto) {
        this.id_producto = id_producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}
