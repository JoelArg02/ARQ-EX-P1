package ec.edu.pinza.cliesc.models;

import java.math.BigDecimal;

public class ItemCarrito {
    private Long idProducto;
    private String nombre;
    private BigDecimal precio;
    private Integer cantidad;
    private Integer stock;

    // Constructores
    public ItemCarrito() {
    }

    public ItemCarrito(Long idProducto, String nombre, BigDecimal precio, Integer cantidad, Integer stock) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.stock = stock;
    }

    // Getters y Setters
    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public BigDecimal getSubtotal() {
        return precio.multiply(new BigDecimal(cantidad));
    }
}
