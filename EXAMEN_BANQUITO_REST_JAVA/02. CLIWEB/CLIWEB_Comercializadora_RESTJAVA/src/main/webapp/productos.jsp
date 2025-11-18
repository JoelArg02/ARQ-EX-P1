<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="ec.edu.pinza.cliweb.models.ProductoDTO" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Productos - Comercializadora MONSTER</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
    <nav class="navbar">
        <div class="nav-container">
            <h1>🛒 COMERCIALIZADORA MONSTER</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/productos" class="active">Productos</a>
                <a href="${pageContext.request.contextPath}/carrito" class="carrito-link">
                    Factura 📄
                    <% 
                        @SuppressWarnings("unchecked")
                        List<ec.edu.pinza.cliweb.models.ItemCarrito> carrito = 
                            (List<ec.edu.pinza.cliweb.models.ItemCarrito>) session.getAttribute("carrito");
                        int totalItems = 0;
                        if (carrito != null) {
                            for (ec.edu.pinza.cliweb.models.ItemCarrito item : carrito) {
                                totalItems += item.getCantidad();
                            }
                        }
                    %>
                    <span class="carrito-badge" id="carrito-badge" <%= (totalItems > 0) ? "" : "style='display:none;'" %>><%= totalItems %></span>
                </a>
                <a href="${pageContext.request.contextPath}/ventas">Mis Ventas</a>
                <a href="${pageContext.request.contextPath}/consulta-credito">Consultar Crédito</a>
                <a href="${pageContext.request.contextPath}/admin/productos" style="background: linear-gradient(135deg, #FF9800 0%, #FF5722 100%); padding: 8px 16px; border-radius: 5px;">🛠️ Admin</a>
                <a href="${pageContext.request.contextPath}/login?action=logout" class="btn-logout">Salir</a>
            </div>
        </div>
    </nav>
    
    <div class="container">
        <div style="text-align: center; margin-bottom: 3rem;">
            <h2 style="font-size: 2.5rem; color: white; text-shadow: 2px 2px 4px rgba(0,0,0,0.3); margin-bottom: 0.5rem;">🏪 Catálogo de Productos</h2>
            <p style="color: rgba(255,255,255,0.9); font-size: 1.1rem;">Encuentra los mejores electrodomésticos para tu hogar</p>
        </div>
        
        <% if (session.getAttribute("error") != null) { %>
            <div class="alert alert-error" style="margin-bottom: 2rem;">
                <%= session.getAttribute("error") %>
            </div>
            <% session.removeAttribute("error"); %>
        <% } %>
        
        <div class="productos-grid">
            <%
                @SuppressWarnings("unchecked")
                List<ProductoDTO> productos = (List<ProductoDTO>) request.getAttribute("productos");
                if (productos != null && !productos.isEmpty()) {
                    for (ProductoDTO producto : productos) {
            %>
                <div class="producto-card">
                    <% if (producto.getImagen() != null && !producto.getImagen().isEmpty()) { %>
                        <div class="producto-imagen">
                            <img src="data:image/jpeg;base64,<%= producto.getImagen() %>" 
                                 alt="<%= producto.getNombre() %>" 
                                 style="width: 100%; height: 200px; object-fit: cover; border-radius: 8px 8px 0 0;">
                        </div>
                    <% } else { %>
                        <div class="producto-imagen-placeholder" style="width: 100%; height: 200px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); display: flex; align-items: center; justify-content: center; border-radius: 8px 8px 0 0;">
                            <span style="font-size: 4rem; color: white; opacity: 0.5;">📦</span>
                        </div>
                    <% } %>
                    <div class="producto-header">
                        <h3><%= producto.getNombre() %></h3>
                        <span class="producto-codigo">Código: <%= producto.getCodigo() %></span>
                    </div>
                    <div class="producto-body">
                        <div class="producto-precio">$<%= producto.getPrecio() %></div>
                        <div class="producto-stock">
                            <% if (producto.getStock() > 0) { %>
                                <span class="stock-disponible">✓ En stock (<%= producto.getStock() %>)</span>
                            <% } else { %>
                                <span class="stock-agotado">✗ Agotado</span>
                            <% } %>
                        </div>
                    </div>
                    <% if (producto.getStock() > 0) { %>
                    <form action="${pageContext.request.contextPath}/carrito" method="post" class="producto-form">
                        <input type="hidden" name="action" value="agregar">
                        <input type="hidden" name="idProducto" value="<%= producto.getIdProducto() %>">
                        <input type="hidden" name="nombre" value="<%= producto.getNombre() %>">
                        <input type="hidden" name="precio" value="<%= producto.getPrecio() %>">
                        <input type="hidden" name="stock" value="<%= producto.getStock() %>">
                        <div class="cantidad-group">
                            <label for="cantidad-<%= producto.getIdProducto() %>">Cantidad:</label>
                            <input type="number" id="cantidad-<%= producto.getIdProducto() %>" 
                                   name="cantidad" min="1" max="<%= producto.getStock() %>" value="1" required>
                        </div>
                        <button type="submit" class="btn btn-primary">Agregar a Factura</button>
                    </form>
                    <% } else { %>
                    <button class="btn btn-disabled" disabled>No Disponible</button>
                    <% } %>
                </div>
            <%
                    }
                } else {
            %>
                <p class="no-data">No hay productos disponibles.</p>
            <%
                }
            %>
        </div>
    </div>
    
    <script>
        // Mostrar mensaje de confirmación al agregar al carrito
        document.addEventListener('DOMContentLoaded', function() {
            const forms = document.querySelectorAll('.producto-form');
            
            forms.forEach(form => {
                form.addEventListener('submit', function(e) {
                    // Mostrar mensaje temporal
                    const badge = document.getElementById('carrito-badge');
                    const currentCount = parseInt(badge.textContent || '0');
                    const cantidad = parseInt(form.querySelector('input[name="cantidad"]').value);
                    const newCount = currentCount + cantidad;
                    
                    // Actualizar badge
                    badge.textContent = newCount;
                    badge.style.display = 'inline-block';
                    badge.style.animation = 'pulse 0.5s ease';
                    
                    // Mostrar mensaje flotante
                    const mensaje = document.createElement('div');
                    mensaje.className = 'mensaje-agregado';
                    mensaje.textContent = '✓ Producto agregado a la factura';
                    document.body.appendChild(mensaje);
                    
                    setTimeout(() => {
                        mensaje.remove();
                    }, 2000);
                });
            });
        });
    </script>
</body>
</html>
