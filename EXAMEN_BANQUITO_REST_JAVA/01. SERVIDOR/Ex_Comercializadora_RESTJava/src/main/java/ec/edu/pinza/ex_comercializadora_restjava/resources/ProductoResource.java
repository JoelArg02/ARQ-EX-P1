package ec.edu.pinza.ex_comercializadora_restjava.resources;

import ec.edu.pinza.ex_comercializadora_restjava.entities.Producto;
import ec.edu.pinza.ex_comercializadora_restjava.repositories.ProductoRepository;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * REST Resource para gestión de productos
 */
@Path("/productos")
public class ProductoResource {
    
    private final ProductoRepository productoRepository = new ProductoRepository();
    
    /**
     * GET /api/productos
     * Obtiene todos los productos disponibles
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerTodosProductos() {
        try {
            List<Producto> productos = productoRepository.findAll();
            return Response.ok(productos).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error al obtener productos: " + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    /**
     * GET /api/productos/{id}
     * Obtiene un producto por ID
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerProductoPorId(@PathParam("id") Integer id) {
        try {
            Producto producto = productoRepository.findById(id).orElse(null);
            if (producto != null) {
                return Response.ok(producto).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Producto no encontrado\"}")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error al obtener producto: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}
