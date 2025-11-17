package ec.edu.arguello.model;

public class AmortizacionCredito {
    private int id;
    private int creditoBancoId;
    private int numeroCuota;
    private double valorCuota;
    private double interesPagado;
    private double capitalPagado;
    private double saldoPendiente;
    private String fechaPago;

    public AmortizacionCredito() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCreditoBancoId() { return creditoBancoId; }
    public void setCreditoBancoId(int creditoBancoId) { this.creditoBancoId = creditoBancoId; }

    public int getNumeroCuota() { return numeroCuota; }
    public void setNumeroCuota(int numeroCuota) { this.numeroCuota = numeroCuota; }

    public double getValorCuota() { return valorCuota; }
    public void setValorCuota(double valorCuota) { this.valorCuota = valorCuota; }

    public double getInteresPagado() { return interesPagado; }
    public void setInteresPagado(double interesPagado) { this.interesPagado = interesPagado; }

    public double getCapitalPagado() { return capitalPagado; }
    public void setCapitalPagado(double capitalPagado) { this.capitalPagado = capitalPagado; }

    public double getSaldoPendiente() { return saldoPendiente; }
    public void setSaldoPendiente(double saldoPendiente) { this.saldoPendiente = saldoPendiente; }

    public String getFechaPago() { return fechaPago; }
    public void setFechaPago(String fechaPago) { this.fechaPago = fechaPago; }

    @Override
    public String toString() {
        return String.format("Cuota %d | Valor: $%.2f | Interes: $%.2f | Capital: $%.2f | Saldo: $%.2f", 
            numeroCuota, valorCuota, interesPagado, capitalPagado, saldoPendiente);
    }
}
