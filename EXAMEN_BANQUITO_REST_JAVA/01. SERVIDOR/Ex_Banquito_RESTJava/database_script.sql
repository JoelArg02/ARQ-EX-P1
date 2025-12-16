-- ============================================
-- SCRIPT SQL - BANCO BANQUITO
-- Sistema de Core Bancario y Módulo de Crédito
-- ============================================

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS banquito_db;
USE banquito_db;

-- ============================================
-- 1. DDL - DEFINICIÓN DE TABLAS
-- ============================================

-- Tabla CLIENTE
DROP TABLE IF EXISTS CUOTA_AMORTIZACION;
DROP TABLE IF EXISTS CREDITO;
DROP TABLE IF EXISTS MOVIMIENTO;
DROP TABLE IF EXISTS CUENTA;
DROP TABLE IF EXISTS CLIENTE;

CREATE TABLE CLIENTE (
    CEDULA VARCHAR(10) NOT NULL PRIMARY KEY,
    NOMBRE VARCHAR(100) NOT NULL,
    FECHA_NACIMIENTO DATE NOT NULL,
    ESTADO_CIVIL VARCHAR(1) NOT NULL COMMENT 'S=Soltero, C=Casado'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla CUENTA
CREATE TABLE CUENTA (
    NUM_CUENTA VARCHAR(8) NOT NULL PRIMARY KEY,
    CEDULA VARCHAR(10) NOT NULL,
    SALDO NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (CEDULA) REFERENCES CLIENTE(CEDULA) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla MOVIMIENTO
CREATE TABLE MOVIMIENTO (
    COD_MOVIMIENTO INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    NUM_CUENTA VARCHAR(8) NOT NULL,
    TIPO VARCHAR(3) NOT NULL COMMENT 'DEP=Depósito, RET=Retiro',
    VALOR NUMERIC(10,2) NOT NULL,
    FECHA DATE NOT NULL,
    FOREIGN KEY (NUM_CUENTA) REFERENCES CUENTA(NUM_CUENTA) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla CREDITO
CREATE TABLE CREDITO (
    ID_CREDITO INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    CEDULA VARCHAR(10) NOT NULL,
    MONTO_APROBADO NUMERIC(10,2) NOT NULL,
    MONTO_SOLICITADO NUMERIC(10,2),
    PLAZO_MESES INT NOT NULL,
    TASA_INTERES_ANUAL NUMERIC(5,2) NOT NULL,
    MONTO_MAXIMO_AUTORIZADO NUMERIC(10,2) NOT NULL,
    FECHA_OTORGAMIENTO DATE NOT NULL,
    ESTADO VARCHAR(20) NOT NULL COMMENT 'APROBADO, RECHAZADO, CANCELADO, VIGENTE',
    FOREIGN KEY (CEDULA) REFERENCES CLIENTE(CEDULA) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla CUOTA_AMORTIZACION
CREATE TABLE CUOTA_AMORTIZACION (
    ID_CUOTA INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ID_CREDITO INT NOT NULL,
    NUMERO_CUOTA INT NOT NULL,
    VALOR_CUOTA NUMERIC(10,2) NOT NULL,
    INTERES_PAGADO NUMERIC(10,2) NOT NULL,
    CAPITAL_PAGADO NUMERIC(10,2) NOT NULL,
    SALDO_RESTANTE NUMERIC(10,2) NOT NULL,
    FOREIGN KEY (ID_CREDITO) REFERENCES CREDITO(ID_CREDITO) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 2. DATOS DE PRUEBA
-- ============================================

-- Insertar 5 Clientes
INSERT INTO CLIENTE (CEDULA, NOMBRE, FECHA_NACIMIENTO, ESTADO_CIVIL) VALUES
('1750123456', 'Juan Carlos Pérez López', '1985-03-15', 'C'),
('1750234567', 'María Elena García Torres', '1990-07-22', 'S'),
('1750345678', 'Pedro Antonio Morales Cruz', '1988-11-10', 'C'),
('1750456789', 'Ana Sofía Ramírez Flores', '1995-05-18', 'S'),
('1750567890', 'Luis Fernando Castro Vega', '2002-09-25', 'S');

-- Insertar 5 Cuentas
INSERT INTO CUENTA (NUM_CUENTA, CEDULA, SALDO) VALUES
('10001234', '1750123456', 5000.00),
('10002345', '1750234567', 8500.00),
('10003456', '1750345678', 12000.00),
('10004567', '1750456789', 3500.00),
('10005678', '1750567890', 6200.00);

-- Insertar 60+ Movimientos (depósitos y retiros en los últimos 3 meses)
-- IMPORTANTE: Incluye depósitos en DICIEMBRE 2025 para cumplir requisito del último mes

-- Movimientos para Juan Carlos Pérez (1750123456) - Cuenta 10001234
INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA) VALUES
-- Diciembre 2025 (último mes)
('10001234', 'DEP', 2500.00, '2025-12-15'),
('10001234', 'DEP', 1800.00, '2025-12-10'),
('10001234', 'RET', 600.00, '2025-12-05'),
-- Noviembre 2025
('10001234', 'DEP', 1500.00, '2025-11-25'),
('10001234', 'DEP', 2000.00, '2025-11-18'),
('10001234', 'RET', 500.00, '2025-11-12'),
-- Octubre 2025
('10001234', 'DEP', 1800.00, '2025-10-28'),
('10001234', 'RET', 300.00, '2025-10-20'),
('10001234', 'DEP', 2200.00, '2025-10-15'),
('10001234', 'RET', 400.00, '2025-10-10'),

-- Movimientos para María Elena García (1750234567) - Cuenta 10002345
-- Diciembre 2025 (último mes)
('10002345', 'DEP', 3200.00, '2025-12-14'),
('10002345', 'DEP', 2800.00, '2025-12-08'),
('10002345', 'RET', 900.00, '2025-12-03'),
-- Noviembre 2025
('10002345', 'DEP', 2500.00, '2025-11-26'),
('10002345', 'DEP', 3000.00, '2025-11-19'),
('10002345', 'RET', 800.00, '2025-11-13'),
-- Octubre 2025
('10002345', 'DEP', 2800.00, '2025-10-30'),
('10002345', 'RET', 500.00, '2025-10-25'),
('10002345', 'DEP', 3200.00, '2025-10-18'),
('10002345', 'RET', 700.00, '2025-10-12'),

-- Movimientos para Pedro Antonio Morales (1750345678) - Cuenta 10003456
-- Diciembre 2025 (último mes)
('10003456', 'DEP', 4500.00, '2025-12-13'),
('10003456', 'DEP', 3800.00, '2025-12-07'),
('10003456', 'RET', 1200.00, '2025-12-02'),
-- Noviembre 2025
('10003456', 'DEP', 3500.00, '2025-11-27'),
('10003456', 'DEP', 4000.00, '2025-11-20'),
('10003456', 'RET', 1000.00, '2025-11-14'),
-- Octubre 2025
('10003456', 'DEP', 3800.00, '2025-10-29'),
('10003456', 'RET', 800.00, '2025-10-22'),
('10003456', 'DEP', 4200.00, '2025-10-16'),
('10003456', 'RET', 1200.00, '2025-10-08'),

-- Movimientos para Ana Sofía Ramírez (1750456789) - Cuenta 10004567
-- Diciembre 2025 (último mes)
('10004567', 'DEP', 1600.00, '2025-12-12'),
('10004567', 'DEP', 1400.00, '2025-12-06'),
('10004567', 'RET', 400.00, '2025-12-01'),
-- Noviembre 2025
('10004567', 'DEP', 1200.00, '2025-11-24'),
('10004567', 'DEP', 1500.00, '2025-11-17'),
('10004567', 'RET', 300.00, '2025-11-11'),
-- Octubre 2025
('10004567', 'DEP', 1400.00, '2025-10-27'),
('10004567', 'RET', 200.00, '2025-10-21'),
('10004567', 'DEP', 1600.00, '2025-10-14'),
('10004567', 'RET', 400.00, '2025-10-09'),

-- Movimientos para Luis Fernando Castro (1750567890) - Cuenta 10005678
-- Diciembre 2025 (último mes)
('10005678', 'DEP', 2400.00, '2025-12-11'),
('10005678', 'DEP', 2000.00, '2025-12-04'),
('10005678', 'RET', 700.00, '2025-12-01'),
-- Noviembre 2025
('10005678', 'DEP', 1800.00, '2025-11-23'),
('10005678', 'DEP', 2200.00, '2025-11-16'),
('10005678', 'RET', 600.00, '2025-11-10'),
-- Octubre 2025
('10005678', 'DEP', 2000.00, '2025-10-26'),
('10005678', 'RET', 400.00, '2025-10-19'),
('10005678', 'DEP', 2400.00, '2025-10-13'),
('10005678', 'RET', 500.00, '2025-10-07');

-- ============================================
-- Insertar créditos históricos CANCELADOS
-- Estos clientes ya pagaron sus créditos anteriores, por lo que pueden solicitar nuevos
-- ============================================
INSERT INTO CREDITO (CEDULA, MONTO_APROBADO, MONTO_SOLICITADO, PLAZO_MESES, 
                     TASA_INTERES_ANUAL, MONTO_MAXIMO_AUTORIZADO, 
                     FECHA_OTORGAMIENTO, ESTADO) VALUES
-- Juan Carlos Pérez - Crédito cancelado
('1750123456', 5000.00, 5000.00, 12, 16.00, 8000.00, '2024-01-15', 'CANCELADO'),
-- María Elena García - Crédito cancelado
('1750234567', 8000.00, 8000.00, 12, 16.00, 15000.00, '2024-05-10', 'CANCELADO'),
-- Pedro Antonio Morales - Crédito cancelado
('1750345678', 15000.00, 15000.00, 18, 16.00, 20000.00, '2024-08-15', 'CANCELADO'),
-- Ana Sofía Ramírez - Crédito cancelado
('1750456789', 3000.00, 3000.00, 6, 16.00, 5000.00, '2024-03-20', 'CANCELADO'),
-- Luis Fernando Castro - Crédito cancelado
('1750567890', 6000.00, 6000.00, 12, 16.00, 10000.00, '2024-06-10', 'CANCELADO');

-- ============================================
-- 3. CONSULTAS DE VERIFICACIÓN
-- ============================================

-- Verificar datos insertados
SELECT 'CLIENTES' AS TABLA, COUNT(*) AS TOTAL FROM CLIENTE
UNION ALL
SELECT 'CUENTAS', COUNT(*) FROM CUENTA
UNION ALL
SELECT 'MOVIMIENTOS', COUNT(*) FROM MOVIMIENTO
UNION ALL
SELECT 'CREDITOS', COUNT(*) FROM CREDITO;

-- Ver clientes con sus cuentas
SELECT c.CEDULA, c.NOMBRE, c.ESTADO_CIVIL, cu.NUM_CUENTA, cu.SALDO
FROM CLIENTE c
INNER JOIN CUENTA cu ON c.CEDULA = cu.CEDULA
ORDER BY c.NOMBRE;

-- Ver resumen de movimientos por cliente (últimos 3 meses)
SELECT 
    c.CEDULA,
    c.NOMBRE,
    COUNT(CASE WHEN m.TIPO = 'DEP' THEN 1 END) AS TOTAL_DEPOSITOS,
    SUM(CASE WHEN m.TIPO = 'DEP' THEN m.VALOR ELSE 0 END) AS SUMA_DEPOSITOS,
    COUNT(CASE WHEN m.TIPO = 'RET' THEN 1 END) AS TOTAL_RETIROS,
    SUM(CASE WHEN m.TIPO = 'RET' THEN m.VALOR ELSE 0 END) AS SUMA_RETIROS
FROM CLIENTE c
INNER JOIN CUENTA cu ON c.CEDULA = cu.CEDULA
LEFT JOIN MOVIMIENTO m ON cu.NUM_CUENTA = m.NUM_CUENTA 
    AND m.FECHA >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)
GROUP BY c.CEDULA, c.NOMBRE
ORDER BY c.NOMBRE;

-- ============================================
-- NOTAS IMPORTANTES
-- ============================================
-- 1. Usuario y contraseña de MySQL deben configurarse en persistence.xml
--    Por defecto: usuario=root, password=root
-- 
-- 2. Los clientes están configurados para poder solicitar créditos:
--    - Todos tienen depósitos en el último mes
--    - Los casados mayores de 25 años califican
--    - Los créditos anteriores están CANCELADOS
--
-- 3. Para probar el sistema:
--    - Primero ejecutar este script en MySQL
--    - Luego desplegar la aplicación Java
--    - Usar los endpoints REST con las cédulas de prueba
--
-- 4. Cédulas de prueba disponibles:
--    1750123456 - Juan Carlos (Casado, 40 años)
--    1750234567 - María Elena (Soltera, 35 años)
--    1750345678 - Pedro Antonio (Casado, 37 años)
--    1750456789 - Ana Sofía (Soltera, 30 años)
--    1750567890 - Luis Fernando (Soltero, 23 años)
