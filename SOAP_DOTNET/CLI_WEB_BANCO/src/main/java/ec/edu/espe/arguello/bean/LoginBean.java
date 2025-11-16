package ec.edu.espe.arguello.bean;

import ec.edu.espe.arguello.models.User;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;

/**
 * Managed Bean para el manejo del login
 */
@Named
@ViewScoped
public class LoginBean implements Serializable {
    
    private String username = "MONSTER";
    private String password = "MONSTER1";
    
    public String login() {
        // Validación simple como en la app móvil
        if (username == null || username.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Por favor ingrese el usuario");
            return null;
        }
        
        if (password == null || password.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Por favor ingrese la contraseña");
            return null;
        }
        
        // Validación temporal hasta que arreglemos las dependencias
        if ("MONSTER".equalsIgnoreCase(username.trim()) && "MONSTER1".equals(password)) {
            // Guardar usuario en sesión
            User user = new User(username.trim(), password);
            user.setStatus("ok");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", user);
            
            // Limpiar campos
            username = "";
            password = "";
            
            // Redirigir a la página principal
            return "main?faces-redirect=true";
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Usuario o contraseña incorrectos");
            return null;
        }
    }
    
    private void addMessage(FacesMessage.Severity severity, String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, message, null));
    }
    
    // Getters and Setters
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
}