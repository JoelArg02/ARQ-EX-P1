package ec.edu.pinza.ex_comercializadora_restjava.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class FacturaResponse {
    private boolean exitoso;
    private String mensaje;
    private Integer idFactura;
    private String cedulaCliente;
    private String nombreCliente;
    private LocalDate fecha;
    private BigDecimal total;
    private String formaPago;
    private Integer idCreditoBanco;
    private InfoCredito infoCredito;
    private List<DetalleFacturaDTO> detalles;
    
    public FacturaResponse() {
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
    
    public InfoCredito getInfoCredito() {
        return infoCredito;
    }
    
    public void setInfoCredito(InfoCredito infoCredito) {
        this.infoCredito = infoCredito;
    }
    
    public List<DetalleFacturaDTO> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<DetalleFacturaDTO> detalles) {
        this.detalles = detalles;
    }
    
    public static class InfoCredito {
        private Integer idCredito;
        private int numeroCuotas;
        private BigDecimal tasaInteres;
        private List<OtorgarCreditoResponseDTO.CuotaDTO> tablaAmortizacion;
        
        public InfoCredito() {
        }
        
        public Integer getIdCredito() {
            return idCredito;
        }
        
        public void setIdCredito(Integer idCredito) {
            this.idCredito = idCredito;
        }
        
        public int getNumeroCuotas() {
            return numeroCuotas;
        }
        
        public void setNumeroCuotas(int numeroCuotas) {
            this.numeroCuotas = numeroCuotas;
        }
        
        public BigDecimal getTasaInteres() {
            return tasaInteres;
        }
        
        public void setTasaInteres(BigDecimal tasaInteres) {
            this.tasaInteres = tasaInteres;
        }
        
        public List<OtorgarCreditoResponseDTO.CuotaDTO> getTablaAmortizacion() {
            return tablaAmortizacion;
        }
        
        public void setTablaAmortizacion(List<OtorgarCreditoResponseDTO.CuotaDTO> tablaAmortizacion) {
            this.tablaAmortizacion = tablaAmortizacion;
        }
    }
    
    public static class DetalleFacturaDTO {
        private String codigoProducto;
        private String nombreProducto;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
        
        public DetalleFacturaDTO() {
        }
        
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
}
