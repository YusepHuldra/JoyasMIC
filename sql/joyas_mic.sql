-- Ejecutar en phpMyAdmin (pestaña SQL) o: mysql -u root < joyas_mic.sql
CREATE DATABASE IF NOT EXISTS joyas_mic CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE joyas_mic;

CREATE TABLE IF NOT EXISTS users (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    rol        VARCHAR(10)  NOT NULL DEFAULT 'CLIENTE',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Si YA tenías la tabla `users` de tu proyecto PHP, solo agrega la columna de rol:
-- ALTER TABLE users ADD COLUMN rol VARCHAR(10) NOT NULL DEFAULT 'CLIENTE';

CREATE TABLE IF NOT EXISTS productos (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    categoria  VARCHAR(30)   NOT NULL,
    nombre     VARCHAR(150)  NOT NULL,
    precio     DECIMAL(10,2) NOT NULL,
    detalles   TEXT          NOT NULL,
    imagen     VARCHAR(255)  NOT NULL,
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Productos que antes estaban fijos en el JavaScript (ejecutar UNA sola vez)
INSERT INTO productos (categoria, nombre, precio, detalles, imagen) VALUES
('Esclavas', 'Esclava Cartier en Plata Ley .925', 820,
 'Esta esclava está fabricada en plata sólida, lo que asegura una gran resistencia y un brillo que perdura con el tiempo.', 'esclava.jpg'),
('Anillos', 'Anillo de Plata ''Manos de Amistad''', 240,
 'Lleva contigo un accesorio con un significado profundo. Este anillo de plata representa el apoyo y la conexión a través de su diseño detallado de manos entrelazadas.', 'anillo.jpg'),
('Collares', 'Collar de Corazones ''Infinite Love''', 240,
 'Este delicado collar cuenta con una serie de corazones entrelazados que simbolizan el amor eterno, todo con el brillo inigualable de la plata.', 'collar.jpg');
