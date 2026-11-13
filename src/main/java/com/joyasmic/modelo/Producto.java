package com.joyasmic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

public class Producto implements Serializable {

    private int id;
    private String categoria;
    private String nombre;
    private BigDecimal precio;
    private String detalles;
    private String imagen;

    public Producto() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public String getDetalles() { return detalles; }
    public void setDetalles(String detalles) { this.detalles = detalles; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
}
