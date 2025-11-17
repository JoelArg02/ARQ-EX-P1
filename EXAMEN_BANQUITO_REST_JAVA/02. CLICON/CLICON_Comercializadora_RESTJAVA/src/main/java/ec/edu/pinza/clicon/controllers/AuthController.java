package ec.edu.pinza.clicon.controllers;

import ec.edu.pinza.clicon.models.UsuarioSesion;

/**
 * Gestiona la autenticacion simple replicando CLIWEB.
 */
public class AuthController {

    private static final String USUARIO_VALIDO = "MONSTER";
    private static final String PASSWORD_VALIDO = "MONSTER9";

    public UsuarioSesion login(String usuario, String password) {
        if (USUARIO_VALIDO.equals(usuario) && PASSWORD_VALIDO.equals(password)) {
            return new UsuarioSesion(usuario);
        }
        return null;
    }
}
