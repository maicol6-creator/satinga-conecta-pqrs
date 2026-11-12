-- ============================================================
-- Satinga Conecta - Modulo PQRS
-- Script de base de datos (compatible con MySQL 8 y MariaDB/XAMPP)
-- Ejecutar en MySQL Workbench o phpMyAdmin antes de correr el proyecto
-- ============================================================

CREATE DATABASE IF NOT EXISTS satinga_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE satinga_db;

-- ------------------------------------------------------------
-- Tabla: usuarios (ciudadanos, funcionarios y administradores)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario     INT AUTO_INCREMENT PRIMARY KEY,
    documento      VARCHAR(20)  NOT NULL,
    nombre_completo VARCHAR(120) NOT NULL,
    correo         VARCHAR(120) NOT NULL,
    telefono       VARCHAR(20)  NULL,
    rol            ENUM('CIUDADANO','FUNCIONARIO','ADMINISTRADOR') NOT NULL DEFAULT 'CIUDADANO',
    fecha_registro DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_usuarios_documento UNIQUE (documento),
    CONSTRAINT uq_usuarios_correo UNIQUE (correo)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Tabla: pqrs (peticiones, quejas, reclamos y sugerencias)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pqrs (
    id_pqrs             INT AUTO_INCREMENT PRIMARY KEY,
    radicado            VARCHAR(30)  NOT NULL,
    tipo                ENUM('PETICION','QUEJA','RECLAMO','SUGERENCIA') NOT NULL,
    asunto              VARCHAR(150) NOT NULL,
    descripcion         TEXT         NOT NULL,
    estado              ENUM('RADICADA','EN_TRAMITE','RESUELTA','CERRADA') NOT NULL DEFAULT 'RADICADA',
    respuesta           TEXT         NULL,
    id_usuario          INT          NOT NULL,
    fecha_creacion      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_pqrs_radicado UNIQUE (radicado),
    CONSTRAINT fk_pqrs_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios (id_usuario)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_pqrs_estado (estado),
    INDEX idx_pqrs_usuario (id_usuario)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Datos iniciales de ejemplo
-- ------------------------------------------------------------
INSERT IGNORE INTO usuarios (documento, nombre_completo, correo, telefono, rol) VALUES
    ('1000000001', 'Maicol Caicedo',      'maicol@satingaconecta.local',  '3000000001', 'ADMINISTRADOR'),
    ('1000000002', 'Funcionario Alcaldia', 'funcionario@satingaconecta.local', '3000000002', 'FUNCIONARIO'),
    ('1000000003', 'Ciudadano de Prueba', 'ciudadano@satingaconecta.local', '3000000003', 'CIUDADANO');
