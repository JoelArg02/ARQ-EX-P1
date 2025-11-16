package ec.edu.espe.arguello.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ec.edu.espe.arguello.config.AppConfig;
import ec.edu.espe.arguello.model.Cuenta;
import ec.edu.espe.arguello.model.Movimiento;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Repository para operaciones SOAP de Cuenta
 */
@ApplicationScoped 
public class CuentaRepository {
    
    private static final Logger logger = Logger.getLogger(CuentaRepository.class.getName());
    
    @Inject
    private AppConfig config;
    
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    
    /**
     * Obtiene todas las cuentas
     */
    public List<Cuenta> getAllCuentas() {
        try {
            String soapRequest = """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetAllCuentas/>
                    </soap:Body>
                </soap:Envelope>
                """;
                
            String response = sendSoapRequest(config.getCuentaServiceUrl(), soapRequest, 
                "http://tempuri.org/ICuentaController/GetAllCuentas");
            
            return parseCuentasFromXml(response);
        } catch (Exception e) {
            logger.severe("Error al obtener cuentas: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene las cuentas por cliente ID
     */
    public List<Cuenta> getCuentasByClienteId(int clienteId) {
        try {
            String soapRequest = String.format("""
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                                  xmlns:tem="http://tempuri.org/">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <tem:GetCuentasByClienteBancoId>
                         <tem:clienteBancoId>%d</tem:clienteBancoId>
                      </tem:GetCuentasByClienteBancoId>
                   </soapenv:Body>
                </soapenv:Envelope>
                """, clienteId);
                
            String response = sendSoapRequest(config.getCuentaServiceUrl(), soapRequest,
                "http://tempuri.org/ICuentaController/GetCuentasByClienteBancoId");
            
            return parseCuentasFromXml(response);
        } catch (Exception e) {
            logger.severe("Error al obtener cuentas por cliente: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Crea una nueva cuenta
     */
    public Cuenta createCuenta(Cuenta cuenta) {
        try {
            int tipoCuentaNumero = "Ahorros".equals(cuenta.getTipoCuenta().trim()) ? 1 : 2;
            
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
                               xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:CreateCuenta>
                            <tem:clienteBancoId>%d</tem:clienteBancoId>
                            <tem:numeroCuenta>%s</tem:numeroCuenta>
                            <tem:saldo>%.2f</tem:saldo>
                            <tem:tipoCuenta>%d</tem:tipoCuenta>
                        </tem:CreateCuenta>
                    </soap:Body>
                </soap:Envelope>
                """, cuenta.getClienteBancoId(), cuenta.getNumeroCuenta(), 
                cuenta.getSaldo(), tipoCuentaNumero);
                
            String response = sendSoapRequest(config.getCuentaServiceUrl(), soapRequest,
                "http://tempuri.org/ICuentaController/CreateCuenta");
            
            return parseCuentaFromXml(response);
        } catch (Exception e) {
            logger.severe("Error al crear cuenta: " + e.getMessage());
            return cuenta;
        }
    }
    
    /**
     * Actualiza una cuenta existente
     */
    public Cuenta updateCuenta(Cuenta cuenta) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:UpdateCuenta>
                            <tem:id>%d</tem:id>
                            <tem:cuenta>
                                <tem:Id>%d</tem:Id>
                                <tem:ClienteBancoId>%d</tem:ClienteBancoId>
                                <tem:NumeroCuenta>%s</tem:NumeroCuenta>
                                <tem:Saldo>%.2f</tem:Saldo>
                                <tem:TipoCuenta>%s</tem:TipoCuenta>
                            </tem:cuenta>
                        </tem:UpdateCuenta>
                    </soap:Body>
                </soap:Envelope>
                """, cuenta.getId(), cuenta.getId(), cuenta.getClienteBancoId(),
                cuenta.getNumeroCuenta(), cuenta.getSaldo(), cuenta.getTipoCuenta());
                
            String response = sendSoapRequest(config.getCuentaServiceUrl(), soapRequest,
                "http://tempuri.org/ICuentaController/UpdateCuenta");
            
            return parseCuentaFromXml(response);
        } catch (Exception e) {
            logger.severe("Error al actualizar cuenta: " + e.getMessage());
            return cuenta;
        }
    }
    
    /**
     * Elimina una cuenta
     */
    public boolean deleteCuenta(int cuentaId) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:DeleteCuenta>
                            <tem:id>%d</tem:id>
                        </tem:DeleteCuenta>
                    </soap:Body>
                </soap:Envelope>
                """, cuentaId);
                
            sendSoapRequest(config.getCuentaServiceUrl(), soapRequest,
                "http://tempuri.org/ICuentaController/DeleteCuenta");
            
            return true;
        } catch (Exception e) {
            logger.severe("Error al eliminar cuenta: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Crea un movimiento (depósito o retiro)
     */
    public Movimiento createMovimiento(Movimiento movimiento) {
        try {
            int tipo = "Deposito".equals(movimiento.getTipoMovimiento()) ? 1 : 2;
            
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:CreateMovimiento>
                            <tem:cuentaId>%d</tem:cuentaId>
                            <tem:tipo>%d</tem:tipo>
                            <tem:monto>%.2f</tem:monto>
                        </tem:CreateMovimiento>
                    </soap:Body>
                </soap:Envelope>
                """, movimiento.getCuentaId(), tipo, movimiento.getMonto());
                
            String response = sendSoapRequest(config.getMovimientoServiceUrl(), soapRequest,
                "http://tempuri.org/IMovimientoController/CreateMovimiento");
            
            return parseMovimientoFromXml(response);
        } catch (Exception e) {
            logger.severe("Error al crear movimiento: " + e.getMessage());
            return movimiento;
        }
    }
    
    /**
     * Envía una petición SOAP
     */
    private String sendSoapRequest(String url, String soapXml, String soapAction) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "text/xml; charset=utf-8");
        httpPost.setHeader("SOAPAction", "\"" + soapAction + "\"");
        
        HttpEntity entity = new StringEntity(soapXml);
        httpPost.setEntity(entity);
        
        return httpClient.execute(httpPost, response -> {
            HttpEntity responseEntity = response.getEntity();
            return EntityUtils.toString(responseEntity);
        });
    }
    
    /**
     * Parsea la respuesta XML para extraer la lista de cuentas
     */
    private List<Cuenta> parseCuentasFromXml(String xml) {
        List<Cuenta> cuentas = new ArrayList<>();
        
        try {
            logger.info("XML recibido para parsear cuentas (primeros 500 chars): " + xml.substring(0, Math.min(500, xml.length())));
            
            // Buscar todos los elementos Cuenta
            Pattern arrayPattern = Pattern.compile("<a:Cuenta>(.*?)</a:Cuenta>", Pattern.DOTALL);
            Matcher arrayMatcher = arrayPattern.matcher(xml);
            
            int contador = 0;
            while (arrayMatcher.find()) {
                contador++;
                String cuentaXml = arrayMatcher.group(1);
                logger.info("Procesando cuenta #" + contador);
                
                try {
                    Cuenta cuenta = new Cuenta();
                    cuenta.setId(Integer.parseInt(extractValueRegex(cuentaXml, "<a:Id>(.*?)</a:Id>")));
                    cuenta.setClienteBancoId(Integer.parseInt(extractValueRegex(cuentaXml, "<a:ClienteBancoId>(.*?)</a:ClienteBancoId>")));
                    cuenta.setNumeroCuenta(extractValueRegex(cuentaXml, "<a:NumeroCuenta>(.*?)</a:NumeroCuenta>"));
                    cuenta.setSaldo(Double.parseDouble(extractValueRegex(cuentaXml, "<a:Saldo>(.*?)</a:Saldo>")));
                    cuenta.setTipoCuenta(extractValueRegex(cuentaXml, "<a:TipoCuenta>(.*?)</a:TipoCuenta>"));
                    
                    cuentas.add(cuenta);
                    logger.info("Cuenta agregada: " + cuenta.getNumeroCuenta() + " (ID: " + cuenta.getId() + ")");
                } catch (Exception e) {
                    logger.severe("Error al parsear cuenta individual: " + e.getMessage());
                }
            }
            
            logger.info("Total de cuentas parseadas: " + cuentas.size());
        } catch (Exception e) {
            logger.severe("Error al parsear cuentas: " + e.getMessage());
            e.printStackTrace();
        }
        
        return cuentas;
    }
    
    /**
     * Parsea una cuenta individual desde XML
     */
    private Cuenta parseCuentaFromXml(String xml) {
        Cuenta cuenta = new Cuenta();
        
        try {
            cuenta.setId(extractValue(xml, "a:Id", Integer.class));
            cuenta.setClienteBancoId(extractValue(xml, "a:ClienteBancoId", Integer.class));
            cuenta.setNumeroCuenta(extractValue(xml, "a:NumeroCuenta", String.class));
            cuenta.setSaldo(extractValue(xml, "a:Saldo", Double.class));
            cuenta.setTipoCuenta(extractValue(xml, "a:TipoCuenta", String.class));
        } catch (Exception e) {
            logger.severe("Error al parsear cuenta: " + e.getMessage());
        }
        
        return cuenta;
    }
    
    /**
     * Parsea un movimiento desde XML
     */
    private Movimiento parseMovimientoFromXml(String xml) {
        Movimiento movimiento = new Movimiento();
        
        try {
            movimiento.setId(extractValue(xml, "a:Id", Integer.class));
            movimiento.setCuentaId(extractValue(xml, "a:CuentaId", Integer.class));
            movimiento.setTipoMovimiento(extractValue(xml, "a:Tipo", String.class));
            movimiento.setMonto(extractValue(xml, "a:Monto", Double.class));
            movimiento.setFecha(extractValue(xml, "a:Fecha", String.class));
            movimiento.setDescripcion(extractValue(xml, "a:Descripcion", String.class));
        } catch (Exception e) {
            logger.severe("Error al parsear movimiento: " + e.getMessage());
        }
        
        return movimiento;
    }
    
    /**
     * Extrae un valor del XML usando regex
     */
    private String extractValueRegex(String xml, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(xml);
        return m.find() ? m.group(1) : "";
    }
    
    /**
     * Extrae un valor del XML y lo convierte al tipo especificado
     */
    @SuppressWarnings("unchecked")
    private <T> T extractValue(String xml, String tagName, Class<T> type) {
        String value = extractValueRegex(xml, "<" + tagName + ">(.*?)</" + tagName + ">");
        
        if (value.isEmpty()) {
            if (type == Integer.class) return (T) Integer.valueOf(0);
            if (type == Boolean.class) return (T) Boolean.FALSE;
            if (type == Double.class) return (T) Double.valueOf(0.0);
            return (T) "";
        }
        
        if (type == Integer.class) return (T) Integer.valueOf(value);
        if (type == Boolean.class) return (T) Boolean.valueOf("true".equalsIgnoreCase(value));
        if (type == Double.class) return (T) Double.valueOf(value);
        return (T) value;
    }
}