package ec.edu.pinza.ex_banquito_restjava.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad Movimiento - Representa una transacción en una cuenta
 */
@Entity
@Table(name = "MOVIMIENTO")
public class Movimiento implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COD_MOVIMIENTO")
    private Integer codMovimiento;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NUM_CUENTA", nullable = false)
    private Cuenta cuenta;
    
    @Column(name = "TIPO", length = 3, nullable = false)
    private String tipo; // 'DEP' = Depósito, 'RET' = Retiro
    
    @Column(name = "VALOR", precision = 10, scale = 2, nullable = false)
    private BigDecimal valor;
    
    @Column(name = "FECHA", nullable = false)
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
