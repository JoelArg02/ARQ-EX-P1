<%@ page import="java.util.List" %>
<%@ page import="ec.edu.espe.comercializadora.models.Factura" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Facturas - Comercializadora</title>
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
        .factura-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        .factura-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 20px;
            background: white;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .factura-card h4 {
            color: #1976D2;
            margin: 0 0 15px 0;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .factura-card .total {
            font-size: 18px;
            font-weight: bold;
            color: #4CAF50;
        }
        .factura-card .cliente {
            background-color: #f8f9fa;
            padding: 10px;
            border-radius: 4px;
            margin: 10px 0;
        }
        .status {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
        }
        .status.efectivo {
            background-color: #4CAF50;
            color: white;
        }
        .status.credito {
            background-color: #FF9800;
            color: white;
        }
        .error {
            color: #f44336;
            background-color: #ffebee;
            padding: 15px;
            border-radius: 4px;
            margin: 15px 0;
            text-align: center;
        }
        .no-facturas {
            text-align: center;
            color: #666;
            font-style: italic;
            padding: 40px 0;
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
            background-color: #f8f9fa;
            font-weight: bold;
            color: #1976D2;
        }
        tr:hover {
            background-color: #f5f5f5;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>COMERCIALIZADORA - FACTURAS</h1>
    </div>

    <div class="nav-container">
        <div class="nav">
            <a href="main">Principal</a>
            <a href="clientes">Clientes</a>
            <a href="productos">Productos</a>
            <a href="facturar">Facturar</a>
            <a href="facturas" class="active">Facturas</a>
        </div>
    </div>

    <div class="content">
        <% if (request.getAttribute("error") != null) { %>
            <div class="error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <div class="section-card">
            <h2>Historial de Facturas</h2>
            
            <%
                @SuppressWarnings("unchecked")
                List<Factura> facturas = (List<Factura>) request.getAttribute("facturas");
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
                
                if (facturas != null && !facturas.isEmpty()) {
            %>
                <table>
                    <thead>
                        <tr>
                            <th>Factura #</th>
                            <th>Cliente</th>
                            <th>Fecha</th>
                            <th>Subtotal</th>
                            <th>IVA</th>
                            <th>Total</th>
                            <th>Forma Pago</th>
                            <th>Productos</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            for (Factura factura : facturas) {
                        %>
                            <tr>
                                <td><strong>#<%= String.format("%03d", factura.getId()) %></strong></td>
                                <td>
                                    <%= factura.getNombreCliente() %><br>
                                    <small>CI: <%= factura.getCedulaCliente() %></small>
                                </td>
                                <td>
                                    <%
                                        try {
                                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                            Date date = inputFormat.parse(factura.getFechaEmision());
                                            out.print(outputFormat.format(date));
                                        } catch (Exception e) {
                                            out.print(factura.getFechaEmision());
                                        }
                                    %>
                                </td>
                                <td><%= currencyFormat.format(factura.getSubtotal()) %></td>
                                <td><%= currencyFormat.format(factura.getIva()) %></td>
                                <td class="total"><%= currencyFormat.format(factura.getTotal()) %></td>
                                <td>
                                    <span class="status <%= factura.getFormaPago().toLowerCase() %>">
                                        <%= factura.getFormaPago().toUpperCase() %>
                                    </span>
                                </td>
                                <td><%= factura.getCantidadProductos() %> items</td>
                            </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
                
                <div style="margin-top: 20px; text-align: center; color: #666;">
                    <p>Total de facturas: <%= facturas.size() %></p>
                </div>
            <%
                } else {
            %>
                <div class="no-facturas">
                    <p>No se encontraron facturas en el sistema.</p>
                    <p>
                        <a href="facturar" style="color: #1976D2; text-decoration: none; font-weight: bold;">
                            → Crear primera factura
                        </a>
                    </p>
                </div>
            <%
                }
            %>
        </div>
    </div>
</body>
</html>