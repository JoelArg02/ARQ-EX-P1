package ec.edu.espe.arguello.model;

import java.io.Serializable;

/**
 * Modelo de Cliente Banco
 */
public class ClienteBanco implements Serializable {
    
    private int id;
    private String cedula;
    private String nombreCompleto;
    private String estadoCivil;
    private String fechaNacimiento;
    private boolean tieneCreditoActivo;
    
    // Constructors
    public ClienteBanco() {}
    
    public ClienteBanco(String cedula, String nombreCompleto, String estadoCivil, String fechaNacimiento) {
        this.cedula = cedula;
        this.nombreCompleto = nombreCompleto;
        this.estadoCivil = estadoCivil;
        this.fechaNacimiento = fechaNacimiento;
        this.tieneCreditoActivo = false;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getCedula() {
        return cedula;
    }
    
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    
    public String getEstadoCivil() {
        return estadoCivil;
    }
    
    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }
    
    public String getFechaNacimiento() {
        return fechaNacimiento;
    }
    
    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    
    public boolean isTieneCreditoActivo() {
        return tieneCreditoActivo;
    }
    
    public void setTieneCreditoActivo(boolean tieneCreditoActivo) {
        this.tieneCreditoActivo = tieneCreditoActivo;
    }
    
    @Override
    public String toString() {
        return "ClienteBanco{" +
                "id=" + id +
                ", cedula='" + cedula + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", estadoCivil='" + estadoCivil + '\'' +
                ", fechaNacimiento='" + fechaNacimiento + '\'' +
                ", tieneCreditoActivo=" + tieneCreditoActivo +
                '}';
    }
}