package com.joyasmic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Carrito de compras que vive en la HttpSession del usuario. */
public class Carrito implements Serializable {

    private final Map<Integer, ItemCarrito> items = new LinkedHashMap<>();

    public synchronized void agregar(Producto p) {
        ItemCarrito existente = items.get(p.getId());
        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + 1);
        } else {
            items.put(p.getId(), new ItemCarrito(p.getId(), p.getNombre(), p.getPrecio(), 1));
        }
    }

    /** delta = +1 o -1. Si la cantidad llega a 0 el producto se quita. */
    public synchronized void cambiarCantidad(int productoId, int delta) {
        ItemCarrito item = items.get(productoId);
        if (item == null) return;
        int nueva = item.getCantidad() + delta;
        if (nueva <= 0) {
            items.remove(productoId);
        } else {
            item.setCantidad(nueva);
        }
    }

    public synchronized List<ItemCarrito> getItems() {
        return new ArrayList<>(items.values());
    }

    public synchronized BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCarrito i : items.values()) total = total.add(i.getSubtotal());
        return total;
    }

    public synchronized int getCantidadTotal() {
        int n = 0;
        for (ItemCarrito i : items.values()) n += i.getCantidad();
        return n;
    }

    public synchronized boolean isVacio() {
        return items.isEmpty();
    }
}
