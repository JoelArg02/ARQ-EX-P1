package ec.edu.pinza.cliesc.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;

/**
 * Utilidades para manejo de imágenes en base64
 */
public class ImageUtils {
    
    /**
     * Convierte una imagen en base64 a ImageIcon
     * @param base64String String en formato "data:image/jpeg;base64,..." o solo el base64
     * @param width Ancho deseado
     * @param height Alto deseado
     * @return ImageIcon escalado o null si hay error
     */
    public static ImageIcon base64ToImageIcon(String base64String, int width, int height) {
        if (base64String == null || base64String.isEmpty()) {
            return createPlaceholderIcon(width, height);
        }
        
        try {
            // Eliminar el prefijo data:image/...;base64, si existe
            String base64Data = base64String;
            if (base64String.contains(",")) {
                base64Data = base64String.split(",")[1];
            }
            
            // Decodificar base64
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            
            // Crear BufferedImage desde bytes
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bis);
            
            if (image == null) {
                return createPlaceholderIcon(width, height);
            }
            
            // Escalar imagen
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
            
        } catch (Exception e) {
            System.err.println("Error al convertir base64 a imagen: " + e.getMessage());
            return createPlaceholderIcon(width, height);
        }
    }
    
    /**
     * Crea un icono placeholder cuando no hay imagen
     */
    private static ImageIcon createPlaceholderIcon(int width, int height) {
        BufferedImage placeholder = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = placeholder.createGraphics();
        
        // Fondo gris
        g2d.setColor(new Color(60, 60, 60));
        g2d.fillRect(0, 0, width, height);
        
        // Texto "Sin imagen"
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "Sin imagen";
        int x = (width - fm.stringWidth(text)) / 2;
        int y = (height - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, x, y);
        
        g2d.dispose();
        return new ImageIcon(placeholder);
    }
}
