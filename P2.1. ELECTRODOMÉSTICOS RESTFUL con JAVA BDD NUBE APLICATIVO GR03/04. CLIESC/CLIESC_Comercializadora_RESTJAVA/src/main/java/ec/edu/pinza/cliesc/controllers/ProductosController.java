package ec.edu.pinza.cliesc.controllers;

import ec.edu.pinza.cliesc.managers.SessionManager;
import ec.edu.pinza.cliesc.models.ItemCarrito;
import ec.edu.pinza.cliesc.models.ProductoDTO;
import ec.edu.pinza.cliesc.services.ComercializadoraRestClient;
import ec.edu.pinza.cliesc.views.CarritoFrame;
import ec.edu.pinza.cliesc.views.LoginFrame;
import ec.edu.pinza.cliesc.views.ProductosFrame;
import ec.edu.pinza.cliesc.views.VentasFrame;
import javax.swing.SwingUtilities;
import java.util.List;

/**
 * Controlador para la vista de Productos.
 */
public class ProductosController {

    private final ProductosFrame view;
    private final ComercializadoraRestClient restClient;

    public ProductosController(ProductosFrame view) {
        this.view = view;
        this.restClient = new ComercializadoraRestClient();
    }

    public void cargarProductos() {
        try {
            List<ProductoDTO> productos = restClient.obtenerProductos();
            view.mostrarProductos(productos);
        } catch (Exception ex) {
            view.mostrarError("Error al cargar productos: " + ex.getMessage());
        }
    }

    public void agregarAlCarrito(ProductoDTO producto, int cantidad) {
        try {
            Integer cantidadActualEnCarrito = SessionManager.getInstance()
                    .getCantidadEnCarrito(producto.getIdProducto());
            Integer cantidadTotal = cantidadActualEnCarrito + cantidad;

            if (cantidadTotal > producto.getStock()) {
                view.mostrarError("No se puede agregar. Stock disponible: " + producto.getStock() +
                        ". Ya tienes " + cantidadActualEnCarrito + " en tu factura.");
                return;
            }

            ItemCarrito item = new ItemCarrito(
                    producto.getIdProducto(),
                    producto.getNombre(),
                    producto.getPrecio(),
                    cantidad,
                    producto.getStock()
            );

            SessionManager.getInstance().agregarAlCarrito(item);
            view.actualizarBadgeCarrito();
            view.mostrarExito("Producto agregado a la factura");

        } catch (Exception ex) {
            view.mostrarError("Error al agregar producto: " + ex.getMessage());
        }
    }

    public void irACarrito() {
        if (SessionManager.getInstance().getCarrito().isEmpty()) {
            view.mostrarError("La factura está vacía");
            return;
        }
        view.dispose();
        SwingUtilities.invokeLater(() -> {
            CarritoFrame carritoFrame = new CarritoFrame();
            carritoFrame.setVisible(true);
        });
    }

    public void irAVentas() {
        view.dispose();
        SwingUtilities.invokeLater(() -> {
            VentasFrame ventasFrame = new VentasFrame();
            ventasFrame.setVisible(true);
        });
    }

    public void cerrarSesion() {
        SessionManager.getInstance().cerrarSesion();
        view.dispose();

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
