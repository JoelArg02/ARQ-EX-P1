package ec.edu.pinza.ex_comercializadora_restjava.dto;

import java.math.BigDecimal;

public class MontoMaximoResponseDTO {
    private String cedula;
    private BigDecimal montoMaximo;
    private String mensaje;
    
    public MontoMaximoResponseDTO() {
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
