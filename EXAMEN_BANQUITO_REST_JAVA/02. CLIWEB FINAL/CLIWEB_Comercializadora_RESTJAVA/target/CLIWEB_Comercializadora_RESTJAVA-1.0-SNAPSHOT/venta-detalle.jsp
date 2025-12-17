<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ec.edu.pinza.cliweb.models.FacturaResponseDTO" %>
<%@ page import="ec.edu.pinza.cliweb.models.FacturaResponseDTO.DetalleFacturaDTO" %>
<%@ page import="ec.edu.pinza.cliweb.models.FacturaResponseDTO.InfoCreditoDTO" %>
<%@ page import="ec.edu.pinza.cliweb.models.FacturaResponseDTO.CuotaAmortizacionDTO" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Detalle de Venta - Comercializadora MONSTER</title>
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
            <h1>🏢 COMERCIALIZADORA MONSTER</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/productos">Productos</a>
                <a href="${pageContext.request.contextPath}/carrito" class="carrito-link">
                    Factura 📄
                    <span class="carrito-badge" style="display:none;">0</span>
                </a>
                <a href="${pageContext.request.contextPath}/ventas" class="active">Mis Ventas</a>
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
        <%
            FacturaResponseDTO factura = (FacturaResponseDTO) request.getAttribute("factura");
            if (factura != null) {
        %>
            <div class="detalle-header">
                <a href="${pageContext.request.contextPath}/ventas" class="btn-back">← Volver</a>
                <h2>Detalle de Factura #<%= factura.getIdFactura() %></h2>
            </div>
            
            <div class="factura-info-grid">
                <div class="info-box">
                    <span class="info-label">📅 Fecha</span>
                    <span class="info-value"><%= factura.getFecha() %></span>
                </div>
                <div class="info-box">
                    <span class="info-label">👤 Cliente</span>
                    <span class="info-value"><%= factura.getNombreCliente() %></span>
                </div>
                <div class="info-box">
                    <span class="info-label">💳 Forma de Pago</span>
                    <span class="info-value"><%= factura.getFormaPago() %></span>
                </div>
                <div class="info-box total-box">
                    <span class="info-label">💰 Total</span>
                    <span class="info-value total-amount">$<%= factura.getTotal() %></span>
                </div>
            </div>
            
            <% if ("EFECTIVO".equals(factura.getFormaPago())) { %>
                <div class="alert" style="background: linear-gradient(135deg, #d4edda, #c3e6cb); border-left: 4px solid #28a745; padding: 1rem; border-radius: 8px; margin-top: 1rem;">
                    <strong style="color: #155724; font-size: 1.1rem;">💰 Descuento Aplicado: 33%</strong>
                    <p style="color: #155724; margin: 0.5rem 0 0 0;">Por pago en efectivo se aplicó un descuento del 33% sobre el precio de venta.</p>
                </div>
            <% } %>
            
            <div class="detalle-section">
                <h3>📦 Productos</h3>
                <table class="detalle-tabla">
                    <thead>
                        <tr>
                            <th>Código</th>
                            <th>Producto</th>
                            <th>Cantidad</th>
                            <th>Precio Unit.</th>
                            <th>Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                            double subtotalProductos = 0;
                            for (DetalleFacturaDTO detalle : factura.getDetalles()) { 
                                subtotalProductos += detalle.getSubtotal().doubleValue();
                        %>
                            <tr>
                                <td><%= detalle.getCodigoProducto() %></td>
                                <td><%= detalle.getNombreProducto() %></td>
                                <td><%= detalle.getCantidad() %></td>
                                <td>$<%= detalle.getPrecioUnitario() %></td>
                                <td><strong>$<%= detalle.getSubtotal() %></strong></td>
                            </tr>
                        <% } %>
                        <tr class="subtotal-row">
                            <td colspan="4" style="text-align: right; font-weight: 600;">Subtotal Productos:</td>
                            <td><strong>$<%= String.format("%.2f", subtotalProductos) %></strong></td>
                        </tr>
                        
                        <% if ("EFECTIVO".equals(factura.getFormaPago())) { 
                            double descuento = subtotalProductos * 0.33;
                        %>
                            <tr style="background: #d4edda;">
                                <td colspan="4" style="text-align: right; font-weight: 600; color: #155724;">💰 Descuento (33%):</td>
                                <td style="color: #155724;"><strong>- $<%= String.format("%.2f", descuento) %></strong></td>
                            </tr>
                            <tr class="total-row" style="background: linear-gradient(135deg, #7c3aed, #667eea);">
                                <td colspan="4" style="text-align: right; font-size: 1.2rem; font-weight: 700; color: white;">💵 TOTAL A PAGAR:</td>
                                <td style="font-size: 1.2rem; color: white;"><strong>$<%= factura.getTotal() %></strong></td>
                            </tr>
                        <% } else if ("CREDITO_DIRECTO".equals(factura.getFormaPago()) && factura.getInfoCredito() != null) { 
                            InfoCreditoDTO credito = factura.getInfoCredito();
                            double totalIntereses = 0;
                            if (credito.getTablaAmortizacion() != null) {
                                for (CuotaAmortizacionDTO cuota : credito.getTablaAmortizacion()) {
                                    totalIntereses += cuota.getInteresPagado().doubleValue();
                                }
                            }
                            double totalConIntereses = subtotalProductos + totalIntereses;
                        %>
                            <tr style="background: #fff3cd;">
                                <td colspan="4" style="text-align: right; font-weight: 600; color: #856404;">💳 Total Intereses (<%= credito.getNumeroCuotas() %> cuotas):</td>
                                <td style="color: #856404;"><strong>+ $<%= String.format("%.2f", totalIntereses) %></strong></td>
                            </tr>
                            <tr class="total-row" style="background: linear-gradient(135deg, #7c3aed, #667eea);">
                                <td colspan="4" style="text-align: right; font-size: 1.2rem; font-weight: 700; color: white;">💰 TOTAL A PAGAR (Crédito):</td>
                                <td style="font-size: 1.2rem; color: white;"><strong>$<%= String.format("%.2f", totalConIntereses) %></strong></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            
            <% if (factura.getInfoCredito() != null) { 
                InfoCreditoDTO credito = factura.getInfoCredito();
            %>
                <div class="detalle-section credito-section">
                    <h3>💳 Información del Crédito</h3>
                    <div class="credito-info-grid">
                        <div class="credito-box">
                            <span class="label">ID Crédito</span>
                            <span class="value">#<%= credito.getIdCredito() %></span>
                        </div>
                        <div class="credito-box">
                            <span class="label">Monto</span>
                            <span class="value">$<%= credito.getMontoCredito() %></span>
                        </div>
                        <div class="credito-box">
                            <span class="label">Cuotas</span>
                            <span class="value"><%= credito.getNumeroCuotas() %> meses</span>
                        </div>
                        <div class="credito-box">
                            <span class="label">Valor Cuota</span>
                            <span class="value">$<%= credito.getValorCuota() %></span>
                        </div>
                        <div class="credito-box">
                            <span class="label">Tasa Interés</span>
                            <span class="value"><%= credito.getTasaInteres() %>%</span>
                        </div>
                        <div class="credito-box">
                            <span class="label">Estado</span>
                            <span class="value badge-estado"><%= credito.getEstado() %></span>
                        </div>
                    </div>
                    
                    <% if (credito.getTablaAmortizacion() != null && !credito.getTablaAmortizacion().isEmpty()) { %>
                        <h4>📊 Tabla de Amortización</h4>
                        <div class="amortizacion-wrapper">
                            <table class="amortizacion-tabla">
                                <thead>
                                    <tr>
                                        <th>Cuota</th>
                                        <th>Valor Cuota</th>
                                        <th>Interés</th>
                                        <th>Capital</th>
                                        <th>Saldo</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% for (CuotaAmortizacionDTO cuota : credito.getTablaAmortizacion()) { %>
                                        <tr>
                                            <td><%= cuota.getNumeroCuota() %></td>
                                            <td>$<%= cuota.getValorCuota() %></td>
                                            <td>$<%= cuota.getInteresPagado() %></td>
                                            <td>$<%= cuota.getCapitalPagado() %></td>
                                            <td>$<%= cuota.getSaldoRestante() %></td>
                                        </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    <% } %>
                </div>
            <% } %>
            
        <% } else { %>
            <div class="alert alert-error">
                No se encontró la factura solicitada.
            </div>
            <a href="${pageContext.request.contextPath}/ventas" class="btn btn-primary">Volver a Ventas</a>
        <% } %>
    </div>
</body>
</html>
