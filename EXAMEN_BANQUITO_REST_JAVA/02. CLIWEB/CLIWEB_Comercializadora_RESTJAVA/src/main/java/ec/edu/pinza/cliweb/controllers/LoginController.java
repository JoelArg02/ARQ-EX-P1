package ec.edu.pinza.cliweb.controllers;

import ec.edu.pinza.cliweb.models.LoginResponseDTO;
import ec.edu.pinza.cliweb.services.ComercializadoraRestClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // Validar campos vacíos
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Usuario y contraseña son requeridos");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }
        
        // Llamar al servicio REST para autenticar
        ComercializadoraRestClient client = new ComercializadoraRestClient();
        try {
            LoginResponseDTO loginResult = client.login(username.trim(), password);
            
            if (loginResult != null && loginResult.isExitoso()) {
                // Login exitoso - crear sesión
                HttpSession session = request.getSession(true);
                session.setAttribute("usuario", loginResult.getUsername());
                session.setAttribute("rol", loginResult.getRol());
                session.setAttribute("cedula", loginResult.getCedula());
                session.setAttribute("nombreCliente", loginResult.getNombreCliente());
                session.setAttribute("idUsuario", loginResult.getIdUsuario());
                session.setAttribute("isAdmin", loginResult.isAdmin());
                
                // Redirigir a productos
                response.sendRedirect(request.getContextPath() + "/productos");
            } else {
                // Login fallido
                String mensaje = loginResult != null ? loginResult.getMensaje() : "Error de conexión";
                request.setAttribute("error", mensaje);
                request.setAttribute("username", username);
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } finally {
            client.close();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // Logout
        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Si ya está logueado, redirigir a productos
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuario") != null) {
            response.sendRedirect(request.getContextPath() + "/productos");
            return;
        }
        
        // Mostrar página de login
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}
