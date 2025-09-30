package org.esfr.BazarBEG.modelos.dtos.checkout;

import java.util.List;

public class CheckoutDto {
    private Integer id_usuario;
    private List<ProductCheckout> productos;

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public List<ProductCheckout> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductCheckout> productos) {
        this.productos = productos;
    }
}
