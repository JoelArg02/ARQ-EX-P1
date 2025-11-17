package ec.edu.pinza.clicon.models;

import java.math.BigDecimal;

/**
 * Item de factura seleccionado por el usuario.
 */
public class ItemCarrito {
    private Integer idProducto;
    private String nombre;
    private BigDecimal precio;
    private Integer cantidad;
    private Integer stockDisponible;

    public ItemCarrito() {
    }

    public ItemCarrito(Integer idProducto, String nombre, BigDecimal precio, Integer cantidad, Integer stockDisponible) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.stockDisponible = stockDisponible;
    }

    public BigDecimal getSubtotal() {
        return precio.multiply(new BigDecimal(cantidad));
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(Integer stockDisponible) {
        this.stockDisponible = stockDisponible;
    }
}
