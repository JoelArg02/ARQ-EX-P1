package ec.edu.pinza.cliesc.controllers;

import ec.edu.pinza.cliesc.managers.SessionManager;
import ec.edu.pinza.cliesc.models.FacturaResponseDTO;
import ec.edu.pinza.cliesc.services.ComercializadoraRestClient;
import ec.edu.pinza.cliesc.views.VentasFrame;
import java.util.List;

/**
 * Controlador para la vista de Ventas (facturas).
 * ADMIN ve todas las facturas, CLIENTE solo ve las suyas.
 */
public class VentasController {

    private final VentasFrame view;
    private final ComercializadoraRestClient comercializadoraClient;
    private List<FacturaResponseDTO> facturas;

    public VentasController(VentasFrame view) {
        this.view = view;
        this.comercializadoraClient = new ComercializadoraRestClient();
    }

    public void cargarFacturas() {
        try {
            SessionManager session = SessionManager.getInstance();
            boolean isAdmin = session.isAdmin();
            String cedula = session.getCedula();
            
            if (isAdmin) {
                // Admin ve TODAS las facturas del sistema
                facturas = comercializadoraClient.obtenerTodasLasFacturas();
            } else {
                // Cliente solo ve sus propias compras
                facturas = comercializadoraClient.obtenerFacturasPorCliente(cedula);
            }
            
            view.mostrarFacturas(facturas);
        } catch (Exception ex) {
            view.mostrarError("Error al cargar facturas: " + ex.getMessage());
        }
    }

    public void verDetalle(int index) {
        try {
            if (index >= 0 && facturas != null && index < facturas.size()) {
                FacturaResponseDTO facturaSeleccionada = facturas.get(index);
                Integer idFactura = facturaSeleccionada.getIdFactura();
                
                // Validar que cliente no pueda ver facturas de otros
                SessionManager session = SessionManager.getInstance();
                if (!session.isAdmin()) {
                    String cedulaUsuario = session.getCedula();
                    if (!cedulaUsuario.equals(facturaSeleccionada.getCedulaCliente())) {
                        view.mostrarError("No tienes permiso para ver esta factura");
                        return;
                    }
                }
                
                FacturaResponseDTO factura = comercializadoraClient.obtenerFacturaPorId(idFactura);
                view.mostrarDetalleFactura(factura);
            }
        } catch (Exception ex) {
            view.mostrarError("Error al obtener detalle: " + ex.getMessage());
        }
    }

    public void verTablaAmortizacion(Integer idFactura) {
        try {
            List<FacturaResponseDTO.CuotaAmortizacionDTO> tabla =
                    comercializadoraClient.obtenerTablaAmortizacion(idFactura);
            view.mostrarTablaAmortizacion(tabla, idFactura);
        } catch (Exception ex) {
            view.mostrarError("Error al obtener tabla de amortización: " + ex.getMessage());
        }
    }
}
