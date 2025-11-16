package ec.edu.espe.arguello.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ec.edu.espe.arguello.config.AppConfig;
import ec.edu.espe.arguello.model.CreditoBanco;
import ec.edu.espe.arguello.model.AmortizacionCredito;
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
 * Repository para operaciones SOAP de Crédito Banco
 */
@ApplicationScoped 
public class CreditoBancoRepository {
    
    private static final Logger logger = Logger.getLogger(CreditoBancoRepository.class.getName());
    
    @Inject
    private AppConfig config;
    
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    
    /**
     * Obtiene todos los créditos
     */
    public List<CreditoBanco> getAllCreditos() {
        try {
            String soapRequest = """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetAllCreditosBanco/>
                    </soap:Body>
                </soap:Envelope>
                """;
                
            String response = sendSoapRequest(config.getCreditoServiceUrl(), soapRequest, 
                "http://tempuri.org/ICreditoBancoController/GetAllCreditosBanco");
            
            return parseCreditosFromXml(response);
        } catch (Exception e) {
            logger.severe("Error al obtener créditos: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene los créditos por cliente ID
     */
    public List<CreditoBanco> getCreditosByClienteId(int clienteId) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetCreditosBancoByClienteBancoId>
                            <tem:clienteBancoId>%d</tem:clienteBancoId>
                        </tem:GetCreditosBancoByClienteBancoId>
                    </soap:Body>
                </soap:Envelope>
                """, clienteId);
                
            String response = sendSoapRequest(config.getCreditoServiceUrl(), soapRequest,
                "http://tempuri.org/ICreditoBancoController/GetCreditosBancoByClienteBancoId");
            
            return parseCreditosFromXml(response);
        } catch (Exception e) {
            logger.severe("Error al obtener créditos por cliente: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Crea un nuevo crédito (aprueba crédito)
     */
    public CreditoBanco createCredito(String cedulaCliente, double montoSolicitado, int numeroCuotas) {
        try {
            String soapRequest = String.format("""
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                                  xmlns:tem="http://tempuri.org/"
                                  xmlns:api="http://schemas.datacontract.org/2004/07/API_BANCO.Application.DTOs.Creditos">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <tem:AprobarCredito>
                         <tem:dto>
                            <api:CedulaCliente>%s</api:CedulaCliente>
                            <api:MontoSolicitado>%.2f</api:MontoSolicitado>
                            <api:NumeroCuotas>%d</api:NumeroCuotas>
                         </tem:dto>
                      </tem:AprobarCredito>
                   </soapenv:Body>
                </soapenv:Envelope>
                """, cedulaCliente, montoSolicitado, numeroCuotas);
                
            String response = sendSoapRequest(config.getCreditoServiceUrl(), soapRequest,
                "http://tempuri.org/ICreditoBancoController/AprobarCredito");
            
            return parseCreditoFromXml(response);
        } catch (Exception e) {
            logger.severe("Error al crear crédito: " + e.getMessage());
            return new CreditoBanco();
        }
    }
    
    /**
     * Actualiza un crédito existente
     */
    public CreditoBanco updateCredito(CreditoBanco credito) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:UpdateCreditoBanco>
                            <tem:id>%d</tem:id>
                            <tem:creditoBanco>
                                <tem:Id>%d</tem:Id>
                                <tem:ClienteBancoId>%d</tem:ClienteBancoId>
                                <tem:MontoAprobado>%.2f</tem:MontoAprobado>
                                <tem:NumeroCuotas>%d</tem:NumeroCuotas>
                                <tem:TasaInteres>%.4f</tem:TasaInteres>
                                <tem:FechaAprobacion>%s</tem:FechaAprobacion>
                                <tem:Activo>%s</tem:Activo>
                            </tem:creditoBanco>
                        </tem:UpdateCreditoBanco>
                    </soap:Body>
                </soap:Envelope>
                """, credito.getId(), credito.getId(), credito.getClienteBancoId(),
                credito.getMontoAprobado(), credito.getNumeroCuotas(), credito.getTasaInteres(),
                credito.getFechaAprobacion(), credito.isActivo());
                
            String response = sendSoapRequest(config.getCreditoServiceUrl(), soapRequest,
                "http://tempuri.org/ICreditoBancoController/UpdateCreditoBanco");
            
            return parseCreditoFromXml(response);
        } catch (Exception e) {
            logger.severe("Error al actualizar crédito: " + e.getMessage());
            return credito;
        }
    }
    
    /**
     * Elimina un crédito
     */
    public boolean deleteCredito(int creditoId) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:DeleteCreditoBanco>
                            <tem:id>%d</tem:id>
                        </tem:DeleteCreditoBanco>
                    </soap:Body>
                </soap:Envelope>
                """, creditoId);
                
            sendSoapRequest(config.getCreditoServiceUrl(), soapRequest,
                "http://tempuri.org/ICreditoBancoController/DeleteCreditoBanco");
            
            return true;
        } catch (Exception e) {
            logger.severe("Error al eliminar crédito: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene las amortizaciones de un crédito
     */
    public List<AmortizacionCredito> getAmortizacionesByCredito(int creditoId) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetAmortizacionesByCreditoBancoId>
                            <tem:creditoBancoId>%d</tem:creditoBancoId>
                        </tem:GetAmortizacionesByCreditoBancoId>
                    </soap:Body>
                </soap:Envelope>
                """, creditoId);
                
            String response = sendSoapRequest(config.getAmortizacionServiceUrl(), soapRequest,
                "http://tempuri.org/IAmortizacionCreditoController/GetAmortizacionesByCreditoBancoId");
            
            return parseAmortizacionesFromXml(response);
        } catch (Exception e) {
            logger.severe("Error al obtener amortizaciones: " + e.getMessage());
            return new ArrayList<>();
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
     * Parsea la respuesta XML para extraer la lista de créditos
     */
    private List<CreditoBanco> parseCreditosFromXml(String xml) {
        List<CreditoBanco> creditos = new ArrayList<>();
        
        try {
            logger.info("XML recibido para parsear créditos (primeros 500 chars): " + xml.substring(0, Math.min(500, xml.length())));
            
            // Buscar todos los elementos CreditoBanco
            Pattern arrayPattern = Pattern.compile("<a:CreditoBanco>(.*?)</a:CreditoBanco>", Pattern.DOTALL);
            Matcher arrayMatcher = arrayPattern.matcher(xml);
            
            int contador = 0;
            while (arrayMatcher.find()) {
                contador++;
                String creditoXml = arrayMatcher.group(1);
                logger.info("Procesando crédito #" + contador);
                
                try {
                    CreditoBanco credito = new CreditoBanco();
                    credito.setId(Integer.parseInt(extractValueRegex(creditoXml, "<a:Id>(.*?)</a:Id>")));
                    credito.setClienteBancoId(Integer.parseInt(extractValueRegex(creditoXml, "<a:ClienteBancoId>(.*?)</a:ClienteBancoId>")));
                    credito.setMontoAprobado(Double.parseDouble(extractValueRegex(creditoXml, "<a:MontoAprobado>(.*?)</a:MontoAprobado>")));
                    credito.setNumeroCuotas(Integer.parseInt(extractValueRegex(creditoXml, "<a:NumeroCuotas>(.*?)</a:NumeroCuotas>")));
                    credito.setTasaInteres(Double.parseDouble(extractValueRegex(creditoXml, "<a:TasaInteres>(.*?)</a:TasaInteres>")));
                    credito.setFechaAprobacion(extractValueRegex(creditoXml, "<a:FechaAprobacion>(.*?)</a:FechaAprobacion>"));
                    String activo = extractValueRegex(creditoXml, "<a:Activo>(.*?)</a:Activo>");
                    credito.setActivo("true".equalsIgnoreCase(activo));
                    
                    creditos.add(credito);
                    logger.info("Crédito agregado: ID=" + credito.getId() + ", Monto=" + credito.getMontoAprobado());
                } catch (Exception e) {
                    logger.severe("Error al parsear crédito individual: " + e.getMessage());
                }
            }
            
            logger.info("Total de créditos parseados: " + creditos.size());
        } catch (Exception e) {
            logger.severe("Error al parsear créditos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return creditos;
    }
    
    /**
     * Parsea un crédito individual desde XML
     */
    private CreditoBanco parseCreditoFromXml(String xml) {
        CreditoBanco credito = new CreditoBanco();
        
        try {
            credito.setId(extractValue(xml, "a:Id", Integer.class));
            credito.setClienteBancoId(extractValue(xml, "a:ClienteBancoId", Integer.class));
            credito.setMontoAprobado(extractValue(xml, "a:MontoAprobado", Double.class));
            credito.setNumeroCuotas(extractValue(xml, "a:NumeroCuotas", Integer.class));
            credito.setTasaInteres(extractValue(xml, "a:TasaInteres", Double.class));
            credito.setFechaAprobacion(extractValue(xml, "a:FechaAprobacion", String.class));
            credito.setActivo(extractValue(xml, "a:Activo", Boolean.class));
        } catch (Exception e) {
            logger.severe("Error al parsear crédito: " + e.getMessage());
        }
        
        return credito;
    }
    
    /**
     * Parsea las amortizaciones desde XML
     */
    private List<AmortizacionCredito> parseAmortizacionesFromXml(String xml) {
        List<AmortizacionCredito> amortizaciones = new ArrayList<>();
        
        try {
            Pattern amortPattern = Pattern.compile(
                "<NumeroCuota>(\\d+)</NumeroCuota>.*?" +
                "<ValorCuota>([\\d.]+)</ValorCuota>.*?" +
                "<CapitalPagado>([\\d.]+)</CapitalPagado>.*?" +
                "<InteresPagado>([\\d.]+)</InteresPagado>.*?" +
                "<SaldoPendiente>([\\d.]+)</SaldoPendiente>",
                Pattern.DOTALL);
                
            Matcher matcher = amortPattern.matcher(xml);
            
            while (matcher.find()) {
                AmortizacionCredito amort = new AmortizacionCredito();
                amort.setNumeroCuota(Integer.parseInt(matcher.group(1)));
                amort.setMontoCuota(Double.parseDouble(matcher.group(2)));
                amort.setCapital(Double.parseDouble(matcher.group(3)));
                amort.setInteres(Double.parseDouble(matcher.group(4)));
                amort.setSaldoPendiente(Double.parseDouble(matcher.group(5)));
                
                amortizaciones.add(amort);
            }
        } catch (Exception e) {
            logger.severe("Error al parsear amortizaciones: " + e.getMessage());
        }
        
        return amortizaciones;
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