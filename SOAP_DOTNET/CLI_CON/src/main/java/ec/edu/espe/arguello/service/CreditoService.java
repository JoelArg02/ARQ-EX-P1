package ec.edu.espe.arguello.service;

import ec.edu.espe.arguello.config.ServiceConfig;
import ec.edu.espe.arguello.model.CreditoBanco;
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

public class CreditoService {
    private final HttpClient httpClient;

    public CreditoService() {
        this.httpClient = HttpClients.createDefault();
    }

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

            String response = sendSoapRequest(ServiceConfig.CREDITO_BANCO_SERVICE_URL, soapRequest,
                "http://tempuri.org/ICreditoBancoController/GetAllCreditosBanco");

            return parseCreditosFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener créditos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

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

            String response = sendSoapRequest(ServiceConfig.CREDITO_BANCO_SERVICE_URL, soapRequest,
                "http://tempuri.org/ICreditoBancoController/GetCreditosBancoByClienteBancoId");

            return parseCreditosFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener créditos del cliente: " + e.getMessage());
            return new ArrayList<>();
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

    private List<CreditoBanco> parseCreditosFromXml(String xml) {
        List<CreditoBanco> creditos = new ArrayList<>();
        try {
            Pattern arrayPattern = Pattern.compile("<a:CreditoBanco>(.*?)</a:CreditoBanco>", Pattern.DOTALL);
            Matcher arrayMatcher = arrayPattern.matcher(xml);

            while (arrayMatcher.find()) {
                String creditoXml = arrayMatcher.group(1);
                try {
                    CreditoBanco credito = new CreditoBanco();
                    credito.setId(Integer.parseInt(extractValue(creditoXml, "a:Id")));
                    credito.setClienteBancoId(Integer.parseInt(extractValue(creditoXml, "a:ClienteBancoId")));
                    credito.setNumeroCreditoBanco(extractValue(creditoXml, "a:NumeroCreditoBanco"));
                    credito.setMonto(Double.parseDouble(extractValue(creditoXml, "a:Monto")));
                    credito.setTasaInteres(Double.parseDouble(extractValue(creditoXml, "a:TasaInteres")));
                    credito.setPlazoCuotas(Integer.parseInt(extractValue(creditoXml, "a:PlazoCuotas")));
                    credito.setEstado(extractValue(creditoXml, "a:Estado"));
                    creditos.add(credito);
                } catch (Exception e) {
                    // Skip invalid credito
                }
            }
        } catch (Exception e) {
            System.err.println("Error al parsear créditos: " + e.getMessage());
        }
        return creditos;
    }

    private String extractValue(String xml, String tagName) {
        Pattern pattern = Pattern.compile("<" + tagName + ">(.*?)</" + tagName + ">");
        Matcher matcher = pattern.matcher(xml);
        return matcher.find() ? matcher.group(1) : "";
    }
}
