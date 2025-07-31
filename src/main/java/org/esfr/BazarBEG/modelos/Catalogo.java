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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getArchivoPDF() {
        return archivoPDF;
    }

    public void setArchivoPDF(String archivoPDF) {
        this.archivoPDF = archivoPDF;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
