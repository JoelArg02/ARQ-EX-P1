package ec.edu.pinza.cliweb.controllers;

import ec.edu.pinza.cliweb.models.ItemCarrito;
import ec.edu.pinza.cliweb.models.ProductoDTO;
import ec.edu.pinza.cliweb.services.ComercializadoraRestClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del Carrito de Compras (MVC)
 */
@WebServlet("/carrito")
public class CarritoController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
        
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }
        
        request.setAttribute("carrito", carrito);
        request.getRequestDispatcher("/carrito.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        
        @SuppressWarnings("unchecked")
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }
        
        if ("agregar".equals(action)) {
            // Agregar producto al carrito
            Integer idProducto = Integer.parseInt(request.getParameter("idProducto"));
            String nombre = request.getParameter("nombre");
            BigDecimal precio = new BigDecimal(request.getParameter("precio"));
            Integer cantidad = Integer.parseInt(request.getParameter("cantidad"));
            Integer stock = Integer.parseInt(request.getParameter("stock"));
            
            // Buscar si ya existe en el carrito
            boolean encontrado = false;
            Integer cantidadActualEnCarrito = 0;
            
            for (ItemCarrito item : carrito) {
                if (item.getIdProducto().equals(idProducto)) {
                    cantidadActualEnCarrito = item.getCantidad();
                    encontrado = true;
                    break;
                }
            }
            
            // Validar que la cantidad total no exceda el stock
            Integer cantidadTotal = cantidadActualEnCarrito + cantidad;
            if (cantidadTotal > stock) {
                // No permitir agregar más del stock disponible
                session.setAttribute("error", "No se puede agregar. Stock disponible: " + stock + ". Ya tienes " + cantidadActualEnCarrito + " en tu factura.");
                response.sendRedirect(request.getContextPath() + "/productos");
                return;
            }
            
            // Si pasa la validación, agregar o actualizar
            if (encontrado) {
                for (ItemCarrito item : carrito) {
                    if (item.getIdProducto().equals(idProducto)) {
                        item.setCantidad(cantidadTotal);
                        break;
                    }
                }
            } else {
                carrito.add(new ItemCarrito(idProducto, nombre, precio, cantidad));
            }
            
            response.sendRedirect(request.getContextPath() + "/productos");
            
        } else if ("eliminar".equals(action)) {
            // Eliminar item del carrito
            Integer idProducto = Integer.parseInt(request.getParameter("idProducto"));
            carrito.removeIf(item -> item.getIdProducto().equals(idProducto));
            
            response.sendRedirect(request.getContextPath() + "/carrito");
            
        } else if ("vaciar".equals(action)) {
            // Vaciar carrito
            carrito.clear();
            response.sendRedirect(request.getContextPath() + "/carrito");
            
        } else {
            response.sendRedirect(request.getContextPath() + "/carrito");
        }
    }
}
