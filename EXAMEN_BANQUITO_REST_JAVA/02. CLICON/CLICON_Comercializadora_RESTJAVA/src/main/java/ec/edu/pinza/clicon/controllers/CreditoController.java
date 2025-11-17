package ec.edu.pinza.clicon.controllers;

import ec.edu.pinza.clicon.services.BanquitoRestClient;
import java.io.IOException;
import java.util.Map;

public class CreditoController {

    private final BanquitoRestClient banquitoClient;

    public CreditoController(BanquitoRestClient banquitoClient) {
        this.banquitoClient = banquitoClient;
    }

    public Map<String, Object> validarSujetoCredito(String cedula) throws IOException, InterruptedException {
        return banquitoClient.validarSujetoCredito(cedula);
    }

    public Map<String, Object> obtenerMontoMaximo(String cedula) throws IOException, InterruptedException {
        return banquitoClient.obtenerMontoMaximo(cedula);
    }
}
