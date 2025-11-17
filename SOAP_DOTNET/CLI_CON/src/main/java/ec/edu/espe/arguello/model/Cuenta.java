package ec.edu.espe.arguello.model;

public class Cuenta {
    private int id;
    private int clienteBancoId;
    private String numeroCuenta;
    private double saldo;
    private String tipoCuenta;

    public Cuenta() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getClienteBancoId() { return clienteBancoId; }
    public void setClienteBancoId(int clienteBancoId) { this.clienteBancoId = clienteBancoId; }
    
    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }
    
    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
    
    public String getTipoCuenta() { return tipoCuenta; }
    public void setTipoCuenta(String tipoCuenta) { this.tipoCuenta = tipoCuenta; }
}
