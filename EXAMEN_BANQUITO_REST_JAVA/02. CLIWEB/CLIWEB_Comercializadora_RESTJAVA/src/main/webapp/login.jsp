<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Comercializadora MONSTER</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <style>
        .login-body {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 2rem;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        .login-container {
            width: 100%;
            max-width: 450px;
        }
        
        .login-card {
            background: white;
            border-radius: 20px;
            padding: 3rem;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
        }
        
        .login-header {
            text-align: center;
            margin-bottom: 2rem;
        }
        
        .login-header h1 {
            font-size: 2.5rem;
            color: #1e293b;
            margin-bottom: 0.5rem;
        }
        
        .login-header p {
            color: #64748b;
            font-size: 1rem;
        }
        
        .login-form .form-group {
            margin-bottom: 1.5rem;
        }
        
        .login-form label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 600;
            color: #1e293b;
        }
        
        .login-form input {
            width: 100%;
            padding: 1rem;
            border: 2px solid #e2e8f0;
            border-radius: 10px;
            font-size: 1rem;
            transition: border-color 0.3s;
        }
        
        .login-form input:focus {
            outline: none;
            border-color: #667eea;
        }
        
        .login-form button {
            width: 100%;
            padding: 1rem;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 1.1rem;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        
        .login-form button:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(102, 126, 234, 0.4);
        }
        
        .alert-error {
            background: #fef2f2;
            border: 1px solid #fecaca;
            color: #dc2626;
            padding: 1rem;
            border-radius: 10px;
            margin-bottom: 1.5rem;
            text-align: center;
        }
        
        .login-footer {
            text-align: center;
            margin-top: 2rem;
            padding-top: 1.5rem;
            border-top: 1px solid #e2e8f0;
        }
        
        .login-footer p {
            color: #64748b;
            font-size: 0.9rem;
            margin-bottom: 0.5rem;
        }
        
        .credentials-info {
            background: #f0fdf4;
            border: 1px solid #bbf7d0;
            border-radius: 10px;
            padding: 1rem;
            margin-top: 1rem;
        }
        
        .credentials-info h4 {
            color: #16a34a;
            margin-bottom: 0.5rem;
            font-size: 0.9rem;
        }
        
        .credentials-info ul {
            list-style: none;
            padding: 0;
            margin: 0;
            font-size: 0.85rem;
            color: #1e293b;
        }
        
        .credentials-info li {
            padding: 0.25rem 0;
        }
        
        .credentials-info strong {
            color: #16a34a;
        }
    </style>
</head>
<body class="login-body">
    <div class="login-container">
        <div class="login-card">
            <div class="login-header">
                <h1>?? MONSTER</h1>
                <p>Comercializadora de Electrodomésticos</p>
            </div>
            
            <% if (request.getAttribute("error") != null) { %>
                <div class="alert-error">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>
            
            <form class="login-form" action="${pageContext.request.contextPath}/login" method="post">
                <div class="form-group">
                    <label for="username">Usuario</label>
                    <input type="text" id="username" name="username" 
                           placeholder="Ingrese su usuario o cédula"
                           value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>"
                           required autofocus>
                </div>
                
                <div class="form-group">
                    <label for="password">Contraseña</label>
                    <input type="password" id="password" name="password" 
                           placeholder="Ingrese su contraseña"
                           required>
                </div>
                
                <button type="submit">Iniciar Sesión</button>
            </form>
            
            <div class="login-footer">
                <p>Sistema de Comercialización de Electrodomésticos</p>
                
                <div class="credentials-info">
                    <h4>?? Credenciales de prueba:</h4>
                    <ul>
                        <li><strong>Admin:</strong> MONSTER / MONSTER9</li>
                        <li><strong>Cliente:</strong> [cédula] / abcd1234</li>
                        <li>Ej: 1750123456 / abcd1234</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
