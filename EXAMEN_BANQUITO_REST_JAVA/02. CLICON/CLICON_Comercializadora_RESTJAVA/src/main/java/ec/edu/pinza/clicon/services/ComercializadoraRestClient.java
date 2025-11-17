package ec.edu.pinza.clicon.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ec.edu.pinza.clicon.models.FacturaResponseDTO;
import ec.edu.pinza.clicon.models.ProductoDTO;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cliente REST que replica el consumo de CLIWEB hacia el mismo servidor.
 */
public class ComercializadoraRestClient implements AutoCloseable {

    private static final String BASE_URL = "http://localhost:8080/Ex_Comercializadora_RESTJava/api";

    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public ComercializadoraRestClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<ProductoDTO> obtenerProductos() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/productos"))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), new TypeReference<List<ProductoDTO>>() {});
        }
        throw new IOException("HTTP " + response.statusCode() + " al obtener productos");
    }

    public FacturaResponseDTO crearFactura(String cedula, String formaPago, Integer numeroCuotas, List<Map<String, Integer>> items)
            throws IOException, InterruptedException {
        Map<String, Object> payload = new HashMap<>();
        payload.put("cedulaCliente", cedula);
        payload.put("formaPago", formaPago);
        if (numeroCuotas != null) {
            payload.put("numeroCuotas", numeroCuotas);
        }
        payload.put("items", items);

        String jsonBody = mapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/facturas"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        if (status == 200 || status == 201 || status == 400) {
            return mapper.readValue(response.body(), FacturaResponseDTO.class);
        }

        FacturaResponseDTO error = new FacturaResponseDTO();
        error.setExitoso(false);
        error.setMensaje("Error HTTP " + status + " al crear la factura");
        return error;
    }

    public List<FacturaResponseDTO> obtenerTodasLasFacturas() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/facturas"))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), new TypeReference<List<FacturaResponseDTO>>() {});
        }
        throw new IOException("HTTP " + response.statusCode() + " al obtener facturas");
    }

    public List<FacturaResponseDTO> obtenerFacturasPorCliente(String cedula) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/facturas/cliente/" + cedula))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), new TypeReference<List<FacturaResponseDTO>>() {});
        }
        throw new IOException("HTTP " + response.statusCode() + " al obtener facturas del cliente");
    }

    public FacturaResponseDTO obtenerFacturaPorId(Integer idFactura) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/facturas/" + idFactura))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), FacturaResponseDTO.class);
        }
        throw new IOException("HTTP " + response.statusCode() + " al obtener factura " + idFactura);
    }

    /**
     * Obtiene la tabla de amortizacion de una factura a credito directo.
     */
    public List<FacturaResponseDTO.CuotaAmortizacionDTO> obtenerTablaAmortizacion(Integer idFactura)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/facturas/" + idFactura + "/amortizacion"))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), new TypeReference<List<FacturaResponseDTO.CuotaAmortizacionDTO>>() {});
        } else if (response.statusCode() == 404) {
            return List.of();
        }
        throw new IOException("HTTP " + response.statusCode() + " al obtener amortizacion de factura " + idFactura);
    }

    @Override
    public void close() {
        // HttpClient no requiere cerrar recursos pero mantenemos la firma para try-with-resources.
    }
}
