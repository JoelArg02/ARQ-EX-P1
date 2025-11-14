<%@ page import="java.util.List" %>
<%@ page import="ec.edu.espe.comercializadora.models.*" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Facturar - Comercializadora</title>
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
        select, input[type="number"] {
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
            margin-right: 10px;
        }
        button:hover {
            background-color: #45a049;
        }
        .btn-primary {
            background-color: #1976D2;
        }
        .btn-primary:hover {
            background-color: #1565C0;
        }
        .carrito-item {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 10px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .elegibilidad {
            background-color: #e8f5e8;
            border: 1px solid #4caf50;
            padding: 15px;
            border-radius: 8px;
            margin: 15px 0;
            animation: slideDown 0.3s ease-out;
        }
        .elegibilidad.no-elegible {
            background-color: #ffebee;
            border-color: #f44336;
        }
        .loading {
            background-color: #fff3e0;
            border-color: #ff9800;
        }
        .elegibilidad-icon {
            font-size: 24px;
            margin-right: 10px;
        }
        .verificacion-loading {
            display: none;
            background-color: #e3f2fd;
            border: 1px solid #2196f3;
            color: #1976d2;
            padding: 15px;
            border-radius: 8px;
            margin: 15px 0;
            text-align: center;
        }
        .spinner {
            display: inline-block;
            width: 20px;
            height: 20px;
            border: 3px solid #f3f3f3;
            border-top: 3px solid #2196f3;
            border-radius: 50%;
            animation: spin 1s linear infinite;
            margin-right: 10px;
        }
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        @keyframes slideDown {
            from { opacity: 0; transform: translateY(-10px); }
            to { opacity: 1; transform: translateY(0); }
        }
        .payment-options {
            display: flex;
            gap: 20px;
            margin: 15px 0;
        }
        .radio-group {
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .totales {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            border: 1px solid #dee2e6;
        }
        .totales p {
            margin: 5px 0;
            display: flex;
            justify-content: space-between;
        }
        .total-final {
            font-size: 18px;
            font-weight: bold;
            border-top: 2px solid #1976D2;
            padding-top: 10px;
            margin-top: 10px;
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
        <h1>COMERCIALIZADORA - FACTURAR</h1>
    </div>

    <div class="nav-container">
        <div class="nav">
            <a href="main">Principal</a>
            <a href="clientes">Clientes</a>
            <a href="productos">Productos</a>
            <a href="facturar" class="active">Facturar</a>
            <a href="facturas">Facturas</a>
        </div>
    </div>

    <div class="content">
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
            <h2>Seleccionar Cliente</h2>
            <%
                Cliente clienteSeleccionado = (Cliente) request.getAttribute("clienteSeleccionado");
                if (clienteSeleccionado != null) {
            %>
                <div class="success">
                    ✅ Cliente seleccionado: <strong><%= clienteSeleccionado.getNombreCompleto() %></strong> (Cédula: <%= clienteSeleccionado.getCedula() %>)
                    <form method="post" action="facturar" style="display: inline; margin-left: 15px;">
                        <input type="hidden" name="action" value="cambiarCliente">
                        <button type="submit" style="background-color: #ff9800; padding: 5px 10px; font-size: 12px;">
                            Cambiar Cliente
                        </button>
                    </form>
                </div>
            <% } else { %>
                <form method="post" action="facturar">
                    <input type="hidden" name="action" value="verificarCredito">
                    
                    <div class="form-group">
                        <label for="cedula">Cliente:</label>
                        <select id="cedula" name="cedula" required>
                            <option value="">Seleccione un cliente...</option>
                            <%
                                @SuppressWarnings("unchecked")
                                List<Cliente> clientes = (List<Cliente>) request.getAttribute("clientes");
                                if (clientes != null) {
                                    for (Cliente cliente : clientes) {
                            %>
                                <option value="<%= cliente.getCedula() %>" <%= cliente.getCedula().equals(request.getAttribute("cedulaSeleccionada")) ? "selected" : "" %>>
                                    <%= cliente.getNombreCompleto() %> - <%= cliente.getCedula() %>
                                </option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>
                    
                    <button type="submit" class="btn-primary" id="verificarBtn">Verificar Elegibilidad de Crédito</button>
                </form>
                
                <div class="verificacion-loading" id="loadingDiv">
                    <div class="spinner"></div>Verificando elegibilidad de crédito...
                </div>
            <% } %>
        </div>

        <% 
            ElegibilidadCredito elegibilidad = (ElegibilidadCredito) request.getAttribute("elegibilidad");
            if (elegibilidad != null) {
        %>
            <div class="section-card">
                <h2>📊 Resultado de Verificación de Crédito</h2>
                <div class="elegibilidad <%= elegibilidad.isEsElegible() ? "" : "no-elegible" %>">
                    <div style="display: flex; align-items: center; margin-bottom: 10px;">
                        <span class="elegibilidad-icon"><%= elegibilidad.isEsElegible() ? "✅" : "❌" %></span>
                        <strong style="font-size: 18px;">
                            <%= elegibilidad.isEsElegible() ? "ELEGIBLE PARA CRÉDITO" : "NO ELEGIBLE PARA CRÉDITO" %>
                        </strong>
                    </div>
                    <p><strong>📝 Mensaje del sistema:</strong> <%= elegibilidad.getMensaje() %></p>
                    <% if (elegibilidad.isEsElegible() && elegibilidad.getMontoMaximoCredito() > 0) { %>
                        <p><strong>💰 Monto máximo de crédito:</strong> $<%= String.format("%.2f", elegibilidad.getMontoMaximoCredito()) %></p>
                    <% } %>
                </div>
            </div>
        <% } %>

        <div class="section-card">
            <h2>Agregar Producto al Carrito</h2>
            <form method="post" action="facturar">
                <input type="hidden" name="action" value="agregarProducto">
                
                <div class="form-group">
                    <label for="productoId">Producto:</label>
                    <select id="productoId" name="productoId" required>
                        <option value="">Seleccione un producto...</option>
                        <%
                            @SuppressWarnings("unchecked")
                            List<Electrodomestico> productos = (List<Electrodomestico>) request.getAttribute("productos");
                            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
                            
                            if (productos != null) {
                                for (Electrodomestico producto : productos) {
                        %>
                            <option value="<%= producto.getId() %>">
                                <%= producto.getNombre() %> - <%= currencyFormat.format(producto.getPrecioVenta()) %>
                            </option>
                        <%
                                }
                            }
                        %>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="cantidad">Cantidad:</label>
                    <input type="number" id="cantidad" name="cantidad" value="1" min="1" required>
                </div>
                
                <button type="submit">Agregar al Carrito</button>
            </form>
        </div>

        <div class="section-card">
            <h2>Carrito de Compras</h2>
            <%
                @SuppressWarnings("unchecked")
                List<ProductoCarrito> carrito = (List<ProductoCarrito>) session.getAttribute("carrito");
                double subtotal = 0;
                
                if (carrito != null && !carrito.isEmpty()) {
                    for (ProductoCarrito item : carrito) {
                        subtotal += item.getSubtotal();
            %>
                <div class="carrito-item">
                    <div>
                        <strong><%= item.getProducto().getNombre() %></strong><br>
                        Cantidad: <%= item.getCantidad() %> | 
                        Precio unitario: <%= currencyFormat.format(item.getProducto().getPrecioVenta()) %>
                    </div>
                    <div>
                        <strong><%= currencyFormat.format(item.getSubtotal()) %></strong>
                    </div>
                </div>
            <%
                    }
                    
                    double iva = subtotal * 0.15;
                    double total = subtotal + iva;
            %>
                <div class="totales">
                    <p><span>Subtotal:</span><span><%= currencyFormat.format(subtotal) %></span></p>
                    <p><span>IVA (15%):</span><span><%= currencyFormat.format(iva) %></span></p>
                    <p class="total-final"><span>TOTAL:</span><span><%= currencyFormat.format(total) %></span></p>
                </div>

                <form method="post" action="facturar">
                    <input type="hidden" name="action" value="generarFactura">
                    <input type="hidden" name="clienteId" value="<%= request.getAttribute("clienteSeleccionado") != null ? ((Cliente)request.getAttribute("clienteSeleccionado")).getId() : "" %>">
                    
                    <h3>Forma de Pago</h3>
                    <div class="payment-options">
                        <div class="radio-group">
                            <input type="radio" id="efectivo" name="formaPago" value="0" checked>
                            <label for="efectivo">Efectivo</label>
                        </div>
                        <div class="radio-group">
                            <input type="radio" id="credito" name="formaPago" value="1">
                            <label for="credito">Crédito</label>
                        </div>
                    </div>
                    
                    <button type="submit" class="btn-primary">Generar Factura</button>
                </form>
            <%
                } else {
            %>
                <p>El carrito está vacío. Agregue productos para continuar.</p>
            <%
                }
            %>
        </div>
    </div>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Mejorar la experiencia de verificación de crédito
            const verificarForm = document.querySelector('form[action="facturar"] input[value="verificarCredito"]').parentNode;
            const submitBtn = document.getElementById('verificarBtn');
            const loadingDiv = document.getElementById('loadingDiv');
            
            verificarForm.addEventListener('submit', function(e) {
                const cedulaSelect = document.getElementById('cedula');
                if (!cedulaSelect.value) {
                    e.preventDefault();
                    alert('⚠️ Por favor seleccione un cliente antes de verificar.');
                    return;
                }
                
                // Mostrar indicador de carga
                loadingDiv.style.display = 'block';
                
                // Deshabilitar el botón
                submitBtn.disabled = true;
                submitBtn.innerHTML = '🔄 Verificando...';
                submitBtn.style.backgroundColor = '#ff9800';
            });
            
            // Función para mostrar información del cliente seleccionado
            const cedulaSelect = document.getElementById('cedula');
            if (cedulaSelect) {
                cedulaSelect.addEventListener('change', function() {
                    const selectedOption = this.options[this.selectedIndex];
                    if (selectedOption.value) {
                        console.log('Cliente seleccionado:', selectedOption.text);
                        // Resetear el estado del botón cuando se cambie el cliente
                        if (submitBtn) {
                            submitBtn.disabled = false;
                            submitBtn.innerHTML = 'Verificar Elegibilidad de Crédito';
                            submitBtn.style.backgroundColor = '#1976D2';
                        }
                        // Ocultar loading si está visible
                        if (loadingDiv) {
                            loadingDiv.style.display = 'none';
                        }
                    }
                });
            }
            
            // Mejorar los mensajes de error y éxito
            const errorMsgs = document.querySelectorAll('.error');
            const successMsgs = document.querySelectorAll('.success');
            
            // Añadir iconos a los mensajes
            errorMsgs.forEach(function(msg) {
                if (!msg.innerHTML.includes('⚠️')) {
                    msg.innerHTML = '⚠️ ' + msg.innerHTML;
                }
            });
            
            successMsgs.forEach(function(msg) {
                if (!msg.innerHTML.includes('✅')) {
                    msg.innerHTML = '✅ ' + msg.innerHTML;
                }
            });
            
            // Ocultar el loading después de 10 segundos si no hay respuesta
            if (loadingDiv && loadingDiv.style.display === 'block') {
                setTimeout(function() {
                    loadingDiv.style.display = 'none';
                    if (submitBtn) {
                        submitBtn.disabled = false;
                        submitBtn.innerHTML = 'Verificar Elegibilidad de Crédito';
                        submitBtn.style.backgroundColor = '#1976D2';
                    }
                }, 10000);
            }
        });
    </script>
</body>
</html>