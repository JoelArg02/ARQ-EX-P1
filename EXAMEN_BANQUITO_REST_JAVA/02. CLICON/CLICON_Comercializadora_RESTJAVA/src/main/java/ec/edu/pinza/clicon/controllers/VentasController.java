package ec.edu.pinza.clicon.controllers;

import ec.edu.pinza.clicon.models.FacturaResponseDTO;
import ec.edu.pinza.clicon.services.ComercializadoraRestClient;
import java.io.IOException;
import java.util.List;

/**
 * Controlador para visualizacion de ventas (admin) y compras por cliente.
 */
public class VentasController {

    private final ComercializadoraRestClient restClient;

    public VentasController(ComercializadoraRestClient restClient) {
        this.restClient = restClient;
    }

    public List<FacturaResponseDTO> listarTodas() throws IOException, InterruptedException {
        return restClient.obtenerTodasLasFacturas();
    }

    public List<FacturaResponseDTO> listarPorCliente(String cedula) throws IOException, InterruptedException {
        return restClient.obtenerFacturasPorCliente(cedula);
    }

    public FacturaResponseDTO obtenerPorId(Integer id) throws IOException, InterruptedException {
        return restClient.obtenerFacturaPorId(id);
    }

    public List<FacturaResponseDTO.CuotaAmortizacionDTO> obtenerTablaAmortizacion(Integer idFactura)
            throws IOException, InterruptedException {
        return restClient.obtenerTablaAmortizacion(idFactura);
    }
}
