package org.esfr.BazarBEG.modelos.dtos.usuarios;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserUpdate {
    @JsonIgnore
    private  Integer id;

    private String nombre;

    private String apellido;

    private String email;

    private Integer rolId;

    private byte[] foto;

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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getRolId() {
        return rolId;
    }

    public void setRolId(Integer rolId) {
        this.rolId = rolId;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }
}
