package ec.edu.espe.arguello.service;

import ec.edu.espe.arguello.config.ServiceConfig;
import ec.edu.espe.arguello.model.ClienteBanco;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClienteService {
    private final HttpClient httpClient;

    public ClienteService() {
        this.httpClient = HttpClients.createDefault();
    }

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

            String response = sendSoapRequest(ServiceConfig.CLIENTE_BANCO_SERVICE_URL, soapRequest,
                "http://tempuri.org/IClienteBancoController/GetAllClientesBanco");

            return parseClientesFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener clientes: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public ClienteBanco getClienteById(int id) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetClienteBancoById>
                            <tem:id>%d</tem:id>
                        </tem:GetClienteBancoById>
                    </soap:Body>
                </soap:Envelope>
                """, id);

            String response = sendSoapRequest(ServiceConfig.CLIENTE_BANCO_SERVICE_URL, soapRequest,
                "http://tempuri.org/IClienteBancoController/GetClienteBancoById");

            return parseClienteFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener cliente: " + e.getMessage());
            return null;
        }
    }

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

    private List<ClienteBanco> parseClientesFromXml(String xml) {
        List<ClienteBanco> clientes = new ArrayList<>();
        try {
            Pattern arrayPattern = Pattern.compile("<a:ClienteBanco>(.*?)</a:ClienteBanco>", Pattern.DOTALL);
            Matcher arrayMatcher = arrayPattern.matcher(xml);

            while (arrayMatcher.find()) {
                String clienteXml = arrayMatcher.group(1);
                try {
                    ClienteBanco cliente = new ClienteBanco();
                    cliente.setId(Integer.parseInt(extractValue(clienteXml, "a:Id")));
                    cliente.setCedula(extractValue(clienteXml, "a:Cedula"));
                    cliente.setNombreCompleto(extractValue(clienteXml, "a:NombreCompleto"));
                    cliente.setEstadoCivil(extractValue(clienteXml, "a:EstadoCivil"));
                    cliente.setFechaNacimiento(extractValue(clienteXml, "a:FechaNacimiento"));
                    String creditoActivo = extractValue(clienteXml, "a:TieneCreditoActivo");
                    cliente.setTieneCreditoActivo("true".equalsIgnoreCase(creditoActivo));
                    clientes.add(cliente);
                } catch (Exception e) {
                    // Skip invalid cliente
                }
            }
        } catch (Exception e) {
            System.err.println("Error al parsear clientes: " + e.getMessage());
        }
        return clientes;
    }

    private ClienteBanco parseClienteFromXml(String xml) {
        ClienteBanco cliente = new ClienteBanco();
        try {
            cliente.setId(Integer.parseInt(extractValue(xml, "a:Id")));
            cliente.setCedula(extractValue(xml, "a:Cedula"));
            cliente.setNombreCompleto(extractValue(xml, "a:NombreCompleto"));
            cliente.setEstadoCivil(extractValue(xml, "a:EstadoCivil"));
            cliente.setFechaNacimiento(extractValue(xml, "a:FechaNacimiento"));
            String creditoActivo = extractValue(xml, "a:TieneCreditoActivo");
            cliente.setTieneCreditoActivo("true".equalsIgnoreCase(creditoActivo));
        } catch (Exception e) {
            return null;
        }
        return cliente;
    }

    private String extractValue(String xml, String tagName) {
        Pattern pattern = Pattern.compile("<" + tagName + ">(.*?)</" + tagName + ">");
        Matcher matcher = pattern.matcher(xml);
        return matcher.find() ? matcher.group(1) : "";
    }
}
