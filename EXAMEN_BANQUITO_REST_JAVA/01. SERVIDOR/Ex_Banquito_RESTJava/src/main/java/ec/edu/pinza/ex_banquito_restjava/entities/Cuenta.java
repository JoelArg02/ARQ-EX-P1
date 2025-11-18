package ec.edu.pinza.ex_banquito_restjava.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * POJO Cuenta - Representa una cuenta bancaria (SIN JPA - usa JDBC)
 */
public class Cuenta implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String numCuenta;
    private Cliente cliente;
    private BigDecimal saldo;
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
