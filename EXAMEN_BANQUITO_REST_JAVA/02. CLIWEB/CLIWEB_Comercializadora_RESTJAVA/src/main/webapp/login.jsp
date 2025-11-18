<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Comercializadora MONSTER</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body class="login-body">
    <!-- Decoración de fondo -->
    <div class="monster-decoration">
        <div class="circle circle-1"></div>
        <div class="circle circle-2"></div>
        <div class="circle circle-3"></div>
    </div>
    
    <div class="login-container">
        <div class="login-card">
            <!-- Sully Character -->
            <div class="monster-character">
                <div class="sully">
                    <div class="sully-head">
                        <div class="sully-horn horn-left"></div>
                        <div class="sully-horn horn-right"></div>
                        <div class="sully-eye eye-left">
                            <div class="pupil"></div>
                        </div>
                        <div class="sully-eye eye-right">
                            <div class="pupil"></div>
                        </div>
                        <div class="sully-nose"></div>
                        <div class="sully-mouth"></div>
                        <div class="sully-spot spot-1"></div>
                        <div class="sully-spot spot-2"></div>
                        <div class="sully-spot spot-3"></div>
                    </div>
                </div>
            </div>
            
            <div class="login-header">
                <h1>🏢 COMERCIALIZADORA MONSTER</h1>
                <p class="subtitle">¡Bienvenido al mundo de las ofertas monstruosas!</p>
            </div>
            
            <form action="${pageContext.request.contextPath}/login" method="post" class="login-form">
                <div class="form-group">
                    <label for="usuario">
                        <span class="label-icon">👤</span> Usuario
                    </label>
                    <input type="text" id="usuario" name="usuario" placeholder="Ingresa tu usuario" required autofocus>
                </div>
                
                <div class="form-group">
                    <label for="password">
                        <span class="label-icon">🔒</span> Contraseña
                    </label>
                    <input type="password" id="password" name="password" placeholder="Ingresa tu contraseña" required>
                </div>
                
                <% if (request.getAttribute("error") != null) { %>
                    <div class="error-message">
                        ⚠️ <%= request.getAttribute("error") %>
                    </div>
                <% } %>
                
                <button type="submit" class="btn btn-login">
                    <span>Iniciar Sesión</span>
                    <span class="btn-arrow">→</span>
                </button>
            </form>
            
            <div style="margin-top: 20px; padding: 15px; background: rgba(255,255,255,0.9); border-radius: 8px; text-align: center;">
                <small style="color: #666;">
                    <strong>Credenciales:</strong> Usuario: MONSTER | Contraseña: MONSTER9
                </small>
            </div>
        </div>
    </div>
</body>
</html>
