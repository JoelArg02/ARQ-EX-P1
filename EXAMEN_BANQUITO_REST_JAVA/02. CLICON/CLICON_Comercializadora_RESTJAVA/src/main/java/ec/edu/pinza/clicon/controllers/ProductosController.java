package ec.edu.pinza.clicon.controllers;

import ec.edu.pinza.clicon.models.ProductoDTO;
import ec.edu.pinza.clicon.services.ComercializadoraRestClient;
import java.io.IOException;
import java.util.List;

/**
 * Controlador para productos.
 */
public class ProductosController {

    private final ComercializadoraRestClient restClient;

    public ProductosController(ComercializadoraRestClient restClient) {
        this.restClient = restClient;
    }

    public List<ProductoDTO> listarProductos() throws IOException, InterruptedException {
        return restClient.obtenerProductos();
    }
}
