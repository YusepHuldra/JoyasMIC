package com.joyasmic.modelo;

import java.io.Serializable;

public class Usuario implements Serializable {

    public static final String ROL_ADMIN = "ADMIN";
    public static final String ROL_CLIENTE = "CLIENTE";

    private int id;
    private String nombre;
    private String email;
    private String passwordHash;
    private String rol = ROL_CLIENTE;

    public Usuario() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    /** En EL se usa como ${usuario.admin} */
    public boolean isAdmin() { return ROL_ADMIN.equals(rol); }
}
