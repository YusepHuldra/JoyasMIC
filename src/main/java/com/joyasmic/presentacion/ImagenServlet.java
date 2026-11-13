package com.joyasmic.presentacion;

import com.joyasmic.negocio.AlmacenImagenes;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * GET /imagenes/{archivo}: sirve primero lo subido por el admin (carpeta externa)
 * y, si no existe, las imágenes que vienen con la app en /img (esclava.jpg, anillo.jpg, collar.jpg).
 */
@WebServlet(name = "ImagenServlet", urlPatterns = "/imagenes/*")
public class ImagenServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String info = req.getPathInfo();
        if (info == null || info.length() < 2) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String nombre = info.substring(1);

        Optional<Path> subida = AlmacenImagenes.ruta(nombre);
        if (subida.isPresent()) {
            String tipo = getServletContext().getMimeType(nombre);
            resp.setContentType(tipo != null ? tipo : "application/octet-stream");
            resp.setContentLengthLong(Files.size(subida.get()));
            resp.setHeader("Cache-Control", "public, max-age=86400");
            Files.copy(subida.get(), resp.getOutputStream());
            return;
        }
        if (nombre.matches("[A-Za-z0-9][A-Za-z0-9._-]*") && getServletContext().getResource("/img/" + nombre) != null) {
            req.getRequestDispatcher("/img/" + nombre).forward(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
