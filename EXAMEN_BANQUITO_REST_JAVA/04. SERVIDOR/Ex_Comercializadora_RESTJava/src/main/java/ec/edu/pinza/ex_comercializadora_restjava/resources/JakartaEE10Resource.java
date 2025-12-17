package ec.edu.pinza.ex_comercializadora_restjava.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * Endpoint de prueba para verificar que el servidor está funcionando
 */
@Path("health")
public class JakartaEE10Resource {
    
    @GET
    public Response ping(){
        return Response
                .ok("{\"status\": \"OK\", \"message\": \"Comercializadora REST Server is running\"}")
                .build();
    }
}
