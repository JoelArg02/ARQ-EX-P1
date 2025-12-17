package ec.edu.pinza.clicon.controllers;

import ec.edu.pinza.clicon.models.LoginResponseDTO;
import ec.edu.pinza.clicon.models.UsuarioSesion;
import ec.edu.pinza.clicon.services.ComercializadoraRestClient;

/**
 * Controlador de autenticación dinámico via REST API.
 * ADMIN: MONSTER / MONSTER9 (puede vender a cualquier cédula)
 * CLIENTES: cédula / abcd1234 (solo pueden comprar para su propia cédula)
 */
public class AuthController {

    private final ComercializadoraRestClient restClient;

    public AuthController(ComercializadoraRestClient restClient) {
        this.restClient = restClient;
    }

    public LoginResponseDTO login(String usuario, String password) {
        return restClient.login(usuario, password);
    }
    
    public UsuarioSesion crearSesion(LoginResponseDTO response) {
        if (response != null && response.isExitoso()) {
            return new UsuarioSesion(
                response.getUsername(),
                response.getRol(),
                response.getCedula()
            );
        }
        return null;
    }
}
