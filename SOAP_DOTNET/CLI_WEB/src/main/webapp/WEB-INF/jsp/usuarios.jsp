<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="ec.edu.espe.comercializadora.models.User" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Usuarios</title>
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
        .nav a:last-child {
            border-right: none;
        }
        .content {
            max-width: 1200px;
            margin: 40px auto;
            padding: 0 20px;
        }
        .card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            padding: 30px;
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
            background-color: #2196F3;
            color: white;
        }
        .btn-danger {
            background-color: #f44336;
            color: white;
        }
        .btn:hover {
            opacity: 0.8;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f1f1f1;
            font-weight: bold;
        }
        tr:hover {
            background-color: #f5f5f5;
        }
        .alert {
            padding: 15px;
            margin: 20px 0;
            border-radius: 4px;
        }
        .alert-success {
            background-color: #dff0d8;
            border-color: #d6e9c6;
            color: #3c763d;
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
        <p>Administra los usuarios del sistema</p>
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
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px;">
                <h2>Lista de Usuarios</h2>
                <a href="usuarios?action=new" class="btn btn-primary">+ Nuevo Usuario</a>
            </div>

            <% if (request.getAttribute("mensaje") != null) { %>
                <div class="alert alert-<%= request.getAttribute("tipo") %>">
                    <%= request.getAttribute("mensaje") %>
                </div>
            <% } %>

            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        List<User> users = (List<User>) request.getAttribute("users");
                        if (users != null && !users.isEmpty()) {
                            for (User user : users) {
                    %>
                        <tr>
                            <td><%= user.getId() %></td>
                            <td><%= user.getNombre() %></td>
                            <td>
                                <a href="usuarios?action=edit&id=<%= user.getId() %>" class="btn btn-secondary">Editar</a>
                                <a href="usuarios?action=delete&id=<%= user.getId() %>" class="btn btn-danger" 
                                   onclick="return confirm('¿Está seguro de eliminar este usuario?')">Eliminar</a>
                            </td>
                        </tr>
                    <% 
                            }
                        } else {
                    %>
                        <tr>
                            <td colspan="3" style="text-align: center;">No hay usuarios registrados</td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>