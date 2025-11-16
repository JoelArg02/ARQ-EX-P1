package ec.edu.pinza.ex_comercializadora_restjava.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Factura")
public class Factura implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idFactura")
    private Integer idFactura;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCliente", nullable = false)
    private ClienteCom cliente;
    
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;
    
    @Column(name = "total", precision = 10, scale = 2, nullable = false)
    private BigDecimal total;
    
    @Column(name = "formaPago", length = 20, nullable = false)
    private String formaPago; // 'EFECTIVO' o 'CREDITO_DIRECTO'
    
    @Column(name = "idCreditoBanco")
    private Integer idCreditoBanco;
    
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
