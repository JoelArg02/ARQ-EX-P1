package ec.edu.espe.arguello.model;

import java.io.Serializable;

/**
 * Modelo de Movimiento
 */
public class Movimiento implements Serializable {
    
    private int id;
    private int cuentaId;
    private String tipoMovimiento;
    private double monto;
    private String fecha;
    private String descripcion;
    
    // Constructors
    public Movimiento() {}
    
    public Movimiento(int cuentaId, String tipoMovimiento, double monto, String descripcion) {
        this.cuentaId = cuentaId;
        this.tipoMovimiento = tipoMovimiento;
        this.monto = monto;
        this.descripcion = descripcion;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getCuentaId() {
        return cuentaId;
    }
    
    public void setCuentaId(int cuentaId) {
        this.cuentaId = cuentaId;
    }
    
    public String getTipoMovimiento() {
        return tipoMovimiento;
    }
    
    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }
    
    public double getMonto() {
        return monto;
    }
    
    public void setMonto(double monto) {
        this.monto = monto;
    }
    
    public String getFecha() {
        return fecha;
    }
    
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    @Override
    public String toString() {
        return "Movimiento{" +
                "id=" + id +
                ", cuentaId=" + cuentaId +
                ", tipoMovimiento='" + tipoMovimiento + '\'' +
                ", monto=" + monto +
                ", fecha='" + fecha + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}