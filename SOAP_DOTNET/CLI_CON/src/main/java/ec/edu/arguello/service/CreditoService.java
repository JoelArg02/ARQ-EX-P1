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

    public CreditoBanco getCreditoBancoById(int id) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetCreditoBancoById>
                            <tem:id>%d</tem:id>
                        </tem:GetCreditoBancoById>
                    </soap:Body>
                </soap:Envelope>
                """, id);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.CREDITO_BANCO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/ICreditoBancoController/GetCreditoBancoById"
            );

            return parseCreditoFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener crédito por ID: " + e.getMessage());
            return null;
        }
    }

    public List<CreditoBanco> getCreditosBancoByClienteBancoId(int clienteBancoId) {
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
                """, clienteBancoId);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.CREDITO_BANCO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/ICreditoBancoController/GetCreditosBancoByClienteBancoId"
            );

            return parseCreditosFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener créditos por cliente: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public CreditoBanco getCreditoBancoActivoByClienteBancoId(int clienteBancoId) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetCreditoBancoActivoByClienteBancoId>
                            <tem:clienteBancoId>%d</tem:clienteBancoId>
                        </tem:GetCreditoBancoActivoByClienteBancoId>
                    </soap:Body>
                </soap:Envelope>
                """, clienteBancoId);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.CREDITO_BANCO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/ICreditoBancoController/GetCreditoBancoActivoByClienteBancoId"
            );

            return parseCreditoFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener crédito activo por cliente: " + e.getMessage());
            return null;
        }
    }

    public CreditoBanco createCreditoBanco(int clienteBancoId, double montoAprobado, int numeroCuotas, double tasaInteres) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:CreateCreditoBanco>
                            <tem:clienteBancoId>%d</tem:clienteBancoId>
                            <tem:montoAprobado>%.2f</tem:montoAprobado>
                            <tem:numeroCuotas>%d</tem:numeroCuotas>
                            <tem:tasaInteres>%.4f</tem:tasaInteres>
                        </tem:CreateCreditoBanco>
                    </soap:Body>
                </soap:Envelope>
                """, clienteBancoId, montoAprobado, numeroCuotas, tasaInteres);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.CREDITO_BANCO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/ICreditoBancoController/CreateCreditoBanco"
            );

            return parseCreditoFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al crear crédito: " + e.getMessage());
            return null;
        }
    }

    public CreditoBanco updateCreditoBanco(int id, double montoAprobado, int numeroCuotas, double tasaInteres, boolean activo) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:UpdateCreditoBanco>
                            <tem:id>%d</tem:id>
                            <tem:montoAprobado>%.2f</tem:montoAprobado>
                            <tem:numeroCuotas>%d</tem:numeroCuotas>
                            <tem:tasaInteres>%.4f</tem:tasaInteres>
                            <tem:activo>%s</tem:activo>
                        </tem:UpdateCreditoBanco>
                    </soap:Body>
                </soap:Envelope>
                """, id, montoAprobado, numeroCuotas, tasaInteres, activo);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.CREDITO_BANCO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/ICreditoBancoController/UpdateCreditoBanco"
            );

            return parseCreditoFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al actualizar crédito: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteCreditoBanco(int id) {
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
                """, id);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.CREDITO_BANCO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/ICreditoBancoController/DeleteCreditoBanco"
            );

            return response.contains("<DeleteCreditoBancoResult>true</DeleteCreditoBancoResult>");
        } catch (Exception e) {
            System.err.println("Error al eliminar crédito: " + e.getMessage());
            return false;
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
                    credito.setMonto(soapClient.extractValue(creditoXml, "a:MontoAprobado", Double.class));
                    credito.setTasaInteres(soapClient.extractValue(creditoXml, "a:TasaInteres", Double.class));
                    credito.setPlazoMeses(soapClient.extractValue(creditoXml, "a:NumeroCuotas", Integer.class));
                    
                    String activo = soapClient.extractValue(creditoXml, "a:Activo", String.class);
                    credito.setEstado("true".equals(activo) ? "Activo" : "Inactivo");
                    
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

    private CreditoBanco parseCreditoFromXml(String xml) {
        try {
            if (xml.contains("i:nil=\"true\"")) {
                return null;
            }

            CreditoBanco credito = new CreditoBanco();
            credito.setId(soapClient.extractValue(xml, "a:Id", Integer.class));
            credito.setClienteBancoId(soapClient.extractValue(xml, "a:ClienteBancoId", Integer.class));
            credito.setMonto(soapClient.extractValue(xml, "a:MontoAprobado", Double.class));
            credito.setTasaInteres(soapClient.extractValue(xml, "a:TasaInteres", Double.class));
            credito.setPlazoMeses(soapClient.extractValue(xml, "a:NumeroCuotas", Integer.class));
            
            String activo = soapClient.extractValue(xml, "a:Activo", String.class);
            credito.setEstado("true".equals(activo) ? "Activo" : "Inactivo");
            
            credito.setFechaAprobacion(soapClient.extractValue(xml, "a:FechaAprobacion", String.class));

            return credito;
        } catch (Exception e) {
            System.err.println("Error al parsear crédito: " + e.getMessage());
            return null;
        }
    }

    public void close() {
        soapClient.close();
    }
}
