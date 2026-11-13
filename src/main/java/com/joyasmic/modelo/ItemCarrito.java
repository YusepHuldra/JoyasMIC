package com.joyasmic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

public class ItemCarrito implements Serializable {

    private final int productoId;
    private final String nombre;
    private final BigDecimal precio;
    private int cantidad;

    public ItemCarrito(int productoId, String nombre, BigDecimal precio, int cantidad) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public int getProductoId() { return productoId; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public BigDecimal getSubtotal() {
        return precio.multiply(BigDecimal.valueOf(cantidad));
    }
}
