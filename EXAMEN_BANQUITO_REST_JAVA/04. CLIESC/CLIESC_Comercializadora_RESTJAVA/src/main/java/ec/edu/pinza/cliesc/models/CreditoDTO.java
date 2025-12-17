package ec.edu.pinza.cliesc.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreditoDTO {
    private Long idCredito;
    private String cedulaCliente;
    private BigDecimal monto;
    private Integer plazoMeses;
    private BigDecimal tasaInteres;
    private LocalDate fechaInicio;
    private String estado;
    private Boolean valido;
    private String mensaje;
    private BigDecimal montoMaximo;
    private Boolean esSujetoCredito;
    private String motivo;

    // Constructores
    public CreditoDTO() {
    }

    public CreditoDTO(String cedulaCliente, BigDecimal monto, Integer plazoMeses, BigDecimal tasaInteres) {
        this.cedulaCliente = cedulaCliente;
        this.monto = monto;
        this.plazoMeses = plazoMeses;
        this.tasaInteres = tasaInteres;
    }

    // Getters y Setters
    public Long getIdCredito() {
        return idCredito;
    }

    public void setIdCredito(Long idCredito) {
        this.idCredito = idCredito;
    }

    public String getCedulaCliente() {
        return cedulaCliente;
    }

    public void setCedulaCliente(String cedulaCliente) {
        this.cedulaCliente = cedulaCliente;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Integer getPlazoMeses() {
        return plazoMeses;
    }

    public void setPlazoMeses(Integer plazoMeses) {
        this.plazoMeses = plazoMeses;
    }

    public BigDecimal getTasaInteres() {
        return tasaInteres;
    }

    public void setTasaInteres(BigDecimal tasaInteres) {
        this.tasaInteres = tasaInteres;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean isValido() {
        return valido;
    }

    public void setValido(Boolean valido) {
        this.valido = valido;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public BigDecimal getMontoMaximo() {
        return montoMaximo;
    }

    public void setMontoMaximo(BigDecimal montoMaximo) {
        this.montoMaximo = montoMaximo;
    }

    public Boolean isEsSujetoCredito() {
        return esSujetoCredito;
    }

    public void setEsSujetoCredito(Boolean esSujetoCredito) {
        this.esSujetoCredito = esSujetoCredito;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
