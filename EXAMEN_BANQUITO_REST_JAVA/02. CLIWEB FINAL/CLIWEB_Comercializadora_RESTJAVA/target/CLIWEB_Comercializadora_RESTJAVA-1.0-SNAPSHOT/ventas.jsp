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
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf-autotable/3.5.31/jspdf.plugin.autotable.min.js"></script>
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
                <% if (isAdmin) { %>
                    <a href="${pageContext.request.contextPath}/admin/productos" style="background: linear-gradient(135deg, #FF9800 0%, #FF5722 100%); padding: 8px 16px; border-radius: 5px;">🛠️ Admin</a>
                <% } %>
                <span style="color: #64748b; padding: 8px;">👤 <%= usuario %> (<%= isAdmin ? "Admin" : nombreCliente %>)</span>
                <a href="${pageContext.request.contextPath}/login?action=logout" class="btn-logout">Salir</a>
            </div>
        </div>
    </nav>
    
    <div class="container">
        <% if ("ADMIN".equals(session.getAttribute("rol"))) { %>
            <h2>📊 Historial de Ventas (Todas)</h2>
        <% } else { %>
            <h2>📊 Mis Compras</h2>
        <% } %>
        
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
                            <% if ("CREDITO_DIRECTO".equals(factura.getFormaPago())) { %>
                                <button type="button" class="btn btn-outline" onclick="mostrarAmortizacion(<%= factura.getIdFactura() %>); return false;">
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
                <div style="display: flex; gap: 0.5rem; align-items: center;">
                    <button type="button" class="btn-imprimir" onclick="descargarPDF(); return false;" style="background: white; color: #7c3aed; border: none; padding: 0.5rem 1rem; border-radius: 8px; cursor: pointer; font-weight: 600; transition: all 0.3s;">
                        📄 Descargar PDF
                    </button>
                    <button type="button" class="modal-close" onclick="cerrarModal(); return false;" style="color: white !important; background: rgba(255, 255, 255, 0.2); border: none; font-size: 2rem; cursor: pointer; padding: 0 0.75rem; border-radius: 8px;">&times;</button>
                </div>
            </div>
            <div class="modal-body">
                <div id="amortizacionContent" class="amortizacion-wrapper">
                    <div class="loading">Cargando...</div>
                </div>
            </div>
        </div>
    </div>
    
    <script>
        let tablaAmortizacionActual = null;
        
        function mostrarAmortizacion(idFactura) {
            console.log('Mostrando amortización para factura:', idFactura);
            
            const modal = document.getElementById('modalAmortizacion');
            const facturaIdSpan = document.getElementById('modalFacturaId');
            const content = document.getElementById('amortizacionContent');
            
            if (!modal || !facturaIdSpan || !content) {
                console.error('No se encontraron los elementos del modal');
                alert('Error: No se pudo abrir el modal');
                return false;
            }
            
            facturaIdSpan.textContent = idFactura;
            modal.style.display = 'flex';
            content.innerHTML = '<div class="loading">Cargando tabla de amortización...</div>';
            tablaAmortizacionActual = null;
            
            console.log('Haciendo petición a la API...');
            
            // Obtener la factura del servidor REST de Comercializadora
            fetch('http://localhost:8081/Ex_Comercializadora_RESTJava-1.0-SNAPSHOT/api/facturas/' + idFactura)
                .then(response => {
                    console.log('Respuesta recibida:', response.status);
                    if (!response.ok) {
                        throw new Error('HTTP ' + response.status);
                    }
                    return response.json();
                })
                .then(factura => {
                    console.log('Datos de factura:', factura);
                    if (factura.infoCredito && factura.infoCredito.tablaAmortizacion) {
                        const tabla = factura.infoCredito.tablaAmortizacion;
                        tablaAmortizacionActual = {
                            idFactura: idFactura,
                            datos: tabla,
                            infoFactura: {
                                fecha: factura.fecha,
                                cliente: factura.nombreCliente,
                                total: factura.total,
                                numeroCuotas: factura.infoCredito.numeroCuotas,
                                valorCuota: factura.infoCredito.valorCuota,
                                tasaInteres: factura.infoCredito.tasaInteres
                            }
                        };
                        console.log('Tabla de amortización encontrada:', tabla.length, 'cuotas');
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
                        console.error('No se encontró info de crédito o tabla');
                        content.innerHTML = '<div class="alert alert-error">No se encontró tabla de amortización para este crédito</div>';
                    }
                })
                .catch(error => {
                    console.error('Error completo:', error);
                    content.innerHTML = '<div class="alert alert-error">Error al cargar la tabla: ' + error.message + '</div>';
                });
            
            return false;
        }
        
        function cerrarModal() {
            document.getElementById('modalAmortizacion').style.display = 'none';
            tablaAmortizacionActual = null;
            return false;
        }
        
        function descargarPDF() {
            if (!tablaAmortizacionActual) {
                alert('No hay datos de amortización disponibles');
                return false;
            }
            
            try {
                const { jsPDF } = window.jspdf;
                const doc = new jsPDF();
                
                // Título
                doc.setFontSize(20);
                doc.setTextColor(124, 58, 237);
                doc.text('COMERCIALIZADORA MONSTER', 105, 20, { align: 'center' });
                
                doc.setFontSize(14);
                doc.setTextColor(0, 0, 0);
                doc.text('Tabla de Amortización', 105, 30, { align: 'center' });
                
                doc.setFontSize(12);
                doc.text('Factura #' + tablaAmortizacionActual.idFactura, 105, 38, { align: 'center' });
                
                // Información de la factura
                const info = tablaAmortizacionActual.infoFactura;
                let yPos = 50;
                
                doc.setFontSize(10);
                doc.setTextColor(0, 0, 0);
                doc.setFont(undefined, 'bold');
                doc.text('Datos de la Factura:', 20, yPos);
                
                doc.setFont(undefined, 'normal');
                doc.setTextColor(60, 60, 60);
                yPos += 7;
                doc.text('Cliente: ' + info.cliente, 20, yPos);
                yPos += 6;
                doc.text('Fecha: ' + info.fecha, 20, yPos);
                yPos += 6;
                doc.text('Total Financiado: $' + info.total, 20, yPos);
                yPos += 6;
                const totalAPagar = (info.valorCuota * info.numeroCuotas).toFixed(2);
                doc.text('Total a Pagar: $' + totalAPagar, 20, yPos);
                
                yPos += 10;
                doc.setFont(undefined, 'bold');
                doc.setTextColor(0, 0, 0);
                doc.text('Condiciones del Crédito:', 20, yPos);
                
                doc.setFont(undefined, 'normal');
                doc.setTextColor(60, 60, 60);
                yPos += 7;
                doc.text('Número de Cuotas: ' + info.numeroCuotas + ' meses', 20, yPos);
                yPos += 6;
                doc.text('Valor de Cuota: $' + info.valorCuota, 20, yPos);
                yPos += 6;
                doc.text('Tasa de Interés: ' + info.tasaInteres.toFixed(2) + '% mensual', 20, yPos);
                
                yPos += 12;
                
                // Preparar datos para la tabla
                const tableData = tablaAmortizacionActual.datos.map(cuota => [
                    cuota.numeroCuota,
                    '$' + cuota.valorCuota.toFixed(2),
                    '$' + cuota.interesPagado.toFixed(2),
                    '$' + cuota.capitalPagado.toFixed(2),
                    '$' + cuota.saldoRestante.toFixed(2)
                ]);
                
                // Crear tabla
                doc.autoTable({
                    startY: yPos,
                    head: [['Cuota #', 'Valor Cuota', 'Interés', 'Capital', 'Saldo']],
                    body: tableData,
                    theme: 'striped',
                    headStyles: {
                        fillColor: [124, 58, 237],
                        textColor: [255, 255, 255],
                        fontStyle: 'bold'
                    },
                    alternateRowStyles: {
                        fillColor: [248, 249, 250]
                    },
                    styles: {
                        fontSize: 9
                    }
                });
                
                // Footer
                const pageCount = doc.internal.getNumberOfPages();
                doc.setFontSize(8);
                doc.setTextColor(100, 100, 100);
                const footerY = doc.internal.pageSize.height - 10;
                doc.text('Este documento ha sido generado automáticamente por el sistema.', 105, footerY - 5, { align: 'center' });
                doc.text('© ' + new Date().getFullYear() + ' Comercializadora MONSTER - Todos los derechos reservados', 105, footerY, { align: 'center' });
                
                // Descargar
                doc.save('Amortizacion_Factura_' + tablaAmortizacionActual.idFactura + '.pdf');
                
                console.log('PDF generado exitosamente');
            } catch (error) {
                console.error('Error al generar PDF:', error);
                alert('Error al generar el PDF: ' + error.message);
            }
            
            return false;
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
