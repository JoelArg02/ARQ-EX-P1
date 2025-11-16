package ec.edu.pinza.ex_comercializadora_restjava.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "ClienteCom")
public class ClienteCom implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCliente")
    private Integer idCliente;
    
    @Column(name = "cedula", length = 10, nullable = false, unique = true)
    private String cedula;
    
    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;
    
    @Column(name = "direccion", length = 200)
    private String direccion;
    
    @Column(name = "telefono", length = 15)
    private String telefono;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Factura> facturas;
    
    public ClienteCom() {
    }
    
    public ClienteCom(String cedula, String nombre, String direccion, String telefono) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }
    
    // Getters y Setters
    public Integer getIdCliente() {
        return idCliente;
    }
    
    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }
    
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
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public List<Factura> getFacturas() {
        return facturas;
    }
    
    public void setFacturas(List<Factura> facturas) {
        this.facturas = facturas;
    }
}
