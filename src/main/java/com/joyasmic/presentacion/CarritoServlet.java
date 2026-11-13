package com.joyasmic.presentacion;

import com.joyasmic.modelo.Carrito;
import com.joyasmic.modelo.Usuario;
import com.joyasmic.negocio.CarritoService;
import com.joyasmic.negocio.ErrorInternoException;
import com.joyasmic.negocio.ValidacionException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/** POST /carrito/agregar · /carrito/cambiar   —   GET /carrito/whatsapp */
@WebServlet(name = "CarritoServlet", urlPatterns = "/carrito/*")
public class CarritoServlet extends HttpServlet {

    private final CarritoService service = new CarritoService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        String categoria = req.getParameter("categoria");

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            pedirLogin(session);
            resp.sendRedirect(Web.catalogo(req, categoria));
            return;
        }
        Carrito carrito = carritoDe(session);
        String accion = req.getPathInfo() == null ? "" : req.getPathInfo();

        try {
            int id = Integer.parseInt(req.getParameter("id"));
            switch (accion) {
                case "/agregar" -> {
                    service.agregar(carrito, id);
                    Web.abrirCarrito(session);
                }
                case "/cambiar" -> {
                    int delta = "1".equals(req.getParameter("delta")) ? 1 : -1;
                    carrito.cambiarCantidad(id, delta);
                    Web.abrirCarrito(session);
                }
                default -> {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            }
        } catch (NumberFormatException e) {
            Web.error(session, "Producto no válido.");
        } catch (ValidacionException e) {
            Web.error(session, e.getMessage());
        } catch (ErrorInternoException e) {
            log("Error en carrito", e);
            Web.error(session, "Ocurrió un error en el servidor. Intenta de nuevo.");
        }
        resp.sendRedirect(Web.catalogo(req, categoria));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!"/whatsapp".equals(req.getPathInfo())) {
            resp.sendRedirect(Web.catalogo(req, null));
            return;
        }
        if (usuario == null) {
            pedirLogin(session);
            resp.sendRedirect(Web.catalogo(req, null));
            return;
        }
        Carrito carrito = carritoDe(session);
        if (carrito.isVacio()) {
            Web.error(session, "Tu carrito está vacío.");
            resp.sendRedirect(Web.catalogo(req, null));
            return;
        }
        resp.sendRedirect(service.urlWhatsapp(usuario, carrito));
    }

    private static Carrito carritoDe(HttpSession session) {
        Carrito c = (Carrito) session.getAttribute("carrito");
        if (c == null) {
            c = new Carrito();
            session.setAttribute("carrito", c);
        }
        return c;
    }

    private static void pedirLogin(HttpSession session) {
        Web.error(session, "Inicia sesión para usar el carrito.");
        Web.abrirModal(session, "login");
    }
}
