package ec.edu.pinza.ex_banquito_restjava.dto;

import java.math.BigDecimal;

/**
 * DTO para solicitud de otorgamiento de crédito
 */
public class OtorgarCreditoRequest {
    private String cedula;
    private BigDecimal precioElectrodomestico;
    private int numeroCuotas;
    
    public OtorgarCreditoRequest() {
    }
    
    public OtorgarCreditoRequest(String cedula, BigDecimal precioElectrodomestico, int numeroCuotas) {
        this.cedula = cedula;
        this.precioElectrodomestico = precioElectrodomestico;
        this.numeroCuotas = numeroCuotas;
    }
    
    public String getCedula() {
        return cedula;
    }
    
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    
    public BigDecimal getPrecioElectrodomestico() {
        return precioElectrodomestico;
    }
    
    public void setPrecioElectrodomestico(BigDecimal precioElectrodomestico) {
        this.precioElectrodomestico = precioElectrodomestico;
    }
    
    public int getNumeroCuotas() {
        return numeroCuotas;
    }
    
    public void setNumeroCuotas(int numeroCuotas) {
        this.numeroCuotas = numeroCuotas;
    }
}
