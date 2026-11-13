package com.joyasmic.presentacion;

import com.joyasmic.modelo.Carrito;
import com.joyasmic.modelo.Usuario;
import com.joyasmic.negocio.ErrorInternoException;
import com.joyasmic.negocio.UsuarioService;
import com.joyasmic.negocio.ValidacionException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/** POST /auth/login · /auth/registro · /auth/logout */
@WebServlet(name = "AuthServlet", urlPatterns = "/auth/*")
public class AuthServlet extends HttpServlet {

    private final UsuarioService usuarios = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(Web.catalogo(req, null));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getPathInfo() == null ? "" : req.getPathInfo();
        HttpSession session = req.getSession();

        switch (accion) {
            case "/login" -> login(req, session);
            case "/registro" -> registro(req, session);
            case "/logout" -> session.invalidate();
            default -> {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }
        resp.sendRedirect(Web.catalogo(req, null));
    }

    private void login(HttpServletRequest req, HttpSession session) {
        try {
            Usuario u = usuarios.autenticar(req.getParameter("email"), req.getParameter("password"));
            iniciarSesion(req, session, u);
        } catch (ValidacionException e) {
            Web.error(session, e.getMessage());
            Web.abrirModal(session, "login");
        } catch (ErrorInternoException e) {
            log("Error en login", e);
            Web.error(session, "Ocurrió un error en el servidor. Intenta de nuevo.");
        }
    }

    private void registro(HttpServletRequest req, HttpSession session) {
        try {
            Usuario u = usuarios.registrar(req.getParameter("nombre"), req.getParameter("email"), req.getParameter("password"));
            iniciarSesion(req, session, u);
            Web.exito(session, "¡Registro exitoso! Ya puedes armar tu pedido.");
        } catch (ValidacionException e) {
            Web.error(session, e.getMessage());
            Web.abrirModal(session, "register");
        } catch (ErrorInternoException e) {
            log("Error en registro", e);
            Web.error(session, "Ocurrió un error en el servidor. Intenta de nuevo.");
        }
    }

    private void iniciarSesion(HttpServletRequest req, HttpSession session, Usuario u) {
        req.changeSessionId(); // previene fijación de sesión
        session.setAttribute("usuario", u);
        session.setAttribute("carrito", new Carrito());
    }
}
