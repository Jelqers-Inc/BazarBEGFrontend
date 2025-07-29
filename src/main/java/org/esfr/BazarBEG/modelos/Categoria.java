package org.esfr.BazarBEG.modelos;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;

    @Lob
    private byte[] imagen;

    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos;

    // Getters y Setters
}

