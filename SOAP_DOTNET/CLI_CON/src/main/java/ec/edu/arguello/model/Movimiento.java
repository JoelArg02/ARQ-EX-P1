package ec.edu.arguello.model;

public class Movimiento {
    private int id;
    private int cuentaId;
    private String tipoMovimiento;
    private double monto;
    private String descripcion;
    private String fecha;

    public Movimiento() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCuentaId() { return cuentaId; }
    public void setCuentaId(int cuentaId) { this.cuentaId = cuentaId; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    @Override
    public String toString() {
        return String.format("ID: %d | Tipo: %s | Monto: $%.2f | Fecha: %s", 
            id, tipoMovimiento, monto, fecha);
    }
}
