package ec.edu.pinza.cliesc.controllers;

import ec.edu.pinza.cliesc.managers.SessionManager;
import ec.edu.pinza.cliesc.models.FacturaResponseDTO;
import ec.edu.pinza.cliesc.models.ItemCarrito;
import ec.edu.pinza.cliesc.services.ComercializadoraRestClient;
import ec.edu.pinza.cliesc.views.CheckoutFrame;
import ec.edu.pinza.cliesc.views.ProductosFrame;
import javax.swing.SwingUtilities;
import java.util.List;

/**
 * Controlador para la vista de Checkout.
 */
public class CheckoutController {

    private final CheckoutFrame view;
    private final ComercializadoraRestClient comercializadoraClient;

    public CheckoutController(CheckoutFrame view) {
        this.view = view;
        this.comercializadoraClient = new ComercializadoraRestClient();
    }

    public void cambiarMetodoPago() {
        view.actualizarTotales();
    }

    public void confirmarCompra() {
        try {
            String cedula = view.getCedula();
            if (cedula == null || cedula.isEmpty()) {
                view.mostrarError("Ingrese la cédula del cliente");
                return;
            }

            List<ItemCarrito> items = SessionManager.getInstance().getCarrito();

            if (items.isEmpty()) {
                view.mostrarError("La factura está vacía");
                return;
            }

            // Validación local de stock
            for (ItemCarrito item : items) {
                if (item.getCantidad() > item.getStock()) {
                    view.mostrarError("Stock insuficiente para " + item.getNombre() +
                            ". Stock disponible: " + item.getStock());
                    return;
                }
            }

            String metodoPago = view.isEfectivo() ? "EFECTIVO" : "CREDITO_DIRECTO";
            Integer cuotas = view.isEfectivo() ? null : view.getCuotas();

            FacturaResponseDTO factura = comercializadoraClient.crearFactura(
                    cedula,
                    metodoPago,
                    cuotas,
                    items
            );

            if (factura != null && factura.isExitoso()) {
                SessionManager.getInstance().limpiarCarrito();
                view.mostrarExito("Compra realizada con éxito. Factura #" + factura.getIdFactura());

                view.dispose();
                SwingUtilities.invokeLater(() -> {
                    ProductosFrame productosFrame = new ProductosFrame();
                    productosFrame.setVisible(true);
                });
            } else {
                String mensaje = factura != null && factura.getMensaje() != null
                        ? factura.getMensaje()
                        : "No se pudo procesar la factura";
                view.mostrarError(mensaje);
            }

        } catch (Exception ex) {
            view.mostrarError("Error al procesar la compra: " + ex.getMessage());
        }
    }
}
