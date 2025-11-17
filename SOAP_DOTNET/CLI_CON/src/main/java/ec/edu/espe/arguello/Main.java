package ec.edu.espe.arguello;

import ec.edu.espe.arguello.model.*;
import ec.edu.espe.arguello.service.*;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final ClienteService clienteService = new ClienteService();
    private static final CuentaService cuentaService = new CuentaService();
    private static final CreditoService creditoService = new CreditoService();
    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║   SISTEMA BANCARIO - CLIENTE CONSOLA     ║");
        System.out.println("╚═══════════════════════════════════════════╝");
        System.out.println();

        if (login()) {
            mainMenu();
        } else {
            System.out.println("\n❌ No se pudo iniciar sesión. Saliendo...");
        }
    }

    private static boolean login() {
        System.out.println("═══════════════ INICIO DE SESIÓN ═══════════════");
        System.out.print("Usuario: ");
        String username = scanner.nextLine();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        currentUser = userService.login(username, password);
        
        if (currentUser != null) {
            System.out.println("\n✓ Bienvenido " + currentUser.getUserName() + " (" + currentUser.getRol() + ")");
            return true;
        } else {
            System.out.println("\n✗ Credenciales inválidas");
            return false;
        }
    }

    private static void mainMenu() {
        while (true) {
            System.out.println("\n╔═══════════════ MENÚ PRINCIPAL ═══════════════╗");
            System.out.println("║ 1. Gestión de Clientes                      ║");
            System.out.println("║ 2. Gestión de Cuentas                       ║");
            System.out.println("║ 3. Gestión de Créditos                      ║");
            System.out.println("║ 4. Realizar Operación Bancaria              ║");
            System.out.println("║ 0. Salir                                    ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.print("Seleccione una opción: ");

            int opcion = leerEntero();
            
            switch (opcion) {
                case 1 -> menuClientes();
                case 2 -> menuCuentas();
                case 3 -> menuCreditos();
                case 4 -> realizarOperacion();
                case 0 -> {
                    System.out.println("\n👋 ¡Hasta pronto!");
                    return;
                }
                default -> System.out.println("❌ Opción inválida");
            }
        }
    }

    private static void menuClientes() {
        while (true) {
            System.out.println("\n╔════════ GESTIÓN DE CLIENTES ════════╗");
            System.out.println("║ 1. Listar todos los clientes       ║");
            System.out.println("║ 2. Buscar cliente por ID           ║");
            System.out.println("║ 0. Volver                           ║");
            System.out.println("╚═════════════════════════════════════╝");
            System.out.print("Seleccione una opción: ");

            int opcion = leerEntero();
            
            switch (opcion) {
                case 1 -> listarClientes();
                case 2 -> buscarCliente();
                case 0 -> { return; }
                default -> System.out.println("❌ Opción inválida");
            }
        }
    }

    private static void listarClientes() {
        System.out.println("\n═══════════════ LISTADO DE CLIENTES ═══════════════");
        List<ClienteBanco> clientes = clienteService.getAllClientes();
        
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados");
            return;
        }

        System.out.println("┌─────┬──────────────┬──────────────────────────┬──────────────┬─────────────────┐");
        System.out.println("│ ID  │   Cédula     │         Nombre           │ Estado Civil │ Crédito Activo  │");
        System.out.println("├─────┼──────────────┼──────────────────────────┼──────────────┼─────────────────┤");
        
        for (ClienteBanco cliente : clientes) {
            System.out.printf("│ %-3d │ %-12s │ %-24s │ %-12s │ %-15s │%n",
                cliente.getId(),
                cliente.getCedula(),
                truncate(cliente.getNombreCompleto(), 24),
                truncate(cliente.getEstadoCivil(), 12),
                cliente.isTieneCreditoActivo() ? "Sí" : "No");
        }
        System.out.println("└─────┴──────────────┴──────────────────────────┴──────────────┴─────────────────┘");
        System.out.println("Total: " + clientes.size() + " clientes");
    }

    private static void buscarCliente() {
        System.out.print("\nIngrese el ID del cliente: ");
        int id = leerEntero();
        
        ClienteBanco cliente = clienteService.getClienteById(id);
        
        if (cliente != null) {
            System.out.println("\n═══════════════ INFORMACIÓN DEL CLIENTE ═══════════════");
            System.out.println("ID: " + cliente.getId());
            System.out.println("Cédula: " + cliente.getCedula());
            System.out.println("Nombre: " + cliente.getNombreCompleto());
            System.out.println("Estado Civil: " + cliente.getEstadoCivil());
            System.out.println("Fecha Nacimiento: " + cliente.getFechaNacimiento());
            System.out.println("Crédito Activo: " + (cliente.isTieneCreditoActivo() ? "Sí" : "No"));
        } else {
            System.out.println("❌ Cliente no encontrado");
        }
    }

    private static void menuCuentas() {
        while (true) {
            System.out.println("\n╔════════ GESTIÓN DE CUENTAS ════════╗");
            System.out.println("║ 1. Listar todas las cuentas       ║");
            System.out.println("║ 2. Buscar cuentas por cliente     ║");
            System.out.println("║ 0. Volver                          ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Seleccione una opción: ");

            int opcion = leerEntero();
            
            switch (opcion) {
                case 1 -> listarCuentas();
                case 2 -> buscarCuentasPorCliente();
                case 0 -> { return; }
                default -> System.out.println("❌ Opción inválida");
            }
        }
    }

    private static void listarCuentas() {
        System.out.println("\n═══════════════ LISTADO DE CUENTAS ═══════════════");
        List<Cuenta> cuentas = cuentaService.getAllCuentas();
        
        if (cuentas.isEmpty()) {
            System.out.println("No hay cuentas registradas");
            return;
        }

        System.out.println("┌─────┬────────────┬──────────────────┬──────────────┬──────────────┐");
        System.out.println("│ ID  │ Cliente ID │ Número Cuenta    │ Tipo         │    Saldo     │");
        System.out.println("├─────┼────────────┼──────────────────┼──────────────┼──────────────┤");
        
        for (Cuenta cuenta : cuentas) {
            System.out.printf("│ %-3d │ %-10d │ %-16s │ %-12s │ $%,-11.2f │%n",
                cuenta.getId(),
                cuenta.getClienteBancoId(),
                cuenta.getNumeroCuenta(),
                cuenta.getTipoCuenta(),
                cuenta.getSaldo());
        }
        System.out.println("└─────┴────────────┴──────────────────┴──────────────┴──────────────┘");
        System.out.println("Total: " + cuentas.size() + " cuentas");
    }

    private static void buscarCuentasPorCliente() {
        System.out.print("\nIngrese el ID del cliente: ");
        int clienteId = leerEntero();
        
        List<Cuenta> cuentas = cuentaService.getCuentasByClienteId(clienteId);
        
        if (cuentas.isEmpty()) {
            System.out.println("❌ No se encontraron cuentas para este cliente");
            return;
        }

        System.out.println("\n═══════════════ CUENTAS DEL CLIENTE ═══════════════");
        System.out.println("┌─────┬──────────────────┬──────────────┬──────────────┐");
        System.out.println("│ ID  │ Número Cuenta    │ Tipo         │    Saldo     │");
        System.out.println("├─────┼──────────────────┼──────────────┼──────────────┤");
        
        for (Cuenta cuenta : cuentas) {
            System.out.printf("│ %-3d │ %-16s │ %-12s │ $%,-11.2f │%n",
                cuenta.getId(),
                cuenta.getNumeroCuenta(),
                cuenta.getTipoCuenta(),
                cuenta.getSaldo());
        }
        System.out.println("└─────┴──────────────────┴──────────────┴──────────────┘");
    }

    private static void menuCreditos() {
        while (true) {
            System.out.println("\n╔════════ GESTIÓN DE CRÉDITOS ════════╗");
            System.out.println("║ 1. Listar todos los créditos       ║");
            System.out.println("║ 2. Buscar créditos por cliente     ║");
            System.out.println("║ 0. Volver                           ║");
            System.out.println("╚═════════════════════════════════════╝");
            System.out.print("Seleccione una opción: ");

            int opcion = leerEntero();
            
            switch (opcion) {
                case 1 -> listarCreditos();
                case 2 -> buscarCreditosPorCliente();
                case 0 -> { return; }
                default -> System.out.println("❌ Opción inválida");
            }
        }
    }

    private static void listarCreditos() {
        System.out.println("\n═══════════════ LISTADO DE CRÉDITOS ═══════════════");
        List<CreditoBanco> creditos = creditoService.getAllCreditos();
        
        if (creditos.isEmpty()) {
            System.out.println("No hay créditos registrados");
            return;
        }

        System.out.println("┌─────┬────────────┬──────────────────┬──────────────┬────────┬────────┬──────────────┐");
        System.out.println("│ ID  │ Cliente ID │ Número Crédito   │    Monto     │  Tasa  │ Cuotas │    Estado    │");
        System.out.println("├─────┼────────────┼──────────────────┼──────────────┼────────┼────────┼──────────────┤");
        
        for (CreditoBanco credito : creditos) {
            System.out.printf("│ %-3d │ %-10d │ %-16s │ $%,-11.2f │ %5.2f%% │ %-6d │ %-12s │%n",
                credito.getId(),
                credito.getClienteBancoId(),
                credito.getNumeroCreditoBanco(),
                credito.getMonto(),
                credito.getTasaInteres(),
                credito.getPlazoCuotas(),
                truncate(credito.getEstado(), 12));
        }
        System.out.println("└─────┴────────────┴──────────────────┴──────────────┴────────┴────────┴──────────────┘");
        System.out.println("Total: " + creditos.size() + " créditos");
    }

    private static void buscarCreditosPorCliente() {
        System.out.print("\nIngrese el ID del cliente: ");
        int clienteId = leerEntero();
        
        List<CreditoBanco> creditos = creditoService.getCreditosByClienteId(clienteId);
        
        if (creditos.isEmpty()) {
            System.out.println("❌ No se encontraron créditos para este cliente");
            return;
        }

        System.out.println("\n═══════════════ CRÉDITOS DEL CLIENTE ═══════════════");
        System.out.println("┌─────┬──────────────────┬──────────────┬────────┬────────┬──────────────┐");
        System.out.println("│ ID  │ Número Crédito   │    Monto     │  Tasa  │ Cuotas │    Estado    │");
        System.out.println("├─────┼──────────────────┼──────────────┼────────┼────────┼──────────────┤");
        
        for (CreditoBanco credito : creditos) {
            System.out.printf("│ %-3d │ %-16s │ $%,-11.2f │ %5.2f%% │ %-6d │ %-12s │%n",
                credito.getId(),
                credito.getNumeroCreditoBanco(),
                credito.getMonto(),
                credito.getTasaInteres(),
                credito.getPlazoCuotas(),
                truncate(credito.getEstado(), 12));
        }
        System.out.println("└─────┴──────────────────┴──────────────┴────────┴────────┴──────────────┘");
    }

    private static void realizarOperacion() {
        System.out.println("\n═══════════════ OPERACIÓN BANCARIA ═══════════════");
        System.out.print("Ingrese el ID de la cuenta: ");
        int cuentaId = leerEntero();
        
        System.out.println("\nTipo de operación:");
        System.out.println("1. Depósito");
        System.out.println("2. Retiro");
        System.out.print("Seleccione: ");
        int tipo = leerEntero();
        
        if (tipo != 1 && tipo != 2) {
            System.out.println("❌ Tipo de operación inválido");
            return;
        }
        
        System.out.print("Ingrese el monto: $");
        double monto = leerDouble();
        
        if (monto <= 0) {
            System.out.println("❌ El monto debe ser mayor a 0");
            return;
        }
        
        Movimiento movimiento = cuentaService.realizarMovimiento(cuentaId, tipo, monto);
        
        if (movimiento != null) {
            System.out.println("\n✓ Operación realizada exitosamente");
            System.out.println("Tipo: " + movimiento.getTipoMovimiento());
            System.out.println("Monto: $" + String.format("%,.2f", movimiento.getMonto()));
            System.out.println("Fecha: " + movimiento.getFecha());
        } else {
            System.out.println("❌ Error al realizar la operación");
        }
    }

    private static int leerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static double leerDouble() {
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
}
