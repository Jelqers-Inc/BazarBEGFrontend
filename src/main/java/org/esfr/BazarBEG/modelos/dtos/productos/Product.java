package org.esfr.BazarBEG.modelos.dtos.productos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.esfr.BazarBEG.modelos.Categoria;
import org.esfr.BazarBEG.modelos.dtos.categorias.Categoriadto;

public class Product {
    private Integer id;
    private String nombre;
    private String descripcion;
    private float precio;
    private int stock;
    @JsonProperty("categoria_id")
    private Integer categoriaId;
    private Integer status;
    private byte[] imagen;


    // Getters y Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }
}
