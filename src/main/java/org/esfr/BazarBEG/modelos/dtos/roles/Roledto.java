package org.esfr.BazarBEG.modelos.dtos.roles;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Roledto {
    // ...
    @JsonProperty("nombre")
    private String nombre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
