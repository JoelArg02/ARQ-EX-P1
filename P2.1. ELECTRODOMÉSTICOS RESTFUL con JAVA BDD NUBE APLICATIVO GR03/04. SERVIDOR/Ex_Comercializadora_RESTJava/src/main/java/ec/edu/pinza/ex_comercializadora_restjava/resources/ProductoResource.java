package ec.edu.pinza.ex_comercializadora_restjava.resources;

import ec.edu.pinza.ex_comercializadora_restjava.entities.Producto;
import ec.edu.pinza.ex_comercializadora_restjava.repositories.ProductoRepository;
import jakarta.ws.rs.*;
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
    
    /**
     * POST /api/productos
     * Crea un nuevo producto
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearProducto(Producto producto) {
        try {
            if (producto.getCodigo() == null || producto.getNombre() == null || producto.getPrecio() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Código, nombre y precio son obligatorios\"}")
                        .build();
            }
            
            Producto nuevoProducto = productoRepository.save(producto);
            return Response.status(Response.Status.CREATED).entity(nuevoProducto).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error al crear producto: " + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    /**
     * PUT /api/productos/{id}
     * Actualiza un producto existente
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarProducto(@PathParam("id") Integer id, Producto producto) {
        try {
            Producto productoExistente = productoRepository.findById(id).orElse(null);
            if (productoExistente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Producto no encontrado\"}")
                        .build();
            }
            
            producto.setIdProducto(id);
            Producto productoActualizado = productoRepository.save(producto);
            return Response.ok(productoActualizado).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error al actualizar producto: " + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    /**
     * DELETE /api/productos/{id}
     * Elimina un producto
     */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarProducto(@PathParam("id") Integer id) {
        try {
            Producto producto = productoRepository.findById(id).orElse(null);
            if (producto == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Producto no encontrado\"}")
                        .build();
            }
            
            productoRepository.delete(id);
            return Response.ok("{\"mensaje\": \"Producto eliminado correctamente\"}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error al eliminar producto: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}
