package ec.edu.pinza.ex_banquito_restjava.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entidad CuotaAmortizacion - Representa una cuota de la tabla de amortización
 */
@Entity
@Table(name = "CUOTA_AMORTIZACION")
public class CuotaAmortizacion implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CUOTA")
    private Integer idCuota;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CREDITO", nullable = false)
    private Credito credito;
    
    @Column(name = "NUMERO_CUOTA", nullable = false)
    private Integer numeroCuota;
    
    @Column(name = "VALOR_CUOTA", precision = 10, scale = 2, nullable = false)
    private BigDecimal valorCuota;
    
    @Column(name = "INTERES_PAGADO", precision = 10, scale = 2, nullable = false)
    private BigDecimal interesPagado;
    
    @Column(name = "CAPITAL_PAGADO", precision = 10, scale = 2, nullable = false)
    private BigDecimal capitalPagado;
    
    @Column(name = "SALDO_RESTANTE", precision = 10, scale = 2, nullable = false)
    private BigDecimal saldoRestante;
    
    // Constructores
    public CuotaAmortizacion() {
    }
    
    public CuotaAmortizacion(Credito credito, Integer numeroCuota, BigDecimal valorCuota,
                            BigDecimal interesPagado, BigDecimal capitalPagado, BigDecimal saldoRestante) {
        this.credito = credito;
        this.numeroCuota = numeroCuota;
        this.valorCuota = valorCuota;
        this.interesPagado = interesPagado;
        this.capitalPagado = capitalPagado;
        this.saldoRestante = saldoRestante;
    }
    
    // Getters y Setters
    public Integer getIdCuota() {
        return idCuota;
    }
    
    public void setIdCuota(Integer idCuota) {
        this.idCuota = idCuota;
    }
    
    public Credito getCredito() {
        return credito;
    }
    
    public void setCredito(Credito credito) {
        this.credito = credito;
    }
    
    public Integer getNumeroCuota() {
        return numeroCuota;
    }
    
    public void setNumeroCuota(Integer numeroCuota) {
        this.numeroCuota = numeroCuota;
    }
    
    public BigDecimal getValorCuota() {
        return valorCuota;
    }
    
    public void setValorCuota(BigDecimal valorCuota) {
        this.valorCuota = valorCuota;
    }
    
    public BigDecimal getInteresPagado() {
        return interesPagado;
    }
    
    public void setInteresPagado(BigDecimal interesPagado) {
        this.interesPagado = interesPagado;
    }
    
    public BigDecimal getCapitalPagado() {
        return capitalPagado;
    }
    
    public void setCapitalPagado(BigDecimal capitalPagado) {
        this.capitalPagado = capitalPagado;
    }
    
    public BigDecimal getSaldoRestante() {
        return saldoRestante;
    }
    
    public void setSaldoRestante(BigDecimal saldoRestante) {
        this.saldoRestante = saldoRestante;
    }
    
    @Override
    public String toString() {
        return "CuotaAmortizacion{" +
                "idCuota=" + idCuota +
                ", numeroCuota=" + numeroCuota +
                ", valorCuota=" + valorCuota +
                ", interesPagado=" + interesPagado +
                ", capitalPagado=" + capitalPagado +
                ", saldoRestante=" + saldoRestante +
                '}';
    }
}
