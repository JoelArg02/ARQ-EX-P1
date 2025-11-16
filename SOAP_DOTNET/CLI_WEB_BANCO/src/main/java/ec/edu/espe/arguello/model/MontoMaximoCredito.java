package ec.edu.espe.arguello.model;

import java.io.Serializable;

/**
 * Modelo de Monto Máximo de Crédito
 */
public class MontoMaximoCredito implements Serializable {
    
    private double montoMaximoCredito;
    private double promedioDepositos;
    private double promedioRetiros;
    private String mensaje;
    
    // Constructors
    public MontoMaximoCredito() {}
    
    public MontoMaximoCredito(double montoMaximoCredito, double promedioDepositos, 
                             double promedioRetiros, String mensaje) {
        this.montoMaximoCredito = montoMaximoCredito;
        this.promedioDepositos = promedioDepositos;
        this.promedioRetiros = promedioRetiros;
        this.mensaje = mensaje;
    }
    
    // Getters and Setters
    public double getMontoMaximoCredito() {
        return montoMaximoCredito;
    }
    
    public void setMontoMaximoCredito(double montoMaximoCredito) {
        this.montoMaximoCredito = montoMaximoCredito;
    }
    
    public double getPromedioDepositos() {
        return promedioDepositos;
    }
    
    public void setPromedioDepositos(double promedioDepositos) {
        this.promedioDepositos = promedioDepositos;
    }
    
    public double getPromedioRetiros() {
        return promedioRetiros;
    }
    
    public void setPromedioRetiros(double promedioRetiros) {
        this.promedioRetiros = promedioRetiros;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    @Override
    public String toString() {
        return "MontoMaximoCredito{" +
                "montoMaximoCredito=" + montoMaximoCredito +
                ", promedioDepositos=" + promedioDepositos +
                ", promedioRetiros=" + promedioRetiros +
                ", mensaje='" + mensaje + '\'' +
                '}';
    }
}