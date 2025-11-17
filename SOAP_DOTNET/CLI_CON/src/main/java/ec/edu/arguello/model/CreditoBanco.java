package ec.edu.arguello.model;

public class CreditoBanco {
    private int id;
    private int clienteBancoId;
    private double monto;
    private double tasaInteres;
    private int plazoMeses;
    private String estado;
    private String fechaAprobacion;

    public CreditoBanco() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClienteBancoId() { return clienteBancoId; }
    public void setClienteBancoId(int clienteBancoId) { this.clienteBancoId = clienteBancoId; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public double getTasaInteres() { return tasaInteres; }
    public void setTasaInteres(double tasaInteres) { this.tasaInteres = tasaInteres; }

    public int getPlazoMeses() { return plazoMeses; }
    public void setPlazoMeses(int plazoMeses) { this.plazoMeses = plazoMeses; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(String fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }

    @Override
    public String toString() {
        return String.format("ID: %d | Monto: $%.2f | Tasa: %.2f%% | Plazo: %d meses | Estado: %s", 
            id, monto, tasaInteres, plazoMeses, estado);
    }
}
