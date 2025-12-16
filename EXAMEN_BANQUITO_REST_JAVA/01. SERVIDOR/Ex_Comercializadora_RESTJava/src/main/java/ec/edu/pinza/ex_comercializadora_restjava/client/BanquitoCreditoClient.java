package ec.edu.pinza.ex_comercializadora_restjava.client;

import ec.edu.pinza.ex_comercializadora_restjava.dto.MontoMaximoResponseDTO;
import ec.edu.pinza.ex_comercializadora_restjava.dto.OtorgarCreditoResponseDTO;
import ec.edu.pinza.ex_comercializadora_restjava.dto.SujetoCreditoResponseDTO;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cliente REST para consumir servicios del Banco BanQuito
 */
public class BanquitoCreditoClient {
    
    // Usar variable de entorno o localhost por defecto
    private static final String BANQUITO_HOST = System.getenv("BANQUITO_HOST") != null 
        ? System.getenv("BANQUITO_HOST") 
        : "localhost:8080";
    private static final String BASE_URL = "http://" + BANQUITO_HOST + "/Ex_Banquito_RESTJava/api/creditos";
    private final Client client;
    private final WebTarget baseTarget;
    
    public BanquitoCreditoClient() {
        this.client = ClientBuilder.newClient();
        this.baseTarget = client.target(BASE_URL);
    }
    
    public BanquitoCreditoClient(String baseUrl) {
        this.client = ClientBuilder.newClient();
        this.baseTarget = client.target(baseUrl);
    }
    
    /**
     * Verifica si un cliente es sujeto de crédito
     * @param cedula Cédula del cliente
     * @return Respuesta con validación
     */
    public SujetoCreditoResponseDTO esSujetoCredito(String cedula) {
        try {
            Response response = baseTarget
                .path("sujeto-credito")
                .path(cedula)
                .request(MediaType.APPLICATION_JSON)
                .get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(SujetoCreditoResponseDTO.class);
            } else {
                SujetoCreditoResponseDTO error = new SujetoCreditoResponseDTO();
                error.setCedula(cedula);
                error.setEsSujetoCredito(false);
                error.setMotivo("Error al consultar servicio del banco: HTTP " + response.getStatus());
                return error;
            }
        } catch (Exception e) {
            SujetoCreditoResponseDTO error = new SujetoCreditoResponseDTO();
            error.setCedula(cedula);
            error.setEsSujetoCredito(false);
            error.setMotivo("Error de comunicación con el banco: " + e.getMessage());
            return error;
        }
    }
    
    /**
     * Obtiene el monto máximo de crédito para un cliente
     * @param cedula Cédula del cliente
     * @return Respuesta con monto máximo
     */
    public MontoMaximoResponseDTO obtenerMontoMaximo(String cedula) {
        try {
            Response response = baseTarget
                .path("monto-maximo")
                .path(cedula)
                .request(MediaType.APPLICATION_JSON)
                .get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(MontoMaximoResponseDTO.class);
            } else {
                MontoMaximoResponseDTO error = new MontoMaximoResponseDTO();
                error.setCedula(cedula);
                error.setMontoMaximo(BigDecimal.ZERO);
                error.setMensaje("Error al consultar monto máximo: HTTP " + response.getStatus());
                return error;
            }
        } catch (Exception e) {
            MontoMaximoResponseDTO error = new MontoMaximoResponseDTO();
            error.setCedula(cedula);
            error.setMontoMaximo(BigDecimal.ZERO);
            error.setMensaje("Error de comunicación con el banco: " + e.getMessage());
            return error;
        }
    }
    
    /**
     * Solicita otorgamiento de crédito en el banco
     * @param cedula Cédula del cliente
     * @param montoSolicitado Monto del crédito
     * @param plazoMeses Plazo en meses
     * @return Respuesta con resultado del otorgamiento
     */
    public OtorgarCreditoResponseDTO otorgarCredito(String cedula, BigDecimal montoSolicitado, int plazoMeses) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("cedula", cedula);
            request.put("precioElectrodomestico", montoSolicitado);
            request.put("numeroCuotas", plazoMeses);
            
            Response response = baseTarget
                .path("otorgar")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(request));
            
            if (response.getStatus() == 200 || response.getStatus() == 201) {
                return response.readEntity(OtorgarCreditoResponseDTO.class);
            } else {
                OtorgarCreditoResponseDTO error = new OtorgarCreditoResponseDTO();
                error.setAprobado(false);
                error.setMensaje("Error al otorgar crédito: HTTP " + response.getStatus());
                return error;
            }
        } catch (Exception e) {
            OtorgarCreditoResponseDTO error = new OtorgarCreditoResponseDTO();
            error.setAprobado(false);
            error.setMensaje("Error de comunicación con el banco: " + e.getMessage());
            return error;
        }
    }
    
    /**
     * Obtiene la información completa del crédito incluyendo estado y tabla de amortización
     * @param idCredito ID del crédito
     * @return Información completa del crédito
     */
    public OtorgarCreditoResponseDTO obtenerInformacionCredito(Integer idCredito) {
        try {
            System.out.println("DEBUG BanquitoClient: Consultando información completa para crédito ID: " + idCredito);
            Response response = baseTarget
                .path(String.valueOf(idCredito))
                .path("amortizacion")
                .request(MediaType.APPLICATION_JSON)
                .get();
            
            System.out.println("DEBUG BanquitoClient: Status HTTP: " + response.getStatus());
            
            if (response.getStatus() == 200) {
                OtorgarCreditoResponseDTO creditoResponse = response.readEntity(OtorgarCreditoResponseDTO.class);
                System.out.println("DEBUG BanquitoClient: Respuesta deserializada correctamente");
                System.out.println("DEBUG BanquitoClient: Estado del crédito: " + creditoResponse.getEstado());
                return creditoResponse;
            } else {
                System.err.println("DEBUG BanquitoClient: Error HTTP " + response.getStatus());
                return null;
            }
        } catch (Exception e) {
            System.err.println("DEBUG BanquitoClient: Exception - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtiene la tabla de amortización de un crédito
     * @param idCredito ID del crédito
     * @return Lista de cuotas
     */
    public List<OtorgarCreditoResponseDTO.CuotaDTO> obtenerTablaAmortizacion(Integer idCredito) {
        try {
            OtorgarCreditoResponseDTO creditoInfo = obtenerInformacionCredito(idCredito);
            return (creditoInfo != null) ? creditoInfo.getTablaAmortizacion() : null;
        } catch (Exception e) {
            System.err.println("DEBUG BanquitoClient: Exception - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Cierra el cliente HTTP
     */
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
