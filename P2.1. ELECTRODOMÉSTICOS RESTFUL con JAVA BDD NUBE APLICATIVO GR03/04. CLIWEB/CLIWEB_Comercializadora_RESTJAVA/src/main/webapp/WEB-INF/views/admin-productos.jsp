<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Administración de Productos - Comercializadora Monster</title>
    <link href="${pageContext.request.contextPath}/assets/css/styles.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }
        
        .admin-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 2rem;
        }
        
        .admin-header {
            text-align: center;
            margin-bottom: 2rem;
        }
        
        .admin-header h2 {
            font-size: 2.5rem;
            color: white;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
            margin-bottom: 0.5rem;
        }
        
        .admin-header p {
            color: rgba(255,255,255,0.9);
            font-size: 1.1rem;
        }
        
        .form-card {
            background: rgba(255, 255, 255, 0.98);
            border-radius: 15px;
            padding: 2rem;
            margin-bottom: 2rem;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
        }
        
        .form-card h3 {
            color: #2563eb;
            margin-bottom: 1.5rem;
            font-size: 1.5rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .products-card {
            background: rgba(255, 255, 255, 0.98);
            border-radius: 15px;
            padding: 2rem;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
        }
        
        .products-card h3 {
            color: #2563eb;
            margin-bottom: 1.5rem;
            font-size: 1.5rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
        }
        
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #e2e8f0;
        }
        
        th {
            background: linear-gradient(135deg, #2563eb 0%, #667eea 100%);
            color: white;
            font-weight: 600;
            font-size: 0.95rem;
        }
        
        tbody tr {
            transition: background 0.2s;
        }
        
        tbody tr:hover {
            background: #f1f5f9;
        }
        
        tbody td img {
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .btn {
            padding: 8px 16px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 600;
            transition: all 0.3s;
            margin-right: 5px;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #2563eb 0%, #667eea 100%);
            color: white;
        }
        
        .btn-success {
            background: #16a34a;
            color: white;
        }
        
        .btn-danger {
            background: #dc2626;
            color: white;
        }
        
        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
        }
        
        .form-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1rem;
            margin-bottom: 1rem;
        }
        
        .form-group {
            display: flex;
            flex-direction: column;
        }
        
        .form-group label {
            color: #1e293b;
            margin-bottom: 0.5rem;
            font-weight: 600;
        }
        
        .form-group input, .form-group select {
            padding: 0.75rem;
            border: 2px solid #e2e8f0;
            border-radius: 8px;
            background: white;
            color: #1e293b;
            font-size: 1rem;
        }
        
        .form-group input:focus, .form-group select:focus {
            outline: none;
            border-color: #2563eb;
        }
        
        .alert {
            padding: 15px;
            border-radius: 10px;
            margin-bottom: 20px;
            font-weight: 600;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        }
        
        .alert-success {
            background: linear-gradient(135deg, #d4edda, #c3e6cb);
            color: #155724;
            border-left: 4px solid #16a34a;
        }
        
        .alert-error {
            background: linear-gradient(135deg, #f8d7da, #f5c6cb);
            color: #721c24;
            border-left: 4px solid #dc2626;
        }
        
        .back-link {
            display: inline-block;
            margin-top: 2rem;
            padding: 0.75rem 1.5rem;
            background: rgba(255, 255, 255, 0.95);
            color: #2563eb;
            border-radius: 10px;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s;
            box-shadow: 0 4px 10px rgba(0,0,0,0.2);
        }
        
        .back-link:hover {
            background: white;
            transform: translateY(-2px);
            box-shadow: 0 6px 15px rgba(0,0,0,0.3);
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="nav-container">
            <h1>🏢 COMERCIALIZADORA MONSTER</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/productos">Productos</a>
                <a href="${pageContext.request.contextPath}/carrito" class="carrito-link">
                    Factura
                    <span class="carrito-badge" style="display:none;">0</span>
                </a>
                <a href="${pageContext.request.contextPath}/ventas">Mis Ventas</a>
                <a href="${pageContext.request.contextPath}/consulta-credito">Consultar Crédito</a>
                <a href="${pageContext.request.contextPath}/admin/productos" class="btn-admin active">Admin</a>
                <a href="${pageContext.request.contextPath}/login?action=logout" class="btn-logout">Salir</a>
            </div>
        </div>
    </nav>
    
    <div class="admin-container">
        <div class="admin-header">
            <h2>🛠️ Administración de Productos</h2>
            <p>Gestiona el catálogo completo de la tienda</p>
        </div>
        
        <c:if test="${not empty mensaje}">
            <div class="alert alert-success">${mensaje}</div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        
        <!-- Formulario para agregar/editar producto -->
        <div class="form-card">
            <h3 id="form-title">➕ Agregar Nuevo Producto</h3>
            <form id="product-form" action="${pageContext.request.contextPath}/admin/productos" method="post">
                <input type="hidden" id="action-type" name="action" value="create">
                <input type="hidden" id="producto-id" name="id">
                
                <div class="form-grid">
                    <div class="form-group">
                        <label for="codigo">Código:</label>
                        <input type="text" id="codigo" name="codigo" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="nombre">Nombre:</label>
                        <input type="text" id="nombre" name="nombre" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="precio">Precio:</label>
                        <input type="number" id="precio" name="precio" step="0.01" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="stock">Stock:</label>
                        <input type="number" id="stock" name="stock" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="imagen">Imagen:</label>
                        <input type="file" id="imagen" name="imagen" accept="image/*" onchange="cargarImagen(event)">
                        <input type="hidden" id="imagenBase64" name="imagenBase64">
                        <img id="imagenPreview" style="display: none; margin-top: 10px; max-width: 200px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);" />
                    </div>
                </div>
                
                <div style="display: flex; gap: 1rem; margin-top: 1rem;">
                    <button type="submit" class="btn btn-success">💾 Guardar</button>
                    <button type="button" class="btn btn-danger" onclick="resetForm()">🔄 Cancelar</button>
                </div>
            </form>
        </div>
        
        <!-- Tabla de productos -->
        <div class="products-card">
            <h3>📦 Lista de Productos</h3>
            <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Código</th>
                    <th>Nombre</th>
                    <th>Precio</th>
                    <th>Stock</th>
                    <th>Imagen</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="p" items="${productos}">
                    <tr>
                        <td>${p.idProducto}</td>
                        <td>${p.codigo}</td>
                        <td>${p.nombre}</td>
                        <td>$${p.precio}</td>
                        <td>${p.stock}</td>
                        <td>
                            <c:if test="${p.imagen != null}">
                                <img src="data:image/jpeg;base64,${p.imagen}" alt="${p.nombre}" style="width: 50px; height: 50px; object-fit: cover;">
                            </c:if>
                            <c:if test="${p.imagen == null}">
                                Sin imagen
                            </c:if>
                        </td>
                        <td>
                            <button type="button" class="btn btn-primary" onclick="editarProducto(${p.idProducto}, '${p.codigo}', '${p.nombre}', ${p.precio}, ${p.stock}, '${p.imagen}')">Editar</button>
                            <a href="${pageContext.request.contextPath}/admin/productos?action=delete&id=${p.idProducto}" 
                               class="btn btn-danger" 
                               onclick="return confirm('¿Está seguro de eliminar este producto?')">Eliminar</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        </div>
        
        <a href="${pageContext.request.contextPath}/productos" class="back-link">← Volver a Productos</a>
    </div>
    
    <script>
        function cargarImagen(event) {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    // Extraer solo el base64, sin el prefijo data:image/...;base64,
                    const base64 = e.target.result.split(',')[1];
                    document.getElementById('imagenBase64').value = base64;
                    
                    // Mostrar preview
                    const preview = document.getElementById('imagenPreview');
                    preview.src = e.target.result;
                    preview.style.display = 'block';
                };
                reader.readAsDataURL(file);
            }
        }
        
        function editarProducto(id, codigo, nombre, precio, stock, imagenBase64) {
            // Cambiar el título del formulario
            document.getElementById('form-title').textContent = '✏️ Editar Producto';
            
            // Llenar los campos del formulario
            document.getElementById('producto-id').value = id;
            document.getElementById('action-type').value = 'update';
            document.getElementById('codigo').value = codigo;
            document.getElementById('nombre').value = nombre;
            document.getElementById('precio').value = precio;
            document.getElementById('stock').value = stock;
            
            // Si hay imagen, mostrar preview
            if (imagenBase64 && imagenBase64 !== 'null' && imagenBase64.length > 0) {
                document.getElementById('imagenBase64').value = imagenBase64;
                const preview = document.getElementById('imagenPreview');
                preview.src = 'data:image/jpeg;base64,' + imagenBase64;
                preview.style.display = 'block';
            }
            
            // Scroll al formulario
            document.getElementById('product-form').scrollIntoView({ behavior: 'smooth' });
        }
        
        function resetForm() {
            // Limpiar el formulario
            document.getElementById('product-form').reset();
            document.getElementById('producto-id').value = '';
            document.getElementById('action-type').value = 'create';
            document.getElementById('imagenBase64').value = '';
            document.getElementById('imagenPreview').style.display = 'none';
            
            // Restaurar título
            document.getElementById('form-title').textContent = '➕ Agregar Nuevo Producto';
        }
    </script>
</body>
</html>
