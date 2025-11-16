package ec.edu.pinza.ex_banquito_restjava.controllers;

import ec.edu.pinza.ex_banquito_restjava.dto.MontoMaximoResponse;
import ec.edu.pinza.ex_banquito_restjava.dto.OtorgarCreditoRequest;
import ec.edu.pinza.ex_banquito_restjava.dto.OtorgarCreditoResponse;
import ec.edu.pinza.ex_banquito_restjava.dto.SujetoCreditoResponse;
import ec.edu.pinza.ex_banquito_restjava.entities.CuotaAmortizacion;
import ec.edu.pinza.ex_banquito_restjava.services.CreditoService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para servicios de crédito
 */
@Path("/api/creditos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CreditoController {
    
    private final CreditoService creditoService = new CreditoService();
    
    /**
     * Endpoint 1: Validar si un cliente es sujeto de crédito
     * GET /api/creditos/sujeto-credito/{cedula}
     */
    @GET
    @Path("/sujeto-credito/{cedula}")
    public Response validarSujetoCredito(@PathParam("cedula") String cedula) {
        try {
            CreditoService.ValidationResult resultado = creditoService.validarSujetoCredito(cedula);
            
            SujetoCreditoResponse response = new SujetoCreditoResponse(
                cedula,
                resultado.isValido(),
                resultado.getMensaje()
            );
            
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error al validar sujeto de crédito: " + e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Endpoint 2: Obtener monto máximo de crédito autorizado
     * GET /api/creditos/monto-maximo/{cedula}
     */
    @GET
    @Path("/monto-maximo/{cedula}")
    public Response obtenerMontoMaximo(@PathParam("cedula") String cedula) {
        try {
            // Primero validar si es sujeto de crédito
            CreditoService.ValidationResult validacion = creditoService.validarSujetoCredito(cedula);
            
            if (!validacion.isValido()) {
                MontoMaximoResponse response = new MontoMaximoResponse(
                    cedula,
                    BigDecimal.ZERO,
                    validacion.getMensaje()
                );
                return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
            }
            
            // Calcular monto máximo
            BigDecimal montoMaximo = creditoService.calcularMontoMaximo(cedula);
            
            MontoMaximoResponse response = new MontoMaximoResponse(
                cedula,
                montoMaximo,
                "Monto máximo calculado exitosamente"
            );
            
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error al calcular monto máximo: " + e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Endpoint 3: Otorgar crédito y crear tabla de amortización
     * POST /api/creditos/otorgar
     */
    @POST
    @Path("/otorgar")
    public Response otorgarCredito(OtorgarCreditoRequest request) {
        try {
            CreditoService.CreditoResult resultado = creditoService.otorgarCredito(
                request.getCedula(),
                request.getPrecioElectrodomestico(),
                request.getNumeroCuotas()
            );
            
            if (!resultado.isAprobado()) {
                OtorgarCreditoResponse response = new OtorgarCreditoResponse();
                response.setAprobado(false);
                response.setMensaje(resultado.getMensaje());
                return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
            }
            
            // Mapear resultado a DTO
            OtorgarCreditoResponse response = new OtorgarCreditoResponse();
            response.setAprobado(true);
            response.setMensaje(resultado.getMensaje());
            response.setIdCredito(resultado.getCredito().getIdCredito());
            response.setCedula(resultado.getCredito().getCliente().getCedula());
            response.setMontoAprobado(resultado.getCredito().getMontoAprobado());
            response.setMontoMaximoAutorizado(resultado.getCredito().getMontoMaximoAutorizado());
            response.setPlazoMeses(resultado.getCredito().getPlazoMeses());
            response.setTasaInteresAnual(resultado.getCredito().getTasaInteresAnual());
            response.setFechaOtorgamiento(resultado.getCredito().getFechaOtorgamiento());
            
            // Mapear tabla de amortización
            List<OtorgarCreditoResponse.CuotaDTO> cuotasDTO = resultado.getTablaAmortizacion()
                .stream()
                .map(this::mapearCuota)
                .collect(Collectors.toList());
            response.setTablaAmortizacion(cuotasDTO);
            
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error al otorgar crédito: " + e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Endpoint 4: Consultar tabla de amortización de un crédito
     * GET /api/creditos/{idCredito}/amortizacion
     */
    @GET
    @Path("/{idCredito}/amortizacion")
    public Response consultarTablaAmortizacion(@PathParam("idCredito") Integer idCredito) {
        try {
            var resultadoOpt = creditoService.consultarTablaAmortizacion(idCredito);
            
            if (resultadoOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Crédito no encontrado"))
                        .build();
            }
            
            CreditoService.CreditoResult resultado = resultadoOpt.get();
            
            // Mapear resultado a DTO
            OtorgarCreditoResponse response = new OtorgarCreditoResponse();
            response.setAprobado(true);
            response.setMensaje("Tabla de amortización");
            response.setIdCredito(resultado.getCredito().getIdCredito());
            response.setCedula(resultado.getCredito().getCliente().getCedula());
            response.setMontoAprobado(resultado.getCredito().getMontoAprobado());
            response.setMontoMaximoAutorizado(resultado.getCredito().getMontoMaximoAutorizado());
            response.setPlazoMeses(resultado.getCredito().getPlazoMeses());
            response.setTasaInteresAnual(resultado.getCredito().getTasaInteresAnual());
            response.setFechaOtorgamiento(resultado.getCredito().getFechaOtorgamiento());
            
            // Mapear tabla de amortización
            List<OtorgarCreditoResponse.CuotaDTO> cuotasDTO = resultado.getTablaAmortizacion()
                .stream()
                .map(this::mapearCuota)
                .collect(Collectors.toList());
            response.setTablaAmortizacion(cuotasDTO);
            
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error al consultar tabla de amortización: " + e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Mapea una entidad CuotaAmortizacion a un DTO
     */
    private OtorgarCreditoResponse.CuotaDTO mapearCuota(CuotaAmortizacion cuota) {
        OtorgarCreditoResponse.CuotaDTO dto = new OtorgarCreditoResponse.CuotaDTO();
        dto.setNumeroCuota(cuota.getNumeroCuota());
        dto.setValorCuota(cuota.getValorCuota());
        dto.setInteresPagado(cuota.getInteresPagado());
        dto.setCapitalPagado(cuota.getCapitalPagado());
        dto.setSaldoRestante(cuota.getSaldoRestante());
        return dto;
    }
    
    /**
     * Clase auxiliar para respuestas de error
     */
    public static class ErrorResponse {
        private String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() {
            return error;
        }
        
        public void setError(String error) {
            this.error = error;
        }
    }
}
