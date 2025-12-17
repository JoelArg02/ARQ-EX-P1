<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="ec.edu.pinza.cliweb.models.ItemCarrito" %>
<%@ page import="java.math.BigDecimal" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Factura - Comercializadora MONSTER</title>
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
                <a href="${pageContext.request.contextPath}/carrito" class="active carrito-link">
                    Factura 📄
                    <% 
                        @SuppressWarnings("unchecked")
                        List<ItemCarrito> carritoNav = (List<ItemCarrito>) request.getAttribute("carrito");
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
        <div style="text-align: center; margin-bottom: 2rem;">
            <h2 style="font-size: 2.5rem; color: white; text-shadow: 2px 2px 4px rgba(0,0,0,0.3); margin-bottom: 0.5rem;">📄 Mi Factura</h2>
            <p style="color: rgba(255,255,255,0.9); font-size: 1.1rem;">Revisa los productos antes de proceder al pago</p>
        </div>
        
        <%
            @SuppressWarnings("unchecked")
            List<ItemCarrito> carrito = (List<ItemCarrito>) request.getAttribute("carrito");
            if (carrito != null && !carrito.isEmpty()) {
                BigDecimal total = BigDecimal.ZERO;
        %>
            <table class="carrito-tabla">
                <thead>
                    <tr>
                        <th>Producto</th>
                        <th>Precio Unit.</th>
                        <th>Cantidad</th>
                        <th>Subtotal</th>
                        <th>Acción</th>
                    </tr>
                </thead>
                <tbody>
                <%
                    for (ItemCarrito item : carrito) {
                        total = total.add(item.getSubtotal());
                %>
                    <tr>
                        <td><%= item.getNombre() %></td>
                        <td>$<%= item.getPrecio() %></td>
                        <td><%= item.getCantidad() %></td>
                        <td class="subtotal">$<%= item.getSubtotal() %></td>
                        <td>
                            <form action="${pageContext.request.contextPath}/carrito" method="post" style="display: inline;">
                                <input type="hidden" name="action" value="eliminar">
                                <input type="hidden" name="idProducto" value="<%= item.getIdProducto() %>">
                                <button type="submit" class="btn btn-danger btn-small">Eliminar</button>
                            </form>
                        </td>
                    </tr>
                <%
                    }
                %>
                </tbody>
                <tfoot>
                    <tr class="total-row">
                        <td colspan="3"><strong>TOTAL</strong></td>
                        <td class="total-amount"><strong>$<%= total %></strong></td>
                        <td></td>
                    </tr>
                </tfoot>
            </table>
            
            <div class="carrito-acciones">
                <form action="${pageContext.request.contextPath}/carrito" method="post" style="display: inline;">
                    <input type="hidden" name="action" value="vaciar">
                    <button type="submit" class="btn btn-secondary">Limpiar Factura</button>
                </form>
                <a href="${pageContext.request.contextPath}/checkout" class="btn btn-success">Proceder al Pago</a>
            </div>
        <%
            } else {
        %>
            <div class="empty-cart">
                <p>Tu factura está vacía. Agrega productos para continuar.</p>
                <a href="${pageContext.request.contextPath}/productos" class="btn btn-primary">Ver Productos</a>
            </div>
        <%
            }
        %>
    </div>
</body>
</html>
