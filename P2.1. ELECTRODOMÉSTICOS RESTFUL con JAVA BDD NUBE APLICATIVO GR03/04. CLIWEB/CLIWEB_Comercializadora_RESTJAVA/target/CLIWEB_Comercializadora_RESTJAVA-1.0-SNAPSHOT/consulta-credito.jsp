<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Consulta de Crédito - Comercializadora MONSTER</title>
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
                <a href="${pageContext.request.contextPath}/ventas">Mis Ventas</a>
                <a href="${pageContext.request.contextPath}/consulta-credito" class="active">Consultar Crédito</a>
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
            <h2 style="font-size: 2.5rem; color: white; text-shadow: 2px 2px 4px rgba(0,0,0,0.3); margin-bottom: 0.5rem;">💳 Consulta de Crédito BanQuito</h2>
            <p style="color: rgba(255,255,255,0.9); font-size: 1.1rem;">Verifica tu elegibilidad y monto máximo autorizado</p>
        </div>
        
    <style>
        .credit-container {
            max-width: 800px;
            margin: 2rem auto;
            padding: 2rem;
        }
        .credit-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 15px;
            padding: 2rem;
            margin-bottom: 1.5rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
        }
        .credit-form {
            display: flex;
            flex-direction: column;
            gap: 1rem;
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
        .form-group input {
            padding: 0.75rem;
            border: 2px solid rgba(124, 58, 237, 0.3);
            border-radius: 8px;
            background: white;
            color: #1e293b;
            font-size: 1rem;
        }
        .form-group input:focus {
            outline: none;
            border-color: #7c3aed;
            background: white;
        }
        .button-group {
            display: flex;
            gap: 1rem;
            margin-top: 1rem;
        }
        .btn-primary, .btn-success {
            flex: 1;
            padding: 1rem;
            border: none;
            border-radius: 8px;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
        }
        .btn-primary {
            background: linear-gradient(135deg, #7c3aed 0%, #667eea 100%);
            color: white;
        }
        .btn-success {
            background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
            color: white;
        }
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(124, 58, 237, 0.4);
        }
        .btn-success:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(34, 197, 94, 0.4);
        }
        .result-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 15px;
            padding: 2rem;
            margin-bottom: 1.5rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
        }
        .result-card h3 {
            color: #1e293b;
            margin-bottom: 1rem;
        }
        .result-info {
            color: #475569;
        }
        .amount-display {
            font-size: 3rem;
            font-weight: bold;
            background: linear-gradient(135deg, #7c3aed 0%, #667eea 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            text-align: center;
            margin: 1rem 0;
        }
        .result-item {
            display: flex;
            justify-content: space-between;
            padding: 0.75rem 0;
            border-bottom: 1px solid #e2e8f0;
        }
        .result-item:last-child {
            border-bottom: none;
        }
        .result-label {
            color: #64748b;
            font-weight: 600;
        }
        .result-value {
            color: #1e293b;
            font-weight: 700;
        }
        .result-value.success {
            color: #22c55e;
        }
        .result-value.error {
            color: #ef4444;
        }
    </style>
</head>
<body>
    <div class="container credit-container">
        <div class="credit-card">
            <form action="${pageContext.request.contextPath}/consulta-credito" method="post" class="credit-form">
                <div class="form-group">
                    <label for="cedula">Cédula del Cliente:</label>
                    <input type="text" id="cedula" name="cedula" placeholder="Ingrese la cédula" 
                           value="<%= request.getParameter("cedula") != null ? request.getParameter("cedula") : "" %>" required>
                </div>

                <div class="button-group">
                    <button type="submit" name="accion" value="validar" class="btn-primary">
                        🔍 Validar Sujeto de Crédito
                    </button>
                    <button type="submit" name="accion" value="monto" class="btn-success">
                        💰 Consultar Monto Máximo
                    </button>
                </div>
            </form>
        </div>

        <% 
            String resultado = (String) request.getAttribute("resultado");
            if (resultado != null) {
        %>
            <div class="result-card">
                <%= resultado %>
            </div>
        <% } %>

        <% if (session.getAttribute("error") != null) { %>
            <div class="alert alert-error" style="margin-top: 1rem;">
                <%= session.getAttribute("error") %>
            </div>
            <% session.removeAttribute("error"); %>
        <% } %>
    </div>
</body>
</html>
