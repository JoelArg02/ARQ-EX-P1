package ec.edu.espe.arguello.model;

public class Movimiento {
    private int id;
    private int cuentaId;
    private String tipoMovimiento;
    private double monto;
    private String fecha;
    private String descripcion;

    public Movimiento() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getCuentaId() { return cuentaId; }
    public void setCuentaId(int cuentaId) { this.cuentaId = cuentaId; }
    
    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }
    
    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
    
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
