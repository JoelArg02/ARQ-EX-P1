package ec.edu.pinza.ex_banquito_restjava.entities;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * POJO CuotaAmortizacion - Representa una cuota de la tabla de amortización (SIN JPA - usa JDBC)
 */
public class CuotaAmortizacion implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer idCuota;
    private Credito credito;
    private Integer numeroCuota;
    private BigDecimal valorCuota;
    private BigDecimal interesPagado;
    private BigDecimal capitalPagado;
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
