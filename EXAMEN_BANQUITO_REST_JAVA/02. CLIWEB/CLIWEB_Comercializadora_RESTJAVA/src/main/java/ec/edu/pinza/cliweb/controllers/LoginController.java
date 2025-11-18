package ec.edu.pinza.cliweb.controllers;

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
        
        // Login simplificado - cualquier usuario puede entrar
        HttpSession session = request.getSession(true);
        session.setAttribute("usuario", "MONSTER");
        session.setAttribute("cedula", "1750123456");
        
        // Redirigir a productos
        response.sendRedirect(request.getContextPath() + "/productos");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Mostrar página de login
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}
