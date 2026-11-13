package com.joyasmic.presentacion;

import com.joyasmic.negocio.ProductoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Utilidades de la capa de presentación: mensajes "flash" y redirecciones. */
final class Web {

    private Web() {}

    static void exito(HttpSession s, String mensaje) {
        s.setAttribute("flashTipo", "exito");
        s.setAttribute("flashTexto", mensaje);
    }

    static void error(HttpSession s, String mensaje) {
        s.setAttribute("flashTipo", "error");
        s.setAttribute("flashTexto", mensaje);
    }

    /** modo: "login" o "register". Hace que la página abra el modal al cargar. */
    static void abrirModal(HttpSession s, String modo) {
        s.setAttribute("abrirModal", modo);
    }

    static void abrirCarrito(HttpSession s) {
        s.setAttribute("abrirCarrito", Boolean.TRUE);
    }

    /** URL del catálogo (con filtro de categoría si es válido). */
    static String catalogo(HttpServletRequest req, String categoria) {
        String base = req.getContextPath() + "/catalogo";
        if (ProductoService.esCategoriaValida(categoria)) {
            return base + "?categoria=" + URLEncoder.encode(categoria, StandardCharsets.UTF_8);
        }
        return base;
    }
}
