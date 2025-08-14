package org.esfr.BazarBEG.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

//Modelo categoria creado, para asociar commit

@Entity
@Table(name = "catalogos")

public class Catalogo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @Lob
    @NotNull(message = "El archivo PDF es obligatorio")
    private byte[] archivoPDF;

    @Lob
    private byte[] imagen;

    @ManyToOne
    @JoinColumn(name = "categoriaid")
    @NotNull(message = "La categoría es obligatoria")
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

    public byte[] getArchivoPDF() {
        return archivoPDF;
    }

    public void setArchivoPDF(byte[] archivoPDF) {
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
