-- =============================================
-- BOLSA DE EMPLEO - Script de Base de Datos
-- Programación 4 - 2026-01
-- =============================================

CREATE DATABASE IF NOT EXISTS BolsaEmpleo
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE BolsaEmpleo;

-- ---- USUARIO ----
-- Tabla base para login. La clave se guarda como HASH BCrypt (VARCHAR largo
-- porque BCrypt genera hashes de 60 caracteres).
CREATE TABLE IF NOT EXISTS usuario (
                                       id      INT AUTO_INCREMENT PRIMARY KEY,
                                       correo  VARCHAR(120) NOT NULL UNIQUE,
    clave   VARCHAR(255) NOT NULL,  -- BCrypt hash
    rol     ENUM('EMPRESA', 'OFERENTE', 'ADMIN') NOT NULL,
    estado  ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO') NOT NULL DEFAULT 'PENDIENTE'
    );

-- ---- ADMINISTRADOR ----
-- El admin ingresa con identificación y clave (no con correo).
-- Tiene su propia tabla separada de usuario.
CREATE TABLE IF NOT EXISTS administrador (
                                             id              INT AUTO_INCREMENT PRIMARY KEY,
                                             identificacion  VARCHAR(30) NOT NULL UNIQUE,
    clave           VARCHAR(255) NOT NULL  -- BCrypt hash
    );

-- ---- EMPRESA ----
CREATE TABLE IF NOT EXISTS empresa (
                                       id           INT AUTO_INCREMENT PRIMARY KEY,
                                       usuario_id   INT NOT NULL UNIQUE,
                                       nombre       VARCHAR(120) NOT NULL,
    localizacion VARCHAR(200) NOT NULL,
    telefono     VARCHAR(20)  NOT NULL,   -- formato +506 XXXX XXXX
    descripcion  TEXT         NOT NULL,
    CONSTRAINT fk_empresa_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
    );

-- ---- OFERENTE ----
CREATE TABLE IF NOT EXISTS oferente (
                                        id              INT AUTO_INCREMENT PRIMARY KEY,
                                        usuario_id      INT          NOT NULL UNIQUE,
                                        identificacion  VARCHAR(30)  NOT NULL UNIQUE,
    nombre          VARCHAR(80)  NOT NULL,
    primer_apellido VARCHAR(80)  NOT NULL,
    nacionalidad    VARCHAR(60)  NOT NULL,
    telefono        VARCHAR(20)  NOT NULL,   -- formato +506 XXXX XXXX
    residencia      VARCHAR(200) NOT NULL,
    curriculum_pdf  VARCHAR(500) NULL,       -- ruta del archivo subido
    CONSTRAINT fk_oferente_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
    );

-- ---- CARACTERISTICA ----
-- Árbol jerárquico (padre_id apunta a la misma tabla).
-- padre_id NULL = raíz (ej: "Lenguajes de programación").
CREATE TABLE IF NOT EXISTS caracteristica (
                                              id        INT AUTO_INCREMENT PRIMARY KEY,
                                              nombre    VARCHAR(120) NOT NULL,
    padre_id  INT NULL,
    CONSTRAINT fk_caracteristica_padre FOREIGN KEY (padre_id) REFERENCES caracteristica(id)
    );

-- ---- MONEDA ----
-- Las notas dicen salario en colones, pero el proyecto tiene entidad Moneda.
-- Se deja la tabla y se inserta CRC como única opción por defecto.
CREATE TABLE IF NOT EXISTS moneda (
                                      id     INT AUTO_INCREMENT PRIMARY KEY,
                                      codigo VARCHAR(10)  NOT NULL UNIQUE,
    nombre VARCHAR(60)  NOT NULL
    );

-- ---- PUESTO ----
CREATE TABLE IF NOT EXISTS puesto (
                                      id              INT AUTO_INCREMENT PRIMARY KEY,
                                      empresa_id      INT            NOT NULL,
                                      descripcion     TEXT           NOT NULL,
                                      salario         DECIMAL(15, 2) NOT NULL,
    moneda_id       INT            NOT NULL,
    tipo            ENUM('PUBLICO', 'PRIVADO') NOT NULL,
    activo          TINYINT(1)     NOT NULL DEFAULT 1,
    fecha_registro  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_puesto_empresa FOREIGN KEY (empresa_id) REFERENCES empresa(id),
    CONSTRAINT fk_puesto_moneda  FOREIGN KEY (moneda_id)  REFERENCES moneda(id)
    );

-- ---- PUESTO_CARACTERISTICA ----
-- Relación entre puesto y las características requeridas con su nivel.
CREATE TABLE IF NOT EXISTS puesto_caracteristica (
                                                     puesto_id         INT NOT NULL,
                                                     caracteristica_id INT NOT NULL,
                                                     nivel_requerido   INT NOT NULL,  -- 1 a 5
                                                     PRIMARY KEY (puesto_id, caracteristica_id),
    CONSTRAINT fk_pc_puesto        FOREIGN KEY (puesto_id)         REFERENCES puesto(id),
    CONSTRAINT fk_pc_caracteristica FOREIGN KEY (caracteristica_id) REFERENCES caracteristica(id)
    );

-- ---- OFERENTE_HABILIDAD ----
-- Habilidades del oferente con su nivel.
CREATE TABLE IF NOT EXISTS oferente_habilidad (
                                                  oferente_id       INT NOT NULL,
                                                  caracteristica_id INT NOT NULL,
                                                  nivel             INT NOT NULL,  -- 1 a 5
                                                  PRIMARY KEY (oferente_id, caracteristica_id),
    CONSTRAINT fk_oh_oferente       FOREIGN KEY (oferente_id)       REFERENCES oferente(id),
    CONSTRAINT fk_oh_caracteristica FOREIGN KEY (caracteristica_id) REFERENCES caracteristica(id)
    );

-- =============================================
-- DATOS INICIALES
-- =============================================

-- Moneda por defecto (colones costarricenses)
INSERT INTO moneda (codigo, nombre) VALUES
                                        ('CRC', 'Colón Costarricense'),
                                        ('USD', 'Dólar Estadounidense');
-- Administrador por defecto
-- Identificación: admin | Clave: admin123 (hash BCrypt)
INSERT INTO administrador (identificacion, clave) VALUES
    ('admin', '$2a$10$tKsAdRe8SXxBjnOUM2t4..tVK.b6TV823bGvk9z/qNKPk6ro6b3ie');

-- Usuario del administrador para login via Spring Security
INSERT INTO usuario (correo, clave, rol, estado) VALUES
    ('admin@bolsaempleo.com',
     '$2a$10$tKsAdRe8SXxBjnOUM2t4..tVK.b6TV823bGvk9z/qNKPk6ro6b3ie',
     'ADMIN', 'APROBADO');

-- Características de ejemplo (árbol jerárquico)
INSERT INTO caracteristica (id, nombre, padre_id) VALUES
                                                      (1,  'Lenguajes de programación', NULL),
                                                      (2,  'Java',       1),
                                                      (3,  'Python',     1),
                                                      (4,  'C#',         1),
                                                      (5,  'Kotlin',     1),
                                                      (6,  'Tecnologías Web', NULL),
                                                      (7,  'HTML',       6),
                                                      (8,  'CSS',        6),
                                                      (9,  'JavaScript', 6),
                                                      (10, 'Bases de Datos', NULL),
                                                      (11, 'MySQL',      10),
                                                      (12, 'Oracle',     10),
                                                      (13, 'MongoDB',    10),
                                                      (14, 'Testing',    NULL),
                                                      (15, 'JUnit',      14),
                                                      (16, 'Selenium',   14),
                                                      (17, 'Ciberseguridad', NULL),
                                                      (18, 'OWASP',      17),
                                                      (19, 'Redes',      17);

