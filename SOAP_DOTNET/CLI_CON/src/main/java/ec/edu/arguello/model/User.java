package ec.edu.arguello.model;

public class User {
    private int id;
    private String nombre;
    private String contrasena;
    private String rol;

    public User() {}

    public User(int id, String nombre, String contrasena, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    // Alias para compatibilidad
    public String getUserName() { return nombre; }
    public void setUserName(String nombre) { this.nombre = nombre; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    
    // Alias para compatibilidad
    public String getPassword() { return contrasena; }
    public void setPassword(String contrasena) { this.contrasena = contrasena; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    @Override
    public String toString() {
        return "User{id=" + id + ", nombre='" + nombre + "', rol='" + rol + "'}";
    }
}
