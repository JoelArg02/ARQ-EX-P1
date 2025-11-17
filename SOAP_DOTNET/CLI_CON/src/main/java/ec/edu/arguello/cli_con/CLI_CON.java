package ec.edu.arguello.cli_con;

import ec.edu.arguello.model.ClienteBanco;
import ec.edu.arguello.model.Cuenta;
import ec.edu.arguello.model.CreditoBanco;
import ec.edu.arguello.model.User;
import ec.edu.arguello.service.ClienteService;
import ec.edu.arguello.service.CreditoService;
import ec.edu.arguello.service.CuentaService;
import ec.edu.arguello.service.UserService;

import java.util.List;
import java.util.Scanner;

/**
 * Cliente de Consola para el Sistema Bancario
 * @author joelarguello
 */
public class CLI_CON {
    private static User currentUser;
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final ClienteService clienteService = new ClienteService();
    private static final CuentaService cuentaService = new CuentaService();
    private static final CreditoService creditoService = new CreditoService();

    public static void main(String[] args) {
        mostrarBienvenida();
        
        if (login()) {
            boolean continuar = true;
            while (continuar) {
                mostrarMenuPrincipal();
                int opcion = leerOpcion();
                
                switch (opcion) {
                    case 1 -> gestionarClientes();
                    case 2 -> gestionarCuentas();
                    case 3 -> gestionarCreditos();
                    case 4 -> realizarOperacionBancaria();
                    case 0 -> {
                        continuar = false;
                        System.out.println("\n¡Hasta pronto!");
                    }
                    default -> System.out.println("Opción no válida");
                }
            }
        }
        
        cerrarRecursos();
    }

    private static void mostrarBienvenida() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   SISTEMA BANCARIO - CLIENTE CONSOLA  ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    private static boolean login() {
        System.out.println("\n=== INICIO DE SESIÓN ===");
        System.out.print("Usuario: ");
        String username = scanner.nextLine().trim();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine().trim();

        currentUser = userService.validateCredentials(username, password);
        
        if (currentUser != null) {
            System.out.println("\n✓ Bienvenido, " + currentUser.getUserName() + "!");
            System.out.println("Rol: " + currentUser.getRol());
            return true;
        } else {
            System.out.println("\n✗ Credenciales incorrectas");
            return false;
        }
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          MENÚ PRINCIPAL                ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ 1. Gestionar Clientes                 ║");
        System.out.println("║ 2. Gestionar Cuentas                  ║");
        System.out.println("║ 3. Gestionar Créditos                 ║");
        System.out.println("║ 4. Operaciones Bancarias              ║");
        System.out.println("║ 0. Salir                              ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.print("Seleccione una opción: ");
    }

    private static void gestionarClientes() {
        while (true) {
            System.out.println("\n=== GESTIÓN DE CLIENTES ===");
            System.out.println("1. Listar todos los clientes");
            System.out.println("2. Crear nuevo cliente");
            System.out.println("3. Editar cliente");
            System.out.println("4. Eliminar cliente");
            System.out.println("0. Volver");
            System.out.print("Opción: ");
            
            int opcion = leerOpcion();
            
            switch (opcion) {
                case 1:
                    listarClientes();
                    break;
                case 2:
                    crearCliente();
                    break;
                case 3:
                    editarCliente();
                    break;
                case 4:
                    eliminarCliente();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private static void crearCliente() {
        System.out.println("\n--- CREAR NUEVO CLIENTE ---");
        
        System.out.print("Ingrese la cédula: ");
        String cedula = scanner.nextLine();
        
        System.out.print("Ingrese el nombre completo: ");
        String nombreCompleto = scanner.nextLine();
        
        System.out.print("Ingrese el estado civil (Soltero/Casado/Divorciado/Viudo): ");
        String estadoCivil = scanner.nextLine();
        
        System.out.print("Ingrese la fecha de nacimiento (yyyy-MM-dd): ");
        String fechaNacimiento = scanner.nextLine();
        
        ClienteBanco nuevoCliente = clienteService.createCliente(cedula, nombreCompleto, estadoCivil, fechaNacimiento);
        
        if (nuevoCliente != null) {
            System.out.println("\n✓ Cliente creado exitosamente:");
            System.out.println(nuevoCliente);
        } else {
            System.out.println("\n✗ Error al crear el cliente");
        }
        
        pausar();
    }

    private static void editarCliente() {
        System.out.println("\n--- EDITAR CLIENTE ---");
        
        // Primero listar clientes
        List<ClienteBanco> clientes = clienteService.getAllClientes();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados");
            pausar();
            return;
        }
        
        System.out.println("\nClientes disponibles:");
        for (ClienteBanco cliente : clientes) {
            System.out.printf("ID: %d - %s (%s)%n", cliente.getId(), cliente.getNombreCompleto(), cliente.getCedula());
        }
        
        System.out.print("\nIngrese el ID del cliente a editar: ");
        int id = leerOpcion();
        
        ClienteBanco clienteExistente = clienteService.getClienteById(id);
        if (clienteExistente == null) {
            System.out.println("Cliente no encontrado");
            pausar();
            return;
        }
        
        System.out.println("\nDatos actuales del cliente:");
        System.out.println(clienteExistente);
        System.out.println("\n(Presione ENTER para mantener el valor actual)");
        
        System.out.print("\nIngrese la nueva cédula [" + clienteExistente.getCedula() + "]: ");
        String inputCedula = scanner.nextLine().trim();
        String cedula = inputCedula.isEmpty() ? clienteExistente.getCedula() : inputCedula;
        
        System.out.print("Ingrese el nuevo nombre completo [" + clienteExistente.getNombreCompleto() + "]: ");
        String inputNombre = scanner.nextLine().trim();
        String nombreCompleto = inputNombre.isEmpty() ? clienteExistente.getNombreCompleto() : inputNombre;
        
        System.out.print("Ingrese el nuevo estado civil [" + clienteExistente.getEstadoCivil() + "]: ");
        String inputEstado = scanner.nextLine().trim();
        String estadoCivil = inputEstado.isEmpty() ? clienteExistente.getEstadoCivil() : inputEstado;
        
        System.out.print("Ingrese la nueva fecha de nacimiento (yyyy-MM-dd) [" + clienteExistente.getFechaNacimiento() + "]: ");
        String inputFecha = scanner.nextLine().trim();
        String fechaNacimiento = inputFecha.isEmpty() ? clienteExistente.getFechaNacimiento() : inputFecha;
        
        System.out.println("\nActualizando cliente...");
        ClienteBanco clienteActualizado = clienteService.updateCliente(id, cedula, nombreCompleto, estadoCivil, fechaNacimiento);
        
        if (clienteActualizado != null) {
            System.out.println("\n✓ Cliente actualizado exitosamente:");
            System.out.println(clienteActualizado);
        } else {
            System.out.println("\n✗ Error al actualizar el cliente");
        }
        
        pausar();
    }

    private static void eliminarCliente() {
        System.out.println("\n--- ELIMINAR CLIENTE ---");
        
        // Primero listar clientes
        List<ClienteBanco> clientes = clienteService.getAllClientes();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados");
            pausar();
            return;
        }
        
        System.out.println("\nClientes disponibles:");
        for (ClienteBanco cliente : clientes) {
            System.out.printf("ID: %d - %s (%s)%n", cliente.getId(), cliente.getNombreCompleto(), cliente.getCedula());
        }
        
        System.out.print("\nIngrese el ID del cliente a eliminar: ");
        int id = leerOpcion();
        
        ClienteBanco cliente = clienteService.getClienteById(id);
        if (cliente == null) {
            System.out.println("Cliente no encontrado");
            pausar();
            return;
        }
        
        System.out.println("\nCliente a eliminar:");
        System.out.println(cliente);
        
        System.out.print("\n¿Está seguro de eliminar este cliente? (S/N): ");
        String confirmacion = scanner.nextLine();
        
        if (confirmacion.equalsIgnoreCase("S")) {
            boolean eliminado = clienteService.deleteCliente(id);
            if (eliminado) {
                System.out.println("\n✓ Cliente eliminado exitosamente");
            } else {
                System.out.println("\n✗ Error al eliminar el cliente");
            }
        } else {
            System.out.println("\nOperación cancelada");
        }
        
        pausar();
    }

    private static void listarClientes() {
        System.out.println("\n--- LISTA DE CLIENTES ---");
        List<ClienteBanco> clientes = clienteService.getAllClientes();
        
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados");
        } else {
            System.out.println("Total de clientes: " + clientes.size());
            System.out.println("-".repeat(80));
            for (ClienteBanco cliente : clientes) {
                System.out.println(cliente);
            }
            System.out.println("-".repeat(80));
        }
        
        pausar();
    }

    private static void gestionarCuentas() {
        System.out.println("\n=== GESTIÓN DE CUENTAS ===");
        System.out.println("1. Listar todas las cuentas");
        System.out.println("0. Volver");
        System.out.print("Opción: ");
        
        int opcion = leerOpcion();
        if (opcion == 1) {
            listarCuentas();
        }
    }

    private static void listarCuentas() {
        System.out.println("\n--- LISTA DE CUENTAS ---");
        List<Cuenta> cuentas = cuentaService.getAllCuentas();
        
        if (cuentas.isEmpty()) {
            System.out.println("No hay cuentas registradas");
        } else {
            System.out.println("Total de cuentas: " + cuentas.size());
            System.out.println("-".repeat(80));
            for (Cuenta cuenta : cuentas) {
                System.out.println(cuenta);
            }
            System.out.println("-".repeat(80));
        }
        
        pausar();
    }

    private static void gestionarCreditos() {
        System.out.println("\n=== GESTIÓN DE CRÉDITOS ===");
        System.out.println("1. Listar todos los créditos");
        System.out.println("0. Volver");
        System.out.print("Opción: ");
        
        int opcion = leerOpcion();
        if (opcion == 1) {
            listarCreditos();
        }
    }

    private static void listarCreditos() {
        System.out.println("\n--- LISTA DE CRÉDITOS ---");
        List<CreditoBanco> creditos = creditoService.getAllCreditos();
        
        if (creditos.isEmpty()) {
            System.out.println("No hay créditos registrados");
        } else {
            System.out.println("Total de créditos: " + creditos.size());
            System.out.println("-".repeat(80));
            for (CreditoBanco credito : creditos) {
                System.out.println(credito);
            }
            System.out.println("-".repeat(80));
        }
        
        pausar();
    }

    private static void realizarOperacionBancaria() {
        System.out.println("\n=== OPERACIONES BANCARIAS ===");
        
        List<Cuenta> cuentas = cuentaService.getAllCuentas();
        if (cuentas.isEmpty()) {
            System.out.println("No hay cuentas disponibles");
            pausar();
            return;
        }
        
        System.out.println("\nCuentas disponibles:");
        for (Cuenta cuenta : cuentas) {
            System.out.println(cuenta);
        }
        
        System.out.print("\nIngrese el ID de la cuenta: ");
        int cuentaId = leerOpcion();
        
        Cuenta cuentaSeleccionada = cuentas.stream()
            .filter(c -> c.getId() == cuentaId)
            .findFirst()
            .orElse(null);
            
        if (cuentaSeleccionada == null) {
            System.out.println("Cuenta no encontrada");
            pausar();
            return;
        }
        
        System.out.println("\nCuenta seleccionada: " + cuentaSeleccionada);
        System.out.println("\n1. Depósito");
        System.out.println("2. Retiro");
        System.out.println("0. Cancelar");
        System.out.print("Seleccione el tipo de operación: ");
        
        int tipoOp = leerOpcion();
        if (tipoOp == 0) return;
        
        String tipoMovimiento = tipoOp == 1 ? "Deposito" : "Retiro";
        
        System.out.print("Ingrese el monto: $");
        double monto = leerDouble();
        
        if (monto <= 0) {
            System.out.println("El monto debe ser mayor a cero");
            pausar();
            return;
        }
        
        System.out.print("¿Confirmar operación? (S/N): ");
        String confirmacion = scanner.nextLine().trim().toUpperCase();
        
        if (confirmacion.equals("S")) {
            boolean exito = cuentaService.createMovimiento(cuentaId, tipoMovimiento, monto);
            if (exito) {
                System.out.println("\n✓ Operación realizada exitosamente");
            } else {
                System.out.println("\n✗ Error al realizar la operación");
            }
        } else {
            System.out.println("Operación cancelada");
        }
        
        pausar();
    }

    private static int leerOpcion() {
        try {
            String linea = scanner.nextLine().trim();
            return linea.isEmpty() ? -1 : Integer.parseInt(linea);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static double leerDouble() {
        try {
            String linea = scanner.nextLine().trim();
            return linea.isEmpty() ? 0 : Double.parseDouble(linea);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static void pausar() {
        System.out.print("\nPresione ENTER para continuar...");
        scanner.nextLine();
    }

    private static void cerrarRecursos() {
        userService.close();
        clienteService.close();
        cuentaService.close();
        creditoService.close();
        scanner.close();
    }
}
