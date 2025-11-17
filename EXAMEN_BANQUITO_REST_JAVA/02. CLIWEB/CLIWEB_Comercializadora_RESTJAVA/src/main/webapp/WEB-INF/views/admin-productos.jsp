<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Administración de Productos - Comercializadora Monster</title>
    <link href="https://fonts.googleapis.com/css2?family=Bangers&family=Quicksand:wght@400;600;700&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/styles.css" rel="stylesheet">
    <style>
        .admin-container {
            max-width: 1200px;
            margin: 50px auto;
            padding: 30px;
            background: white;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
        }
        
        .admin-title {
            font-family: 'Bangers', cursive;
            color: #4A0E4E;
            font-size: 2.5em;
            text-align: center;
            margin-bottom: 30px;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 30px;
        }
        
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        
        th {
            background: linear-gradient(135deg, #4A0E4E 0%, #9C27B0 100%);
            color: white;
            font-weight: 600;
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
            background: linear-gradient(135deg, #4A0E4E 0%, #9C27B0 100%);
            color: white;
        }
        
        .btn-success {
            background: #4CAF50;
            color: white;
        }
        
        .btn-danger {
            background: #f44336;
            color: white;
        }
        
        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
        }
        
        .form-container {
            background: #f5f5f5;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 30px;
        }
        
        .form-group {
            margin-bottom: 15px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: 600;
            color: #4A0E4E;
        }
        
        .form-group input, .form-group textarea {
            width: 100%;
            padding: 10px;
            border: 2px solid #ddd;
            border-radius: 5px;
            font-size: 1em;
        }
        
        .image-preview {
            max-width: 200px;
            max-height: 150px;
            margin-top: 10px;
            border: 2px solid #ddd;
            border-radius: 5px;
        }
        
        .alert {
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        
        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        #imagenPreview {
            display: none;
        }
    </style>
</head>
<body>
    <div class="admin-container">
        <h1 class="admin-title">🛠️ ADMINISTRACIÓN DE PRODUCTOS</h1>
        
        <c:if test="${not empty mensaje}">
            <div class="alert alert-success">${mensaje}</div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        
        <!-- Formulario de producto -->
        <div class="form-container">
            <h2>Formulario de Producto</h2>
            <form method="POST" action="${pageContext.request.contextPath}/admin/productos" id="formProducto">
                <input type="hidden" name="id" id="productoId" value="${producto != null ? producto.idProducto : ''}">
                <input type="hidden" name="imagen" id="imagenBase64">
                
                <div class="form-group">
                    <label for="codigo">Código:</label>
                    <input type="text" id="codigo" name="codigo" required value="${producto != null ? producto.codigo : ''}">
                </div>
                
                <div class="form-group">
                    <label for="nombre">Nombre:</label>
                    <input type="text" id="nombre" name="nombre" required value="${producto != null ? producto.nombre : ''}">
                </div>
                
                <div class="form-group">
                    <label for="precio">Precio:</label>
                    <input type="number" step="0.01" id="precio" name="precio" required value="${producto != null ? producto.precio : ''}">
                </div>
                
                <div class="form-group">
                    <label for="stock">Stock:</label>
                    <input type="number" id="stock" name="stock" required value="${producto != null ? producto.stock : ''}">
                </div>
                
                <div class="form-group">
                    <label for="imagenFile">Imagen del Producto:</label>
                    <input type="file" id="imagenFile" accept="image/*" onchange="cargarImagen(event)">
                    <img id="imagenPreview" class="image-preview" src="" alt="Vista previa">
                    <c:if test="${producto != null && producto.imagen != null}">
                        <img class="image-preview" src="data:image/jpeg;base64,${producto.imagen}" alt="Imagen actual">
                    </c:if>
                </div>
                
                <button type="submit" class="btn btn-success">
                    ${producto != null ? 'Actualizar' : 'Crear'} Producto
                </button>
                <button type="button" class="btn btn-primary" onclick="limpiarFormulario()">Cancelar</button>
            </form>
        </div>
        
        <!-- Tabla de productos -->
        <h2>Lista de Productos</h2>
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
                            <a href="${pageContext.request.contextPath}/admin/productos?action=edit&id=${p.idProducto}" class="btn btn-primary">Editar</a>
                            <a href="${pageContext.request.contextPath}/admin/productos?action=delete&id=${p.idProducto}" 
                               class="btn btn-danger" 
                               onclick="return confirm('¿Está seguro de eliminar este producto?')">Eliminar</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        <a href="${pageContext.request.contextPath}/productos" class="btn btn-primary">Volver a Productos</a>
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
        
        function limpiarFormulario() {
            document.getElementById('formProducto').reset();
            document.getElementById('productoId').value = '';
            document.getElementById('imagenBase64').value = '';
            document.getElementById('imagenPreview').style.display = 'none';
            window.location.href = '${pageContext.request.contextPath}/admin/productos';
        }
    </script>
</body>
</html>
