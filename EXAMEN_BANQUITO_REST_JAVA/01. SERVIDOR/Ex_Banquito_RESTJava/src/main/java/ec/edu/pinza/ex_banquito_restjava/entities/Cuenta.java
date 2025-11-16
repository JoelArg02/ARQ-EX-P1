package ec.edu.pinza.ex_banquito_restjava.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Entidad Cuenta - Representa una cuenta bancaria
 */
@Entity
@Table(name = "CUENTA")
public class Cuenta implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "NUM_CUENTA", length = 8, nullable = false)
    private String numCuenta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CEDULA", nullable = false)
    private Cliente cliente;
    
    @Column(name = "SALDO", precision = 10, scale = 2, nullable = false)
    private BigDecimal saldo;
    
    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Movimiento> movimientos;
    
    // Constructores
    public Cuenta() {
    }
    
    public Cuenta(String numCuenta, Cliente cliente, BigDecimal saldo) {
        this.numCuenta = numCuenta;
        this.cliente = cliente;
        this.saldo = saldo;
    }
    
    // Getters y Setters
    public String getNumCuenta() {
        return numCuenta;
    }
    
    public void setNumCuenta(String numCuenta) {
        this.numCuenta = numCuenta;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public BigDecimal getSaldo() {
        return saldo;
    }
    
    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
    
    public List<Movimiento> getMovimientos() {
        return movimientos;
    }
    
    public void setMovimientos(List<Movimiento> movimientos) {
        this.movimientos = movimientos;
    }
    
    @Override
    public String toString() {
        return "Cuenta{" +
                "numCuenta='" + numCuenta + '\'' +
                ", saldo=" + saldo +
                '}';
    }
}
