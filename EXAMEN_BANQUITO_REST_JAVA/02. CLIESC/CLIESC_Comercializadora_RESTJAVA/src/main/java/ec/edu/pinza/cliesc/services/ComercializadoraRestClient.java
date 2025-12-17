package ec.edu.pinza.cliesc.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import ec.edu.pinza.cliesc.models.FacturaResponseDTO;
import ec.edu.pinza.cliesc.models.ItemCarrito;
import ec.edu.pinza.cliesc.models.LoginResponseDTO;
import ec.edu.pinza.cliesc.models.ProductoDTO;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cliente REST alineado a los endpoints usados por CLIWEB.
 */
public class ComercializadoraRestClient {

    // Para desarrollo local con Docker backend en puerto 8081
    private static final String BASE_URL = "http://localhost:8081/Ex_Comercializadora_RESTJava-1.0-SNAPSHOT/api";
    private final HttpClient httpClient;
    private final Gson gson;

    public ComercializadoraRestClient() {
        this.httpClient = HttpClient.newHttpClient();

        // Deserializador para fechas ISO (LocalDate)
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                        LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
                .create();
    }

    // ==================== AUTENTICACIÓN ====================
    
    /**
     * Autentica un usuario con username y password
     */
    public LoginResponseDTO login(String username, String password) {
        try {
            String url = BASE_URL + "/auth/login";
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", username);
            requestBody.put("password", password);
            
            String jsonBody = gson.toJson(requestBody);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200 || response.statusCode() == 401 || response.statusCode() == 400) {
                return gson.fromJson(response.body(), LoginResponseDTO.class);
            }
            
            LoginResponseDTO error = new LoginResponseDTO();
            error.setExitoso(false);
            error.setMensaje("Error de conexión: HTTP " + response.statusCode());
            return error;
            
        } catch (Exception e) {
            e.printStackTrace();
            LoginResponseDTO error = new LoginResponseDTO();
            error.setExitoso(false);
            error.setMensaje("Error de comunicación con el servidor: " + e.getMessage());
            return error;
        }
    }

    // ==================== PRODUCTOS ====================

    /**
     * Obtiene todos los productos.
     */
    public List<ProductoDTO> obtenerProductos() throws Exception {
        String url = BASE_URL + "/productos";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ProductoDTO[] productos = gson.fromJson(response.body(), ProductoDTO[].class);
            return List.of(productos);
        } else {
            throw new Exception("Error al obtener productos: " + response.statusCode());
        }
    }

    /**
     * Obtiene un producto por ID.
     */
    public ProductoDTO obtenerProductoPorId(Long id) throws Exception {
        String url = BASE_URL + "/productos/" + id;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), ProductoDTO.class);
        } else {
            throw new Exception("Error al obtener producto: " + response.statusCode());
        }
    }

    /**
     * Crea un nuevo producto.
     */
    public ProductoDTO crearProducto(ProductoDTO producto) throws Exception {
        String url = BASE_URL + "/productos";
        String jsonBody = gson.toJson(producto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return gson.fromJson(response.body(), ProductoDTO.class);
        } else {
            throw new Exception("Error al crear producto: " + response.statusCode());
        }
    }

    /**
     * Actualiza un producto existente.
     */
    public ProductoDTO actualizarProducto(Long id, ProductoDTO producto) throws Exception {
        String url = BASE_URL + "/productos/" + id;
        String jsonBody = gson.toJson(producto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), ProductoDTO.class);
        } else {
            throw new Exception("Error al actualizar producto: " + response.statusCode());
        }
    }

    /**
     * Elimina un producto.
     */
    public void eliminarProducto(Long id) throws Exception {
        String url = BASE_URL + "/productos/" + id;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Error al eliminar producto: " + response.statusCode());
        }
    }

    // ==================== FACTURAS ====================

    /**
     * Crea una factura (efectivo o credito directo).
     */
    public FacturaResponseDTO crearFactura(String cedula, String formaPago, Integer numeroCuotas,
                                           List<ItemCarrito> items) throws Exception {
        String url = BASE_URL + "/facturas";

        Map<String, Object> body = new HashMap<>();
        body.put("cedulaCliente", cedula);
        body.put("formaPago", formaPago);
        if (numeroCuotas != null) {
            body.put("numeroCuotas", numeroCuotas);
        }

        List<Map<String, Integer>> itemsBody = new ArrayList<>();
        for (ItemCarrito item : items) {
            Map<String, Integer> itemMap = new HashMap<>();
            itemMap.put("idProducto", item.getIdProducto().intValue());
            itemMap.put("cantidad", item.getCantidad());
            itemsBody.add(itemMap);
        }
        body.put("items", itemsBody);

        String jsonBody = gson.toJson(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // API responde 201 para exito, 400 para errores de negocio
        if (response.statusCode() == 201 || response.statusCode() == 200 || response.statusCode() == 400) {
            return gson.fromJson(response.body(), FacturaResponseDTO.class);
        } else {
            throw new Exception("Error al crear factura: HTTP " + response.statusCode());
        }
    }

    /**
     * Obtiene todas las facturas (vista administrador).
     */
    public List<FacturaResponseDTO> obtenerTodasLasFacturas() throws Exception {
        String url = BASE_URL + "/facturas";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            FacturaResponseDTO[] facturas = gson.fromJson(response.body(), FacturaResponseDTO[].class);
            return List.of(facturas);
        } else {
            throw new Exception("Error al obtener facturas: " + response.statusCode());
        }
    }

    /**
     * Obtiene facturas por cedula de cliente.
     */
    public List<FacturaResponseDTO> obtenerFacturasPorCliente(String cedula) throws Exception {
        String url = BASE_URL + "/facturas/cliente/" + cedula;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            FacturaResponseDTO[] facturas = gson.fromJson(response.body(), FacturaResponseDTO[].class);
            return List.of(facturas);
        } else {
            throw new Exception("Error al obtener facturas del cliente: " + response.statusCode());
        }
    }

    /**
     * Obtiene factura especifica.
     */
    public FacturaResponseDTO obtenerFacturaPorId(Integer idFactura) throws Exception {
        String url = BASE_URL + "/facturas/" + idFactura;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), FacturaResponseDTO.class);
        } else {
            throw new Exception("Error al obtener factura: " + response.statusCode());
        }
    }

    /**
     * Obtiene tabla de amortizacion para una factura a credito.
     */
    public List<FacturaResponseDTO.CuotaAmortizacionDTO> obtenerTablaAmortizacion(Integer idFactura) throws Exception {
        String url = BASE_URL + "/facturas/" + idFactura + "/amortizacion";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            FacturaResponseDTO.CuotaAmortizacionDTO[] cuotas =
                    gson.fromJson(response.body(), FacturaResponseDTO.CuotaAmortizacionDTO[].class);
            return List.of(cuotas);
        } else if (response.statusCode() == 404) {
            return List.of();
        } else {
            throw new Exception("Error al obtener amortizacion: " + response.statusCode());
        }
    }
}
