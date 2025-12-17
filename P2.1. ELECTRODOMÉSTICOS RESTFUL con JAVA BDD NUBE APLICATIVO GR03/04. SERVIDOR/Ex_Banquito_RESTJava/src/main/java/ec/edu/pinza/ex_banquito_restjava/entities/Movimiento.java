package ec.edu.pinza.ex_banquito_restjava.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * POJO Movimiento - Representa una transacción en una cuenta (SIN JPA - usa JDBC)
 */
public class Movimiento implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer codMovimiento;
    private Cuenta cuenta;
    private String tipo; // 'DEP' = Depósito, 'RET' = Retiro
    private BigDecimal valor;
    private LocalDate fecha;
    
    // Constructores
    public Movimiento() {
    }
    
    public Movimiento(Cuenta cuenta, String tipo, BigDecimal valor, LocalDate fecha) {
        this.cuenta = cuenta;
        this.tipo = tipo;
        this.valor = valor;
        this.fecha = fecha;
    }
    
    // Getters y Setters
    public Integer getCodMovimiento() {
        return codMovimiento;
    }
    
    public void setCodMovimiento(Integer codMovimiento) {
        this.codMovimiento = codMovimiento;
    }
    
    public Cuenta getCuenta() {
        return cuenta;
    }
    
    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public BigDecimal getValor() {
        return valor;
    }
    
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    @Override
    public String toString() {
        return "Movimiento{" +
                "codMovimiento=" + codMovimiento +
                ", tipo='" + tipo + '\'' +
                ", valor=" + valor +
                ", fecha=" + fecha +
                '}';
    }
}
