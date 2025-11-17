package ec.edu.pinza.cliweb.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Controlador de Login (MVC)
 */
@WebServlet("/login")
public class LoginController extends HttpServlet {
    
    private static final String USUARIO_VALIDO = "MONSTER";
    private static final String PASSWORD_VALIDO = "MONSTER9";
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String usuario = request.getParameter("usuario");
        String password = request.getParameter("password");
        
        if (USUARIO_VALIDO.equals(usuario) && PASSWORD_VALIDO.equals(password)) {
            // Login exitoso - crear sesión
            HttpSession session = request.getSession(true);
            session.setAttribute("usuario", usuario);
            session.setAttribute("cedula", "1750123456"); // Cédula hardcodeada para demo
            session.setMaxInactiveInterval(30 * 60); // 30 minutos
            
            response.sendRedirect(request.getContextPath() + "/productos");
        } else {
            // Login fallido
            request.setAttribute("error", "Usuario o contraseña incorrectos");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Logout
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
}
