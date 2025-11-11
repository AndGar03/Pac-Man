-- Script de creación de base de datos y tabla de usuarios
-- Para Pac-Man Client-Server

CREATE DATABASE IF NOT EXISTS pacman_db;
USE pacman_db;

CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50) UNIQUE NOT NULL,
    contraseña VARCHAR(50) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Datos de ejemplo (serán cargados desde properties)
-- Estos son solo ejemplos, los reales se cargarán desde el archivo de propiedades

