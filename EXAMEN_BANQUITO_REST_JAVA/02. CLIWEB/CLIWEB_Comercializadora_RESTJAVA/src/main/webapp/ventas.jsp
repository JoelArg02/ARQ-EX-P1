<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="ec.edu.pinza.cliweb.models.FacturaResponseDTO" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis Ventas - Comercializadora MONSTER</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
    <nav class="navbar">
        <div class="nav-container">
            <h1>🏢 COMERCIALIZADORA MONSTER</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/productos">Productos</a>
                <a href="${pageContext.request.contextPath}/carrito" class="carrito-link">
                    Factura 📄
                    <% 
                        @SuppressWarnings("unchecked")
                        List<ec.edu.pinza.cliweb.models.ItemCarrito> carritoNav = 
                            (List<ec.edu.pinza.cliweb.models.ItemCarrito>) session.getAttribute("carrito");
                        int totalItemsNav = 0;
                        if (carritoNav != null) {
                            for (ec.edu.pinza.cliweb.models.ItemCarrito item : carritoNav) {
                                totalItemsNav += item.getCantidad();
                            }
                        }
                    %>
                    <span class="carrito-badge" <%= (totalItemsNav > 0) ? "" : "style='display:none;'" %>><%= totalItemsNav %></span>
                </a>
                <a href="${pageContext.request.contextPath}/ventas" class="active">Mis Ventas</a>
                <a href="${pageContext.request.contextPath}/consulta-credito">Consultar Crédito</a>
                <a href="${pageContext.request.contextPath}/login?action=logout" class="btn-logout">Salir</a>
            </div>
        </div>
    </nav>
    
    <div class="container">
        <h2>📊 Historial de Ventas</h2>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <%
            @SuppressWarnings("unchecked")
            List<FacturaResponseDTO> facturas = (List<FacturaResponseDTO>) request.getAttribute("facturas");
            if (facturas != null && !facturas.isEmpty()) {
        %>
            <div class="ventas-grid">
                <% for (FacturaResponseDTO factura : facturas) { %>
                    <div class="venta-card">
                        <div class="venta-header">
                            <h3>Factura #<%= factura.getIdFactura() %></h3>
                            <span class="venta-fecha">📅 <%= factura.getFecha() %></span>
                        </div>
                        <div class="venta-body">
                            <div class="venta-info">
                                <span class="info-label">Cliente:</span>
                                <span class="info-value"><%= factura.getNombreCliente() %></span>
                            </div>
                            <div class="venta-info">
                                <span class="info-label">Forma de Pago:</span>
                                <span class="badge-pago <%= factura.getFormaPago().equals("EFECTIVO") ? "badge-efectivo" : "badge-credito" %>">
                                    <%= factura.getFormaPago().equals("EFECTIVO") ? "💵 Efectivo" : "💳 Crédito" %>
                                </span>
                            </div>
                            <% if ("EFECTIVO".equals(factura.getFormaPago())) { %>
                                <div class="venta-info" style="background: #d4edda; padding: 0.5rem; border-radius: 8px; margin-top: 0.5rem;">
                                    <span class="info-label" style="color: #155724;">💰 Descuento Aplicado:</span>
                                    <span class="info-value" style="color: #155724; font-weight: 700;">33% por pago en efectivo</span>
                                </div>
                            <% } %>
                            <% if ("CREDITO_DIRECTO".equals(factura.getFormaPago()) && factura.getInfoCredito() != null) { %>
                                <div class="venta-info">
                                    <span class="info-label">Cuotas:</span>
                                    <span class="info-value"><%= factura.getInfoCredito().getNumeroCuotas() %> meses × $<%= factura.getInfoCredito().getValorCuota() %></span>
                                </div>
                            <% } %>
                            <div class="venta-total">
                                <span>Total:</span>
                                <strong>$<%= factura.getTotal() %></strong>
                            </div>
                        </div>
                        <div class="venta-actions">
                            <a href="${pageContext.request.contextPath}/ventas/detalle?id=<%= factura.getIdFactura() %>" class="btn btn-secondary">
                                Ver Detalle →
                            </a>
                            <% if ("CREDITO_DIRECTO".equals(factura.getFormaPago()) && factura.getInfoCredito() != null) { %>
                                <button class="btn btn-outline" onclick="mostrarAmortizacion(<%= factura.getIdFactura() %>)">
                                    📊 Tabla Amortización
                                </button>
                            <% } %>
                        </div>
                    </div>
                <% } %>
            </div>
        <% } else { %>
            <div class="empty-state">
                <div class="empty-icon">📋</div>
                <h3>No hay ventas registradas</h3>
                <p>Aún no has realizado ninguna compra.</p>
                <a href="${pageContext.request.contextPath}/productos" class="btn btn-primary">Ir a Productos</a>
            </div>
        <% } %>
    </div>
    
    <!-- Modal para tabla de amortización -->
    <div id="modalAmortizacion" class="modal" style="display: none;">
        <div class="modal-content">
            <div class="modal-header" style="background: linear-gradient(135deg, #7c3aed, #667eea);">
                <h3 style="color: white !important; margin: 0;">📊 Tabla de Amortización - Factura #<span id="modalFacturaId" style="color: white !important;"></span></h3>
                <button class="modal-close" onclick="cerrarModal()" style="color: white !important; background: rgba(255, 255, 255, 0.2);">&times;</button>
            </div>
            <div class="modal-body">
                <div id="amortizacionContent" class="amortizacion-wrapper">
                    <div class="loading">Cargando...</div>
                </div>
            </div>
        </div>
    </div>
    
    <script>
        function mostrarAmortizacion(idFactura) {
            const modal = document.getElementById('modalAmortizacion');
            const facturaIdSpan = document.getElementById('modalFacturaId');
            const content = document.getElementById('amortizacionContent');
            
            facturaIdSpan.textContent = idFactura;
            modal.style.display = 'flex';
            content.innerHTML = '<div class="loading">Cargando tabla de amortización...</div>';
            
            // Obtener la factura del servidor REST de Comercializadora
            fetch('http://159.203.120.118:8081/Ex_Comercializadora_RESTJava-1.0-SNAPSHOT/api/facturas/' + idFactura)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('HTTP ' + response.status);
                    }
                    return response.json();
                })
                .then(factura => {
                    if (factura.infoCredito && factura.infoCredito.tablaAmortizacion) {
                        const tabla = factura.infoCredito.tablaAmortizacion;
                        let html = '<table class="amortizacion-tabla">';
                        html += '<thead><tr>';
                        html += '<th>Cuota #</th>';
                        html += '<th>Valor Cuota</th>';
                        html += '<th>Interés</th>';
                        html += '<th>Capital</th>';
                        html += '<th>Saldo</th>';
                        html += '</tr></thead><tbody>';
                        
                        tabla.forEach(cuota => {
                            html += '<tr>';
                            html += '<td>' + cuota.numeroCuota + '</td>';
                            html += '<td>$' + cuota.valorCuota.toFixed(2) + '</td>';
                            html += '<td>$' + cuota.interesPagado.toFixed(2) + '</td>';
                            html += '<td>$' + cuota.capitalPagado.toFixed(2) + '</td>';
                            html += '<td>$' + cuota.saldoRestante.toFixed(2) + '</td>';
                            html += '</tr>';
                        });
                        
                        html += '</tbody></table>';
                        content.innerHTML = html;
                    } else {
                        content.innerHTML = '<div class="alert alert-error">No se encontró tabla de amortización para este crédito</div>';
                    }
                })
                .catch(error => {
                    console.error('Error completo:', error);
                    content.innerHTML = '<div class="alert alert-error">Error al cargar la tabla: ' + error.message + '</div>';
                });
        }
        
        function cerrarModal() {
            document.getElementById('modalAmortizacion').style.display = 'none';
        }
        
        // Cerrar modal al hacer clic fuera de él
        window.onclick = function(event) {
            const modal = document.getElementById('modalAmortizacion');
            if (event.target === modal) {
                cerrarModal();
            }
        }
    </script>
</body>
</html>
