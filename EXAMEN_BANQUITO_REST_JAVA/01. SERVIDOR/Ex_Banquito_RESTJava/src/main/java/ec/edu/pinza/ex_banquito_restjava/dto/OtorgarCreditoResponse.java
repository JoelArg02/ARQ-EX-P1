package ec.edu.pinza.ex_banquito_restjava.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para respuesta de otorgamiento de crédito
 */
public class OtorgarCreditoResponse {
    private boolean aprobado;
    private String mensaje;
    private Integer idCredito;
    private String cedula;
    private BigDecimal montoAprobado;
    private BigDecimal montoMaximoAutorizado;
    private int plazoMeses;
    private BigDecimal tasaInteresAnual;
    private LocalDate fechaOtorgamiento;
    private List<CuotaDTO> tablaAmortizacion;
    
    public OtorgarCreditoResponse() {
    }
    
    public boolean isAprobado() {
        return aprobado;
    }
    
    public void setAprobado(boolean aprobado) {
        this.aprobado = aprobado;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public Integer getIdCredito() {
        return idCredito;
    }
    
    public void setIdCredito(Integer idCredito) {
        this.idCredito = idCredito;
    }
    
    public String getCedula() {
        return cedula;
    }
    
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    
    public BigDecimal getMontoAprobado() {
        return montoAprobado;
    }
    
    public void setMontoAprobado(BigDecimal montoAprobado) {
        this.montoAprobado = montoAprobado;
    }
    
    public BigDecimal getMontoMaximoAutorizado() {
        return montoMaximoAutorizado;
    }
    
    public void setMontoMaximoAutorizado(BigDecimal montoMaximoAutorizado) {
        this.montoMaximoAutorizado = montoMaximoAutorizado;
    }
    
    public int getPlazoMeses() {
        return plazoMeses;
    }
    
    public void setPlazoMeses(int plazoMeses) {
        this.plazoMeses = plazoMeses;
    }
    
    public BigDecimal getTasaInteresAnual() {
        return tasaInteresAnual;
    }
    
    public void setTasaInteresAnual(BigDecimal tasaInteresAnual) {
        this.tasaInteresAnual = tasaInteresAnual;
    }
    
    public LocalDate getFechaOtorgamiento() {
        return fechaOtorgamiento;
    }
    
    public void setFechaOtorgamiento(LocalDate fechaOtorgamiento) {
        this.fechaOtorgamiento = fechaOtorgamiento;
    }
    
    public List<CuotaDTO> getTablaAmortizacion() {
        return tablaAmortizacion;
    }
    
    public void setTablaAmortizacion(List<CuotaDTO> tablaAmortizacion) {
        this.tablaAmortizacion = tablaAmortizacion;
    }
    
    public static class CuotaDTO {
        private int numeroCuota;
        private BigDecimal valorCuota;
        private BigDecimal interesPagado;
        private BigDecimal capitalPagado;
        private BigDecimal saldoRestante;
        
        public CuotaDTO() {
        }
        
        public int getNumeroCuota() {
            return numeroCuota;
        }
        
        public void setNumeroCuota(int numeroCuota) {
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
    }
}
