package ec.edu.espe.arguello.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ec.edu.espe.arguello.config.AppConfig;
import ec.edu.espe.arguello.model.*;
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
 * Repository para operaciones SOAP de Cliente Banco
 */
@ApplicationScoped 
public class ClienteBancoRepository {
    
    private static final Logger logger = Logger.getLogger(ClienteBancoRepository.class.getName());
    
    @Inject
    private AppConfig config;
    
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    
    /**
     * Obtiene todos los clientes del banco
     */
    public List<ClienteBanco> getAllClientes() {
        try {
            String soapRequest = """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetAllClientesBanco/>
                    </soap:Body>
                </soap:Envelope>
                """;
                
            String response = sendSoapRequest(config.getClienteServiceUrl(), soapRequest, 
                "http://tempuri.org/IClienteBancoController/GetAllClientesBanco");
            
            return parseClientesFromXml(response);
        } catch (Exception e) {
            logger.severe("Error al obtener clientes: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Crea un nuevo cliente
     */
    public ClienteBanco createCliente(ClienteBanco cliente) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
                               xmlns:tem="http://tempuri.org/"
                               xmlns:api="http://schemas.datacontract.org/2004/07/API_BANCO.Application.DTOs.Clientes">
                    <soap:Header/>
                    <soap:Body>
                        <tem:CreateClienteBanco>
                            <tem:dto>
                                <api:Cedula>%s</api:Cedula>
                                <api:EstadoCivil>%s</api:EstadoCivil>
                                <api:FechaNacimiento>%s</api:FechaNacimiento>
                                <api:NombreCompleto>%s</api:NombreCompleto>
                            </tem:dto>
                        </tem:CreateClienteBanco>
                    </soap:Body>
                </soap:Envelope>
                """, cliente.getCedula(), cliente.getEstadoCivil(), 
                cliente.getFechaNacimiento(), cliente.getNombreCompleto());
                
            String response = sendSoapRequest(config.getClienteServiceUrl(), soapRequest,
                "http://tempuri.org/IClienteBancoController/CreateClienteBanco");
            
            return parseClienteFromXml(response);
        } catch (Exception e) {
            logger.severe("Error al crear cliente: " + e.getMessage());
            return cliente;
        }
    }
    
    /**
     * Actualiza un cliente existente
     */
    public ClienteBanco updateCliente(ClienteBanco cliente) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:UpdateClienteBanco>
                            <tem:id>%d</tem:id>
                            <tem:clienteBanco>
                                <tem:Id>%d</tem:Id>
                                <tem:Cedula>%s</tem:Cedula>
                                <tem:NombreCompleto>%s</tem:NombreCompleto>
                                <tem:EstadoCivil>%s</tem:EstadoCivil>
                                <tem:FechaNacimiento>%s</tem:FechaNacimiento>
                                <tem:TieneCreditoActivo>%s</tem:TieneCreditoActivo>
                            </tem:clienteBanco>
                        </tem:UpdateClienteBanco>
                    </soap:Body>
                </soap:Envelope>
                """, cliente.getId(), cliente.getId(), cliente.getCedula(), cliente.getNombreCompleto(),
                cliente.getEstadoCivil(), cliente.getFechaNacimiento(), cliente.isTieneCreditoActivo());
                
            String response = sendSoapRequest(config.getClienteServiceUrl(), soapRequest,
                "http://tempuri.org/IClienteBancoController/UpdateClienteBanco");
            
            return parseClienteFromXml(response);
        } catch (Exception e) {
            logger.severe("Error al actualizar cliente: " + e.getMessage());
            return cliente;
        }
    }
    
    /**
     * Elimina un cliente
     */
    public boolean deleteCliente(int clienteId) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:DeleteClienteBanco>
                            <tem:id>%d</tem:id>
                        </tem:DeleteClienteBanco>
                    </soap:Body>
                </soap:Envelope>
                """, clienteId);
                
            sendSoapRequest(config.getClienteServiceUrl(), soapRequest,
                "http://tempuri.org/IClienteBancoController/DeleteClienteBanco");
            
            return true;
        } catch (Exception e) {
            logger.severe("Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica la elegibilidad de un cliente para crédito
     */
    public ElegibilidadCliente verificarElegibilidadCliente(String cedula) {
        try {
            String soapRequest = String.format("""
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                                  xmlns:tem="http://tempuri.org/">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <tem:VerificarElegibilidadCliente>
                         <tem:cedula>%s</tem:cedula>
                      </tem:VerificarElegibilidadCliente>
                   </soapenv:Body>
                </soapenv:Envelope>
                """, cedula);
                
            String response = sendSoapRequest(config.getClienteServiceUrl(), soapRequest,
                "http://tempuri.org/IClienteBancoController/VerificarElegibilidadCliente");
            
            return parseElegibilidadFromXml(response);
        } catch (Exception e) {
            logger.severe("Error al verificar elegibilidad: " + e.getMessage());
            return new ElegibilidadCliente();
        }
    }
    
    /**
     * Calcula el monto máximo de crédito para un cliente
     */
    public MontoMaximoCredito calcularMontoMaximoCredito(String cedula) {
        try {
            String soapRequest = String.format("""
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                                  xmlns:tem="http://tempuri.org/">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <tem:CalcularMontoMaximoCredito>
                         <tem:cedula>%s</tem:cedula>
                      </tem:CalcularMontoMaximoCredito>
                   </soapenv:Body>
                </soapenv:Envelope>
                """, cedula);
                
            String response = sendSoapRequest(config.getClienteServiceUrl(), soapRequest,
                "http://tempuri.org/IClienteBancoController/CalcularMontoMaximoCredito");
            
            return parseMontoMaximoFromXml(response);
        } catch (Exception e) {
            logger.severe("Error al calcular monto máximo: " + e.getMessage());
            return new MontoMaximoCredito();
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
     * Parsea la respuesta XML para extraer la lista de clientes
     */
    private List<ClienteBanco> parseClientesFromXml(String xml) {
        List<ClienteBanco> clientes = new ArrayList<>();
        
        try {
            logger.info("XML recibido para parsear: " + xml);
            
            // Buscar todos los elementos ClienteBanco
            Pattern arrayPattern = Pattern.compile("<a:ClienteBanco>(.*?)</a:ClienteBanco>", Pattern.DOTALL);
            Matcher arrayMatcher = arrayPattern.matcher(xml);
            
            int contador = 0;
            while (arrayMatcher.find()) {
                contador++;
                String clienteXml = arrayMatcher.group(1);
                logger.info("Procesando cliente #" + contador);
                
                try {
                    ClienteBanco cliente = new ClienteBanco();
                    cliente.setId(Integer.parseInt(extractValueRegex(clienteXml, "<a:Id>(.*?)</a:Id>")));
                    cliente.setCedula(extractValueRegex(clienteXml, "<a:Cedula>(.*?)</a:Cedula>"));
                    cliente.setNombreCompleto(extractValueRegex(clienteXml, "<a:NombreCompleto>(.*?)</a:NombreCompleto>"));
                    cliente.setEstadoCivil(extractValueRegex(clienteXml, "<a:EstadoCivil>(.*?)</a:EstadoCivil>"));
                    cliente.setFechaNacimiento(extractValueRegex(clienteXml, "<a:FechaNacimiento>(.*?)</a:FechaNacimiento>"));
                    String creditoActivo = extractValueRegex(clienteXml, "<a:TieneCreditoActivo>(.*?)</a:TieneCreditoActivo>");
                    cliente.setTieneCreditoActivo("true".equalsIgnoreCase(creditoActivo));
                    
                    clientes.add(cliente);
                    logger.info("Cliente agregado: " + cliente.getNombreCompleto() + " (ID: " + cliente.getId() + ")");
                } catch (Exception e) {
                    logger.severe("Error al parsear cliente individual: " + e.getMessage());
                }
            }
            
            logger.info("Total de clientes parseados: " + clientes.size());
        } catch (Exception e) {
            logger.severe("Error al parsear clientes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return clientes;
    }
    
    /**
     * Parsea un cliente individual desde XML
     */
    private ClienteBanco parseClienteFromXml(String xml) {
        ClienteBanco cliente = new ClienteBanco();
        
        try {
            cliente.setId(extractValue(xml, "a:Id", Integer.class));
            cliente.setCedula(extractValue(xml, "a:Cedula", String.class));
            cliente.setNombreCompleto(extractValue(xml, "a:NombreCompleto", String.class));
            cliente.setEstadoCivil(extractValue(xml, "a:EstadoCivil", String.class));
            cliente.setFechaNacimiento(extractValue(xml, "a:FechaNacimiento", String.class));
            cliente.setTieneCreditoActivo(extractValue(xml, "a:TieneCreditoActivo", Boolean.class));
        } catch (Exception e) {
            logger.severe("Error al parsear cliente: " + e.getMessage());
        }
        
        return cliente;
    }
    
    /**
     * Parsea la elegibilidad desde XML
     */
    private ElegibilidadCliente parseElegibilidadFromXml(String xml) {
        try {
            boolean cumpleEdad = xml.contains("<a:CumpleEdadEstadoCivil>true</a:CumpleEdadEstadoCivil>");
            boolean esCliente = xml.contains("<a:EsCliente>true</a:EsCliente>");
            boolean tieneDeposito = xml.contains("<a:TieneDepositoUltimoMes>true</a:TieneDepositoUltimoMes>");
            boolean noTieneCredito = xml.contains("<a:NoTieneCreditoActivo>true</a:NoTieneCreditoActivo>");
            boolean esElegible = xml.contains("<a:EsElegible>true</a:EsElegible>");
            String mensaje = extractValueRegex(xml, "<a:Mensaje>(.*?)</a:Mensaje>")
                .replace("&#xF1;", "ñ");
            
            return new ElegibilidadCliente(esCliente, cumpleEdad, tieneDeposito, 
                noTieneCredito, esElegible, mensaje);
        } catch (Exception e) {
            logger.severe("Error al parsear elegibilidad: " + e.getMessage());
            return new ElegibilidadCliente();
        }
    }
    
    /**
     * Parsea el monto máximo desde XML
     */
    private MontoMaximoCredito parseMontoMaximoFromXml(String xml) {
        try {
            double monto = Double.parseDouble(extractValueRegex(xml, "<a:MontoMaximoCredito>(.*?)</a:MontoMaximoCredito>"));
            double depositos = Double.parseDouble(extractValueRegex(xml, "<a:PromedioDepositos>(.*?)</a:PromedioDepositos>"));
            double retiros = Double.parseDouble(extractValueRegex(xml, "<a:PromedioRetiros>(.*?)</a:PromedioRetiros>"));
            String mensaje = extractValueRegex(xml, "<a:Mensaje>(.*?)</a:Mensaje>")
                .replace("&#xF1;", "ñ");
            
            return new MontoMaximoCredito(monto, depositos, retiros, mensaje);
        } catch (Exception e) {
            logger.severe("Error al parsear monto máximo: " + e.getMessage());
            return new MontoMaximoCredito();
        }
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