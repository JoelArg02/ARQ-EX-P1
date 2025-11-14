package ec.edu.espe.comercializadora.servlets;

import ec.edu.espe.comercializadora.models.User;
import ec.edu.espe.comercializadora.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/usuarios")
public class UsuarioServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() {
        userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        
        try {
            if ("edit".equals(action)) {
                String idStr = request.getParameter("id");
                if (idStr == null || idStr.trim().isEmpty()) {
                    request.setAttribute("mensaje", "ID de usuario no válido");
                    request.setAttribute("tipo", "error");
                    doGet(request, response);
                    return;
                }
                
                int id = Integer.parseInt(idStr.trim());
                User user = userService.getUserById(id);
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/jsp/usuario-form.jsp").forward(request, response);
            } else if ("delete".equals(action)) {
                String idStr = request.getParameter("id");
                if (idStr == null || idStr.trim().isEmpty()) {
                    request.setAttribute("mensaje", "ID de usuario no válido");
                    request.setAttribute("tipo", "error");
                    doGet(request, response);
                    return;
                }
                
                int id = Integer.parseInt(idStr.trim());
                boolean success = userService.deleteUser(id);
                if (success) {
                    request.setAttribute("mensaje", "Usuario eliminado correctamente");
                    request.setAttribute("tipo", "success");
                } else {
                    request.setAttribute("mensaje", "Error al eliminar usuario");
                    request.setAttribute("tipo", "error");
                }
                // Redirect to avoid resubmission
                response.sendRedirect(request.getContextPath() + "/usuarios");
                return;
            } else if ("new".equals(action)) {
                request.getRequestDispatcher("/WEB-INF/jsp/usuario-form.jsp").forward(request, response);
                return;
            } else {
                // Mostrar lista de usuarios
                List<User> users = userService.getAllUsers();
                request.setAttribute("users", users);
                request.getRequestDispatcher("/WEB-INF/jsp/usuarios.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("mensaje", "Error: " + e.getMessage());
            request.setAttribute("tipo", "error");
            request.getRequestDispatcher("/WEB-INF/jsp/usuarios.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        String nombre = request.getParameter("nombre");
        String contrasena = request.getParameter("contrasena");

        try {
            if ("create".equals(action)) {
                User user = new User();
                user.setNombre(nombre);
                user.setContrasena(contrasena);
                
                boolean success = userService.createUser(user);
                
                if (success) {
                    request.setAttribute("mensaje", "Usuario creado correctamente");
                    request.setAttribute("tipo", "success");
                } else {
                    request.setAttribute("mensaje", "Error al crear usuario");
                    request.setAttribute("tipo", "error");
                }
            } else if ("update".equals(action)) {
                String idStr = request.getParameter("id");
                if (idStr == null || idStr.trim().isEmpty()) {
                    request.setAttribute("mensaje", "ID de usuario no válido");
                    request.setAttribute("tipo", "error");
                    doGet(request, response);
                    return;
                }
                
                int id = Integer.parseInt(idStr.trim());
                User user = new User();
                user.setId(id);
                user.setNombre(nombre);
                user.setContrasena(contrasena);
                
                boolean success = userService.updateUser(user);
                
                if (success) {
                    request.setAttribute("mensaje", "Usuario actualizado correctamente");
                    request.setAttribute("tipo", "success");
                } else {
                    request.setAttribute("mensaje", "Error al actualizar usuario");
                    request.setAttribute("tipo", "error");
                }
            }
            
            // Redirect to avoid resubmission
            response.sendRedirect(request.getContextPath() + "/usuarios");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("mensaje", "Error: " + e.getMessage());
            request.setAttribute("tipo", "error");
            request.getRequestDispatcher("/WEB-INF/jsp/usuario-form.jsp").forward(request, response);
        }
    }
}