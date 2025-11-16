package ec.edu.pinza.ex_comercializadora_restjava.resources;

import ec.edu.pinza.ex_comercializadora_restjava.dto.CrearFacturaRequest;
import ec.edu.pinza.ex_comercializadora_restjava.dto.FacturaResponse;
import ec.edu.pinza.ex_comercializadora_restjava.dto.OtorgarCreditoResponseDTO;
import ec.edu.pinza.ex_comercializadora_restjava.repositories.ClienteComRepository;
import ec.edu.pinza.ex_comercializadora_restjava.service.FacturaService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para operaciones de facturación
 */
@Path("/facturas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FacturaResource {
    
    private final FacturaService facturaService = new FacturaService();
    
    /**
     * Endpoint de diagnóstico - Buscar cliente por cédula
     * GET /api/facturas/test-cliente/{cedula}
     */
    @GET
    @Path("/test-cliente/{cedula}")
    public Response testCliente(@PathParam("cedula") String cedula) {
        Map<String, Object> result = new HashMap<>();
        try {
            ClienteComRepository repo = new ClienteComRepository();
            var clienteOpt = repo.findByCedula(cedula);
            
            result.put("cedula_buscada", cedula);
            result.put("encontrado", clienteOpt.isPresent());
            
            if (clienteOpt.isPresent()) {
                var cliente = clienteOpt.get();
                result.put("id", cliente.getIdCliente());
                result.put("nombre", cliente.getNombre());
                result.put("direccion", cliente.getDireccion());
            }
            
            return Response.ok(result).build();
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("stacktrace", e.toString());
            return Response.status(500).entity(result).build();
        }
    }
    
    /**
     * Crea una nueva factura (efectivo o crédito)
     * POST /api/facturas
     */
    @POST
    public Response crearFactura(CrearFacturaRequest request) {
        try {
            FacturaResponse result = facturaService.crearFactura(request);
            
            if (result.isExitoso()) {
                return Response.status(Response.Status.CREATED).entity(result).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
            }
        } catch (Exception e) {
            FacturaResponse error = new FacturaResponse();
            error.setExitoso(false);
            error.setMensaje("Error interno: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }
    
    /**
     * Obtiene tabla de amortización de una factura a crédito
     * GET /api/facturas/{idFactura}/amortizacion
     */
    @GET
    @Path("/{idFactura}/amortizacion")
    public Response obtenerTablaAmortizacion(@PathParam("idFactura") Integer idFactura) {
        try {
            List<OtorgarCreditoResponseDTO.CuotaDTO> tabla = facturaService.obtenerTablaAmortizacion(idFactura);
            
            if (tabla == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"mensaje\": \"Factura no encontrada o no corresponde a un crédito\"}")
                        .build();
            }
            
            return Response.ok(tabla).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensaje\": \"Error interno: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}
