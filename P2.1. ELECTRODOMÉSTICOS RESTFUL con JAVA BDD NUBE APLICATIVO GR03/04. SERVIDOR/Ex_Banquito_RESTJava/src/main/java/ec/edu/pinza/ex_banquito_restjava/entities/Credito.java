package ec.edu.pinza.ex_banquito_restjava.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * POJO Credito - Representa un crédito otorgado a un cliente (SIN JPA - usa JDBC)
 */
public class Credito implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer idCredito;
    private Cliente cliente;
    private BigDecimal montoAprobado;
    private BigDecimal montoSolicitado;
    private Integer plazoMeses;
    private BigDecimal tasaInteresAnual; // 16.00
    private BigDecimal montoMaximoAutorizado;
    private LocalDate fechaOtorgamiento;
    private String estado; // 'APROBADO', 'RECHAZADO', 'CANCELADO'
    private List<CuotaAmortizacion> cuotasAmortizacion;
    
    // Constructores
    public Credito() {
    }
    
    public Credito(Cliente cliente, BigDecimal montoAprobado, BigDecimal montoSolicitado, 
                   Integer plazoMeses, BigDecimal tasaInteresAnual, BigDecimal montoMaximoAutorizado,
                   LocalDate fechaOtorgamiento, String estado) {
        this.cliente = cliente;
        this.montoAprobado = montoAprobado;
        this.montoSolicitado = montoSolicitado;
        this.plazoMeses = plazoMeses;
        this.tasaInteresAnual = tasaInteresAnual;
        this.montoMaximoAutorizado = montoMaximoAutorizado;
        this.fechaOtorgamiento = fechaOtorgamiento;
        this.estado = estado;
    }
    
    // Getters y Setters
    public Integer getIdCredito() {
        return idCredito;
    }
    
    public void setIdCredito(Integer idCredito) {
        this.idCredito = idCredito;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public BigDecimal getMontoAprobado() {
        return montoAprobado;
    }
    
    public void setMontoAprobado(BigDecimal montoAprobado) {
        this.montoAprobado = montoAprobado;
    }
    
    public BigDecimal getMontoSolicitado() {
        return montoSolicitado;
    }
    
    public void setMontoSolicitado(BigDecimal montoSolicitado) {
        this.montoSolicitado = montoSolicitado;
    }
    
    public Integer getPlazoMeses() {
        return plazoMeses;
    }
    
    public void setPlazoMeses(Integer plazoMeses) {
        this.plazoMeses = plazoMeses;
    }
    
    public BigDecimal getTasaInteresAnual() {
        return tasaInteresAnual;
    }
    
    public void setTasaInteresAnual(BigDecimal tasaInteresAnual) {
        this.tasaInteresAnual = tasaInteresAnual;
    }
    
    public BigDecimal getMontoMaximoAutorizado() {
        return montoMaximoAutorizado;
    }
    
    public void setMontoMaximoAutorizado(BigDecimal montoMaximoAutorizado) {
        this.montoMaximoAutorizado = montoMaximoAutorizado;
    }
    
    public LocalDate getFechaOtorgamiento() {
        return fechaOtorgamiento;
    }
    
    public void setFechaOtorgamiento(LocalDate fechaOtorgamiento) {
        this.fechaOtorgamiento = fechaOtorgamiento;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public List<CuotaAmortizacion> getCuotasAmortizacion() {
        return cuotasAmortizacion;
    }
    
    public void setCuotasAmortizacion(List<CuotaAmortizacion> cuotasAmortizacion) {
        this.cuotasAmortizacion = cuotasAmortizacion;
    }
    
    @Override
    public String toString() {
        return "Credito{" +
                "idCredito=" + idCredito +
                ", montoAprobado=" + montoAprobado +
                ", plazoMeses=" + plazoMeses +
                ", estado='" + estado + '\'' +
                '}';
    }
}
