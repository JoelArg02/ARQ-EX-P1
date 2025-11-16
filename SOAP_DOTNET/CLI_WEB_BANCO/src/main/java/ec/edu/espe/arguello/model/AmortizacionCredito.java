package ec.edu.espe.arguello.model;

import java.io.Serializable;

/**
 * Modelo de Amortización de Crédito
 */
public class AmortizacionCredito implements Serializable {
    
    private int numeroCuota;
    private double montoCuota;
    private double capital;
    private double interes;
    private double saldoPendiente;
    
    // Constructors
    public AmortizacionCredito() {}
    
    public AmortizacionCredito(int numeroCuota, double montoCuota, double capital, 
                              double interes, double saldoPendiente) {
        this.numeroCuota = numeroCuota;
        this.montoCuota = montoCuota;
        this.capital = capital;
        this.interes = interes;
        this.saldoPendiente = saldoPendiente;
    }
    
    // Getters and Setters
    public int getNumeroCuota() {
        return numeroCuota;
    }
    
    public void setNumeroCuota(int numeroCuota) {
        this.numeroCuota = numeroCuota;
    }
    
    public double getMontoCuota() {
        return montoCuota;
    }
    
    public void setMontoCuota(double montoCuota) {
        this.montoCuota = montoCuota;
    }
    
    public double getCapital() {
        return capital;
    }
    
    public void setCapital(double capital) {
        this.capital = capital;
    }
    
    public double getInteres() {
        return interes;
    }
    
    public void setInteres(double interes) {
        this.interes = interes;
    }
    
    public double getSaldoPendiente() {
        return saldoPendiente;
    }
    
    public void setSaldoPendiente(double saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }
    
    @Override
    public String toString() {
        return "AmortizacionCredito{" +
                "numeroCuota=" + numeroCuota +
                ", montoCuota=" + montoCuota +
                ", capital=" + capital +
                ", interes=" + interes +
                ", saldoPendiente=" + saldoPendiente +
                '}';
    }
}