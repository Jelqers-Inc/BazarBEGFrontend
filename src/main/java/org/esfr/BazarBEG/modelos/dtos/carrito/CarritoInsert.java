package org.esfr.BazarBEG.modelos.dtos.carrito;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CarritoInsert {
    @JsonProperty("id_usuario")
    private Integer idUsuario;
    @JsonProperty("id_producto")
    private Integer idProducto;
    @JsonProperty("cantidad")
    private Integer cantidad;

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
