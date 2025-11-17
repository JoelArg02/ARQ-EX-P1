package ec.edu.pinza.cliesc.controllers;

import ec.edu.pinza.cliesc.managers.SessionManager;
import ec.edu.pinza.cliesc.models.ItemCarrito;
import ec.edu.pinza.cliesc.views.CarritoFrame;
import ec.edu.pinza.cliesc.views.CheckoutFrame;
import javax.swing.SwingUtilities;
import java.util.List;

/**
 * Controlador para la vista del Carrito.
 */
public class CarritoController {

    private final CarritoFrame view;

    public CarritoController(CarritoFrame view) {
        this.view = view;
    }

    public void eliminarItem(int index) {
        try {
            List<ItemCarrito> carrito = SessionManager.getInstance().getCarrito();
            if (index >= 0 && index < carrito.size()) {
                ItemCarrito item = carrito.get(index);
                SessionManager.getInstance().eliminarDelCarrito(item.getIdProducto());
                view.cargarCarrito();
            }
        } catch (Exception ex) {
            view.mostrarError("Error al eliminar producto: " + ex.getMessage());
        }
    }

    public void limpiarCarrito() {
        view.mostrarConfirmacion("¿Está seguro de limpiar toda la factura?", () -> {
            SessionManager.getInstance().limpiarCarrito();
            view.cargarCarrito();
        });
    }

    public void irACheckout() {
        if (SessionManager.getInstance().getCarrito().isEmpty()) {
            view.mostrarError("La factura está vacía");
            return;
        }

        view.dispose();
        SwingUtilities.invokeLater(() -> {
            CheckoutFrame checkoutFrame = new CheckoutFrame();
            checkoutFrame.setVisible(true);
        });
    }
}
