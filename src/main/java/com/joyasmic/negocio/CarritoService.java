package com.joyasmic.negocio;

import com.joyasmic.modelo.Carrito;
import com.joyasmic.modelo.ItemCarrito;
import com.joyasmic.modelo.Producto;
import com.joyasmic.modelo.Usuario;
import com.joyasmic.util.Config;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CarritoService {

    private final ProductoService productos = new ProductoService();

    public void agregar(Carrito carrito, int productoId) throws ValidacionException {
        Producto p = productos.buscar(productoId)
                .orElseThrow(() -> new ValidacionException("Ese producto ya no está disponible."));
        carrito.agregar(p);
    }

    /** Arma el mismo mensaje que generaba tu JavaScript y devuelve la URL de wa.me. */
    public String urlWhatsapp(Usuario usuario, Carrito carrito) {
        StringBuilder msg = new StringBuilder();
        msg.append("Hola Joyas Mic, me llamo *").append(usuario.getNombre()).append("* (")
           .append(usuario.getEmail())
           .append(") y me gustaría consultar la disponibilidad de las siguientes piezas:\n\n");

        for (ItemCarrito it : carrito.getItems()) {
            msg.append("▪️ *").append(it.getCantidad()).append("x* ").append(it.getNombre())
               .append(" - ($").append(numero(it.getPrecio())).append(" c/u) -> *$")
               .append(numero(it.getSubtotal())).append("*\n");
        }
        msg.append("\n *Total Estimado:* *$").append(numero(carrito.getTotal()))
           .append("*\n\n¿Tienen disponibilidad para coordinar la compra? Gracias.");

        String texto = URLEncoder.encode(msg.toString(), StandardCharsets.UTF_8).replace("+", "%20");
        return "https://wa.me/" + Config.get("whatsapp.numero") + "?text=" + texto;
    }

    private static String numero(BigDecimal n) {
        return n.signum() == 0 ? "0" : n.stripTrailingZeros().toPlainString();
    }
}
