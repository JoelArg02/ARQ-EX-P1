package ec.edu.pinza.ex_banquito_restjava.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Entidad Credito - Representa un crédito otorgado a un cliente
 */
@Entity
@Table(name = "CREDITO")
public class Credito implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CREDITO")
    private Integer idCredito;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CEDULA", nullable = false)
    private Cliente cliente;
    
    @Column(name = "MONTO_APROBADO", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoAprobado;
    
    @Column(name = "MONTO_SOLICITADO", precision = 10, scale = 2)
    private BigDecimal montoSolicitado;
    
    @Column(name = "PLAZO_MESES", nullable = false)
    private Integer plazoMeses;
    
    @Column(name = "TASA_INTERES_ANUAL", precision = 5, scale = 2, nullable = false)
    private BigDecimal tasaInteresAnual; // 16.00
    
    @Column(name = "MONTO_MAXIMO_AUTORIZADO", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoMaximoAutorizado;
    
    @Column(name = "FECHA_OTORGAMIENTO", nullable = false)
    private LocalDate fechaOtorgamiento;
    
    @Column(name = "ESTADO", length = 20, nullable = false)
    private String estado; // 'APROBADO', 'RECHAZADO', 'CANCELADO'
    
    @OneToMany(mappedBy = "credito", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
