package com.joyasmic.negocio;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.joyasmic.datos.UsuarioDAO;
import com.joyasmic.modelo.Usuario;
import com.joyasmic.util.Config;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/** Reglas de negocio de usuarios (las mismas validaciones VF1, VF2, VF4, VF7 de tu proyecto original). */
public class UsuarioService {

    private static final Logger LOG = Logger.getLogger(UsuarioService.class.getName());
    private static final Pattern NOMBRE_VALIDO = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$");
    private static final Pattern EMAIL_VALIDO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UsuarioDAO dao = new UsuarioDAO();

    public Usuario registrar(String nombre, String email, String password) throws ValidacionException {
        nombre = nombre == null ? "" : nombre.trim().toUpperCase(Locale.ROOT);
        email = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        password = password == null ? "" : password;

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            throw new ValidacionException("VF1: Todos los campos son obligatorios y no deben estar vacíos.");
        }
        if (!NOMBRE_VALIDO.matcher(nombre).matches()) {
            throw new ValidacionException("El nombre solo puede contener letras y espacios. No se permiten signos de puntuación ni números.");
        }
        if (nombre.length() > 100 || email.length() > 150) {
            throw new ValidacionException("El nombre o el correo son demasiado largos.");
        }
        if (email.contains(" ") || password.contains(" ")) {
            throw new ValidacionException("VF2: El correo y la contraseña no pueden contener espacios en blanco.");
        }
        if (!EMAIL_VALIDO.matcher(email).matches()) {
            throw new ValidacionException("El correo electrónico no tiene un formato válido.");
        }
        if (password.length() < 8) {
            throw new ValidacionException("VF7: La contraseña debe tener un mínimo de 8 caracteres.");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ValidacionException("La contraseña es demasiado larga (máximo 72 bytes).");
        }

        try {
            if (dao.existeEmail(email)) {
                throw new ValidacionException("VF4: Este correo ya está registrado.");
            }
            Usuario u = new Usuario();
            u.setNombre(nombre);
            u.setEmail(email);
            u.setPasswordHash(hash(password));
            u.setRol(Usuario.ROL_CLIENTE);
            u.setId(dao.insertar(u));
            u.setPasswordHash(null); // el hash nunca viaja a la sesión
            return u;
        } catch (SQLException e) {
            throw new ErrorInternoException("No se pudo registrar al usuario", e);
        }
    }

    public Usuario autenticar(String email, String password) throws ValidacionException {
        email = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        password = password == null ? "" : password;
        if (email.isEmpty() || password.isEmpty()) {
            throw new ValidacionException("VF1: Los campos de acceso no pueden estar vacíos.");
        }
        try {
            Optional<Usuario> encontrado = dao.buscarPorEmail(email);
            if (encontrado.isEmpty() || !verificar(password, encontrado.get().getPasswordHash())) {
                throw new ValidacionException("Correo o contraseña incorrectos.");
            }
            Usuario u = encontrado.get();
            u.setPasswordHash(null);
            return u;
        } catch (SQLException e) {
            throw new ErrorInternoException("No se pudo validar el acceso", e);
        }
    }

    /** Crea el administrador definido en db.properties si todavía no existe. Se llama al arrancar. */
    public void asegurarAdministrador() {
        String email = Config.get("admin.email").toLowerCase(Locale.ROOT);
        String password = Config.get("admin.password");
        if (email.isEmpty() || password.isEmpty()) return;
        try {
            if (dao.existeEmail(email)) return;
            Usuario admin = new Usuario();
            admin.setNombre(Config.get("admin.nombre").toUpperCase(Locale.ROOT));
            admin.setEmail(email);
            admin.setPasswordHash(hash(password));
            admin.setRol(Usuario.ROL_ADMIN);
            dao.insertar(admin);
            LOG.info("Administrador inicial creado: " + email);
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "No se pudo crear el administrador inicial (¿BD encendida y script SQL ejecutado?)", e);
        }
    }

    private static String hash(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    private static boolean verificar(String password, String hashGuardado) {
        if (hashGuardado == null) return false;
        // PHP genera "$2y$"; es el mismo algoritmo que "$2a$", solo cambia el prefijo.
        String normalizado = hashGuardado.startsWith("$2y$") ? "$2a$" + hashGuardado.substring(4) : hashGuardado;
        return BCrypt.verifyer().verify(password.toCharArray(), normalizado).verified;
    }
}
