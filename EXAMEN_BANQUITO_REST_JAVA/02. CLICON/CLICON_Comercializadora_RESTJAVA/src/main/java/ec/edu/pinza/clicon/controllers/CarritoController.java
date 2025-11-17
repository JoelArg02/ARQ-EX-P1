package ec.edu.pinza.clicon.controllers;

import ec.edu.pinza.clicon.models.ItemCarrito;
import ec.edu.pinza.clicon.models.ProductoDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controlador que mantiene el estado del carrito de compras en consola.
 */
public class CarritoController {

    private final List<ItemCarrito> carrito = new ArrayList<>();

    public List<ItemCarrito> obtenerCarrito() {
        return carrito;
    }

    public BigDecimal obtenerTotal() {
        return carrito.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Replica la validacion de stock de CLIWEB: no se puede superar el stock disponible
     * considerando lo ya agregado en la factura actual.
     */
    public String agregarProducto(ProductoDTO producto, int cantidad) {
        if (cantidad <= 0) {
            return "La cantidad debe ser mayor a cero.";
        }

        Optional<ItemCarrito> existente = carrito.stream()
                .filter(item -> item.getIdProducto().equals(producto.getIdProducto()))
                .findFirst();

        int cantidadActual = existente.map(ItemCarrito::getCantidad).orElse(0);
        int totalSolicitado = cantidadActual + cantidad;
        int stock = producto.getStock();

        if (totalSolicitado > stock) {
            return "No se puede agregar. Stock disponible: " + stock + ". Ya tienes " + cantidadActual + " en tu factura.";
        }

        if (existente.isPresent()) {
            existente.get().setCantidad(totalSolicitado);
        } else {
            carrito.add(new ItemCarrito(
                    producto.getIdProducto(),
                    producto.getNombre(),
                    producto.getPrecio(),
                    cantidad,
                    producto.getStock()
            ));
        }
        return null;
    }

    public boolean eliminarProducto(Integer idProducto) {
        return carrito.removeIf(item -> item.getIdProducto().equals(idProducto));
    }

    public void vaciar() {
        carrito.clear();
    }

    public boolean estaVacio() {
        return carrito.isEmpty();
    }
}
