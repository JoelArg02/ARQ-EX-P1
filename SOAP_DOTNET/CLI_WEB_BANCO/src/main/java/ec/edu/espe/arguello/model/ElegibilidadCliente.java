package ec.edu.espe.arguello.model;

import java.io.Serializable;

/**
 * Modelo de Elegibilidad de Cliente
 */
public class ElegibilidadCliente implements Serializable {
    
    private boolean esCliente;
    private boolean cumpleEdadEstadoCivil;
    private boolean tieneDepositoUltimoMes;
    private boolean noTieneCreditoActivo;
    private boolean esElegible;
    private String mensaje;
    
    // Constructors
    public ElegibilidadCliente() {}
    
    public ElegibilidadCliente(boolean esCliente, boolean cumpleEdadEstadoCivil, 
                              boolean tieneDepositoUltimoMes, boolean noTieneCreditoActivo, 
                              boolean esElegible, String mensaje) {
        this.esCliente = esCliente;
        this.cumpleEdadEstadoCivil = cumpleEdadEstadoCivil;
        this.tieneDepositoUltimoMes = tieneDepositoUltimoMes;
        this.noTieneCreditoActivo = noTieneCreditoActivo;
        this.esElegible = esElegible;
        this.mensaje = mensaje;
    }
    
    // Getters and Setters
    public boolean isEsCliente() {
        return esCliente;
    }
    
    public void setEsCliente(boolean esCliente) {
        this.esCliente = esCliente;
    }
    
    public boolean isCumpleEdadEstadoCivil() {
        return cumpleEdadEstadoCivil;
    }
    
    public void setCumpleEdadEstadoCivil(boolean cumpleEdadEstadoCivil) {
        this.cumpleEdadEstadoCivil = cumpleEdadEstadoCivil;
    }
    
    public boolean isTieneDepositoUltimoMes() {
        return tieneDepositoUltimoMes;
    }
    
    public void setTieneDepositoUltimoMes(boolean tieneDepositoUltimoMes) {
        this.tieneDepositoUltimoMes = tieneDepositoUltimoMes;
    }
    
    public boolean isNoTieneCreditoActivo() {
        return noTieneCreditoActivo;
    }
    
    public void setNoTieneCreditoActivo(boolean noTieneCreditoActivo) {
        this.noTieneCreditoActivo = noTieneCreditoActivo;
    }
    
    public boolean isEsElegible() {
        return esElegible;
    }
    
    public void setEsElegible(boolean esElegible) {
        this.esElegible = esElegible;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    @Override
    public String toString() {
        return "ElegibilidadCliente{" +
                "esCliente=" + esCliente +
                ", cumpleEdadEstadoCivil=" + cumpleEdadEstadoCivil +
                ", tieneDepositoUltimoMes=" + tieneDepositoUltimoMes +
                ", noTieneCreditoActivo=" + noTieneCreditoActivo +
                ", esElegible=" + esElegible +
                ", mensaje='" + mensaje + '\'' +
                '}';
    }
}