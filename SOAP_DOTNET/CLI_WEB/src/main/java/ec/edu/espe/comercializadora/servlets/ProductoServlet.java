package ec.edu.espe.comercializadora.servlets;

import ec.edu.espe.comercializadora.models.Electrodomestico;
import ec.edu.espe.comercializadora.services.ElectrodomesticoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/productos")
public class ProductoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        ElectrodomesticoService electrodomesticoService = new ElectrodomesticoService();
        
        try {
            List<Electrodomestico> electrodomesticos = electrodomesticoService.getAllElectrodomesticos();
            request.setAttribute("productos", electrodomesticos);
            request.getRequestDispatcher("/WEB-INF/jsp/productos.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar productos: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/productos.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        String action = request.getParameter("action");
        
        if ("create".equals(action)) {
            createElectrodomestico(request, response);
        }
    }

    private void createElectrodomestico(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Electrodomestico electrodomestico = new Electrodomestico();
        electrodomestico.setNombre(request.getParameter("nombre"));
        electrodomestico.setDescripcion(request.getParameter("descripcion"));
        electrodomestico.setMarca(request.getParameter("marca"));
        
        String precioVentaStr = request.getParameter("precioVenta");
        if (precioVentaStr == null || precioVentaStr.trim().isEmpty()) {
            request.setAttribute("error", "El precio de venta es obligatorio");
            doGet(request, response);
            return;
        }
        
        try {
            electrodomestico.setPrecioVenta(Double.parseDouble(precioVentaStr.trim()));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "El precio de venta debe ser un número válido");
            doGet(request, response);
            return;
        }
        
        electrodomestico.setActivo("on".equals(request.getParameter("activo")));
        
        ElectrodomesticoService electrodomesticoService = new ElectrodomesticoService();
        
        try {
            boolean created = electrodomesticoService.createElectrodomestico(electrodomestico);
            if (created) {
                response.sendRedirect(request.getContextPath() + "/productos");
            } else {
                request.setAttribute("error", "Error al crear el producto");
                doGet(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error al crear producto: " + e.getMessage());
            doGet(request, response);
        }
    }
}