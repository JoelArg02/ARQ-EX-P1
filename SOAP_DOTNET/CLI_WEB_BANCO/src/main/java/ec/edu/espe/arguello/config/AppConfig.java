package ec.edu.espe.arguello.config;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Properties;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Configuración de la aplicación para conexión con servicios SOAP
 */
@ApplicationScoped
public class AppConfig {
    
    private static final Logger logger = Logger.getLogger(AppConfig.class.getName());
    private Properties properties;
    
    public AppConfig() {
        loadProperties();
    }
    
    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                logger.warning("config.properties no encontrado, usando valores por defecto");
                setDefaultProperties();
            }
        } catch (Exception e) {
            logger.severe("Error al cargar config.properties: " + e.getMessage());
            setDefaultProperties();
        }
    }
    
    private void setDefaultProperties() {
        properties.setProperty("base.url", "http://localhost:5001/");
        properties.setProperty("services.user", "UserService.svc");
        properties.setProperty("services.cliente", "ClienteBancoService.svc");
        properties.setProperty("services.cuenta", "CuentaService.svc");
        properties.setProperty("services.credito", "CreditoBancoService.svc");
        properties.setProperty("services.movimiento", "MovimientoService.svc");
        properties.setProperty("services.amortizacion", "AmortizacionCreditoService.svc");
    }
    
    public String getBaseUrl() {
        return properties.getProperty("base.url", "http://localhost:5001/");
    }
    
    public String getUserServiceUrl() {
        return getBaseUrl() + properties.getProperty("services.user", "UserService.svc");
    }
    
    public String getClienteServiceUrl() {
        return getBaseUrl() + properties.getProperty("services.cliente", "ClienteBancoService.svc");
    }
    
    public String getCuentaServiceUrl() {
        return getBaseUrl() + properties.getProperty("services.cuenta", "CuentaService.svc");
    }
    
    public String getCreditoServiceUrl() {
        return getBaseUrl() + properties.getProperty("services.credito", "CreditoBancoService.svc");
    }
    
    public String getMovimientoServiceUrl() {
        return getBaseUrl() + properties.getProperty("services.movimiento", "MovimientoService.svc");
    }
    
    public String getAmortizacionServiceUrl() {
        return getBaseUrl() + properties.getProperty("services.amortizacion", "AmortizacionCreditoService.svc");
    }
}