package ec.edu.pinza.ex_comercializadora_restjava.dto;

/**
 * DTO para respuesta de login
 */
public class LoginResponseDTO {
    
    private boolean exitoso;
    private String mensaje;
    private Integer idUsuario;
    private String username;
    private String rol;
    private String cedula;
    private String nombreCliente;
    
    public LoginResponseDTO() {
    }
    
    // Constructor para login fallido
    public static LoginResponseDTO error(String mensaje) {
        LoginResponseDTO response = new LoginResponseDTO();
        response.setExitoso(false);
        response.setMensaje(mensaje);
        return response;
    }
    
    // Constructor para login exitoso
    public static LoginResponseDTO exitoso(Integer idUsuario, String username, String rol, 
                                           String cedula, String nombreCliente) {
        LoginResponseDTO response = new LoginResponseDTO();
        response.setExitoso(true);
        response.setMensaje("Login exitoso");
        response.setIdUsuario(idUsuario);
        response.setUsername(username);
        response.setRol(rol);
        response.setCedula(cedula);
        response.setNombreCliente(nombreCliente);
        return response;
    }
    
    // Getters y Setters
    public boolean isExitoso() {
        return exitoso;
    }
    
    public void setExitoso(boolean exitoso) {
        this.exitoso = exitoso;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public Integer getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getRol() {
        return rol;
    }
    
    public void setRol(String rol) {
        this.rol = rol;
    }
    
    public String getCedula() {
        return cedula;
    }
    
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    
    public String getNombreCliente() {
        return nombreCliente;
    }
    
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
    
    public boolean isAdmin() {
        return "ADMIN".equals(this.rol);
    }
}
