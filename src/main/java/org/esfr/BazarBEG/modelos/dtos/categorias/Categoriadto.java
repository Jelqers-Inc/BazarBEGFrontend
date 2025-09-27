package org.esfr.BazarBEG.modelos.dtos.categorias;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable; // Opcional, pero buena práctica para DTOs

public class Categoriadto  {

    // El ID se usa para obtener, editar y eliminar
    private Integer id;

    @JsonProperty("nombre")
    private String nombre;

    // Nuevo campo según la API de Hoppscotch
    @JsonProperty("imagen")
    private String imagen;

    // --- Constructor vacío ---
    public Categoriadto() {
    }

    // --- Getters y Setters ---

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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}