package ec.edu.pinza.ex_comercializadora_restjava.dto;

import java.util.List;

public class CrearFacturaRequest {
    private String cedulaCliente;
    private String formaPago; // 'EFECTIVO' o 'CREDITO_DIRECTO'
    private Integer numeroCuotas; // solo si formaPago = 'CREDITO_DIRECTO'
    private List<ItemFactura> items;
    
    public CrearFacturaRequest() {
    }
    
    public String getCedulaCliente() {
        return cedulaCliente;
    }
    
    public void setCedulaCliente(String cedulaCliente) {
        this.cedulaCliente = cedulaCliente;
    }
    
    public String getFormaPago() {
        return formaPago;
    }
    
    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }
    
    public Integer getNumeroCuotas() {
        return numeroCuotas;
    }
    
    public void setNumeroCuotas(Integer numeroCuotas) {
        this.numeroCuotas = numeroCuotas;
    }
    
    public List<ItemFactura> getItems() {
        return items;
    }
    
    public void setItems(List<ItemFactura> items) {
        this.items = items;
    }
    
    public static class ItemFactura {
        private Integer idProducto;
        private Integer cantidad;
        
        public ItemFactura() {
        }
        
        public Integer getIdProducto() {
            return idProducto;
        }
        
        public void setIdProducto(Integer idProducto) {
            this.idProducto = idProducto;
        }
        
        public Integer getCantidad() {
            return cantidad;
        }
        
        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }
}
