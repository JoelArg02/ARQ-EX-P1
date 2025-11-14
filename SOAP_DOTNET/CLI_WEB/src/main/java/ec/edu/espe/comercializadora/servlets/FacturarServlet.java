package ec.edu.espe.comercializadora.servlets;

import ec.edu.espe.comercializadora.models.Cliente;
import ec.edu.espe.comercializadora.models.Electrodomestico;
import ec.edu.espe.comercializadora.models.ElegibilidadCredito;
import ec.edu.espe.comercializadora.models.ProductoCarrito;
import ec.edu.espe.comercializadora.services.ClienteService;
import ec.edu.espe.comercializadora.services.ElectrodomesticoService;
import ec.edu.espe.comercializadora.services.FacturaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/facturar")
public class FacturarServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        
        ClienteService clienteService = new ClienteService();
        ElectrodomesticoService electrodomesticoService = new ElectrodomesticoService();
        
        try {
            List<Cliente> clientes = clienteService.getAllClientes();
            List<Electrodomestico> productos = electrodomesticoService.getElectrodomesticosActivos();
            
            request.setAttribute("clientes", clientes);
            request.setAttribute("productos", productos);
            
            // Recuperar el cliente seleccionado de la sesión si existe
            HttpSession session = request.getSession();
            Cliente clienteSeleccionado = (Cliente) session.getAttribute("clienteSeleccionado");
            if (clienteSeleccionado != null) {
                request.setAttribute("clienteSeleccionado", clienteSeleccionado);
                request.setAttribute("cedulaSeleccionada", clienteSeleccionado.getCedula());
                
                // También recuperar la elegibilidad si existe
                ElegibilidadCredito elegibilidad = (ElegibilidadCredito) session.getAttribute("elegibilidad");
                if (elegibilidad != null) {
                    request.setAttribute("elegibilidad", elegibilidad);
                }
            }
            request.getRequestDispatcher("/WEB-INF/jsp/facturar.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar datos: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/facturar.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        
        String action = request.getParameter("action");
        
        if ("verificarCredito".equals(action)) {
            verificarCredito(request, response);
        } else if ("generarFactura".equals(action)) {
            generarFactura(request, response);
        } else if ("agregarProducto".equals(action)) {
            agregarProducto(request, response);
        } else if ("cambiarCliente".equals(action)) {
            cambiarCliente(request, response);
        }
    }

    private void verificarCredito(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cedula = request.getParameter("cedula");
        String montoStr = request.getParameter("monto");
        FacturaService facturaService = new FacturaService();
        ClienteService clienteService = new ClienteService();
        
        try {
            double monto = 0.0;
            if (montoStr != null && !montoStr.isEmpty()) {
                monto = Double.parseDouble(montoStr);
            }
            
            ElegibilidadCredito elegibilidad = facturaService.verificarElegibilidadCredito(cedula, monto);
            
            // Validar que elegibilidad no sea null
            if (elegibilidad == null) {
                elegibilidad = new ElegibilidadCredito();
                elegibilidad.setEsElegible(false);
                elegibilidad.setMensaje("Error al verificar elegibilidad - servicio no disponible");
                elegibilidad.setMontoMaximoCredito(0.0);
            }
            
            // Debug: imprimir el resultado
            System.out.println("=== DEBUG VERIFICACION CREDITO ===");
            System.out.println("Cedula: " + cedula);
            System.out.println("Es elegible: " + elegibilidad.isEsElegible());
            System.out.println("Mensaje: " + elegibilidad.getMensaje());
            System.out.println("Monto maximo: " + elegibilidad.getMontoMaximoCredito());
            System.out.println("====================================");
            
            request.setAttribute("elegibilidad", elegibilidad);
            request.setAttribute("cedulaSeleccionada", cedula);
            
            // Buscar y pasar el cliente seleccionado
            List<Cliente> clientes = clienteService.getAllClientes();
            Cliente clienteSeleccionado = clientes.stream()
                    .filter(c -> c.getCedula().equals(cedula))
                    .findFirst()
                    .orElse(null);
            
            if (clienteSeleccionado != null) {
                request.setAttribute("clienteSeleccionado", clienteSeleccionado);
                // Almacenar en la sesión para mantenerlo durante toda la facturación
                HttpSession session = request.getSession();
                session.setAttribute("clienteSeleccionado", clienteSeleccionado);
                session.setAttribute("elegibilidad", elegibilidad);
            }
            
            doGet(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error al verificar elegibilidad: " + e.getMessage());
            doGet(request, response);
        }
    }

    @SuppressWarnings("unchecked")
    private void generarFactura(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        List<ProductoCarrito> carrito = (List<ProductoCarrito>) session.getAttribute("carrito");
        
        if (carrito == null || carrito.isEmpty()) {
            request.setAttribute("error", "El carrito está vacío");
            doGet(request, response);
            return;
        }
        
        String clienteIdStr = request.getParameter("clienteId");
        String formaPagoStr = request.getParameter("formaPago");
        
        // Si no se proporciona clienteId en el parámetro, intentar obtenerlo de la sesión
        if (clienteIdStr == null || clienteIdStr.trim().isEmpty()) {
            Cliente clienteSeleccionado = (Cliente) session.getAttribute("clienteSeleccionado");
            if (clienteSeleccionado != null) {
                clienteIdStr = String.valueOf(clienteSeleccionado.getId());
            }
        }
        
        if (clienteIdStr == null || clienteIdStr.trim().isEmpty()) {
            request.setAttribute("error", "Debe seleccionar un cliente");
            doGet(request, response);
            return;
        }
        
        if (formaPagoStr == null || formaPagoStr.trim().isEmpty()) {
            request.setAttribute("error", "Debe seleccionar una forma de pago");
            doGet(request, response);
            return;
        }
        
        int clienteId = Integer.parseInt(clienteIdStr.trim());
        int formaPago = Integer.parseInt(formaPagoStr.trim());
        int numeroCuotas = formaPago == 1 ? 6 : 0;
        
        List<Integer> electrodomesticoIds = new ArrayList<>();
        List<Integer> cantidades = new ArrayList<>();
        
        for (ProductoCarrito item : carrito) {
            electrodomesticoIds.add(item.getProducto().getId());
            cantidades.add(item.getCantidad());
        }
        
        FacturaService facturaService = new FacturaService();
        
        try {
            String mensaje = facturaService.crearFacturaConValidacion(clienteId, electrodomesticoIds, cantidades, formaPago, numeroCuotas);
            
            // Si llegamos aquí, la factura se creó exitosamente
            session.removeAttribute("carrito");
            request.setAttribute("success", mensaje);
            
            doGet(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error al generar factura: " + e.getMessage());
            doGet(request, response);
        }
    }

    @SuppressWarnings("unchecked")
    private void agregarProducto(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        List<ProductoCarrito> carrito = (List<ProductoCarrito>) session.getAttribute("carrito");
        
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }
        
        String productoIdStr = request.getParameter("productoId");
        String cantidadStr = request.getParameter("cantidad");
        
        if (productoIdStr == null || productoIdStr.trim().isEmpty()) {
            request.setAttribute("error", "Debe seleccionar un producto");
            doGet(request, response);
            return;
        }
        
        if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
            request.setAttribute("error", "Debe especificar la cantidad");
            doGet(request, response);
            return;
        }
        
        int productoId = Integer.parseInt(productoIdStr.trim());
        int cantidad = Integer.parseInt(cantidadStr.trim());
        
        if (cantidad <= 0) {
            request.setAttribute("error", "La cantidad debe ser mayor a cero");
            doGet(request, response);
            return;
        }
        
        ElectrodomesticoService electrodomesticoService = new ElectrodomesticoService();
        
        try {
            List<Electrodomestico> productos = electrodomesticoService.getAllElectrodomesticos();
            Electrodomestico producto = productos.stream()
                    .filter(p -> p.getId() == productoId)
                    .findFirst()
                    .orElse(null);
            
            if (producto != null) {
                boolean found = false;
                for (ProductoCarrito item : carrito) {
                    if (item.getProducto().getId() == productoId) {
                        item.setCantidad(item.getCantidad() + cantidad);
                        found = true;
                        break;
                    }
                }
                
                if (!found) {
                    carrito.add(new ProductoCarrito(producto, cantidad));
                }
            }
            
            doGet(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error al agregar producto: " + e.getMessage());
            doGet(request, response);
        }
    }
    
    private void cambiarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        // Limpiar el cliente seleccionado y la elegibilidad de la sesión
        session.removeAttribute("clienteSeleccionado");
        session.removeAttribute("elegibilidad");
        
        // Redirigir al doGet para mostrar la selección de cliente
        doGet(request, response);
    }
}