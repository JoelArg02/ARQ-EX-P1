package ec.edu.pinza.cliesc.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AmortizacionDTO {
    private Long idAmortizacion;
    private Long idCredito;
    private Integer numeroCuota;
    private LocalDate fechaPago;
    private BigDecimal montoCuota;
    private BigDecimal capital;
    private BigDecimal interes;
    private BigDecimal saldo;

    // Constructores
    public AmortizacionDTO() {
    }

    public AmortizacionDTO(Long idAmortizacion, Long idCredito, Integer numeroCuota, 
                          LocalDate fechaPago, BigDecimal montoCuota, BigDecimal capital, 
                          BigDecimal interes, BigDecimal saldo) {
        this.idAmortizacion = idAmortizacion;
        this.idCredito = idCredito;
        this.numeroCuota = numeroCuota;
        this.fechaPago = fechaPago;
        this.montoCuota = montoCuota;
        this.capital = capital;
        this.interes = interes;
        this.saldo = saldo;
    }

    // Getters y Setters
    public Long getIdAmortizacion() {
        return idAmortizacion;
    }

    public void setIdAmortizacion(Long idAmortizacion) {
        this.idAmortizacion = idAmortizacion;
    }

    public Long getIdCredito() {
        return idCredito;
    }

    public void setIdCredito(Long idCredito) {
        this.idCredito = idCredito;
    }

    public Integer getNumeroCuota() {
        return numeroCuota;
    }

    public void setNumeroCuota(Integer numeroCuota) {
        this.numeroCuota = numeroCuota;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getMontoCuota() {
        return montoCuota;
    }

    public void setMontoCuota(BigDecimal montoCuota) {
        this.montoCuota = montoCuota;
    }

    public BigDecimal getCapital() {
        return capital;
    }

    public void setCapital(BigDecimal capital) {
        this.capital = capital;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public void setInteres(BigDecimal interes) {
        this.interes = interes;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
