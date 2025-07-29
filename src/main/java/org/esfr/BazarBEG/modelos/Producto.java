package org.esfr.BazarBEG.modelos;

import jakarta.persistence.*;

@Entity
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    private String descripcion;
    private float precio;
    private int stock;

    @Lob
    private byte[] imagen;

    @ManyToOne
    @JoinColumn(name = "categoriaid")
    private Categoria categoria;

    // Getters y Setters
}


