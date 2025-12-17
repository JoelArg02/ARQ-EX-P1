package ec.edu.pinza.clicon.models;

/**
 * Representa la sesion de usuario autenticado con rol y cedula.
 */
public class UsuarioSesion {
    private String usuario;
    private String rol;
    private String cedula;

    public UsuarioSesion(String usuario, String rol, String cedula) {
        this.usuario = usuario;
        this.rol = rol;
        this.cedula = cedula;
    }

    public String getUsuario() {
        return usuario;
    }
    
    public String getRol() {
        return rol;
    }
    
    public String getCedula() {
        return cedula;
    }
    
    public boolean isAdmin() {
        return "ADMIN".equals(rol);
    }
}
