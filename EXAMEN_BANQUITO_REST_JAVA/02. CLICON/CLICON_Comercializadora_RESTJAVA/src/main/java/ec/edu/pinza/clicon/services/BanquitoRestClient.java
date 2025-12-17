package ec.edu.pinza.clicon.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class BanquitoRestClient implements AutoCloseable {

    // LOCAL: private static final String BASE_URL = "http://localhost:8086/Ex_Banquito_RESTJava-1.0-SNAPSHOT/api";
    private static final String BASE_URL = "http://159.203.120.118:8086/Ex_Banquito_RESTJava-1.0-SNAPSHOT/api";

    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public BanquitoRestClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Map<String, Object> validarSujetoCredito(String cedula) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/creditos/sujeto-credito/" + cedula))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
        }
        throw new IOException("HTTP " + response.statusCode() + " al validar sujeto de crédito");
    }

    public Map<String, Object> obtenerMontoMaximo(String cedula) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/creditos/monto-maximo/" + cedula))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200 || response.statusCode() == 400) {
            return mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
        }
        throw new IOException("HTTP " + response.statusCode() + " al obtener monto máximo");
    }

    @Override
    public void close() {
    }
}
