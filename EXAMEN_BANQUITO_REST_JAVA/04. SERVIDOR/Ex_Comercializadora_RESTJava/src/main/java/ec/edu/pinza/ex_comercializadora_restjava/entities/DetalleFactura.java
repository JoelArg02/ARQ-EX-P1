package ec.edu.pinza.ex_comercializadora_restjava.entities;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * POJO DetalleFactura - Detalle de factura (SIN JPA - usa JDBC)
 */
public class DetalleFactura implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer idDetalle;
    private Factura factura;
    private Producto producto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    
    public DetalleFactura() {
    }
    
    public DetalleFactura(Factura factura, Producto producto, Integer cantidad, BigDecimal precioUnitario) {
        this.factura = factura;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
    }
    
    // Getters y Setters
    public Integer getIdDetalle() {
        return idDetalle;
    }
    
    public void setIdDetalle(Integer idDetalle) {
        this.idDetalle = idDetalle;
    }
    
    public Factura getFactura() {
        return factura;
    }
    
    public void setFactura(Factura factura) {
        this.factura = factura;
    }
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
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
