package ec.edu.pinza.clicon.controllers;

import ec.edu.pinza.clicon.models.ProductoDTO;
import ec.edu.pinza.clicon.services.ComercializadoraRestClient;
import ec.edu.pinza.clicon.views.ConsoleView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * Controlador para administración de productos en CLI Consola
 */
public class AdminProductosController {
    
    private final ComercializadoraRestClient restClient;
    private final ConsoleView view;
    private final Scanner scanner;
    
    public AdminProductosController(ComercializadoraRestClient restClient, ConsoleView view, Scanner scanner) {
        this.restClient = restClient;
        this.view = view;
        this.scanner = scanner;
    }
    
    public void mostrarMenuAdmin() {
        boolean continuar = true;
        
        while (continuar) {
            view.mostrarMensaje("\n========================================");
            view.mostrarMensaje("  ADMINISTRACIÓN DE PRODUCTOS");
            view.mostrarMensaje("========================================");
            view.mostrarMensaje("1) Listar productos");
            view.mostrarMensaje("2) Agregar producto");
            view.mostrarMensaje("3) Editar producto");
            view.mostrarMensaje("4) Eliminar producto");
            view.mostrarMensaje("0) Volver");
            view.mostrarMensaje("========================================");
            System.out.print("Selecciona una opción: ");
            
            String opcion = scanner.nextLine().trim();
            
            switch (opcion) {
                case "1" -> listarProductos();
                case "2" -> agregarProducto();
                case "3" -> editarProducto();
                case "4" -> eliminarProducto();
                case "0" -> continuar = false;
                default -> view.mostrarError("Opción no válida");
            }
        }
    }
    
    private void listarProductos() {
        try {
            List<ProductoDTO> productos = restClient.obtenerProductos();
            
            if (productos.isEmpty()) {
                view.mostrarMensaje("No hay productos disponibles.");
                return;
            }
            
            view.mostrarMensaje("\n========== LISTA DE PRODUCTOS ==========");
            for (ProductoDTO p : productos) {
                view.mostrarMensaje(String.format(
                    "ID: %d | Código: %s | Nombre: %s | Precio: $%.2f | Stock: %d",
                    p.getIdProducto(), p.getCodigo(), p.getNombre(), p.getPrecio(), p.getStock()
                ));
            }
            view.mostrarMensaje("========================================");
            
        } catch (Exception e) {
            view.mostrarError("Error al listar productos: " + e.getMessage());
        }
    }
    
    private void agregarProducto() {
        try {
            view.mostrarMensaje("\n=== AGREGAR NUEVO PRODUCTO ===");
            
            System.out.print("Código: ");
            String codigo = scanner.nextLine().trim();
            
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine().trim();
            
            System.out.print("Precio: ");
            BigDecimal precio = new BigDecimal(scanner.nextLine().trim());
            
            System.out.print("Stock: ");
            Integer stock = Integer.parseInt(scanner.nextLine().trim());
            
            ProductoDTO producto = new ProductoDTO();
            producto.setCodigo(codigo);
            producto.setNombre(nombre);
            producto.setPrecio(precio);
            producto.setStock(stock);
            // imagen queda null - solo web/escritorio manejan imágenes
            
            ProductoDTO creado = restClient.crearProducto(producto);
            view.mostrarExito("✓ Producto creado exitosamente. ID: " + creado.getIdProducto());
            
        } catch (NumberFormatException e) {
            view.mostrarError("Error: Precio o stock inválido");
        } catch (Exception e) {
            view.mostrarError("Error al crear producto: " + e.getMessage());
        }
    }
    
    private void editarProducto() {
        try {
            listarProductos();
            
            System.out.print("\nIngrese el ID del producto a editar: ");
            Integer id = Integer.parseInt(scanner.nextLine().trim());
            
            // Obtener producto actual
            ProductoDTO productoActual = restClient.obtenerProductoPorId(id);
            
            view.mostrarMensaje("\nProducto actual:");
            view.mostrarMensaje(String.format("Código: %s | Nombre: %s | Precio: $%.2f | Stock: %d",
                productoActual.getCodigo(), productoActual.getNombre(), 
                productoActual.getPrecio(), productoActual.getStock()));
            
            view.mostrarMensaje("\n--- Ingrese los nuevos datos (Enter para mantener actual) ---");
            
            System.out.print("Código [" + productoActual.getCodigo() + "]: ");
            String codigo = scanner.nextLine().trim();
            if (codigo.isEmpty()) codigo = productoActual.getCodigo();
            
            System.out.print("Nombre [" + productoActual.getNombre() + "]: ");
            String nombre = scanner.nextLine().trim();
            if (nombre.isEmpty()) nombre = productoActual.getNombre();
            
            System.out.print("Precio [" + productoActual.getPrecio() + "]: ");
            String precioStr = scanner.nextLine().trim();
            BigDecimal precio = precioStr.isEmpty() ? productoActual.getPrecio() : new BigDecimal(precioStr);
            
            System.out.print("Stock [" + productoActual.getStock() + "]: ");
            String stockStr = scanner.nextLine().trim();
            Integer stock = stockStr.isEmpty() ? productoActual.getStock() : Integer.parseInt(stockStr);
            
            ProductoDTO producto = new ProductoDTO();
            producto.setIdProducto(id);
            producto.setCodigo(codigo);
            producto.setNombre(nombre);
            producto.setPrecio(precio);
            producto.setStock(stock);
            // Preservar la imagen existente
            producto.setImagen(productoActual.getImagen());
            
            ProductoDTO actualizado = restClient.actualizarProducto(id, producto);
            view.mostrarExito("✓ Producto actualizado exitosamente");
            
        } catch (NumberFormatException e) {
            view.mostrarError("Error: ID, precio o stock inválido");
        } catch (Exception e) {
            view.mostrarError("Error al editar producto: " + e.getMessage());
        }
    }
    
    private void eliminarProducto() {
        try {
            listarProductos();
            
            System.out.print("\nIngrese el ID del producto a eliminar: ");
            Integer id = Integer.parseInt(scanner.nextLine().trim());
            
            // Mostrar producto a eliminar
            ProductoDTO producto = restClient.obtenerProductoPorId(id);
            view.mostrarMensaje("\nProducto a eliminar:");
            view.mostrarMensaje(producto.getCodigo() + " - " + producto.getNombre());
            
            System.out.print("¿Está seguro? (S/N): ");
            String confirmacion = scanner.nextLine().trim().toUpperCase();
            
            if (confirmacion.equals("S")) {
                restClient.eliminarProducto(id);
                view.mostrarExito("✓ Producto eliminado exitosamente");
            } else {
                view.mostrarMensaje("Operación cancelada");
            }
            
        } catch (NumberFormatException e) {
            view.mostrarError("Error: ID inválido");
        } catch (Exception e) {
            view.mostrarError("Error al eliminar producto: " + e.getMessage());
        }
    }
}
