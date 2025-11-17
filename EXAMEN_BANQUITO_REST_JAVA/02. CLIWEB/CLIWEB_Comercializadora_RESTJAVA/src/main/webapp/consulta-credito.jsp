<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Consulta de Crédito - Comercializadora MONSTER</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <style>
        .credit-container {
            max-width: 800px;
            margin: 2rem auto;
            padding: 2rem;
        }
        .credit-card {
            background: rgba(31, 41, 55, 0.9);
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
            color: white;
            margin-bottom: 0.5rem;
            font-weight: 600;
        }
        .form-group input {
            padding: 0.75rem;
            border: 2px solid rgba(124, 58, 237, 0.5);
            border-radius: 8px;
            background: rgba(17, 24, 39, 0.8);
            color: white;
            font-size: 1rem;
        }
        .form-group input:focus {
            outline: none;
            border-color: #7c3aed;
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
            background: rgba(31, 41, 55, 0.9);
            border-radius: 15px;
            padding: 2rem;
            margin-top: 2rem;
            border-left: 5px solid #7c3aed;
        }
        .result-title {
            color: #7c3aed;
            font-size: 1.5rem;
            margin-bottom: 1rem;
            font-weight: bold;
        }
        .result-item {
            display: flex;
            justify-content: space-between;
            padding: 0.75rem 0;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }
        .result-item:last-child {
            border-bottom: none;
        }
        .result-label {
            color: rgba(255, 255, 255, 0.7);
            font-weight: 600;
        }
        .result-value {
            color: white;
            font-weight: bold;
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
    <nav class="navbar">
        <div class="nav-container">
            <h1>🛒 COMERCIALIZADORA MONSTER</h1>
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/productos">Productos</a>
                <a href="${pageContext.request.contextPath}/carrito">Factura 📄</a>
                <a href="${pageContext.request.contextPath}/ventas">Mis Ventas</a>
                <a href="${pageContext.request.contextPath}/consulta-credito" class="active">Consultar Crédito</a>
                <a href="${pageContext.request.contextPath}/login?action=logout" class="btn-logout">Salir</a>
            </div>
        </div>
    </nav>

    <div class="container credit-container">
        <h2 style="color: white; text-align: center; font-size: 2rem; margin-bottom: 2rem;">💳 Consulta de Crédito BanQuito</h2>

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
