package ec.edu.pinza.cliweb.controllers;

import ec.edu.pinza.cliweb.models.ProductoDTO;
import ec.edu.pinza.cliweb.services.ComercializadoraRestClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/productos")
public class ProductosController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        ComercializadoraRestClient client = new ComercializadoraRestClient();
        try {
            List<ProductoDTO> productos = client.obtenerProductos();
            request.setAttribute("productos", productos);
            request.getRequestDispatcher("/productos.jsp").forward(request, response);
        } finally {
            client.close();
        }
    }
}
