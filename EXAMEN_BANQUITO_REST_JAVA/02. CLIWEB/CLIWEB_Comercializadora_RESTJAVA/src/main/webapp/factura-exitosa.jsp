<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ec.edu.pinza.cliweb.models.FacturaResponseDTO" %>
<%@ page import="ec.edu.pinza.cliweb.models.FacturaResponseDTO.DetalleFacturaDTO" %>
<%@ page import="ec.edu.pinza.cliweb.models.FacturaResponseDTO.InfoCreditoDTO" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Compra Exitosa - Comercializadora MONSTER</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
    <%
        // Obtener información del usuario logueado
        String rol = (String) session.getAttribute("rol");
        String cedulaUsuario = (String) session.getAttribute("cedula");
        String nombreCliente = (String) session.getAttribute("nombreCliente");
        String usuario = (String) session.getAttribute("usuario");
        boolean isAdmin = "ADMIN".equals(rol);
    %>
    <nav class="navbar">
        <div class="nav-container">
            <h1>🛒 COMERCIALIZADORA MONSTER</h1>
                        <div class="nav-links">
                <a href="${pageContext.request.contextPath}/productos">Productos</a>
                <a href="${pageContext.request.contextPath}/carrito" class="carrito-link">
                    Factura 📄
                    <span class="carrito-badge" style="display:none;">0</span>
                </a>
                <a href="${pageContext.request.contextPath}/ventas">Mis Ventas</a>
                <a href="${pageContext.request.contextPath}/consulta-credito">Consultar Crédito</a>
                <% if (isAdmin) { %>
                    <a href="${pageContext.request.contextPath}/admin/productos" style="background: linear-gradient(135deg, #FF9800 0%, #FF5722 100%); padding: 8px 16px; border-radius: 5px;">🛠️ Admin</a>
                <% } %>
                <span style="color: #64748b; padding: 8px;">👤 <%= usuario %> (<%= isAdmin ? "Admin" : nombreCliente %>)</span>
                <a href="${pageContext.request.contextPath}/login?action=logout" class="btn-logout">Salir</a>
            </div>
        </div>
    </nav>
    
    <div class="container">
        <div class="success-message">
            <h2>✅ ¡Compra Realizada con Éxito!</h2>
            <p>Tu factura ha sido generada correctamente</p>
        </div>
        
        <%
            FacturaResponseDTO factura = (FacturaResponseDTO) request.getAttribute("factura");
            if (factura != null) {
        %>
            <div class="factura-detalle">
                <div class="factura-header">
                    <h3>Factura #<%= factura.getIdFactura() %></h3>
                    <p><strong>Fecha:</strong> <%= factura.getFecha() %></p>
                    <p><strong>Cliente:</strong> <%= factura.getNombreCliente() %></p>
                    <p><strong>Forma de Pago:</strong> <%= factura.getFormaPago() %></p>
                </div>
                
                <h4>Detalle de Productos</h4>
                <table class="factura-tabla">
                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th>Precio Unit.</th>
                            <th>Cantidad</th>
                            <th>Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                    <%
                        for (DetalleFacturaDTO detalle : factura.getDetalles()) {
                    %>
                        <tr>
                            <td><%= detalle.getNombreProducto() %></td>
                            <td>$<%= detalle.getPrecioUnitario() %></td>
                            <td><%= detalle.getCantidad() %></td>
                            <td>$<%= detalle.getSubtotal() %></td>
                        </tr>
                    <%
                        }
                    %>
                    </tbody>
                    <tfoot>
                        <tr class="total-row">
                            <td colspan="3"><strong>TOTAL</strong></td>
                            <td class="total-amount"><strong>$<%= factura.getTotal() %></strong></td>
                        </tr>
                    </tfoot>
                </table>
                
                <%
                    InfoCreditoDTO credito = factura.getInfoCredito();
                    if (credito != null) {
                %>
                    <div class="credito-info">
                        <h4>Información del Crédito</h4>
                        <div class="credito-datos">
                            <p><strong>ID Crédito BanQuito:</strong> <%= credito.getIdCredito() %></p>
                            <p><strong>Monto del Crédito:</strong> $<%= credito.getMontoCredito() %></p>
                            <p><strong>Número de Cuotas:</strong> <%= credito.getNumeroCuotas() %></p>
                            <p><strong>Valor de Cuota:</strong> $<%= credito.getValorCuota() %></p>
                            <p><strong>Tasa de Interés:</strong> <%= credito.getTasaInteres() %>%</p>
                            <p><strong>Estado:</strong> <span class="badge badge-success"><%= credito.getEstado() %></span></p>
                        </div>
                        <p class="credito-mensaje">
                            💡 Tu crédito ha sido aprobado. Puedes consultar la tabla de amortización 
                            en el sistema BanQuito con el ID de crédito proporcionado.
                        </p>
                    </div>
                <%
                    }
                %>
            </div>
            
            <div class="factura-acciones">
                <a href="${pageContext.request.contextPath}/productos" class="btn btn-primary">Continuar Comprando</a>
            </div>
        <%
            }
        %>
    </div>
</body>
</html>
