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
        
        @SuppressWarnings("unchecked")
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
        
        if (carrito == null || carrito.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/productos");
            return;
        }
        
        // Capturar datos del formulario
        String cedula = request.getParameter("cedula");
        String formaPago = request.getParameter("formaPago");
        String numeroCuotasStr = request.getParameter("numeroCuotas");
        
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
