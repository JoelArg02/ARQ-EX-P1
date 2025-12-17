-- ====================================================================
-- SCRIPT DE BASE DE DATOS: COMERCIALIZADORA DE ELECTRODOMÉSTICOS
-- Base de datos: comercializadora_db
-- Sistema: Servidor REST que consume servicios del Banco BanQuito
-- ====================================================================

-- Crear base de datos
DROP DATABASE IF EXISTS comercializadora_db;
CREATE DATABASE comercializadora_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE comercializadora_db;

-- ====================================================================
-- TABLA: Producto
-- Descripción: Catálogo de electrodomésticos disponibles
-- ====================================================================
CREATE TABLE Producto (
    idProducto INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    imagen LONGTEXT NULL COMMENT 'Imagen del producto en formato base64',
    CONSTRAINT chk_precio_positivo CHECK (precio > 0),
    CONSTRAINT chk_stock_positivo CHECK (stock >= 0)
) ENGINE=InnoDB;

-- ====================================================================
-- TABLA: ClienteCom
-- Descripción: Clientes de la comercializadora
-- ====================================================================
CREATE TABLE ClienteCom (
    idCliente INT AUTO_INCREMENT PRIMARY KEY,
    cedula VARCHAR(10) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200),
    telefono VARCHAR(15)
) ENGINE=InnoDB;

-- ====================================================================
-- TABLA: Factura
-- Descripción: Facturas de ventas (efectivo o crédito directo)
-- ====================================================================
CREATE TABLE Factura (
    idFactura INT AUTO_INCREMENT PRIMARY KEY,
    idCliente INT NOT NULL,
    fecha DATE NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    formaPago VARCHAR(20) NOT NULL,
    idCreditoBanco INT NULL,
    CONSTRAINT fk_factura_cliente FOREIGN KEY (idCliente) 
        REFERENCES ClienteCom(idCliente),
    CONSTRAINT chk_forma_pago CHECK (formaPago IN ('EFECTIVO', 'CREDITO_DIRECTO')),
    CONSTRAINT chk_total_positivo CHECK (total > 0)
) ENGINE=InnoDB;

-- ====================================================================
-- TABLA: DetalleFactura
-- Descripción: Líneas de detalle de cada factura
-- ====================================================================
CREATE TABLE DetalleFactura (
    idDetalle INT AUTO_INCREMENT PRIMARY KEY,
    idFactura INT NOT NULL,
    idProducto INT NOT NULL,
    cantidad INT NOT NULL,
    precioUnitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_detalle_factura FOREIGN KEY (idFactura) 
        REFERENCES Factura(idFactura) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto FOREIGN KEY (idProducto) 
        REFERENCES Producto(idProducto),
    CONSTRAINT chk_cantidad_positiva CHECK (cantidad > 0),
    CONSTRAINT chk_precio_unitario_positivo CHECK (precioUnitario > 0),
    CONSTRAINT chk_subtotal_positivo CHECK (subtotal > 0)
) ENGINE=InnoDB;

-- ====================================================================
-- ÍNDICES PARA OPTIMIZACIÓN DE CONSULTAS
-- ====================================================================
CREATE INDEX idx_factura_cliente ON Factura(idCliente);
CREATE INDEX idx_factura_fecha ON Factura(fecha);
CREATE INDEX idx_factura_credito ON Factura(idCreditoBanco);
CREATE INDEX idx_detalle_factura ON DetalleFactura(idFactura);
CREATE INDEX idx_detalle_producto ON DetalleFactura(idProducto);

-- ====================================================================
-- DATOS DE PRUEBA: PRODUCTOS
-- ====================================================================
INSERT INTO Producto (codigo, nombre, precio, stock) VALUES
('REF001', 'Refrigeradora LG 14 pies', 899.00, 15),
('LAV001', 'Lavadora Samsung 20 lb', 749.00, 20),
('COC001', 'Cocina Indurama 6 quemadores', 489.00, 12),
('MIC001', 'Microondas Panasonic 1.2 cu ft', 159.00, 30),
('LIC001', 'Licuadora Oster 10 velocidades', 89.00, 40),
('TV001', 'Smart TV LG 55 pulgadas', 1299.00, 8),
('TV002', 'Smart TV Samsung 43 pulgadas', 849.00, 10),
('ASP001', 'Aspiradora Electrolux 1800W', 249.00, 18),
('PLAN001', 'Plancha a vapor Black+Decker', 45.00, 50),
('VEN001', 'Ventilador de Torre Samurai', 79.00, 25);

-- ====================================================================
-- DATOS DE PRUEBA: CLIENTES
-- Nota: Las cédulas DEBEN coincidir EXACTAMENTE con clientes del banco BanQuito
--       para que puedan solicitar créditos
-- ====================================================================
INSERT INTO ClienteCom (cedula, nombre, direccion, telefono) VALUES
('1750123456', 'Juan Carlos Pérez López', 'Av. Amazonas N123 y Patria', '0991234567'),
('1750234567', 'María Elena García Torres', 'Calle Sucre 456', '0987654321'),
('1750345678', 'Pedro Antonio Morales Cruz', 'Av. 6 de Diciembre N789', '0998877665'),
('1750456789', 'Ana Sofía Ramírez Flores', 'Calle Bolívar 321', '0976543210'),
('1750567890', 'Luis Fernando Castro Vega', 'Av. Shyris N555', '0965432109');

-- ====================================================================
-- DATOS DE PRUEBA: FACTURAS EN EFECTIVO
-- ====================================================================
INSERT INTO Factura (idCliente, fecha, total, formaPago, idCreditoBanco) VALUES
(1, '2024-01-15', 944.00, 'EFECTIVO', NULL),
(2, '2024-01-20', 159.00, 'EFECTIVO', NULL);

-- Detalles factura 1 (Juan Carlos Pérez - Efectivo)
INSERT INTO DetalleFactura (idFactura, idProducto, cantidad, precioUnitario, subtotal) VALUES
(1, 1, 1, 899.00, 899.00),  -- Refrigeradora
(1, 9, 1, 45.00, 45.00);     -- Plancha

-- Detalles factura 2 (María Elena García - Efectivo)
INSERT INTO DetalleFactura (idFactura, idProducto, cantidad, precioUnitario, subtotal) VALUES
(2, 4, 1, 159.00, 159.00);   -- Microondas

-- ====================================================================
-- DATOS DE PRUEBA: FACTURAS A CRÉDITO
-- Nota: Los idCreditoBanco son IDs reales que fueron generados previamente
--       en el banco BanQuito. En producción estos se obtienen al llamar
--       al endpoint POST /api/creditos/otorgar del banco.
--       
--       Mapeo de créditos BanQuito (estado CANCELADO = pueden solicitar nuevos):
--       - ID_CREDITO=1: María Elena García (1750234567), $8000, 12 meses
--       - ID_CREDITO=2: Pedro Antonio Morales (1750345678), $15000, 18 meses
--
--       Las siguientes facturas usan créditos históricos CANCELADOS como referencia.
-- ====================================================================
INSERT INTO Factura (idCliente, fecha, total, formaPago, idCreditoBanco) VALUES
(2, '2024-02-01', 944.00, 'CREDITO_DIRECTO', 1),
(3, '2024-02-10', 1637.00, 'CREDITO_DIRECTO', 2);

-- Detalles factura 3 (María Elena García - Crédito 12 meses, ID_CREDITO=1 en BanQuito)
INSERT INTO DetalleFactura (idFactura, idProducto, cantidad, precioUnitario, subtotal) VALUES
(3, 1, 1, 899.00, 899.00),    -- Refrigeradora
(3, 9, 1, 45.00, 45.00);       -- Plancha

-- Detalles factura 4 (Pedro Antonio Morales - Crédito 18 meses, ID_CREDITO=2 en BanQuito)
INSERT INTO DetalleFactura (idFactura, idProducto, cantidad, precioUnitario, subtotal) VALUES
(4, 6, 1, 1299.00, 1299.00),  -- Smart TV 55"
(4, 8, 1, 249.00, 249.00),    -- Aspiradora
(4, 5, 1, 89.00, 89.00);      -- Licuadora

-- ====================================================================
-- CONSULTAS DE VERIFICACIÓN
-- ====================================================================

-- Ver todos los productos
SELECT * FROM Producto ORDER BY codigo;

-- Ver todos los clientes
SELECT * FROM ClienteCom ORDER BY nombre;

-- Ver resumen de facturas
SELECT 
    f.idFactura,
    c.nombre AS cliente,
    f.fecha,
    f.total,
    f.formaPago,
    f.idCreditoBanco
FROM Factura f
INNER JOIN ClienteCom c ON f.idCliente = c.idCliente
ORDER BY f.fecha DESC;

-- Ver detalles de facturas
SELECT 
    f.idFactura,
    c.nombre AS cliente,
    p.codigo AS codigoProducto,
    p.nombre AS producto,
    d.cantidad,
    d.precioUnitario,
    d.subtotal
FROM DetalleFactura d
INNER JOIN Factura f ON d.idFactura = f.idFactura
INNER JOIN ClienteCom c ON f.idCliente = c.idCliente
INNER JOIN Producto p ON d.idProducto = p.idProducto
ORDER BY f.idFactura, d.idDetalle;

-- Ver facturas a crédito con referencia al banco
SELECT 
    f.idFactura,
    c.cedula,
    c.nombre,
    f.fecha,
    f.total,
    f.idCreditoBanco
FROM Factura f
INNER JOIN ClienteCom c ON f.idCliente = c.idCliente
WHERE f.formaPago = 'CREDITO_DIRECTO'
ORDER BY f.fecha DESC;

-- Ver stock actual de productos
SELECT 
    codigo,
    nombre,
    precio,
    stock,
    (precio * stock) AS valorInventario
FROM Producto
ORDER BY stock DESC;

-- ====================================================================
-- NOTAS IMPORTANTES
-- ====================================================================
-- 1. Esta BD se integra con el servidor BanQuito para créditos
-- 2. Los clientes deben existir primero en el banco para créditos
-- 3. El idCreditoBanco se obtiene al llamar POST /api/creditos/otorgar
-- 4. La tabla de amortización se consulta vía REST al banco
-- 5. El servidor valida stock antes de crear facturas
-- 6. El servidor actualiza stock automáticamente al crear facturas
-- ====================================================================

-- ====================================================================
-- TABLA: Usuario
-- Descripción: Usuarios del sistema para autenticación
-- ====================================================================
CREATE TABLE Usuario (
    idUsuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    idCliente INT NULL,
    rol VARCHAR(20) NOT NULL DEFAULT 'CLIENTE',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_usuario_cliente FOREIGN KEY (idCliente) 
        REFERENCES ClienteCom(idCliente) ON DELETE SET NULL,
    CONSTRAINT chk_rol CHECK (rol IN ('ADMIN', 'CLIENTE'))
) ENGINE=InnoDB;

-- ====================================================================
-- DATOS DE PRUEBA: USUARIOS
-- Admin: MONSTER/MONSTER9 (puede vender a cualquier cédula)
-- Clientes: usan su cédula y password abcd1234
-- ====================================================================

-- Usuario ADMIN (sin cliente asociado, puede vender a cualquier cédula)
INSERT INTO Usuario (username, password, idCliente, rol, activo) VALUES
('MONSTER', 'MONSTER9', NULL, 'ADMIN', TRUE);

-- Usuarios CLIENTE (cada uno asociado a su cliente, solo pueden comprar para su propia cédula)
INSERT INTO Usuario (username, password, idCliente, rol, activo) VALUES
('1750123456', 'abcd1234', 1, 'CLIENTE', TRUE),
('1750234567', 'abcd1234', 2, 'CLIENTE', TRUE),
('1750345678', 'abcd1234', 3, 'CLIENTE', TRUE),
('1750456789', 'abcd1234', 4, 'CLIENTE', TRUE),
('1750567890', 'abcd1234', 5, 'CLIENTE', TRUE);

-- Verificar usuarios creados
SELECT u.idUsuario, u.username, u.rol, u.activo, c.cedula, c.nombre
FROM Usuario u
LEFT JOIN ClienteCom c ON u.idCliente = c.idCliente
ORDER BY u.rol, u.username;
