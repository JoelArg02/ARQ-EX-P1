package ec.edu.pinza.clicon.models;

/**
 * Representa la sesion de usuario autenticado.
 */
public class UsuarioSesion {
    private String usuario;

    public UsuarioSesion(String usuario) {
        this.usuario = usuario;
    }

    public String getUsuario() {
        return usuario;
    }
}
