<%@ page import="java.util.List" %>
<%@ page import="ec.edu.espe.comercializadora.models.Electrodomestico" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Productos - Comercializadora</title>
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
        }
        .nav a:hover, .nav a.active {
            background-color: #1976D2;
        }
        .content {
            max-width: 1200px;
            margin: 40px auto;
            padding: 0 20px;
        }
        .section-card {
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            margin-bottom: 30px;
        }
        h2 {
            color: #1976D2;
            margin-bottom: 20px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="text"], input[type="number"] {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        .checkbox-group {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        button {
            padding: 12px 24px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: bold;
        }
        button:hover {
            background-color: #45a049;
        }
        .producto-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        .producto-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 20px;
            background: white;
        }
        .producto-card h4 {
            color: #1976D2;
            margin: 0 0 10px 0;
        }
        .producto-card .precio {
            font-size: 18px;
            font-weight: bold;
            color: #4CAF50;
        }
        .status {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
        }
        .status.activo {
            background-color: #4CAF50;
            color: white;
        }
        .status.inactivo {
            background-color: #f44336;
            color: white;
        }
        .error {
            color: #f44336;
            background-color: #ffebee;
            padding: 10px;
            border-radius: 4px;
            margin: 15px 0;
        }
        .success {
            color: #4caf50;
            background-color: #e8f5e8;
            padding: 10px;
            border-radius: 4px;
            margin: 15px 0;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>COMERCIALIZADORA - PRODUCTOS</h1>
    </div>

    <div class="nav-container">
        <div class="nav">
            <a href="main">Principal</a>
            <a href="clientes">Clientes</a>
            <a href="productos" class="active">Productos</a>
            <a href="facturar">Facturar</a>
            <a href="facturas">Facturas</a>
        </div>
    </div>

    <div class="content">
        <div class="section-card">
            <h2>Nuevo Producto</h2>
            <form method="post" action="productos">
                <input type="hidden" name="action" value="create">
                
                <div class="form-group">
                    <label for="nombre">Nombre:</label>
                    <input type="text" id="nombre" name="nombre" required>
                </div>
                
                <div class="form-group">
                    <label for="descripcion">Descripción:</label>
                    <input type="text" id="descripcion" name="descripcion">
                </div>
                
                <div class="form-group">
                    <label for="marca">Marca:</label>
                    <input type="text" id="marca" name="marca">
                </div>
                
                <div class="form-group">
                    <label for="precioVenta">Precio de Venta:</label>
                    <input type="number" id="precioVenta" name="precioVenta" step="0.01" required>
                </div>
                
                <div class="form-group">
                    <div class="checkbox-group">
                        <input type="checkbox" id="activo" name="activo" checked>
                        <label for="activo">Activo</label>
                    </div>
                </div>
                
                <button type="submit">Crear Producto</button>
            </form>
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <% if (request.getAttribute("success") != null) { %>
            <div class="success">
                <%= request.getAttribute("success") %>
            </div>
        <% } %>

        <div class="section-card">
            <h2>Lista de Productos</h2>
            
            <div class="producto-grid">
                <%
                    @SuppressWarnings("unchecked")
                    List<Electrodomestico> productos = (List<Electrodomestico>) request.getAttribute("productos");
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
                    
                    if (productos != null && !productos.isEmpty()) {
                        for (Electrodomestico producto : productos) {
                %>
                    <div class="producto-card">
                        <h4><%= producto.getNombre() %></h4>
                        
                        <% if (producto.getDescripcion() != null && !producto.getDescripcion().isEmpty()) { %>
                            <p><strong>Descripción:</strong> <%= producto.getDescripcion() %></p>
                        <% } %>
                        
                        <% if (producto.getMarca() != null && !producto.getMarca().isEmpty()) { %>
                            <p><strong>Marca:</strong> <%= producto.getMarca() %></p>
                        <% } %>
                        
                        <p class="precio"><%= currencyFormat.format(producto.getPrecioVenta()) %></p>
                        
                        <p>
                            <span class="status <%= producto.isActivo() ? "activo" : "inactivo" %>">
                                <%= producto.isActivo() ? "ACTIVO" : "INACTIVO" %>
                            </span>
                        </p>
                    </div>
                <%
                        }
                    } else {
                %>
                    <p>No hay productos registrados.</p>
                <%
                    }
                %>
            </div>
        </div>
    </div>
</body>
</html>