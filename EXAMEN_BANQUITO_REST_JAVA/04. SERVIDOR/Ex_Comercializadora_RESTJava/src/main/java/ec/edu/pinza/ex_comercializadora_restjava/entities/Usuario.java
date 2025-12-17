package ec.edu.pinza.ex_comercializadora_restjava.entities;

import java.io.Serializable;

/**
 * POJO Usuario - Usuario del sistema de comercializadora (SIN JPA - usa JDBC)
 */
public class Usuario implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer idUsuario;
    private String username;
    private String password;
    private Integer idCliente;
    private String rol; // ADMIN o CLIENTE
    private Boolean activo;
    
    // Campos adicionales cargados por JOIN
    private String cedula;      // Cédula del cliente asociado
    private String nombreCliente; // Nombre del cliente asociado
    
    public Usuario() {
    }
    
    public Usuario(String username, String password, Integer idCliente, String rol) {
        this.username = username;
        this.password = password;
        this.idCliente = idCliente;
        this.rol = rol;
        this.activo = true;
    }
    
    // Getters y Setters
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Integer getIdCliente() {
        return idCliente;
    }
    
    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }
    
    public String getRol() {
        return rol;
    }
    
    public void setRol(String rol) {
        this.rol = rol;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
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
    
    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", username='" + username + '\'' +
                ", rol='" + rol + '\'' +
                ", activo=" + activo +
                ", cedula='" + cedula + '\'' +
                ", nombreCliente='" + nombreCliente + '\'' +
                '}';
    }
}
