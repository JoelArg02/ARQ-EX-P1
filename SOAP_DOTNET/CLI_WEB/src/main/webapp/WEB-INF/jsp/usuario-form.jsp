<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ec.edu.espe.comercializadora.models.User" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Usuario - Formulario</title>
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
        .nav a:hover, .nav a.active {
            background-color: #1976D2;
        }
        .content {
            max-width: 800px;
            margin: 40px auto;
            padding: 0 20px;
        }
        .card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            padding: 30px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #333;
        }
        input[type="text"], input[type="password"] {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 16px;
        }
        input[type="text"]:focus, input[type="password"]:focus {
            border-color: #4CAF50;
            outline: none;
        }
        .btn {
            display: inline-block;
            padding: 12px 24px;
            text-decoration: none;
            border-radius: 4px;
            cursor: pointer;
            margin: 5px;
            border: none;
            font-size: 14px;
        }
        .btn-primary {
            background-color: #4CAF50;
            color: white;
        }
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        .btn:hover {
            opacity: 0.8;
        }
        .alert {
            padding: 15px;
            margin: 20px 0;
            border-radius: 4px;
        }
        .alert-error {
            background-color: #f2dede;
            border-color: #ebccd1;
            color: #a94442;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>GESTIÓN DE USUARIOS</h1>
        <p>
            <% 
                User user = (User) request.getAttribute("user");
                boolean isEdit = user != null;
            %>
            <%= isEdit ? "Editar Usuario" : "Nuevo Usuario" %>
        </p>
    </div>

    <div class="nav-container">
        <div class="nav">
            <a href="main">Inicio</a>
            <a href="clientes">Clientes</a>
            <a href="productos">Productos</a>
            <a href="facturar">Facturar</a>
            <a href="facturas">Facturas</a>
            <a href="usuarios" class="active">Usuarios</a>
        </div>
    </div>

    <div class="content">
        <div class="card">
            <h2><%= isEdit ? "Editar Usuario" : "Crear Nuevo Usuario" %></h2>

            <% if (request.getAttribute("mensaje") != null) { %>
                <div class="alert alert-<%= request.getAttribute("tipo") %>">
                    <%= request.getAttribute("mensaje") %>
                </div>
            <% } %>

            <form method="post" action="usuarios">
                <input type="hidden" name="action" value="<%= isEdit ? "update" : "create" %>">
                <% if (isEdit) { %>
                    <input type="hidden" name="id" value="<%= user.getId() %>">
                <% } %>

                <div class="form-group">
                    <label for="nombre">Nombre de Usuario:</label>
                    <input type="text" id="nombre" name="nombre" 
                           value="<%= isEdit ? user.getNombre() : "" %>" 
                           required maxlength="50">
                </div>

                <div class="form-group">
                    <label for="contrasena">Contraseña:</label>
                    <input type="password" id="contrasena" name="contrasena" 
                           value="<%= isEdit ? user.getContrasena() : "" %>" 
                           required maxlength="100">
                </div>

                <div style="margin-top: 30px;">
                    <button type="submit" class="btn btn-primary">
                        <%= isEdit ? "Actualizar" : "Crear" %> Usuario
                    </button>
                    <a href="usuarios" class="btn btn-secondary">Cancelar</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>