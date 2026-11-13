package com.joyasmic.datos;

import com.joyasmic.modelo.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoDAO {

    private static final String COLUMNAS = "id, categoria, nombre, precio, detalles, imagen";

    /** categoria = null → todos los productos. */
    public List<Producto> listar(String categoria) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM productos"
                + (categoria != null ? " WHERE categoria = ?" : "")
                + " ORDER BY id";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (categoria != null) ps.setString(1, categoria);
            try (ResultSet rs = ps.executeQuery()) {
                List<Producto> lista = new ArrayList<>();
                while (rs.next()) lista.add(mapear(rs));
                return lista;
            }
        }
    }

    public Optional<Producto> buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM productos WHERE id = ?";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public int insertar(Producto p) throws SQLException {
        String sql = "INSERT INTO productos (categoria, nombre, precio, detalles, imagen) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getCategoria());
            ps.setString(2, p.getNombre());
            ps.setBigDecimal(3, p.getPrecio());
            ps.setString(4, p.getDetalles());
            ps.setString(5, p.getImagen());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM productos WHERE id = ?";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("id"));
        p.setCategoria(rs.getString("categoria"));
        p.setNombre(rs.getString("nombre"));
        p.setPrecio(rs.getBigDecimal("precio"));
        p.setDetalles(rs.getString("detalles"));
        p.setImagen(rs.getString("imagen"));
        return p;
    }
}
