package ec.edu.pinza.ex_comercializadora_restjava.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * POJO Factura - Factura de venta (SIN JPA - usa JDBC)
 */
public class Factura implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer idFactura;
    private ClienteCom cliente;
    private LocalDate fecha;
    private BigDecimal total;
    private String formaPago; // 'EFECTIVO' o 'CREDITO_DIRECTO'
    private Integer idCreditoBanco;
    private List<DetalleFactura> detalles = new ArrayList<>();
    
    public Factura() {
    }
    
    public Factura(ClienteCom cliente, LocalDate fecha, BigDecimal total, String formaPago) {
        this.cliente = cliente;
        this.fecha = fecha;
        this.total = total;
        this.formaPago = formaPago;
    }
    
    // Getters y Setters
    public Integer getIdFactura() {
        return idFactura;
    }
    
    public void setIdFactura(Integer idFactura) {
        this.idFactura = idFactura;
    }
    
    public ClienteCom getCliente() {
        return cliente;
    }
    
    public void setCliente(ClienteCom cliente) {
        this.cliente = cliente;
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
    
    public List<DetalleFactura> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<DetalleFactura> detalles) {
        this.detalles = detalles;
    }
    
    public void addDetalle(DetalleFactura detalle) {
        detalles.add(detalle);
        detalle.setFactura(this);
    }
}
