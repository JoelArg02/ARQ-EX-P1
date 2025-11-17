package ec.edu.pinza.cliweb.services;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class BanquitoRestClient {
    
    private static final String BASE_URL = "http://localhost:8080/Ex_Banquito_RESTJava/api";
    private final Client client;
    private final WebTarget baseTarget;
    
    public BanquitoRestClient() {
        this.client = ClientBuilder.newClient();
        this.baseTarget = client.target(BASE_URL);
    }
    
    public Map<String, Object> validarSujetoCredito(String cedula) {
        try {
            Response response = baseTarget
                .path("creditos/sujeto-credito/" + cedula)
                .request(MediaType.APPLICATION_JSON)
                .get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(new GenericType<Map<String, Object>>(){});
            }
            
            Map<String, Object> error = new HashMap<>();
            error.put("valido", false);
            error.put("mensaje", "Error HTTP " + response.getStatus());
            return error;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("valido", false);
            error.put("mensaje", "Error: " + e.getMessage());
            return error;
        }
    }
    
    public Map<String, Object> obtenerMontoMaximo(String cedula) {
        try {
            Response response = baseTarget
                .path("creditos/monto-maximo/" + cedula)
                .request(MediaType.APPLICATION_JSON)
                .get();
            
            if (response.getStatus() == 200 || response.getStatus() == 400) {
                return response.readEntity(new GenericType<Map<String, Object>>(){});
            }
            
            Map<String, Object> error = new HashMap<>();
            error.put("montoMaximo", 0);
            error.put("mensaje", "Error HTTP " + response.getStatus());
            return error;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("montoMaximo", 0);
            error.put("mensaje", "Error: " + e.getMessage());
            return error;
        }
    }
    
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
