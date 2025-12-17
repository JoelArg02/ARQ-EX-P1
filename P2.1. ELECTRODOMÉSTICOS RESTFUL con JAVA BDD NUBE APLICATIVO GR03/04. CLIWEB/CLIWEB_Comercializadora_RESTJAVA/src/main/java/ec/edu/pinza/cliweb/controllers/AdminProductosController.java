package ec.edu.pinza.cliweb.controllers;

import ec.edu.pinza.cliweb.models.ProductoDTO;
import ec.edu.pinza.cliweb.services.ComercializadoraRestClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador para administración de productos
 */
@WebServlet(name = "AdminProductosController", urlPatterns = {"/admin/productos"})
public class AdminProductosController extends HttpServlet {
    
    private final ComercializadoraRestClient restClient = new ComercializadoraRestClient();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("edit".equals(action)) {
            // Cargar producto para editar
            String idStr = request.getParameter("id");
            if (idStr != null) {
                Integer id = Integer.parseInt(idStr);
                ProductoDTO producto = restClient.obtenerProductoPorId(id);
                request.setAttribute("producto", producto);
            }
        } else if ("delete".equals(action)) {
            // Eliminar producto
            String idStr = request.getParameter("id");
            if (idStr != null) {
                Integer id = Integer.parseInt(idStr);
                boolean eliminado = restClient.eliminarProducto(id);
                if (eliminado) {
                    request.setAttribute("mensaje", "Producto eliminado exitosamente");
                } else {
                    request.setAttribute("error", "Error al eliminar producto");
                }
            }
        }
        
        // Cargar lista de productos
        List<ProductoDTO> productos = restClient.obtenerProductos();
        request.setAttribute("productos", productos);
        
        request.getRequestDispatcher("/WEB-INF/views/admin-productos.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");
        String nombre = request.getParameter("nombre");
        String codigo = request.getParameter("codigo");
        String precioStr = request.getParameter("precio");
        String stockStr = request.getParameter("stock");
        String imagenBase64 = request.getParameter("imagenBase64"); // Base64
        
        try {
            ProductoDTO producto = new ProductoDTO();
            producto.setNombre(nombre);
            producto.setCodigo(codigo);
            producto.setPrecio(new BigDecimal(precioStr));
            producto.setStock(Integer.parseInt(stockStr));
            
            // Solo actualizar la imagen si se proporcionó una nueva
            if (imagenBase64 != null && !imagenBase64.trim().isEmpty()) {
                producto.setImagen(imagenBase64);
            }
            
            if ("update".equals(action) && idStr != null && !idStr.isEmpty()) {
                // Actualizar
                Integer id = Integer.parseInt(idStr);
                producto.setIdProducto(id);
                ProductoDTO actualizado = restClient.actualizarProducto(id, producto);
                if (actualizado != null) {
                    request.setAttribute("mensaje", "Producto actualizado exitosamente");
                } else {
                    request.setAttribute("error", "Error al actualizar producto");
                }
            } else {
                // Crear
                ProductoDTO creado = restClient.crearProducto(producto);
                if (creado != null) {
                    request.setAttribute("mensaje", "Producto creado exitosamente");
                } else {
                    request.setAttribute("error", "Error al crear producto");
                }
            }
            
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
        }
        
        // Recargar lista
        List<ProductoDTO> productos = restClient.obtenerProductos();
        request.setAttribute("productos", productos);
        
        request.getRequestDispatcher("/WEB-INF/views/admin-productos.jsp").forward(request, response);
    }
}
