package ec.edu.pinza.ex_comercializadora_restjava.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * POJO Producto - Producto de comercializadora (SIN JPA - usa JDBC)
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public class Producto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer idProducto;
    private String codigo;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
    private String imagen;
    
    public Producto() {
    }
    
    public Producto(String codigo, String nombre, BigDecimal precio, Integer stock, String image) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.imagen = image;
    }
    
    // Getters y Setters
    public Integer getIdProducto() {
        return idProducto;
    }
    
    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
    
    public Integer getStock() {
        return stock;
    }
    
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    
    public String getImagen() {
        return imagen;
    }
    
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
