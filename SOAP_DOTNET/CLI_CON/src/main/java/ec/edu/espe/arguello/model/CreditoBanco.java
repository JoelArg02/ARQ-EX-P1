package ec.edu.espe.arguello.model;

public class CreditoBanco {
    private int id;
    private int clienteBancoId;
    private String numeroCreditoBanco;
    private double monto;
    private double tasaInteres;
    private int plazoCuotas;
    private String estado;

    public CreditoBanco() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getClienteBancoId() { return clienteBancoId; }
    public void setClienteBancoId(int clienteBancoId) { this.clienteBancoId = clienteBancoId; }
    
    public String getNumeroCreditoBanco() { return numeroCreditoBanco; }
    public void setNumeroCreditoBanco(String numeroCreditoBanco) { this.numeroCreditoBanco = numeroCreditoBanco; }
    
    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
    
    public double getTasaInteres() { return tasaInteres; }
    public void setTasaInteres(double tasaInteres) { this.tasaInteres = tasaInteres; }
    
    public int getPlazoCuotas() { return plazoCuotas; }
    public void setPlazoCuotas(int plazoCuotas) { this.plazoCuotas = plazoCuotas; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
