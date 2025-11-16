package ec.edu.pinza.ex_banquito_restjava.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Entidad Cliente - Representa un cliente del banco
 */
@Entity
@Table(name = "CLIENTE")
public class Cliente implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "CEDULA", length = 10, nullable = false)
    private String cedula;
    
    @Column(name = "NOMBRE", length = 100, nullable = false)
    private String nombre;
    
    @Column(name = "FECHA_NACIMIENTO", nullable = false)
    private LocalDate fechaNacimiento;
    
    @Column(name = "ESTADO_CIVIL", length = 1, nullable = false)
    private String estadoCivil; // 'S' = Soltero, 'C' = Casado
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cuenta> cuentas;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Credito> creditos;
    
    // Constructores
    public Cliente() {
    }
    
    public Cliente(String cedula, String nombre, LocalDate fechaNacimiento, String estadoCivil) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.estadoCivil = estadoCivil;
    }
    
    // Getters y Setters
    public String getCedula() {
        return cedula;
    }
    
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    
    public String getEstadoCivil() {
        return estadoCivil;
    }
    
    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }
    
    public List<Cuenta> getCuentas() {
        return cuentas;
    }
    
    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }
    
    public List<Credito> getCreditos() {
        return creditos;
    }
    
    public void setCreditos(List<Credito> creditos) {
        this.creditos = creditos;
    }
    
    @Override
    public String toString() {
        return "Cliente{" +
                "cedula='" + cedula + '\'' +
                ", nombre='" + nombre + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", estadoCivil='" + estadoCivil + '\'' +
                '}';
    }
}
