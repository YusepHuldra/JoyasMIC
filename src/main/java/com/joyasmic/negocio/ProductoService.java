package com.joyasmic.negocio;

import com.joyasmic.datos.ProductoDAO;
import com.joyasmic.modelo.Producto;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductoService {

    public static final String TODOS = "Todos";
    public static final List<String> CATEGORIAS = List.of("Esclavas", "Anillos", "Collares", "Aretes");
    public static final List<String> PESTANAS = List.of(TODOS, "Esclavas", "Anillos", "Collares", "Aretes");

    private final ProductoDAO dao = new ProductoDAO();

    public static boolean esCategoriaValida(String categoria) {
        return categoria != null && CATEGORIAS.contains(categoria);
    }

    /** categoria = "Todos" (o inválida) devuelve todo el catálogo. */
    public List<Producto> listar(String categoria) {
        try {
            return dao.listar(esCategoriaValida(categoria) ? categoria : null);
        } catch (SQLException e) {
            throw new ErrorInternoException("No se pudo leer el catálogo", e);
        }
    }

    public Optional<Producto> buscar(int id) {
        try {
            return dao.buscarPorId(id);
        } catch (SQLException e) {
            throw new ErrorInternoException("No se pudo buscar el producto", e);
        }
    }

    public void publicar(String categoria, String nombre, String precioTexto, String detalles,
                         InputStream imagen, String nombreImagen, long tamanoImagen)
            throws ValidacionException {

        nombre = nombre == null ? "" : nombre.trim();
        detalles = detalles == null ? "" : detalles.trim();

        if (!esCategoriaValida(categoria)) {
            throw new ValidacionException("Selecciona una categoría válida.");
        }
        if (nombre.isEmpty() || nombre.length() > 150) {
            throw new ValidacionException("El título es obligatorio (máximo 150 caracteres).");
        }
        if (detalles.isEmpty()) {
            throw new ValidacionException("La descripción es obligatoria.");
        }
        BigDecimal precio;
        try {
            precio = new BigDecimal(precioTexto == null ? "" : precioTexto.trim());
        } catch (NumberFormatException e) {
            throw new ValidacionException("El precio no es un número válido.");
        }
        if (precio.signum() <= 0 || precio.scale() > 2 || precio.compareTo(new BigDecimal("99999999")) > 0) {
            throw new ValidacionException("El precio debe ser mayor a 0 y tener máximo 2 decimales.");
        }

        String archivo;
        try {
            archivo = AlmacenImagenes.guardar(imagen, nombreImagen, tamanoImagen);
        } catch (IOException e) {
            throw new ErrorInternoException("No se pudo guardar la imagen", e);
        }

        Producto p = new Producto();
        p.setCategoria(categoria);
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setDetalles(detalles);
        p.setImagen(archivo);
        try {
            dao.insertar(p);
        } catch (SQLException e) {
            AlmacenImagenes.eliminar(archivo); // no dejar imágenes huérfanas
            throw new ErrorInternoException("No se pudo publicar el producto", e);
        }
    }

    public void eliminar(int id) {
        try {
            Optional<Producto> p = dao.buscarPorId(id);
            if (p.isPresent() && dao.eliminar(id)) {
                AlmacenImagenes.eliminar(p.get().getImagen());
            }
        } catch (SQLException e) {
            throw new ErrorInternoException("No se pudo eliminar el producto", e);
        }
    }
}
