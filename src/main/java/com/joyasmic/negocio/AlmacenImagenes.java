package com.joyasmic.negocio;

import com.joyasmic.util.Config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/** Guarda en disco (fuera del WAR) las imágenes que sube el administrador. */
public final class AlmacenImagenes {

    public static final long MAX_BYTES = 5L * 1024 * 1024;
    private static final Set<String> EXTENSIONES = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final Pattern NOMBRE_SEGURO = Pattern.compile("^[A-Za-z0-9][A-Za-z0-9._-]*$");

    private AlmacenImagenes() {}

    public static Path directorio() {
        String configurado = Config.get("uploads.dir");
        Path p = configurado.isEmpty()
                ? Path.of(System.getProperty("user.home"), "joyasmic-uploads")
                : Path.of(configurado);
        return p.toAbsolutePath().normalize();
    }

    /** Devuelve el nombre de archivo generado. */
    public static String guardar(InputStream in, String nombreOriginal, long tamano)
            throws ValidacionException, IOException {
        if (in == null || tamano <= 0) {
            throw new ValidacionException("Debes seleccionar una imagen del producto.");
        }
        if (tamano > MAX_BYTES) {
            throw new ValidacionException("La imagen no debe pesar más de 5 MB.");
        }
        String ext = extension(nombreOriginal);
        if (!EXTENSIONES.contains(ext)) {
            throw new ValidacionException("Formato de imagen no permitido (usa JPG, PNG, GIF o WEBP).");
        }
        Files.createDirectories(directorio());
        String nombre = UUID.randomUUID() + "." + ext;
        Files.copy(in, directorio().resolve(nombre));
        return nombre;
    }

    /** Ruta del archivo si existe y el nombre es seguro (evita path traversal). */
    public static Optional<Path> ruta(String nombre) {
        if (nombre == null || !NOMBRE_SEGURO.matcher(nombre).matches()) return Optional.empty();
        Path base = directorio();
        Path p = base.resolve(nombre).normalize();
        return (p.startsWith(base) && Files.isRegularFile(p)) ? Optional.of(p) : Optional.empty();
    }

    public static void eliminar(String nombre) {
        ruta(nombre).ifPresent(p -> {
            try {
                Files.deleteIfExists(p);
            } catch (IOException ignorado) {
                // si no se puede borrar el archivo, el producto ya salió del catálogo
            }
        });
    }

    private static String extension(String nombre) {
        if (nombre == null) return "";
        int i = nombre.lastIndexOf('.');
        return i < 0 ? "" : nombre.substring(i + 1).toLowerCase(Locale.ROOT);
    }
}
