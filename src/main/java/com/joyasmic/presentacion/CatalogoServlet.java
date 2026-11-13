package com.joyasmic.presentacion;

import com.joyasmic.negocio.ErrorInternoException;
import com.joyasmic.negocio.ProductoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "CatalogoServlet", urlPatterns = "/catalogo")
public class CatalogoServlet extends HttpServlet {

    private final ProductoService productos = new ProductoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String categoria = req.getParameter("categoria");
        if (!ProductoService.esCategoriaValida(categoria)) {
            categoria = ProductoService.TODOS;
        }
        try {
            req.setAttribute("productos", productos.listar(categoria));
        } catch (ErrorInternoException e) {
            log("Error leyendo el catálogo", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo cargar el catálogo. Revisa que la base de datos esté encendida.");
            return;
        }
        req.setAttribute("categoriaActual", categoria);
        req.setAttribute("pestanas", ProductoService.PESTANAS);
        req.getRequestDispatcher("/WEB-INF/jsp/catalogo.jsp").forward(req, resp);
    }
}
