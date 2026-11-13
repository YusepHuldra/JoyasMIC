package com.joyasmic.presentacion;

import com.joyasmic.negocio.ErrorInternoException;
import com.joyasmic.negocio.ProductoService;
import com.joyasmic.negocio.ValidacionException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;

/** GET /admin (panel) · POST /admin/publicar · POST /admin/eliminar  (protegido por AdminFilter) */
@WebServlet(name = "AdminServlet", urlPatterns = "/admin/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class AdminServlet extends HttpServlet {

    private final ProductoService productos = new ProductoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("productos", productos.listar(ProductoService.TODOS));
        } catch (ErrorInternoException e) {
            log("Error leyendo productos", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        req.setAttribute("categorias", ProductoService.CATEGORIAS);
        req.getRequestDispatcher("/WEB-INF/jsp/admin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        String accion = req.getPathInfo() == null ? "" : req.getPathInfo();
        String destino = req.getContextPath() + "/admin";

        try {
            switch (accion) {
                case "/publicar" -> {
                    publicar(req);
                    Web.exito(session, "¡Pieza publicada con éxito!");
                }
                case "/eliminar" -> {
                    productos.eliminar(Integer.parseInt(req.getParameter("id")));
                    Web.exito(session, "Pieza eliminada del catálogo.");
                    if ("catalogo".equals(req.getParameter("volver"))) {
                        destino = Web.catalogo(req, req.getParameter("categoria"));
                    }
                }
                default -> {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            }
        } catch (ValidacionException e) {
            Web.error(session, e.getMessage());
        } catch (NumberFormatException e) {
            Web.error(session, "Producto no válido.");
        } catch (ErrorInternoException | ServletException | IllegalStateException e) {
            // IllegalStateException = el archivo superó el límite de tamaño del @MultipartConfig
            log("Error en el panel de administración", e);
            Web.error(session, "No se pudo completar la operación (revisa que la imagen pese menos de 5 MB).");
        }
        resp.sendRedirect(destino);
    }

    private void publicar(HttpServletRequest req) throws ValidacionException, IOException, ServletException {
        Part imagen = req.getPart("imagen");
        if (imagen == null) {
            throw new ValidacionException("Debes seleccionar una imagen del producto.");
        }
        try (InputStream in = imagen.getInputStream()) {
            productos.publicar(
                    req.getParameter("categoria"),
                    req.getParameter("nombre"),
                    req.getParameter("precio"),
                    req.getParameter("detalles"),
                    in, imagen.getSubmittedFileName(), imagen.getSize());
        }
    }
}
