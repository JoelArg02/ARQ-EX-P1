package ec.edu.espe.comercializadora.servlets;

import ec.edu.espe.comercializadora.models.Factura;
import ec.edu.espe.comercializadora.services.FacturaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/facturas")
public class FacturaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        FacturaService facturaService = new FacturaService();
        
        try {
            List<Factura> facturas = facturaService.getAllFacturas();
            request.setAttribute("facturas", facturas);
            request.getRequestDispatcher("/WEB-INF/jsp/facturas.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar facturas: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/facturas.jsp").forward(request, response);
        }
    }
}