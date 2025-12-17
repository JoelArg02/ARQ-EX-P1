package ec.edu.pinza.clicon.app;

import ec.edu.pinza.clicon.controllers.AdminProductosController;
import ec.edu.pinza.clicon.controllers.AuthController;
import ec.edu.pinza.clicon.controllers.CarritoController;
import ec.edu.pinza.clicon.controllers.CheckoutController;
import ec.edu.pinza.clicon.controllers.CreditoController;
import ec.edu.pinza.clicon.controllers.ProductosController;
import ec.edu.pinza.clicon.controllers.VentasController;
import ec.edu.pinza.clicon.models.FacturaResponseDTO;
import ec.edu.pinza.clicon.models.ItemCarrito;
import ec.edu.pinza.clicon.models.LoginResponseDTO;
import ec.edu.pinza.clicon.models.ProductoDTO;
import ec.edu.pinza.clicon.models.UsuarioSesion;
import ec.edu.pinza.clicon.services.BanquitoRestClient;
import ec.edu.pinza.clicon.services.ComercializadoraRestClient;
import ec.edu.pinza.clicon.views.ConsoleView;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * Cliente de consola con login dinámico y diferenciación de roles.
 * ADMIN: MONSTER / MONSTER9 (puede vender a cualquier cédula, ver todas las ventas)
 * CLIENTES: cédula / abcd1234 (solo puede comprar para su propia cédula, ver sus compras)
 */
public class CliconApplication {

    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new CliconApplication().run();
    }

    private void run() {
        System.out.println("=== CLICON Comercializadora REST Java ===");
        System.out.println("Credenciales:");
        System.out.println("  Admin: MONSTER / MONSTER9");
        System.out.println("  Cliente: cedula / abcd1234");
        System.out.println();
        
        try (ComercializadoraRestClient restClient = new ComercializadoraRestClient();
             BanquitoRestClient banquitoClient = new BanquitoRestClient()) {
            
            AuthController authController = new AuthController(restClient);
            UsuarioSesion sesion = loginLoop(authController);
            if (sesion == null) {
                return;
            }

            ConsoleView view = new ConsoleView();
            ProductosController productosController = new ProductosController(restClient);
            CarritoController carritoController = new CarritoController();
            CheckoutController checkoutController = new CheckoutController(restClient);
            VentasController ventasController = new VentasController(restClient);
            CreditoController creditoController = new CreditoController(banquitoClient);
            AdminProductosController adminProductosController = new AdminProductosController(restClient, view, scanner);

            boolean salir = false;
            while (!salir) {
                mostrarMenuPrincipal(sesion);
                int opcion = leerEntero("Selecciona una opcion: ");
                
                // Opciones comunes
                switch (opcion) {
                    case 1 -> flujoProductos(productosController, carritoController);
                    case 2 -> flujoCarrito(carritoController);
                    case 3 -> flujoCheckout(checkoutController, carritoController, sesion);
                    case 4 -> flujoVentas(ventasController, sesion);
                    case 5 -> flujoDetalleVenta(ventasController, sesion);
                    case 0 -> salir = true;
                    default -> {
                        // Opciones solo para admin
                        if (sesion.isAdmin()) {
                            switch (opcion) {
                                case 6 -> flujoValidarSujetoCredito(creditoController);
                                case 7 -> flujoConsultarMontoMaximo(creditoController);
                                case 8 -> adminProductosController.mostrarMenuAdmin();
                                default -> System.out.println("Opcion invalida.");
                            }
                        } else {
                            System.out.println("Opcion invalida.");
                        }
                    }
                }
            }
            System.out.println("Hasta pronto!");
        } catch (Exception e) {
            System.out.println("Ocurrio un error: " + e.getMessage());
        }
    }

    private void mostrarMenuPrincipal(UsuarioSesion sesion) {
        System.out.println("\n" + (sesion.isAdmin() ? "[ADMIN]" : "[CLIENTE]") + " Usuario: " + sesion.getUsuario());
        if (!sesion.isAdmin() && sesion.getCedula() != null) {
            System.out.println("CI: " + sesion.getCedula());
        }
        System.out.println("1) Ver productos / agregar al carrito");
        System.out.println("2) Ver carrito");
        System.out.println("3) Checkout / pagar");
        System.out.println("4) " + (sesion.isAdmin() ? "Ver todas las ventas" : "Mis compras"));
        System.out.println("5) Ver detalle de venta");
        
        if (sesion.isAdmin()) {
            System.out.println("6) Validar sujeto de credito");
            System.out.println("7) Consultar monto maximo");
            System.out.println("8) Administrar productos (CRUD)");
        }
        
        System.out.println("0) Salir");
    }

    private UsuarioSesion loginLoop(AuthController authController) {
        int intentos = 0;
        while (intentos < 3) {
            System.out.print("Usuario: ");
            String usuario = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            LoginResponseDTO response = authController.login(usuario, password);
            if (response != null && response.isExitoso()) {
                UsuarioSesion sesion = authController.crearSesion(response);
                System.out.println("Login exitoso! Rol: " + response.getRol());
                return sesion;
            }
            
            String mensaje = response != null && response.getMensaje() != null 
                    ? response.getMensaje() 
                    : "Usuario o password incorrectos.";
            System.out.println(mensaje);
            intentos++;
        }
        System.out.println("Demasiados intentos fallidos. Cerrando.");
        return null;
    }

    private void flujoProductos(ProductosController productosController, CarritoController carritoController) {
        try {
            List<ProductoDTO> productos = productosController.listarProductos();
            ConsoleView.mostrarProductos(productos);

            while (true) {
                System.out.print("Deseas agregar un producto al carrito? (s/n): ");
                String resp = scanner.nextLine().trim().toLowerCase();
                if (!resp.equals("s")) {
                    break;
                }

                int id = leerEntero("Ingresa el ID del producto: ");
                Optional<ProductoDTO> prodOpt = productos.stream()
                        .filter(p -> p.getIdProducto() == id)
                        .findFirst();
                if (prodOpt.isEmpty()) {
                    System.out.println("Producto no encontrado.");
                    continue;
                }

                int cantidad = leerEntero("Cantidad: ");
                String error = carritoController.agregarProducto(prodOpt.get(), cantidad);
                if (error != null) {
                    System.out.println(error);
                } else {
                    System.out.println("Producto agregado al carrito.");
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error al cargar productos: " + e.getMessage());
        }
    }

    private void flujoCarrito(CarritoController carritoController) {
        boolean continuar = true;
        while (continuar) {
            ConsoleView.mostrarCarrito(carritoController.obtenerCarrito(), carritoController.obtenerTotal());
            if (carritoController.estaVacio()) {
                return;
            }
            System.out.println("Opciones: 1) Eliminar item  2) Vaciar carrito  0) Volver");
            int opcion = leerEntero("Selecciona: ");
            switch (opcion) {
                case 1 -> {
                    int id = leerEntero("ID del producto a eliminar: ");
                    if (carritoController.eliminarProducto(id)) {
                        System.out.println("Producto eliminado.");
                    } else {
                        System.out.println("Producto no encontrado en el carrito.");
                    }
                }
                case 2 -> {
                    carritoController.vaciar();
                    System.out.println("Carrito vaciado.");
                }
                default -> continuar = false;
            }
        }
    }

    private void flujoCheckout(CheckoutController checkoutController, CarritoController carritoController, UsuarioSesion sesion) {
        if (carritoController.estaVacio()) {
            System.out.println("El carrito esta vacio. Agrega productos antes de pagar.");
            return;
        }

        String cedula;
        if (sesion.isAdmin()) {
            // Admin puede vender a cualquier cédula
            System.out.print("Ingresa la cedula del cliente: ");
            cedula = scanner.nextLine().trim();
            if (cedula.isEmpty()) {
                System.out.println("La cedula es obligatoria para facturar.");
                return;
            }
        } else {
            // Cliente solo puede comprar para su propia cédula
            cedula = sesion.getCedula();
            System.out.println("Comprando para tu cedula: " + cedula);
        }

        System.out.println("Formas de pago: 1) EFECTIVO  2) CREDITO_DIRECTO");
        int opcion = leerEntero("Selecciona: ");
        String formaPago;
        Integer numeroCuotas = null;
        if (opcion == 1) {
            formaPago = "EFECTIVO";
        } else if (opcion == 2) {
            formaPago = "CREDITO_DIRECTO";
            numeroCuotas = leerEntero("Numero de cuotas: ");
            if (numeroCuotas < 3 || numeroCuotas > 24) {
                System.out.println("Numero de cuotas invalido. Debe ser entre 3 y 24.");
                return;
            }
        } else {
            System.out.println("Opcion invalida.");
            return;
        }

        try {
            FacturaResponseDTO resultado = checkoutController.pagar(
                    cedula,
                    formaPago,
                    numeroCuotas,
                    carritoController.obtenerCarrito()
            );

            if (resultado != null && resultado.isExitoso()) {
                carritoController.vaciar();
                ConsoleView.mostrarFacturaDetalle(resultado);
            } else if (resultado != null) {
                System.out.println("No se pudo generar la factura: " + resultado.getMensaje());
            } else {
                System.out.println("No se obtuvo respuesta del servidor.");
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Error al crear la factura: " + e.getMessage());
        }
    }

    private void flujoVentas(VentasController ventasController, UsuarioSesion sesion) {
        try {
            List<FacturaResponseDTO> facturas;
            String titulo;
            
            if (sesion.isAdmin()) {
                // Admin ve TODAS las facturas
                facturas = ventasController.listarTodas();
                titulo = "Historial de Ventas (Todas)";
            } else {
                // Cliente solo ve sus propias compras
                facturas = ventasController.listarPorCliente(sesion.getCedula());
                titulo = "Mis Compras";
            }
            
            ConsoleView.mostrarFacturasResumen(facturas, titulo);
        } catch (IOException | InterruptedException e) {
            System.out.println("Error al cargar ventas: " + e.getMessage());
        }
    }

    private void flujoDetalleVenta(VentasController ventasController, UsuarioSesion sesion) {
        int id = leerEntero("Ingresa el ID de la factura: ");
        try {
            FacturaResponseDTO factura = ventasController.obtenerPorId(id);
            
            // Validar que cliente no pueda ver facturas de otros
            if (!sesion.isAdmin() && factura != null) {
                if (!sesion.getCedula().equals(factura.getCedulaCliente())) {
                    System.out.println("No tienes permiso para ver esta factura.");
                    return;
                }
            }
            
            ConsoleView.mostrarFacturaDetalle(factura);
        } catch (IOException | InterruptedException e) {
            System.out.println("Error al cargar detalle: " + e.getMessage());
        }
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String valor = scanner.nextLine();
            try {
                return Integer.parseInt(valor.trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un numero valido.");
            }
        }
    }

    private void flujoValidarSujetoCredito(CreditoController creditoController) {
        System.out.print("Ingresa la cedula del cliente: ");
        String cedula = scanner.nextLine().trim();
        if (cedula.isEmpty()) {
            System.out.println("La cedula es obligatoria.");
            return;
        }

        try {
            Map<String, Object> response = creditoController.validarSujetoCredito(cedula);
            System.out.println("\n=== Validacion de Sujeto de Credito ===");
            System.out.println("Cedula: " + response.get("cedula"));
            System.out.println("Es sujeto de credito: " + response.get("esSujetoCredito"));
            System.out.println("Mensaje: " + response.get("motivo"));
        } catch (IOException | InterruptedException e) {
            System.out.println("Error al validar sujeto de credito: " + e.getMessage());
        }
    }

    private void flujoConsultarMontoMaximo(CreditoController creditoController) {
        System.out.print("Ingresa la cedula del cliente: ");
        String cedula = scanner.nextLine().trim();
        if (cedula.isEmpty()) {
            System.out.println("La cedula es obligatoria.");
            return;
        }

        try {
            Map<String, Object> response = creditoController.obtenerMontoMaximo(cedula);
            System.out.println("\n=== Monto Maximo de Credito ===");
            System.out.println("Cedula: " + response.get("cedula"));
            System.out.println("Monto maximo: $" + response.get("montoMaximo"));
            System.out.println("Mensaje: " + response.get("mensaje"));
        } catch (IOException | InterruptedException e) {
            System.out.println("Error al consultar monto maximo: " + e.getMessage());
        }
    }
}
