package ec.edu.pinza.cliesc.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Utilidades para formateo de datos
 */
public class FormatUtils {
    
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("es", "EC"));
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Formatea un BigDecimal como moneda ($)
     */
    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "$0.00";
        }
        return CURRENCY_FORMAT.format(amount);
    }
    
    /**
     * Formatea una fecha LocalDate
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * Formatea una fecha LocalDateTime
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    /**
     * Calcula el 33% de descuento
     */
    public static BigDecimal calcularDescuento33(BigDecimal monto) {
        if (monto == null) {
            return BigDecimal.ZERO;
        }
        return monto.multiply(new BigDecimal("0.33")).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * Aplica el descuento del 33%
     */
    public static BigDecimal aplicarDescuento33(BigDecimal monto) {
        if (monto == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal descuento = calcularDescuento33(monto);
        return monto.subtract(descuento).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * Formatea un número entero con separador de miles
     */
    public static String formatNumber(Integer number) {
        if (number == null) {
            return "0";
        }
        return NumberFormat.getIntegerInstance(new Locale("es", "EC")).format(number);
    }
}
