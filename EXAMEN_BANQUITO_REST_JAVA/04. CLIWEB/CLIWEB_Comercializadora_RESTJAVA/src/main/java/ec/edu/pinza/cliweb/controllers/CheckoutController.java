package ec.edu.pinza.cliweb.controllers;

import ec.edu.pinza.cliweb.models.FacturaResponseDTO;
import ec.edu.pinza.cliweb.models.ItemCarrito;
import ec.edu.pinza.cliweb.services.ComercializadoraRestClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador de Checkout/Pago (MVC)
 */
@WebServlet("/checkout")
public class CheckoutController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Verificar que el usuario esté logueado
        if (session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        @SuppressWarnings("unchecked")
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
        
        if (carrito == null || carrito.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/productos");
            return;
        }
        
        request.setAttribute("carrito", carrito);
        request.getRequestDispatcher("/checkout.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Verificar que el usuario esté logueado
        if (session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        @SuppressWarnings("unchecked")
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
        
        if (carrito == null || carrito.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/productos");
            return;
        }
        
        // Obtener información del usuario de la sesión
        String rol = (String) session.getAttribute("rol");
        String cedulaUsuario = (String) session.getAttribute("cedula");
        boolean isAdmin = "ADMIN".equals(rol);
        
        // Capturar datos del formulario
        String cedula = request.getParameter("cedula");
        String formaPago = request.getParameter("formaPago");
        String numeroCuotasStr = request.getParameter("numeroCuotas");
        
        // VALIDACIÓN DE SEGURIDAD: Si no es admin, forzar su propia cédula
        if (!isAdmin) {
            if (cedulaUsuario == null || cedulaUsuario.isEmpty()) {
                request.setAttribute("error", "Error: No se pudo determinar la cédula del usuario");
                request.setAttribute("carrito", carrito);
                request.getRequestDispatcher("/checkout.jsp").forward(request, response);
                return;
            }
            // Forzar la cédula del usuario logueado (ignorar lo que venga del formulario)
            cedula = cedulaUsuario;
        }
        
        // Validar que se haya proporcionado una cédula
        if (cedula == null || cedula.trim().isEmpty()) {
            request.setAttribute("error", "La cédula del cliente es requerida");
            request.setAttribute("carrito", carrito);
            request.getRequestDispatcher("/checkout.jsp").forward(request, response);
            return;
        }
        
        Integer numeroCuotas = null;
        if ("CREDITO_DIRECTO".equals(formaPago) && numeroCuotasStr != null && !numeroCuotasStr.isEmpty()) {
            numeroCuotas = Integer.parseInt(numeroCuotasStr);
        }
        
        // Convertir carrito a formato API
        List<Map<String, Integer>> items = new ArrayList<>();
        for (ItemCarrito item : carrito) {
            Map<String, Integer> itemMap = new HashMap<>();
            itemMap.put("idProducto", item.getIdProducto());
            itemMap.put("cantidad", item.getCantidad());
            items.add(itemMap);
        }
        
        // Llamar al servicio REST
        ComercializadoraRestClient client = new ComercializadoraRestClient();
        try {
            FacturaResponseDTO resultado = client.crearFactura(cedula, formaPago, numeroCuotas, items);
            
            if (resultado.isExitoso()) {
                // Éxito - vaciar carrito
                carrito.clear();
                request.setAttribute("factura", resultado);
                request.getRequestDispatcher("/factura-exitosa.jsp").forward(request, response);
            } else {
                // Error
                request.setAttribute("error", resultado.getMensaje());
                request.setAttribute("carrito", carrito);
                request.getRequestDispatcher("/checkout.jsp").forward(request, response);
            }
            
        } finally {
            client.close();
        }
    }
}
