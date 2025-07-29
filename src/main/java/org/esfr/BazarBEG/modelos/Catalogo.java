package org.esfr.BazarBEG.modelos;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Catalogo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    private String archivoPDF;

    @Lob
    private byte[] imagen;

    @ManyToOne
    @JoinColumn(name = "categoriaid")
    private Categoria categoria;

    // Getters y Setters
}
