package ec.edu.pinza.cliesc.controllers;

import ec.edu.pinza.cliesc.managers.SessionManager;
import ec.edu.pinza.cliesc.views.LoginFrame;
import ec.edu.pinza.cliesc.views.ProductosFrame;
import javax.swing.SwingUtilities;

/**
 * Controlador para la vista de Login (mismas credenciales que CLIWEB).
 */
public class LoginController {

    private static final String USUARIO_VALIDO = "MONSTER";
    private static final String PASSWORD_VALIDO = "MONSTER9";
    private static final String CEDULA_DEMO = "1750123456";

    private final LoginFrame view;

    public LoginController(LoginFrame view) {
        this.view = view;
    }

    public void iniciarSesion() {
        String usuario = view.getCorreo();
        String contrasena = view.getContrasena();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            view.mostrarError("Por favor complete todos los campos");
            return;
        }

        if (USUARIO_VALIDO.equals(usuario) && PASSWORD_VALIDO.equals(contrasena)) {
            SessionManager.getInstance().setCliente(usuario, CEDULA_DEMO);

            view.limpiarCampos();
            view.dispose();

            SwingUtilities.invokeLater(() -> {
                ProductosFrame productosFrame = new ProductosFrame();
                productosFrame.setVisible(true);
            });
        } else {
            view.mostrarError("Usuario o contraseña incorrectos");
        }
    }
}
