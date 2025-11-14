<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Comercializadora - Principal</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f5f5f5;
        }
        .header {
            background-color: #1976D2;
            color: white;
            padding: 20px;
            text-align: center;
        }
        .nav-container {
            background-color: #1565C0;
            padding: 0;
        }
        .nav {
            display: flex;
            justify-content: center;
        }
        .nav a {
            color: white;
            text-decoration: none;
            padding: 20px 30px;
            display: block;
            border-right: 1px solid #1976D2;
            transition: background-color 0.3s;
        }
        .nav a:hover {
            background-color: #1976D2;
        }
        .nav a:last-child {
            border-right: none;
        }
        .content {
            max-width: 1200px;
            margin: 40px auto;
            padding: 0 20px;
        }
        .welcome-card {
            background: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            text-align: center;
        }
        .welcome-card h2 {
            color: #1976D2;
            margin-bottom: 20px;
        }
        .options {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 30px;
            margin-top: 40px;
        }
        .option-card {
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            text-align: center;
            transition: transform 0.3s;
        }
        .option-card:hover {
            transform: translateY(-5px);
        }
        .option-card h3 {
            color: #1976D2;
            margin-bottom: 15px;
        }
        .option-card a {
            display: inline-block;
            background-color: #4CAF50;
            color: white;
            padding: 12px 24px;
            text-decoration: none;
            border-radius: 4px;
            margin-top: 15px;
        }
        .option-card a:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>COMERCIALIZADORA</h1>
        <p>Bienvenido, <%= session.getAttribute("usuario") %></p>
    </div>

    <div class="nav-container">
        <div class="nav">
            <a href="clientes">Clientes</a>
            <a href="productos">Productos</a>
            <a href="facturar">Facturar</a>
            <a href="facturas">Facturas</a>
            <a href="usuarios">Usuarios</a>
        </div>
    </div>

    <div class="content">
        <div class="welcome-card">
            <h2>Sistema de Comercialización</h2>
            <p>Gestiona clientes, productos y facturas desde una sola plataforma</p>
        </div>

        <div class="options">
            <div class="option-card">
                <h3>👥 Gestión de Clientes</h3>
                <p>Administra la información de tus clientes</p>
                <a href="clientes">Ver Clientes</a>
            </div>
            
            <div class="option-card">
                <h3>📦 Gestión de Productos</h3>
                <p>Maneja tu inventario de electrodomésticos</p>
                <a href="productos">Ver Productos</a>
            </div>
            
            <div class="option-card">
                <h3>💰 Facturación</h3>
                <p>Genera facturas y procesa pagos</p>
                <a href="facturar">Crear Factura</a>
            </div>
            
            <div class="option-card">
                <h3>📋 Historial</h3>
                <p>Consulta facturas anteriores</p>
                <a href="facturas">Ver Facturas</a>
            </div>
            
            <div class="option-card">
                <h3>👤 Usuarios</h3>
                <p>Gestiona usuarios del sistema</p>
                <a href="usuarios">Administrar Usuarios</a>
            </div>
        </div>
    </div>
</body>
</html>