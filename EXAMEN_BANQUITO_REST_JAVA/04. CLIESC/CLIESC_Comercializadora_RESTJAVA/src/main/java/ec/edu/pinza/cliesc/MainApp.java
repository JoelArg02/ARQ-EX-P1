package ec.edu.pinza.cliesc;

import com.formdev.flatlaf.FlatDarkLaf;
import ec.edu.pinza.cliesc.views.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Clase principal de la aplicación de escritorio
 * Cliente de la Comercializadora Monster
 */
public class MainApp {
    
    public static void main(String[] args) {
        // Configurar Look & Feel moderno (FlatLaf Dark)
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        
        // Iniciar la aplicación en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
