CREATE DATABASE IF NOT EXISTS BolsaEmpleo
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE BolsaEmpleo;

CREATE TABLE IF NOT EXISTS usuario (
                                       id      INT AUTO_INCREMENT PRIMARY KEY,
                                       correo  VARCHAR(120) NOT NULL UNIQUE,
    clave   VARCHAR(255) NOT NULL,
    rol     ENUM('EMPRESA', 'OFERENTE', 'ADMIN') NOT NULL,
    estado  ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO') NOT NULL DEFAULT 'PENDIENTE'
    );

CREATE TABLE IF NOT EXISTS administrador (
                                             id              INT AUTO_INCREMENT PRIMARY KEY,
                                             identificacion  VARCHAR(30) NOT NULL UNIQUE,
    clave           VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS empresa (
                                       id           INT AUTO_INCREMENT PRIMARY KEY,
                                       usuario_id   INT NOT NULL UNIQUE,
                                       nombre       VARCHAR(120) NOT NULL,
    localizacion VARCHAR(200) NOT NULL,
    telefono     VARCHAR(20)  NOT NULL,
    descripcion  TEXT         NOT NULL,
    CONSTRAINT fk_empresa_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
    );

CREATE TABLE IF NOT EXISTS oferente (
                                        id              INT AUTO_INCREMENT PRIMARY KEY,
                                        usuario_id      INT          NOT NULL UNIQUE,
                                        identificacion  VARCHAR(30)  NOT NULL UNIQUE,
    nombre          VARCHAR(80)  NOT NULL,
    primer_apellido VARCHAR(80)  NOT NULL,
    nacionalidad    VARCHAR(60)  NOT NULL,
    telefono        VARCHAR(20)  NOT NULL,
    residencia      VARCHAR(200) NOT NULL,
    curriculum_pdf  VARCHAR(500) NULL,
    CONSTRAINT fk_oferente_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
    );

CREATE TABLE IF NOT EXISTS caracteristica (
                                              id        INT AUTO_INCREMENT PRIMARY KEY,
                                              nombre    VARCHAR(120) NOT NULL,
    padre_id  INT NULL,
    CONSTRAINT fk_caracteristica_padre FOREIGN KEY (padre_id) REFERENCES caracteristica(id)
    );

CREATE TABLE IF NOT EXISTS moneda (
                                      id     INT AUTO_INCREMENT PRIMARY KEY,
                                      codigo VARCHAR(10)  NOT NULL UNIQUE,
    nombre VARCHAR(60)  NOT NULL
    );

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

CREATE TABLE IF NOT EXISTS puesto_caracteristica (
                                                     puesto_id         INT NOT NULL,
                                                     caracteristica_id INT NOT NULL,
                                                     nivel_requerido   INT NOT NULL,
                                                     PRIMARY KEY (puesto_id, caracteristica_id),
    CONSTRAINT fk_pc_puesto        FOREIGN KEY (puesto_id)         REFERENCES puesto(id),
    CONSTRAINT fk_pc_caracteristica FOREIGN KEY (caracteristica_id) REFERENCES caracteristica(id)
    );

CREATE TABLE IF NOT EXISTS oferente_habilidad (
                                                  oferente_id       INT NOT NULL,
                                                  caracteristica_id INT NOT NULL,
                                                  nivel             INT NOT NULL,
                                                  PRIMARY KEY (oferente_id, caracteristica_id),
    CONSTRAINT fk_oh_oferente       FOREIGN KEY (oferente_id)       REFERENCES oferente(id),
    CONSTRAINT fk_oh_caracteristica FOREIGN KEY (caracteristica_id) REFERENCES caracteristica(id)
    );

INSERT INTO moneda (codigo, nombre) VALUES
                                        ('CRC', 'Colón Costarricense'),
                                        ('USD', 'Dólar Estadounidense');

INSERT INTO administrador (identificacion, clave) VALUES
    ('admin', '$2a$10$tKsAdRe8SXxBjnOUM2t4..tVK.b6TV823bGvk9z/qNKPk6ro6b3ie');

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

