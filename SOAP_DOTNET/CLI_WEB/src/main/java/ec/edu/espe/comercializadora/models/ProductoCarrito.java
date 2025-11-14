package ec.edu.espe.comercializadora.models;

public class ProductoCarrito {
    private Electrodomestico producto;
    private int cantidad;

    public ProductoCarrito() {}

    public ProductoCarrito(Electrodomestico producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Electrodomestico getProducto() {
        return producto;
    }

    public void setProducto(Electrodomestico producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return producto.getPrecioVenta() * cantidad;
    }
}