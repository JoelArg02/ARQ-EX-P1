package ec.edu.pinza.ex_banquito_restjava.dto;

import java.math.BigDecimal;

/**
 * DTO para respuesta de monto máximo de crédito
 */
public class MontoMaximoResponse {
    private String cedula;
    private BigDecimal montoMaximo;
    private String mensaje;
    
    public MontoMaximoResponse() {
    }
    
    public MontoMaximoResponse(String cedula, BigDecimal montoMaximo, String mensaje) {
        this.cedula = cedula;
        this.montoMaximo = montoMaximo;
        this.mensaje = mensaje;
    }
    
    public String getCedula() {
        return cedula;
    }
    
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    
    public BigDecimal getMontoMaximo() {
        return montoMaximo;
    }
    
    public void setMontoMaximo(BigDecimal montoMaximo) {
        this.montoMaximo = montoMaximo;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
