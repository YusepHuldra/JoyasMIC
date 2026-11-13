package com.joyasmic.datos;

import com.joyasmic.util.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Equivale al ConexionBD.conectar() de tu conector original. */
public final class ConexionBD {

    static {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private ConexionBD() {}

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(
                Config.get("db.url"), Config.get("db.user"), Config.get("db.password"));
    }
}
