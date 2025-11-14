<%@ page import="java.util.List" %>
<%@ page import="ec.edu.espe.comercializadora.models.Cliente" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Clientes - Comercializadora</title>
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
        input[type="text"], input[type="email"] {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
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
        .cliente-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        .cliente-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 20px;
            background: white;
        }
        .cliente-card h4 {
            color: #1976D2;
            margin: 0 0 10px 0;
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
        <h1>COMERCIALIZADORA - CLIENTES</h1>
    </div>

    <div class="nav-container">
        <div class="nav">
            <a href="main">Principal</a>
            <a href="clientes" class="active">Clientes</a>
            <a href="productos">Productos</a>
            <a href="facturar">Facturar</a>
            <a href="facturas">Facturas</a>
        </div>
    </div>

    <div class="content">
        <div class="section-card">
            <h2>Nuevo Cliente</h2>
            <form method="post" action="clientes">
                <input type="hidden" name="action" value="create">
                
                <div class="form-group">
                    <label for="cedula">Cédula:</label>
                    <input type="text" id="cedula" name="cedula" required>
                </div>
                
                <div class="form-group">
                    <label for="nombreCompleto">Nombre Completo:</label>
                    <input type="text" id="nombreCompleto" name="nombreCompleto" required>
                </div>
                
                <div class="form-group">
                    <label for="correo">Correo:</label>
                    <input type="email" id="correo" name="correo">
                </div>
                
                <div class="form-group">
                    <label for="telefono">Teléfono:</label>
                    <input type="text" id="telefono" name="telefono">
                </div>
                
                <div class="form-group">
                    <label for="direccion">Dirección:</label>
                    <input type="text" id="direccion" name="direccion">
                </div>
                
                <button type="submit">Crear Cliente</button>
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
            <h2>Lista de Clientes</h2>
            
            <div class="cliente-grid">
                <%
                    @SuppressWarnings("unchecked")
                    List<Cliente> clientes = (List<Cliente>) request.getAttribute("clientes");
                    if (clientes != null && !clientes.isEmpty()) {
                        for (Cliente cliente : clientes) {
                %>
                    <div class="cliente-card">
                        <h4><%= cliente.getNombreCompleto() %></h4>
                        <p><strong>CI:</strong> <%= cliente.getCedula() %></p>
                        <% if (cliente.getCorreo() != null && !cliente.getCorreo().isEmpty()) { %>
                            <p><strong>Correo:</strong> <%= cliente.getCorreo() %></p>
                        <% } %>
                        <% if (cliente.getTelefono() != null && !cliente.getTelefono().isEmpty()) { %>
                            <p><strong>Teléfono:</strong> <%= cliente.getTelefono() %></p>
                        <% } %>
                        <% if (cliente.getDireccion() != null && !cliente.getDireccion().isEmpty()) { %>
                            <p><strong>Dirección:</strong> <%= cliente.getDireccion() %></p>
                        <% } %>
                    </div>
                <%
                        }
                    } else {
                %>
                    <p>No hay clientes registrados.</p>
                <%
                    }
                %>
            </div>
        </div>
    </div>
</body>
</html>