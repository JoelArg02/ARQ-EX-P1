package ec.edu.pinza.cliweb.controllers;

import ec.edu.pinza.cliweb.models.FacturaResponseDTO;
import ec.edu.pinza.cliweb.services.ComercializadoraRestClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Controlador para visualizar historial de ventas
 */
@WebServlet({"/ventas", "/ventas/detalle"})
public class VentasController extends HttpServlet {
    
    private final ComercializadoraRestClient restClient = new ComercializadoraRestClient();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        
        if ("/ventas/detalle".equals(path)) {
            mostrarDetalle(request, response);
        } else {
            listarVentas(request, response);
        }
    }
    
    private void listarVentas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            HttpSession session = request.getSession();
            String rol = (String) session.getAttribute("rol");
            String cedula = (String) session.getAttribute("cedula");
            boolean isAdmin = "ADMIN".equals(rol);
            
            List<FacturaResponseDTO> facturas;
            
            if (isAdmin) {
                // Admin ve TODAS las facturas del sistema
                facturas = restClient.obtenerTodasLasFacturas();
            } else {
                // Cliente solo ve sus propias compras
                facturas = restClient.obtenerFacturasPorCliente(cedula);
            }
            
            request.setAttribute("facturas", facturas);
            request.getRequestDispatcher("/ventas.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar las ventas: " + e.getMessage());
            request.getRequestDispatcher("/ventas.jsp").forward(request, response);
        }
    }
    
    private void mostrarDetalle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idFacturaStr = request.getParameter("id");
        
        if (idFacturaStr == null || idFacturaStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/ventas");
            return;
        }
        
        try {
            HttpSession session = request.getSession();
            String rol = (String) session.getAttribute("rol");
            String cedula = (String) session.getAttribute("cedula");
            boolean isAdmin = "ADMIN".equals(rol);
            
            Integer idFactura = Integer.parseInt(idFacturaStr);
            FacturaResponseDTO factura = restClient.obtenerFacturaPorId(idFactura);
            
            // Validar que el cliente solo pueda ver sus propias facturas
            if (!isAdmin && factura != null && !cedula.equals(factura.getCedulaCliente())) {
                request.setAttribute("error", "No tienes permiso para ver esta factura");
                response.sendRedirect(request.getContextPath() + "/ventas");
                return;
            }
            
            request.setAttribute("factura", factura);
            request.getRequestDispatcher("/venta-detalle.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar el detalle: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ventas");
        }
    }
}
