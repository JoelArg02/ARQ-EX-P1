package ec.edu.pinza.clicon.controllers;

import ec.edu.pinza.clicon.models.FacturaResponseDTO;
import ec.edu.pinza.clicon.models.ItemCarrito;
import ec.edu.pinza.clicon.services.ComercializadoraRestClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador de pago/checkout.
 */
public class CheckoutController {

    private final ComercializadoraRestClient restClient;

    public CheckoutController(ComercializadoraRestClient restClient) {
        this.restClient = restClient;
    }

    public FacturaResponseDTO pagar(String cedulaCliente, String formaPago, Integer numeroCuotas, List<ItemCarrito> carrito)
            throws IOException, InterruptedException {

        List<Map<String, Integer>> items = new ArrayList<>();
        for (ItemCarrito item : carrito) {
            Map<String, Integer> map = new HashMap<>();
            map.put("idProducto", item.getIdProducto());
            map.put("cantidad", item.getCantidad());
            items.add(map);
        }

        return restClient.crearFactura(cedulaCliente, formaPago, numeroCuotas, items);
    }
}
