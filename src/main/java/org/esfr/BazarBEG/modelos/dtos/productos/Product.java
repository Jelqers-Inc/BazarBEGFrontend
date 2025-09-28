package org.esfr.BazarBEG.modelos.dtos.productos;

import org.esfr.BazarBEG.modelos.Categoria;
import org.esfr.BazarBEG.modelos.dtos.categorias.Categoriadto;

// DTO para mostrar la información completa del Producto (incluyendo el DTO de Categoría)
public class Product {
    private Integer id;
    private String nombre;
    private String descripcion;
    private float precio;
    private int stock;
    private String imagen;
    private int status;
    private Categoriadto categoria; // Usamos el DTO de Categoría

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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Categoriadto getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = new Categoriadto();
    }


}
