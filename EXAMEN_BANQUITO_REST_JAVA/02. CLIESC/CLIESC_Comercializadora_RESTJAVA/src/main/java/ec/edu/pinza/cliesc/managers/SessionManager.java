package ec.edu.pinza.cliesc.managers;

import ec.edu.pinza.cliesc.models.ItemCarrito;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de sesion - reemplazo de HttpSession en escritorio.
 */
public class SessionManager {
    private static SessionManager instance;

    private String usuario;
    private String cedula;
    private String rol;
    private final List<ItemCarrito> carrito;

    private SessionManager() {
        this.carrito = new ArrayList<>();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // ===== Datos de usuario =====
    public void setCliente(String usuario, String cedula, String rol) {
        this.usuario = usuario;
        this.cedula = cedula;
        this.rol = rol;
    }
    
    public String getRol() {
        return rol;
    }
    
    public boolean isAdmin() {
        return "ADMIN".equals(rol);
    }

    public String getUsuario() {
        return usuario;
    }

    public String getCedula() {
        return cedula;
    }

    public boolean isAutenticado() {
        return cedula != null;
    }

    public void cerrarSesion() {
        this.usuario = null;
        this.cedula = null;
        this.rol = null;
        this.carrito.clear();
    }

    // ===== Carrito =====
    public List<ItemCarrito> getCarrito() {
        return carrito;
    }

    public void agregarAlCarrito(ItemCarrito item) {
        for (ItemCarrito itemExistente : carrito) {
            if (itemExistente.getIdProducto().equals(item.getIdProducto())) {
                itemExistente.setCantidad(itemExistente.getCantidad() + item.getCantidad());
                return;
            }
        }
        carrito.add(item);
    }

    public void actualizarCantidad(Long idProducto, Integer nuevaCantidad) {
        for (ItemCarrito item : carrito) {
            if (item.getIdProducto().equals(idProducto)) {
                item.setCantidad(nuevaCantidad);
                return;
            }
        }
    }

    public void eliminarDelCarrito(Long idProducto) {
        carrito.removeIf(item -> item.getIdProducto().equals(idProducto));
    }

    public void limpiarCarrito() {
        carrito.clear();
    }

    public int getTotalItems() {
        return carrito.stream().mapToInt(ItemCarrito::getCantidad).sum();
    }

    public BigDecimal getSubtotalCarrito() {
        return carrito.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Integer getCantidadEnCarrito(Long idProducto) {
        for (ItemCarrito item : carrito) {
            if (item.getIdProducto().equals(idProducto)) {
                return item.getCantidad();
            }
        }
        return 0;
    }
}
