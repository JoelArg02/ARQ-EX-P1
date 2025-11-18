package ec.edu.pinza.clicon.views;

import ec.edu.pinza.clicon.models.FacturaResponseDTO;
import ec.edu.pinza.clicon.models.ItemCarrito;
import ec.edu.pinza.clicon.models.ProductoDTO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Utilitario para renderizar vista en consola.
 */
public class ConsoleView {

    // --- METHODS ADDED TO FIX THE ERROR ---
    
    /**
     * Muestra un mensaje estándar en la consola.
     * @param mensaje El texto a mostrar.
     */
    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }
    
    /**
     * Muestra un mensaje de error, usualmente en un color diferente (rojo) en la consola.
     * @param mensajeError El texto del error a mostrar.
     */
    public void mostrarError(String mensajeError) {
        // System.err suele imprimir en rojo en muchos IDEs y terminales.
        System.err.println("ERROR: " + mensajeError);
    }

    /**
     * Muestra un mensaje de éxito.
     * @param mensajeExito El texto de éxito a mostrar.
     */
    public void mostrarExito(String mensajeExito) {
        System.out.println(mensajeExito);
    }
    
    // --- EXISTING STATIC METHODS ---

    public static void mostrarProductos(List<ProductoDTO> productos) {
        System.out.println("\n=== Productos disponibles ===");
        System.out.printf("%-5s %-12s %-40s %-10s %-8s%n", "ID", "Codigo", "Nombre", "Precio", "Stock");
        for (ProductoDTO p : productos) {
            System.out.printf("%-5d %-12s %-40s %-10s %-8d%n",
                    p.getIdProducto(), p.getCodigo(), p.getNombre(),
                    p.getPrecio(), p.getStock());
        }
    }

    public static void mostrarCarrito(List<ItemCarrito> carrito, BigDecimal total) {
        System.out.println("\n=== Carrito ===");
        if (carrito.isEmpty()) {
            System.out.println("No tienes productos en el carrito.");
            return;
        }
        System.out.printf("%-5s %-40s %-10s %-8s %-10s%n", "ID", "Producto", "Precio", "Cant", "Subtotal");
        for (ItemCarrito item : carrito) {
            System.out.printf("%-5d %-40s %-10s %-8d %-10s%n",
                    item.getIdProducto(), item.getNombre(), item.getPrecio(),
                    item.getCantidad(), item.getSubtotal());
        }
        System.out.println("Total: " + total);
    }

    public static void mostrarFacturasResumen(List<FacturaResponseDTO> facturas, String titulo) {
        System.out.println("\n=== " + titulo + " ===");
        if (facturas == null || facturas.isEmpty()) {
            System.out.println("No hay facturas para mostrar.");
            return;
        }
        System.out.printf("%-6s %-12s %-20s %-12s %-15s%n", "ID", "Cedula", "Cliente", "Total", "Forma Pago");
        for (FacturaResponseDTO f : facturas) {
            System.out.printf("%-6d %-12s %-20s %-12s %-15s%n",
                    f.getIdFactura(), safe(f.getCedulaCliente()), safe(f.getNombreCliente()),
                    f.getTotal(), safe(f.getFormaPago()));
        }
    }

    public static void mostrarFacturaDetalle(FacturaResponseDTO factura) {
        if (factura == null) {
            System.out.println("Factura no encontrada.");
            return;
        }
        System.out.println("\n=== Detalle de factura #" + factura.getIdFactura() + " ===");
        System.out.println("Cliente: " + factura.getNombreCliente() + " (cedula " + factura.getCedulaCliente() + ")");
        System.out.println("Fecha  : " + factura.getFecha());
        System.out.println("Pago   : " + factura.getFormaPago());
        if (factura.getIdCreditoBanco() != null) {
            System.out.println("Id credito banco: " + factura.getIdCreditoBanco());
        }

        BigDecimal subtotalProductos = calcularSubtotal(factura);
        BigDecimal descuento = calcularDescuentoEfectivo(factura, subtotalProductos);
        System.out.println("Resumen:");
        System.out.println("  Subtotal productos: " + subtotalProductos);
        if (descuento.compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("  Descuento 33% (efectivo): -" + descuento);
        }
        System.out.println("  Total (API, incluye impuestos/financiamiento): " + factura.getTotal());

        System.out.printf("%-40s %-6s %-10s %-10s%n", "Producto", "Cant", "Precio", "Subtotal");
        if (factura.getDetalles() != null) {
            factura.getDetalles().forEach(d -> System.out.printf("%-40s %-6d %-10s %-10s%n",
                    d.getNombreProducto(), d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal()));
        }
        System.out.println("Total: " + factura.getTotal());

        if (factura.getInfoCredito() != null) {
            FacturaResponseDTO.InfoCreditoDTO credito = factura.getInfoCredito();
            System.out.println("\n--- Credito Directo ---");
            System.out.println("Credito #" + credito.getIdCredito() + " | Monto: " + credito.getMontoCredito()
                    + " | Cuotas: " + credito.getNumeroCuotas() + " | Valor cuota: " + credito.getValorCuota()
                    + " | Tasa: " + credito.getTasaInteres());
            if (credito.getTablaAmortizacion() != null && !credito.getTablaAmortizacion().isEmpty()) {
                System.out.printf("%-8s %-12s %-12s %-12s %-12s%n",
                        "Cuota", "Valor", "Interes", "Capital", "Saldo");
                credito.getTablaAmortizacion().forEach(c -> System.out.printf("%-8d %-12s %-12s %-12s %-12s%n",
                        c.getNumeroCuota(), c.getValorCuota(), c.getInteresPagado(),
                        c.getCapitalPagado(), c.getSaldoRestante()));
            }
        }
    }

    public static void mostrarTablaAmortizacion(List<FacturaResponseDTO.CuotaAmortizacionDTO> amortizacion) {
        if (amortizacion == null || amortizacion.isEmpty()) {
            System.out.println("No hay tabla de amortizacion disponible.");
            return;
        }
        System.out.println("\n--- Tabla de amortizacion ---");
        System.out.printf("%-8s %-12s %-12s %-12s %-12s%n",
                "Cuota", "Valor", "Interes", "Capital", "Saldo");
        amortizacion.forEach(c -> System.out.printf("%-8d %-12s %-12s %-12s %-12s%n",
                c.getNumeroCuota(), c.getValorCuota(), c.getInteresPagado(),
                c.getCapitalPagado(), c.getSaldoRestante()));
    }

    private static BigDecimal calcularSubtotal(FacturaResponseDTO factura) {
        if (factura.getDetalles() == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal subtotal = factura.getDetalles().stream()
                .map(FacturaResponseDTO.DetalleFacturaDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal calcularDescuentoEfectivo(FacturaResponseDTO factura, BigDecimal subtotal) {
        if (factura.getFormaPago() == null || !"EFECTIVO".equalsIgnoreCase(factura.getFormaPago())) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return subtotal.multiply(new BigDecimal("0.33")).setScale(2, RoundingMode.HALF_UP);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}