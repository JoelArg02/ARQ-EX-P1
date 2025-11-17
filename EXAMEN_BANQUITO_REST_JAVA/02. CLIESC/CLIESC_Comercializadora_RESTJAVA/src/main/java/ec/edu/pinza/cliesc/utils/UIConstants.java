package ec.edu.pinza.cliesc.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;

/**
 * Constantes para el diseño de la aplicación - Tema Monsters Inc
 */
public class UIConstants {
    // Colores principales del gradiente (púrpura)
    public static final Color COLOR_PRIMARY_START = new Color(124, 58, 237); // #7c3aed
    public static final Color COLOR_PRIMARY_END = new Color(102, 126, 234);  // #667eea
    
    // Colores complementarios
    public static final Color COLOR_SUCCESS = new Color(34, 197, 94);        // Verde
    public static final Color COLOR_ERROR = new Color(239, 68, 68);          // Rojo
    public static final Color COLOR_WARNING = new Color(251, 191, 36);       // Amarillo
    public static final Color COLOR_INFO = new Color(59, 130, 246);          // Azul
    
    // Colores de texto
    public static final Color COLOR_TEXT_PRIMARY = new Color(255, 255, 255); // Blanco
    public static final Color COLOR_TEXT_SECONDARY = new Color(156, 163, 175); // Gris claro
    public static final Color COLOR_TEXT_DARK = new Color(31, 41, 55);       // Gris oscuro
    
    // Colores de fondo
    public static final Color COLOR_BACKGROUND = new Color(17, 24, 39);      // Fondo oscuro
    public static final Color COLOR_CARD = new Color(31, 41, 55);            // Card oscuro
    public static final Color COLOR_CARD_HOVER = new Color(55, 65, 81);      // Card hover
    
    // Fuentes
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    
    // Dimensiones
    public static final int PADDING_SMALL = 8;
    public static final int PADDING_MEDIUM = 16;
    public static final int PADDING_LARGE = 24;
    public static final int BORDER_RADIUS = 10;
    
    // Tamaños de ventana
    public static final int WINDOW_WIDTH_SMALL = 500;
    public static final int WINDOW_WIDTH_MEDIUM = 800;
    public static final int WINDOW_WIDTH_LARGE = 1200;
    public static final int WINDOW_HEIGHT_SMALL = 400;
    public static final int WINDOW_HEIGHT_MEDIUM = 600;
    public static final int WINDOW_HEIGHT_LARGE = 700;
    
    /**
     * Crea un GradientPaint horizontal con los colores principales
     */
    public static GradientPaint createGradient(int width) {
        return new GradientPaint(0, 0, COLOR_PRIMARY_START, width, 0, COLOR_PRIMARY_END);
    }
    
    /**
     * Obtiene un color más claro
     */
    public static Color lighten(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() + (255 - color.getRed()) * factor));
        int g = Math.min(255, (int)(color.getGreen() + (255 - color.getGreen()) * factor));
        int b = Math.min(255, (int)(color.getBlue() + (255 - color.getBlue()) * factor));
        return new Color(r, g, b);
    }
    
    /**
     * Obtiene un color más oscuro
     */
    public static Color darken(Color color, float factor) {
        int r = Math.max(0, (int)(color.getRed() * (1 - factor)));
        int g = Math.max(0, (int)(color.getGreen() * (1 - factor)));
        int b = Math.max(0, (int)(color.getBlue() * (1 - factor)));
        return new Color(r, g, b);
    }
}
