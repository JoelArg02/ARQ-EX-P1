package ec.edu.pinza.cliesc.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import ec.edu.pinza.cliesc.models.AmortizacionDTO;
import ec.edu.pinza.cliesc.models.CreditoDTO;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Cliente REST para consumir la API de BanQuito
 */
public class BanquitoRestClient {
    
    private static final String BASE_URL = "http://192.168.68.104:8080/Ex_Banquito_RESTJava-1.0-SNAPSHOT/api";
    private final HttpClient httpClient;
    private final Gson gson;
    
    public BanquitoRestClient() {
        this.httpClient = HttpClient.newHttpClient();
        
        // Configurar Gson con deserializador para LocalDate
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                        LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
                .create();
    }
    
    /**
     * Solicita un crédito en BanQuito
     */
    public CreditoDTO solicitarCredito(CreditoDTO credito) throws Exception {
        String url = BASE_URL + "/creditos";
        
        String jsonBody = gson.toJson(credito);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 201) {
            return gson.fromJson(response.body(), CreditoDTO.class);
        } else {
            throw new Exception("Error al solicitar crédito: " + response.body());
        }
    }
    
    /**
     * Obtiene la información de un crédito específico
     */
    public CreditoDTO obtenerCredito(Long idCredito) throws Exception {
        String url = BASE_URL + "/creditos/" + idCredito;
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), CreditoDTO.class);
        } else {
            throw new Exception("Error al obtener crédito: " + response.statusCode());
        }
    }
    
    /**
     * Obtiene la tabla de amortización de un crédito
     */
    public List<AmortizacionDTO> obtenerTablaAmortizacion(Long idCredito) throws Exception {
        String url = BASE_URL + "/creditos/" + idCredito + "/amortizacion";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            AmortizacionDTO[] amortizaciones = gson.fromJson(response.body(), AmortizacionDTO[].class);
            return List.of(amortizaciones);
        } else {
            throw new Exception("Error al obtener tabla de amortización: " + response.statusCode());
        }
    }
    
    public CreditoDTO validarSujetoCredito(String cedula) throws Exception {
        String url = BASE_URL + "/creditos/sujeto-credito/" + cedula;
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), CreditoDTO.class);
        } else {
            throw new Exception("Error al validar sujeto de crédito: " + response.statusCode());
        }
    }
    
    public CreditoDTO obtenerMontoMaximo(String cedula) throws Exception {
        String url = BASE_URL + "/creditos/monto-maximo/" + cedula;
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200 || response.statusCode() == 400) {
            return gson.fromJson(response.body(), CreditoDTO.class);
        } else {
            throw new Exception("Error al obtener monto máximo: " + response.statusCode());
        }
    }
}
