package ec.edu.espe.arguello.model;

import java.io.Serializable;

/**
 * Modelo de Credito Banco
 */
public class CreditoBanco implements Serializable {
    
    private int id;
    private int clienteBancoId;
    private double montoAprobado;
    private int numeroCuotas;
    private double tasaInteres;
    private String fechaAprobacion;
    private boolean activo;
    
    // Constructors
    public CreditoBanco() {
        this.tasaInteres = 0.16;
        this.activo = true;
    }
    
    public CreditoBanco(int clienteBancoId, double montoAprobado, int numeroCuotas, String fechaAprobacion) {
        this.clienteBancoId = clienteBancoId;
        this.montoAprobado = montoAprobado;
        this.numeroCuotas = numeroCuotas;
        this.fechaAprobacion = fechaAprobacion;
        this.tasaInteres = 0.16;
        this.activo = true;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getClienteBancoId() {
        return clienteBancoId;
    }
    
    public void setClienteBancoId(int clienteBancoId) {
        this.clienteBancoId = clienteBancoId;
    }
    
    public double getMontoAprobado() {
        return montoAprobado;
    }
    
    public void setMontoAprobado(double montoAprobado) {
        this.montoAprobado = montoAprobado;
    }
    
    public int getNumeroCuotas() {
        return numeroCuotas;
    }
    
    public void setNumeroCuotas(int numeroCuotas) {
        this.numeroCuotas = numeroCuotas;
    }
    
    public double getTasaInteres() {
        return tasaInteres;
    }
    
    public void setTasaInteres(double tasaInteres) {
        this.tasaInteres = tasaInteres;
    }
    
    public String getFechaAprobacion() {
        return fechaAprobacion;
    }
    
    public void setFechaAprobacion(String fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    @Override
    public String toString() {
        return "CreditoBanco{" +
                "id=" + id +
                ", clienteBancoId=" + clienteBancoId +
                ", montoAprobado=" + montoAprobado +
                ", numeroCuotas=" + numeroCuotas +
                ", tasaInteres=" + tasaInteres +
                ", fechaAprobacion='" + fechaAprobacion + '\'' +
                ", activo=" + activo +
                '}';
    }
}