package ec.edu.pinza.cliesc.controllers;

import ec.edu.pinza.cliesc.managers.SessionManager;
import ec.edu.pinza.cliesc.models.LoginResponseDTO;
import ec.edu.pinza.cliesc.services.ComercializadoraRestClient;
import ec.edu.pinza.cliesc.views.LoginFrame;
import ec.edu.pinza.cliesc.views.ProductosFrame;
import javax.swing.SwingUtilities;

/**
 * Controlador para la vista de Login con autenticación dinámica via REST.
 * ADMIN: MONSTER / MONSTER9 (puede vender a cualquier cédula)
 * CLIENTES: cédula / abcd1234 (solo pueden comprar para su propia cédula)
 */
public class LoginController {

    private final LoginFrame view;
    private final ComercializadoraRestClient restClient;

    public LoginController(LoginFrame view) {
        this.view = view;
        this.restClient = new ComercializadoraRestClient();
    }

    public void iniciarSesion() {
        String usuario = view.getCorreo();
        String contrasena = view.getContrasena();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            view.mostrarError("Por favor complete todos los campos");
            return;
        }

        // Llamar al API de autenticación
        LoginResponseDTO response = restClient.login(usuario, contrasena);
        
        if (response != null && response.isExitoso()) {
            // Guardar datos en sesión con el rol
            SessionManager.getInstance().setCliente(
                response.getUsername(),
                response.getCedula(),
                response.getRol()
            );

            view.limpiarCampos();
            view.dispose();

            SwingUtilities.invokeLater(() -> {
                ProductosFrame productosFrame = new ProductosFrame();
                productosFrame.setVisible(true);
            });
        } else {
            String mensaje = response != null && response.getMensaje() != null
                    ? response.getMensaje()
                    : "Usuario o contraseña incorrectos";
            view.mostrarError(mensaje);
        }
    }
}
