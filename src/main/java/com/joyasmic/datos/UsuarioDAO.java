package com.joyasmic.datos;

import com.joyasmic.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class UsuarioDAO {

    public Optional<Usuario> buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT id, name, email, password, rol FROM users WHERE email = ?";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("name"));
                    u.setEmail(rs.getString("email"));
                    u.setPasswordHash(rs.getString("password"));
                    u.setRol(rs.getString("rol"));
                    return Optional.of(u);
                }
                return Optional.empty();
            }
        }
    }

    public boolean existeEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Inserta el usuario y devuelve el id generado. */
    public int insertar(Usuario u) throws SQLException {
        String sql = "INSERT INTO users (name, email, password, rol) VALUES (?, ?, ?, ?)";
        try (Connection c = ConexionBD.conectar();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPasswordHash());
            ps.setString(4, u.getRol());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }
}
