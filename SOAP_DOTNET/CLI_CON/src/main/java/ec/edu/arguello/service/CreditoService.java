package ec.edu.arguello.service;

import ec.edu.arguello.config.ServiceConfig;
import ec.edu.arguello.model.CreditoBanco;
import ec.edu.arguello.util.SoapClient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreditoService {
    private final SoapClient soapClient;

    public CreditoService() {
        this.soapClient = new SoapClient();
    }

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

            String response = soapClient.sendSoapRequest(
                ServiceConfig.CREDITO_BANCO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/ICreditoBancoController/GetAllCreditosBanco"
            );

            return parseCreditosFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener créditos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Parsea la respuesta XML para extraer la lista de créditos
     */
    private List<CreditoBanco> parseCreditosFromXml(String xml) {
        List<CreditoBanco> creditos = new ArrayList<>();

        try {
            Pattern arrayPattern = Pattern.compile("<a:CreditoBanco>(.*?)</a:CreditoBanco>", Pattern.DOTALL);
            Matcher arrayMatcher = arrayPattern.matcher(xml);

            while (arrayMatcher.find()) {
                String creditoXml = arrayMatcher.group(1);

                try {
                    CreditoBanco credito = new CreditoBanco();
                    credito.setId(soapClient.extractValue(creditoXml, "a:Id", Integer.class));
                    credito.setClienteBancoId(soapClient.extractValue(creditoXml, "a:ClienteBancoId", Integer.class));
                    credito.setMonto(soapClient.extractValue(creditoXml, "a:Monto", Double.class));
                    credito.setTasaInteres(soapClient.extractValue(creditoXml, "a:TasaInteres", Double.class));
                    credito.setPlazoMeses(soapClient.extractValue(creditoXml, "a:PlazoMeses", Integer.class));
                    credito.setEstado(soapClient.extractValue(creditoXml, "a:Estado", String.class));
                    credito.setFechaAprobacion(soapClient.extractValue(creditoXml, "a:FechaAprobacion", String.class));

                    creditos.add(credito);
                } catch (Exception e) {
                    System.err.println("Error al parsear crédito individual: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al parsear créditos: " + e.getMessage());
        }

        return creditos;
    }

    public void close() {
        soapClient.close();
    }
}
