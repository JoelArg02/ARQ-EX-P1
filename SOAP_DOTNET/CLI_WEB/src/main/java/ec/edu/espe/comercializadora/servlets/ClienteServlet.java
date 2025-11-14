package ec.edu.espe.comercializadora.servlets;

import ec.edu.espe.comercializadora.models.Cliente;
import ec.edu.espe.comercializadora.services.ClienteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/clientes")
public class ClienteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ClienteService clienteService = new ClienteService();
        
        try {
            List<Cliente> clientes = clienteService.getAllClientes();
            request.setAttribute("clientes", clientes);
            request.getRequestDispatcher("/WEB-INF/jsp/clientes.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar clientes: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/clientes.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("create".equals(action)) {
            createCliente(request, response);
        }
    }

    private void createCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cliente cliente = new Cliente();
        cliente.setCedula(request.getParameter("cedula"));
        cliente.setNombreCompleto(request.getParameter("nombreCompleto"));
        cliente.setCorreo(request.getParameter("correo"));
        cliente.setTelefono(request.getParameter("telefono"));
        cliente.setDireccion(request.getParameter("direccion"));
        
        ClienteService clienteService = new ClienteService();
        
        try {
            boolean created = clienteService.createCliente(cliente);
            if (created) {
                response.sendRedirect(request.getContextPath() + "/clientes");
            } else {
                request.setAttribute("error", "Error al crear el cliente");
                doGet(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error al crear cliente: " + e.getMessage());
            doGet(request, response);
        }
    }
}