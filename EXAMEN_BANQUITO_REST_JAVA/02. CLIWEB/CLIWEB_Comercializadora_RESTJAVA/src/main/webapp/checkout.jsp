<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="ec.edu.pinza.cliweb.models.ItemCarrito" %>
<%@ page import="java.math.BigDecimal" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout - Comercializadora MONSTER</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
    <nav class="navbar">
        <div class="nav-container">
            <h1>🛒 COMERCIALIZADORA MONSTER</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/productos">Productos</a>
                <a href="${pageContext.request.contextPath}/carrito" class="carrito-link">
                    Factura 📄
                    <% 
                        @SuppressWarnings("unchecked")
                        List<ItemCarrito> carritoNav = (List<ItemCarrito>) session.getAttribute("carrito");
                        int totalItemsNav = 0;
                        if (carritoNav != null) {
                            for (ItemCarrito item : carritoNav) {
                                totalItemsNav += item.getCantidad();
                            }
                        }
                    %>
                    <span class="carrito-badge" <%= (totalItemsNav > 0) ? "" : "style='display:none;'" %>><%= totalItemsNav %></span>
                </a>
                <a href="${pageContext.request.contextPath}/ventas">Mis Ventas</a>
                <a href="${pageContext.request.contextPath}/login?action=logout" class="btn-logout">Salir</a>
            </div>
        </div>
    </nav>
    
    <div class="container">
        <div style="text-align: center; margin-bottom: 2rem;">
            <h2 style="font-size: 2.5rem; color: white; text-shadow: 2px 2px 4px rgba(0,0,0,0.3); margin-bottom: 0.5rem;">💳 Finalizar Compra</h2>
            <p style="color: rgba(255,255,255,0.9); font-size: 1.1rem;">Completa tus datos para procesar el pago</p>
        </div>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <div class="checkout-grid">
            <!-- Resumen del pedido -->
            <div class="checkout-resumen">
                <h3>Resumen del Pedido</h3>
                <%
                    @SuppressWarnings("unchecked")
                    List<ItemCarrito> carrito = (List<ItemCarrito>) request.getAttribute("carrito");
                    BigDecimal total = BigDecimal.ZERO;
                    if (carrito != null) {
                        for (ItemCarrito item : carrito) {
                            total = total.add(item.getSubtotal());
                %>
                    <div class="resumen-item">
                        <span><%= item.getNombre() %> x<%= item.getCantidad() %></span>
                        <span>$<%= item.getSubtotal() %></span>
                    </div>
                <%
                        }
                    }
                %>
                <div class="resumen-total">
                    <strong>Total a Pagar:</strong>
                    <strong class="total-amount">$<%= total %></strong>
                </div>
            </div>
            
            <!-- Formulario de pago -->
            <div class="checkout-form">
                <h3>Datos de Pago</h3>
                <form action="${pageContext.request.contextPath}/checkout" method="post" id="checkoutForm">
                    <div class="form-group">
                        <label for="cedula">Cédula del Cliente *</label>
                        <input type="text" id="cedula" name="cedula" required 
                               pattern="\d{10}" title="Ingrese 10 dígitos"
                               placeholder="Ej: 1750123456">
                        <small>Clientes disponibles: 1750123456, 1750234567, 1750345678, 1750456789, 1750567890</small>
                    </div>
                    
                    <div class="form-group">
                        <label for="formaPago">Forma de Pago *</label>
                        <select id="formaPago" name="formaPago" required onchange="toggleCuotas()">
                            <option value="">Seleccione...</option>
                            <option value="EFECTIVO">Pago en Efectivo</option>
                            <option value="CREDITO_DIRECTO">Crédito Directo (BanQuito)</option>
                        </select>
                    </div>
                    
                    <div class="form-group" id="cuotasGroup" style="display: none;">
                        <label for="numeroCuotas">Número de Cuotas *</label>
                        <select id="numeroCuotas" name="numeroCuotas">
                            <option value="">Seleccione...</option>
                            <option value="3">3 meses</option>
                            <option value="4">4 meses</option>
                            <option value="5">5 meses</option>
                            <option value="6">6 meses</option>
                            <option value="7">7 meses</option>
                            <option value="8">8 meses</option>
                            <option value="9">9 meses</option>
                            <option value="10">10 meses</option>
                            <option value="11">11 meses</option>
                            <option value="12">12 meses</option>
                            <option value="13">13 meses</option>
                            <option value="14">14 meses</option>
                            <option value="15">15 meses</option>
                            <option value="16">16 meses</option>
                            <option value="17">17 meses</option>
                            <option value="18">18 meses</option>
                            <option value="19">19 meses</option>
                            <option value="20">20 meses</option>
                            <option value="21">21 meses</option>
                            <option value="22">22 meses</option>
                            <option value="23">23 meses</option>
                            <option value="24">24 meses</option>
                        </select>
                        <small>Tasa de interés: 16% anual - Sistema de amortización francés</small>
                    </div>
                    
                    <div class="form-actions">
                        <a href="${pageContext.request.contextPath}/carrito" class="btn btn-secondary">Volver al Carrito</a>
                        <button type="submit" class="btn btn-success">Confirmar Compra</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <script>
        function toggleCuotas() {
            const formaPago = document.getElementById('formaPago').value;
            const cuotasGroup = document.getElementById('cuotasGroup');
            const numeroCuotas = document.getElementById('numeroCuotas');
            
            if (formaPago === 'CREDITO_DIRECTO') {
                cuotasGroup.style.display = 'block';
                numeroCuotas.required = true;
            } else {
                cuotasGroup.style.display = 'none';
                numeroCuotas.required = false;
                numeroCuotas.value = '';
            }
        }
    </script>
</body>
</html>
