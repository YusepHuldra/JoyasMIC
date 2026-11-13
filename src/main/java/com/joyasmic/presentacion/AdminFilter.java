package com.joyasmic.presentacion;

import com.joyasmic.modelo.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/** Solo deja pasar a /admin/* a usuarios con rol ADMIN (la validación ya no está en el JavaScript). */
@WebFilter(urlPatterns = "/admin/*")
public class AdminFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        Usuario u = session == null ? null : (Usuario) session.getAttribute("usuario");

        if (u == null || !u.isAdmin()) {
            HttpSession nueva = req.getSession();
            Web.error(nueva, "Acceso restringido a administradores.");
            Web.abrirModal(nueva, "login");
            resp.sendRedirect(Web.catalogo(req, null));
            return;
        }
        chain.doFilter(req, resp);
    }
}
