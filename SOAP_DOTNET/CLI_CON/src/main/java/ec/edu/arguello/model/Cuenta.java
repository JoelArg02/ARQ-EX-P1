package ec.edu.arguello.model;

public class Cuenta {
    private int id;
    private int clienteBancoId;
    private String numeroCuenta;
    private double saldo;
    private String tipoCuenta;

    public Cuenta() {}

    // Getters and Setters
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

    @Override
    public String toString() {
        return String.format("ID: %d | Número: %s | Tipo: %s | Saldo: $%.2f", 
            id, numeroCuenta, tipoCuenta, saldo);
    }
}
