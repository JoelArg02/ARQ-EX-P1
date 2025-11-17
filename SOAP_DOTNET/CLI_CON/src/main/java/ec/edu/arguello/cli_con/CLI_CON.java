package ec.edu.arguello.cli_con;

import ec.edu.arguello.model.AmortizacionCredito;
import ec.edu.arguello.model.ClienteBanco;
import ec.edu.arguello.model.Cuenta;
import ec.edu.arguello.model.CreditoBanco;
import ec.edu.arguello.model.Movimiento;
import ec.edu.arguello.model.User;
import ec.edu.arguello.service.AmortizacionService;
import ec.edu.arguello.service.ClienteService;
import ec.edu.arguello.service.CreditoService;
import ec.edu.arguello.service.CuentaService;
import ec.edu.arguello.service.MovimientoService;
import ec.edu.arguello.service.UserService;

import java.util.List;
import java.util.Scanner;

public class CLI_CON {
    private static User currentUser;
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final ClienteService clienteService = new ClienteService();
    private static final CuentaService cuentaService = new CuentaService();
    private static final CreditoService creditoService = new CreditoService();
    private static final MovimientoService movimientoService = new MovimientoService();
    private static final AmortizacionService amortizacionService = new AmortizacionService();

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
        while (true) {
            System.out.println("\n=== GESTIÓN DE CUENTAS ===");
            System.out.println("1. Listar todas las cuentas");
            System.out.println("2. Buscar cuenta por ID");
            System.out.println("3. Buscar cuenta por número");
            System.out.println("4. Buscar cuentas por cliente");
            System.out.println("5. Crear nueva cuenta");
            System.out.println("6. Actualizar cuenta");
            System.out.println("7. Eliminar cuenta");
            System.out.println("0. Volver");
            System.out.print("Opción: ");
            
            int opcion = leerOpcion();
            
            switch (opcion) {
                case 1:
                    listarCuentas();
                    break;
                case 2:
                    buscarCuentaPorId();
                    break;
                case 3:
                    buscarCuentaPorNumero();
                    break;
                case 4:
                    buscarCuentasPorCliente();
                    break;
                case 5:
                    crearCuenta();
                    break;
                case 6:
                    actualizarCuenta();
                    break;
                case 7:
                    eliminarCuenta();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opción no válida");
            }
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
        while (true) {
            System.out.println("\n=== GESTIÓN DE CRÉDITOS ===");
            System.out.println("1. Listar todos los créditos");
            System.out.println("2. Buscar crédito por ID");
            System.out.println("3. Buscar créditos por cliente");
            System.out.println("4. Verificar crédito activo de cliente");
            System.out.println("5. Crear nuevo crédito");
            System.out.println("6. Actualizar crédito");
            System.out.println("7. Eliminar crédito");
            System.out.println("8. Ver tabla de amortización");
            System.out.println("0. Volver");
            System.out.print("Opción: ");
            
            int opcion = leerOpcion();
            
            switch (opcion) {
                case 1:
                    listarCreditos();
                    break;
                case 2:
                    buscarCreditoPorId();
                    break;
                case 3:
                    buscarCreditosPorCliente();
                    break;
                case 4:
                    verificarCreditoActivoCliente();
                    break;
                case 5:
                    crearCredito();
                    break;
                case 6:
                    actualizarCredito();
                    break;
                case 7:
                    eliminarCredito();
                    break;
                case 8:
                    verTablaAmortizacion();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Opción no válida");
            }
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
            int tipo = tipoOp;
            Movimiento movimiento = movimientoService.createMovimiento(cuentaId, tipo, monto);
            if (movimiento != null) {
                System.out.println("\nOperación realizada exitosamente");
                System.out.println("ID Movimiento: " + movimiento.getId());
            } else {
                System.out.println("\nError al realizar la operación");
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

    private static void buscarCuentaPorId() {
        System.out.println("\n--- BUSCAR CUENTA POR ID ---");
        System.out.print("Ingrese el ID de la cuenta: ");
        int id = leerOpcion();
        
        Cuenta cuenta = cuentaService.getCuentaById(id);
        if (cuenta != null) {
            System.out.println("\nCuenta encontrada:");
            System.out.println(cuenta);
        } else {
            System.out.println("Cuenta no encontrada");
        }
        
        pausar();
    }

    private static void buscarCuentaPorNumero() {
        System.out.println("\n--- BUSCAR CUENTA POR NÚMERO ---");
        System.out.print("Ingrese el número de cuenta: ");
        String numeroCuenta = scanner.nextLine().trim();
        
        Cuenta cuenta = cuentaService.getCuentaByNumeroCuenta(numeroCuenta);
        if (cuenta != null) {
            System.out.println("\nCuenta encontrada:");
            System.out.println(cuenta);
        } else {
            System.out.println("Cuenta no encontrada");
        }
        
        pausar();
    }

    private static void buscarCuentasPorCliente() {
        System.out.println("\n--- BUSCAR CUENTAS POR CLIENTE ---");
        
        List<ClienteBanco> clientes = clienteService.getAllClientes();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados");
            pausar();
            return;
        }
        
        System.out.println("\nClientes disponibles:");
        for (ClienteBanco cliente : clientes) {
            System.out.printf("ID: %d - %s%n", cliente.getId(), cliente.getNombreCompleto());
        }
        
        System.out.print("\nIngrese el ID del cliente: ");
        int clienteId = leerOpcion();
        
        List<Cuenta> cuentas = cuentaService.getCuentasByClienteBancoId(clienteId);
        if (cuentas.isEmpty()) {
            System.out.println("El cliente no tiene cuentas registradas");
        } else {
            System.out.println("\nCuentas del cliente:");
            System.out.println("-".repeat(80));
            for (Cuenta cuenta : cuentas) {
                System.out.println(cuenta);
            }
            System.out.println("-".repeat(80));
        }
        
        pausar();
    }

    private static void crearCuenta() {
        System.out.println("\n--- CREAR NUEVA CUENTA ---");
        
        List<ClienteBanco> clientes = clienteService.getAllClientes();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados");
            pausar();
            return;
        }
        
        System.out.println("\nClientes disponibles:");
        for (ClienteBanco cliente : clientes) {
            System.out.printf("ID: %d - %s%n", cliente.getId(), cliente.getNombreCompleto());
        }
        
        System.out.print("\nIngrese el ID del cliente: ");
        int clienteId = leerOpcion();
        
        System.out.print("Ingrese el número de cuenta: ");
        String numeroCuenta = scanner.nextLine().trim();
        
        System.out.print("Ingrese el saldo inicial: $");
        double saldo = leerDouble();
        
        System.out.println("\nTipo de cuenta:");
        System.out.println("1. Ahorros");
        System.out.println("2. Corriente");
        System.out.print("Seleccione: ");
        int tipoCuenta = leerOpcion();
        
        Cuenta nuevaCuenta = cuentaService.createCuenta(clienteId, numeroCuenta, saldo, tipoCuenta);
        
        if (nuevaCuenta != null) {
            System.out.println("\nCuenta creada exitosamente:");
            System.out.println(nuevaCuenta);
        } else {
            System.out.println("Error al crear la cuenta");
        }
        
        pausar();
    }

    private static void actualizarCuenta() {
        System.out.println("\n--- ACTUALIZAR CUENTA ---");
        
        List<Cuenta> cuentas = cuentaService.getAllCuentas();
        if (cuentas.isEmpty()) {
            System.out.println("No hay cuentas registradas");
            pausar();
            return;
        }
        
        System.out.println("\nCuentas disponibles:");
        for (Cuenta cuenta : cuentas) {
            System.out.printf("ID: %d - Número: %s - Saldo: $%.2f%n", 
                cuenta.getId(), cuenta.getNumeroCuenta(), cuenta.getSaldo());
        }
        
        System.out.print("\nIngrese el ID de la cuenta a actualizar: ");
        int id = leerOpcion();
        
        Cuenta cuentaExistente = cuentaService.getCuentaById(id);
        if (cuentaExistente == null) {
            System.out.println("Cuenta no encontrada");
            pausar();
            return;
        }
        
        System.out.println("\nDatos actuales:");
        System.out.println(cuentaExistente);
        System.out.println("\n(Presione ENTER para mantener el valor actual)");
        
        System.out.print("\nNuevo número de cuenta [" + cuentaExistente.getNumeroCuenta() + "]: ");
        String inputNumero = scanner.nextLine().trim();
        String numeroCuenta = inputNumero.isEmpty() ? cuentaExistente.getNumeroCuenta() : inputNumero;
        
        System.out.print("Nuevo saldo [" + cuentaExistente.getSaldo() + "]: $");
        String inputSaldo = scanner.nextLine().trim();
        double saldo = inputSaldo.isEmpty() ? cuentaExistente.getSaldo() : Double.parseDouble(inputSaldo);
        
        System.out.println("\nTipo de cuenta:");
        System.out.println("1. Ahorros");
        System.out.println("2. Corriente");
        System.out.print("Seleccione [actual: " + cuentaExistente.getTipoCuenta() + "]: ");
        String inputTipo = scanner.nextLine().trim();
        int tipoCuenta = inputTipo.isEmpty() ? 
            (cuentaExistente.getTipoCuenta().equals("Ahorros") ? 1 : 2) : Integer.parseInt(inputTipo);
        
        Cuenta cuentaActualizada = cuentaService.updateCuenta(id, numeroCuenta, saldo, tipoCuenta);
        
        if (cuentaActualizada != null) {
            System.out.println("\nCuenta actualizada exitosamente:");
            System.out.println(cuentaActualizada);
        } else {
            System.out.println("Error al actualizar la cuenta");
        }
        
        pausar();
    }

    private static void eliminarCuenta() {
        System.out.println("\n--- ELIMINAR CUENTA ---");
        
        List<Cuenta> cuentas = cuentaService.getAllCuentas();
        if (cuentas.isEmpty()) {
            System.out.println("No hay cuentas registradas");
            pausar();
            return;
        }
        
        System.out.println("\nCuentas disponibles:");
        for (Cuenta cuenta : cuentas) {
            System.out.printf("ID: %d - Número: %s%n", cuenta.getId(), cuenta.getNumeroCuenta());
        }
        
        System.out.print("\nIngrese el ID de la cuenta a eliminar: ");
        int id = leerOpcion();
        
        Cuenta cuenta = cuentaService.getCuentaById(id);
        if (cuenta == null) {
            System.out.println("Cuenta no encontrada");
            pausar();
            return;
        }
        
        System.out.println("\nCuenta a eliminar:");
        System.out.println(cuenta);
        
        System.out.print("\n¿Está seguro de eliminar esta cuenta? (S/N): ");
        String confirmacion = scanner.nextLine().trim().toUpperCase();
        
        if (confirmacion.equals("S")) {
            boolean eliminado = cuentaService.deleteCuenta(id);
            if (eliminado) {
                System.out.println("Cuenta eliminada exitosamente");
            } else {
                System.out.println("Error al eliminar la cuenta");
            }
        } else {
            System.out.println("Operación cancelada");
        }
        
        pausar();
    }

    private static void buscarCreditoPorId() {
        System.out.println("\n--- BUSCAR CRÉDITO POR ID ---");
        System.out.print("Ingrese el ID del crédito: ");
        int id = leerOpcion();
        
        CreditoBanco credito = creditoService.getCreditoBancoById(id);
        if (credito != null) {
            System.out.println("\nCrédito encontrado:");
            System.out.println(credito);
        } else {
            System.out.println("Crédito no encontrado");
        }
        
        pausar();
    }

    private static void buscarCreditosPorCliente() {
        System.out.println("\n--- BUSCAR CRÉDITOS POR CLIENTE ---");
        
        List<ClienteBanco> clientes = clienteService.getAllClientes();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados");
            pausar();
            return;
        }
        
        System.out.println("\nClientes disponibles:");
        for (ClienteBanco cliente : clientes) {
            System.out.printf("ID: %d - %s%n", cliente.getId(), cliente.getNombreCompleto());
        }
        
        System.out.print("\nIngrese el ID del cliente: ");
        int clienteId = leerOpcion();
        
        List<CreditoBanco> creditos = creditoService.getCreditosBancoByClienteBancoId(clienteId);
        if (creditos.isEmpty()) {
            System.out.println("El cliente no tiene créditos registrados");
        } else {
            System.out.println("\nCréditos del cliente:");
            System.out.println("-".repeat(80));
            for (CreditoBanco credito : creditos) {
                System.out.println(credito);
            }
            System.out.println("-".repeat(80));
        }
        
        pausar();
    }

    private static void verificarCreditoActivoCliente() {
        System.out.println("\n--- VERIFICAR CRÉDITO ACTIVO ---");
        
        List<ClienteBanco> clientes = clienteService.getAllClientes();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados");
            pausar();
            return;
        }
        
        System.out.println("\nClientes disponibles:");
        for (ClienteBanco cliente : clientes) {
            System.out.printf("ID: %d - %s%n", cliente.getId(), cliente.getNombreCompleto());
        }
        
        System.out.print("\nIngrese el ID del cliente: ");
        int clienteId = leerOpcion();
        
        CreditoBanco creditoActivo = creditoService.getCreditoBancoActivoByClienteBancoId(clienteId);
        if (creditoActivo != null) {
            System.out.println("\nEl cliente tiene un crédito activo:");
            System.out.println(creditoActivo);
        } else {
            System.out.println("\nEl cliente no tiene créditos activos");
        }
        
        pausar();
    }

    private static void crearCredito() {
        System.out.println("\n--- CREAR NUEVO CRÉDITO ---");
        
        List<ClienteBanco> clientes = clienteService.getAllClientes();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados");
            pausar();
            return;
        }
        
        System.out.println("\nClientes disponibles:");
        for (ClienteBanco cliente : clientes) {
            System.out.printf("ID: %d - %s%n", cliente.getId(), cliente.getNombreCompleto());
        }
        
        System.out.print("\nIngrese el ID del cliente: ");
        int clienteId = leerOpcion();
        
        CreditoBanco creditoActivo = creditoService.getCreditoBancoActivoByClienteBancoId(clienteId);
        if (creditoActivo != null) {
            System.out.println("\nEl cliente ya tiene un crédito activo:");
            System.out.println(creditoActivo);
            System.out.println("\nNo se puede crear un nuevo crédito");
            pausar();
            return;
        }
        
        System.out.print("\nIngrese el monto del crédito: $");
        double monto = leerDouble();
        
        System.out.print("Ingrese el número de cuotas: ");
        int numeroCuotas = leerOpcion();
        
        System.out.print("Ingrese la tasa de interés (decimal, ej: 0.12): ");
        double tasaInteres = leerDouble();
        
        CreditoBanco nuevoCredito = creditoService.createCreditoBanco(clienteId, monto, numeroCuotas, tasaInteres);
        
        if (nuevoCredito != null) {
            System.out.println("\nCrédito creado exitosamente:");
            System.out.println(nuevoCredito);
        } else {
            System.out.println("Error al crear el crédito");
        }
        
        pausar();
    }

    private static void actualizarCredito() {
        System.out.println("\n--- ACTUALIZAR CRÉDITO ---");
        
        List<CreditoBanco> creditos = creditoService.getAllCreditos();
        if (creditos.isEmpty()) {
            System.out.println("No hay créditos registrados");
            pausar();
            return;
        }
        
        System.out.println("\nCréditos disponibles:");
        for (CreditoBanco credito : creditos) {
            System.out.printf("ID: %d - Monto: $%.2f - Estado: %s%n", 
                credito.getId(), credito.getMonto(), credito.getEstado());
        }
        
        System.out.print("\nIngrese el ID del crédito a actualizar: ");
        int id = leerOpcion();
        
        CreditoBanco creditoExistente = creditoService.getCreditoBancoById(id);
        if (creditoExistente == null) {
            System.out.println("Crédito no encontrado");
            pausar();
            return;
        }
        
        System.out.println("\nDatos actuales:");
        System.out.println(creditoExistente);
        System.out.println("\n(Presione ENTER para mantener el valor actual)");
        
        System.out.print("\nNuevo monto [" + creditoExistente.getMonto() + "]: $");
        String inputMonto = scanner.nextLine().trim();
        double monto = inputMonto.isEmpty() ? creditoExistente.getMonto() : Double.parseDouble(inputMonto);
        
        System.out.print("Nuevo número de cuotas [" + creditoExistente.getPlazoMeses() + "]: ");
        String inputCuotas = scanner.nextLine().trim();
        int numeroCuotas = inputCuotas.isEmpty() ? creditoExistente.getPlazoMeses() : Integer.parseInt(inputCuotas);
        
        System.out.print("Nueva tasa de interés [" + creditoExistente.getTasaInteres() + "]: ");
        String inputTasa = scanner.nextLine().trim();
        double tasaInteres = inputTasa.isEmpty() ? creditoExistente.getTasaInteres() : Double.parseDouble(inputTasa);
        
        System.out.print("¿Está activo? (S/N) [" + creditoExistente.getEstado() + "]: ");
        String inputActivo = scanner.nextLine().trim().toUpperCase();
        boolean activo = inputActivo.isEmpty() ? 
            creditoExistente.getEstado().equals("Activo") : inputActivo.equals("S");
        
        CreditoBanco creditoActualizado = creditoService.updateCreditoBanco(id, monto, numeroCuotas, tasaInteres, activo);
        
        if (creditoActualizado != null) {
            System.out.println("\nCrédito actualizado exitosamente:");
            System.out.println(creditoActualizado);
        } else {
            System.out.println("Error al actualizar el crédito");
        }
        
        pausar();
    }

    private static void eliminarCredito() {
        System.out.println("\n--- ELIMINAR CRÉDITO ---");
        
        List<CreditoBanco> creditos = creditoService.getAllCreditos();
        if (creditos.isEmpty()) {
            System.out.println("No hay créditos registrados");
            pausar();
            return;
        }
        
        System.out.println("\nCréditos disponibles:");
        for (CreditoBanco credito : creditos) {
            System.out.printf("ID: %d - Monto: $%.2f%n", credito.getId(), credito.getMonto());
        }
        
        System.out.print("\nIngrese el ID del crédito a eliminar: ");
        int id = leerOpcion();
        
        CreditoBanco credito = creditoService.getCreditoBancoById(id);
        if (credito == null) {
            System.out.println("Crédito no encontrado");
            pausar();
            return;
        }
        
        System.out.println("\nCrédito a eliminar:");
        System.out.println(credito);
        
        System.out.print("\n¿Está seguro de eliminar este crédito? (S/N): ");
        String confirmacion = scanner.nextLine().trim().toUpperCase();
        
        if (confirmacion.equals("S")) {
            boolean eliminado = creditoService.deleteCreditoBanco(id);
            if (eliminado) {
                System.out.println("Crédito eliminado exitosamente");
            } else {
                System.out.println("Error al eliminar el crédito");
            }
        } else {
            System.out.println("Operación cancelada");
        }
        
        pausar();
    }

    private static void verTablaAmortizacion() {
        System.out.println("\n--- TABLA DE AMORTIZACIÓN ---");
        
        List<CreditoBanco> creditos = creditoService.getAllCreditos();
        if (creditos.isEmpty()) {
            System.out.println("No hay créditos registrados");
            pausar();
            return;
        }
        
        System.out.println("\nCréditos disponibles:");
        for (CreditoBanco credito : creditos) {
            System.out.printf("ID: %d - Monto: $%.2f%n", credito.getId(), credito.getMonto());
        }
        
        System.out.print("\nIngrese el ID del crédito: ");
        int creditoId = leerOpcion();
        
        List<AmortizacionCredito> amortizaciones = amortizacionService.getAmortizacionesByCreditoBancoId(creditoId);
        if (amortizaciones.isEmpty()) {
            System.out.println("\nNo hay amortizaciones registradas para este crédito");
        } else {
            System.out.println("\nTabla de Amortización:");
            System.out.println("-".repeat(80));
            for (AmortizacionCredito amort : amortizaciones) {
                System.out.println(amort);
            }
            System.out.println("-".repeat(80));
        }
        
        pausar();
    }

    private static void cerrarRecursos() {
        userService.close();
        clienteService.close();
        cuentaService.close();
        creditoService.close();
        movimientoService.close();
        amortizacionService.close();
        scanner.close();
    }
}
