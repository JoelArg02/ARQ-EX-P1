package ec.edu.pinza.cliesc.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO alineado al cliente web para respuestas de facturas.
 */
public class FacturaResponseDTO {
    private boolean exitoso;
    private String mensaje;
    private Integer idFactura;
    private String cedulaCliente;
    private String nombreCliente;
    private LocalDate fecha;
    private BigDecimal total;
    private String formaPago;
    private Integer idCreditoBanco;
    private List<DetalleFacturaDTO> detalles;
    private InfoCreditoDTO infoCredito;

    public static class DetalleFacturaDTO {
        private String codigoProducto;
        private String nombreProducto;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;

        public String getCodigoProducto() {
            return codigoProducto;
        }

        public void setCodigoProducto(String codigoProducto) {
            this.codigoProducto = codigoProducto;
        }

        public String getNombreProducto() {
            return nombreProducto;
        }

        public void setNombreProducto(String nombreProducto) {
            this.nombreProducto = nombreProducto;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }

        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(BigDecimal precioUnitario) {
            this.precioUnitario = precioUnitario;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }
    }

    public static class InfoCreditoDTO {
        private Integer idCredito;
        private BigDecimal montoCredito;
        private Integer numeroCuotas;
        private BigDecimal valorCuota;
        private BigDecimal tasaInteres;
        private String estado;
        private List<CuotaAmortizacionDTO> tablaAmortizacion;

        public Integer getIdCredito() {
            return idCredito;
        }

        public void setIdCredito(Integer idCredito) {
            this.idCredito = idCredito;
        }

        public BigDecimal getMontoCredito() {
            return montoCredito;
        }

        public void setMontoCredito(BigDecimal montoCredito) {
            this.montoCredito = montoCredito;
        }

        public Integer getNumeroCuotas() {
            return numeroCuotas;
        }

        public void setNumeroCuotas(Integer numeroCuotas) {
            this.numeroCuotas = numeroCuotas;
        }

        public BigDecimal getValorCuota() {
            return valorCuota;
        }

        public void setValorCuota(BigDecimal valorCuota) {
            this.valorCuota = valorCuota;
        }

        public BigDecimal getTasaInteres() {
            return tasaInteres;
        }

        public void setTasaInteres(BigDecimal tasaInteres) {
            this.tasaInteres = tasaInteres;
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }

        public List<CuotaAmortizacionDTO> getTablaAmortizacion() {
            return tablaAmortizacion;
        }

        public void setTablaAmortizacion(List<CuotaAmortizacionDTO> tablaAmortizacion) {
            this.tablaAmortizacion = tablaAmortizacion;
        }
    }

    public static class CuotaAmortizacionDTO {
        private Integer numeroCuota;
        private BigDecimal valorCuota;
        private BigDecimal interesPagado;
        private BigDecimal capitalPagado;
        private BigDecimal saldoRestante;

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
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public void setExitoso(boolean exitoso) {
        this.exitoso = exitoso;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Integer getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Integer idFactura) {
        this.idFactura = idFactura;
    }

    public String getCedulaCliente() {
        return cedulaCliente;
    }

    public void setCedulaCliente(String cedulaCliente) {
        this.cedulaCliente = cedulaCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public Integer getIdCreditoBanco() {
        return idCreditoBanco;
    }

    public void setIdCreditoBanco(Integer idCreditoBanco) {
        this.idCreditoBanco = idCreditoBanco;
    }

    public List<DetalleFacturaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFacturaDTO> detalles) {
        this.detalles = detalles;
    }

    public InfoCreditoDTO getInfoCredito() {
        return infoCredito;
    }

    public void setInfoCredito(InfoCreditoDTO infoCredito) {
        this.infoCredito = infoCredito;
    }
}
