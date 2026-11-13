package com.joyasmic.presentacion;

import com.joyasmic.negocio.AlmacenImagenes;
import com.joyasmic.negocio.UsuarioService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/** Se ejecuta una vez al arrancar la aplicación en Tomcat. */
@WebListener
public class AppListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        new UsuarioService().asegurarAdministrador();
        sce.getServletContext().log("Carpeta de imágenes: " + AlmacenImagenes.directorio());
    }
}
