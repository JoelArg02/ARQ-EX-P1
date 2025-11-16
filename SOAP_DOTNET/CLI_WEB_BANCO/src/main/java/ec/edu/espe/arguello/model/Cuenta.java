package ec.edu.espe.arguello.model;

import java.io.Serializable;

/**
 * Modelo de Cuenta
 */
public class Cuenta implements Serializable {
    
    private int id;
    private int clienteBancoId;
    private String numeroCuenta;
    private double saldo;
    private String tipoCuenta;
    
    // Constructors
    public Cuenta() {
        this.tipoCuenta = "Ahorros";
        this.saldo = 0.0;
    }
    
    public Cuenta(int clienteBancoId, String numeroCuenta, double saldo, String tipoCuenta) {
        this.clienteBancoId = clienteBancoId;
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.tipoCuenta = tipoCuenta;
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
    
    public String getNumeroCuenta() {
        return numeroCuenta;
    }
    
    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }
    
    public double getSaldo() {
        return saldo;
    }
    
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
    
    public String getTipoCuenta() {
        return tipoCuenta;
    }
    
    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }
    
    @Override
    public String toString() {
        return "Cuenta{" +
                "id=" + id +
                ", clienteBancoId=" + clienteBancoId +
                ", numeroCuenta='" + numeroCuenta + '\'' +
                ", saldo=" + saldo +
                ", tipoCuenta='" + tipoCuenta + '\'' +
                '}';
    }
}