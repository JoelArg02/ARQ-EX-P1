package ec.edu.pinza.cliweb.services;

import ec.edu.pinza.cliweb.models.FacturaResponseDTO;
import ec.edu.pinza.cliweb.models.LoginResponseDTO;
import ec.edu.pinza.cliweb.models.ProductoDTO;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cliente REST para consumir API de Comercializadora
 */
public class ComercializadoraRestClient {
    
    // Para desarrollo local con Docker backend en puerto 8081
    private static final String BASE_URL = "http://localhost:8081/Ex_Comercializadora_RESTJava-1.0-SNAPSHOT/api";
    private final Client client;
    private final WebTarget baseTarget;
    
    public ComercializadoraRestClient() {
        this.client = ClientBuilder.newClient();
        this.baseTarget = client.target(BASE_URL);
    }
    
    /**
     * Autentica un usuario con username y password
     */
    public LoginResponseDTO login(String username, String password) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("username", username);
            request.put("password", password);
            
            Response response = baseTarget
                .path("auth/login")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(request));
            
            if (response.getStatus() == 200) {
                return response.readEntity(LoginResponseDTO.class);
            } else if (response.getStatus() == 401 || response.getStatus() == 400) {
                return response.readEntity(LoginResponseDTO.class);
            }
            
            LoginResponseDTO error = new LoginResponseDTO();
            error.setExitoso(false);
            error.setMensaje("Error de conexiÛn: HTTP " + response.getStatus());
            return error;
            
        } catch (Exception e) {
            e.printStackTrace();
            LoginResponseDTO error = new LoginResponseDTO();
            error.setExitoso(false);
            error.setMensaje("Error de comunicaciÛn con el servidor: " + e.getMessage());
            return error;
        }
    }

    /**
     * Obtiene todos los productos disponibles
     */
    public List<ProductoDTO> obtenerProductos() {
        try {
            Response response = baseTarget
                .path("productos")
                .request(MediaType.APPLICATION_JSON)
                .get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(new GenericType<List<ProductoDTO>>(){});
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtiene un producto por ID
     */
    public ProductoDTO obtenerProductoPorId(Integer id) {
        try {
            Response response = baseTarget
                .path("productos/" + id)
                .request(MediaType.APPLICATION_JSON)
                .get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(ProductoDTO.class);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Crea un nuevo producto
     */
    public ProductoDTO crearProducto(ProductoDTO producto) {
        try {
            Response response = baseTarget
                .path("productos")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(producto));
            
            if (response.getStatus() == 201 || response.getStatus() == 200) {
                return response.readEntity(ProductoDTO.class);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Actualiza un producto existente
     */
    public ProductoDTO actualizarProducto(Integer id, ProductoDTO producto) {
        try {
            Response response = baseTarget
                .path("productos/" + id)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(producto));
            
            if (response.getStatus() == 200) {
                return response.readEntity(ProductoDTO.class);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Elimina un producto
     */
    public boolean eliminarProducto(Integer id) {
        try {
            Response response = baseTarget
                .path("productos/" + id)
                .request(MediaType.APPLICATION_JSON)
                .delete();
            
            return response.getStatus() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Crea una factura (efectivo o cr√©dito)
     */
    public FacturaResponseDTO crearFactura(String cedula, String formaPago, Integer numeroCuotas, List<Map<String, Integer>> items) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("cedulaCliente", cedula);
            request.put("formaPago", formaPago);
            if (numeroCuotas != null) {
                request.put("numeroCuotas", numeroCuotas);
            }
            request.put("items", items);
            
            Response response = baseTarget
                .path("facturas")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(request));
            
            if (response.getStatus() == 201 || response.getStatus() == 200) {
                return response.readEntity(FacturaResponseDTO.class);
            } else if (response.getStatus() == 400) {
                return response.readEntity(FacturaResponseDTO.class);
            }
            
            FacturaResponseDTO error = new FacturaResponseDTO();
            error.setExitoso(false);
            error.setMensaje("Error: HTTP " + response.getStatus());
            return error;
            
        } catch (Exception e) {
            e.printStackTrace();
            FacturaResponseDTO error = new FacturaResponseDTO();
            error.setExitoso(false);
            error.setMensaje("Error de comunicaci√≥n: " + e.getMessage());
            return error;
        }
    }
    
    /**
     * Obtiene TODAS las facturas del sistema (para administrador)
     */
    public List<FacturaResponseDTO> obtenerTodasLasFacturas() {
        try {
            Response response = baseTarget
                .path("facturas")
                .request(MediaType.APPLICATION_JSON)
                .get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(new GenericType<List<FacturaResponseDTO>>(){});
            }
            return List.of();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Obtiene todas las facturas de un cliente
     */
    public List<FacturaResponseDTO> obtenerFacturasPorCliente(String cedula) {
        try {
            Response response = baseTarget
                .path("facturas/cliente/" + cedula)
                .request(MediaType.APPLICATION_JSON)
                .get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(new GenericType<List<FacturaResponseDTO>>(){});
            }
            return List.of();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Obtiene una factura espec√≠fica por ID
     */
    public FacturaResponseDTO obtenerFacturaPorId(Integer idFactura) {
        try {
            Response response = baseTarget
                .path("facturas/" + idFactura)
                .request(MediaType.APPLICATION_JSON)
                .get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(FacturaResponseDTO.class);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
