package ec.edu.pinza.cliesc.controllers;

import ec.edu.pinza.cliesc.models.FacturaResponseDTO;
import ec.edu.pinza.cliesc.services.ComercializadoraRestClient;
import ec.edu.pinza.cliesc.views.VentasFrame;
import java.util.List;

/**
 * Controlador para la vista de Ventas (facturas).
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
            // Igual que CLIWEB: vista administrador obtiene todas las facturas.
            facturas = comercializadoraClient.obtenerTodasLasFacturas();
            view.mostrarFacturas(facturas);
        } catch (Exception ex) {
            view.mostrarError("Error al cargar facturas: " + ex.getMessage());
        }
    }

    public void verDetalle(int index) {
        try {
            if (index >= 0 && facturas != null && index < facturas.size()) {
                Integer idFactura = facturas.get(index).getIdFactura();
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
