package ec.edu.arguello.service;

import ec.edu.arguello.config.ServiceConfig;
import ec.edu.arguello.model.AmortizacionCredito;
import ec.edu.arguello.util.SoapClient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmortizacionService {
    private final SoapClient soapClient;

    public AmortizacionService() {
        this.soapClient = new SoapClient();
    }

    public List<AmortizacionCredito> getAllAmortizaciones() {
        try {
            String soapRequest = """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetAllAmortizaciones/>
                    </soap:Body>
                </soap:Envelope>
                """;

            String response = soapClient.sendSoapRequest(
                ServiceConfig.AMORTIZACION_CREDITO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IAmortizacionCreditoController/GetAllAmortizaciones"
            );

            return parseAmortizacionesFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener amortizaciones: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public AmortizacionCredito getAmortizacionById(int id) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetAmortizacionById>
                            <tem:id>%d</tem:id>
                        </tem:GetAmortizacionById>
                    </soap:Body>
                </soap:Envelope>
                """, id);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.AMORTIZACION_CREDITO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IAmortizacionCreditoController/GetAmortizacionById"
            );

            return parseAmortizacionFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener amortización por ID: " + e.getMessage());
            return null;
        }
    }

    public List<AmortizacionCredito> getAmortizacionesByCreditoBancoId(int creditoBancoId) {
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
                """, creditoBancoId);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.AMORTIZACION_CREDITO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IAmortizacionCreditoController/GetAmortizacionesByCreditoBancoId"
            );

            return parseAmortizacionesFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener amortizaciones por crédito: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public AmortizacionCredito createAmortizacion(int creditoBancoId, int numeroCuota, double valorCuota, 
                                                   double interesPagado, double capitalPagado, double saldoPendiente) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:CreateAmortizacion>
                            <tem:creditoBancoId>%d</tem:creditoBancoId>
                            <tem:numeroCuota>%d</tem:numeroCuota>
                            <tem:valorCuota>%.2f</tem:valorCuota>
                            <tem:interesPagado>%.2f</tem:interesPagado>
                            <tem:capitalPagado>%.2f</tem:capitalPagado>
                            <tem:saldoPendiente>%.2f</tem:saldoPendiente>
                        </tem:CreateAmortizacion>
                    </soap:Body>
                </soap:Envelope>
                """, creditoBancoId, numeroCuota, valorCuota, interesPagado, capitalPagado, saldoPendiente);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.AMORTIZACION_CREDITO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IAmortizacionCreditoController/CreateAmortizacion"
            );

            return parseAmortizacionFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al crear amortización: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteAmortizacion(int id) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:DeleteAmortizacion>
                            <tem:id>%d</tem:id>
                        </tem:DeleteAmortizacion>
                    </soap:Body>
                </soap:Envelope>
                """, id);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.AMORTIZACION_CREDITO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IAmortizacionCreditoController/DeleteAmortizacion"
            );

            return response.contains("<DeleteAmortizacionResult>true</DeleteAmortizacionResult>");
        } catch (Exception e) {
            System.err.println("Error al eliminar amortización: " + e.getMessage());
            return false;
        }
    }

    private List<AmortizacionCredito> parseAmortizacionesFromXml(String xml) {
        List<AmortizacionCredito> amortizaciones = new ArrayList<>();

        try {
            Pattern arrayPattern = Pattern.compile("<a:AmortizacionCredito>(.*?)</a:AmortizacionCredito>", Pattern.DOTALL);
            Matcher arrayMatcher = arrayPattern.matcher(xml);

            while (arrayMatcher.find()) {
                String amortizacionXml = arrayMatcher.group(1);

                try {
                    AmortizacionCredito amortizacion = new AmortizacionCredito();
                    amortizacion.setId(soapClient.extractValue(amortizacionXml, "a:Id", Integer.class));
                    amortizacion.setCreditoBancoId(soapClient.extractValue(amortizacionXml, "a:CreditoBancoId", Integer.class));
                    amortizacion.setNumeroCuota(soapClient.extractValue(amortizacionXml, "a:NumeroCuota", Integer.class));
                    amortizacion.setValorCuota(soapClient.extractValue(amortizacionXml, "a:ValorCuota", Double.class));
                    amortizacion.setInteresPagado(soapClient.extractValue(amortizacionXml, "a:InteresPagado", Double.class));
                    amortizacion.setCapitalPagado(soapClient.extractValue(amortizacionXml, "a:CapitalPagado", Double.class));
                    amortizacion.setSaldoPendiente(soapClient.extractValue(amortizacionXml, "a:SaldoPendiente", Double.class));
                    amortizacion.setFechaPago(soapClient.extractValue(amortizacionXml, "a:FechaPago", String.class));

                    amortizaciones.add(amortizacion);
                } catch (Exception e) {
                    System.err.println("Error al parsear amortización individual: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al parsear amortizaciones: " + e.getMessage());
        }

        return amortizaciones;
    }

    private AmortizacionCredito parseAmortizacionFromXml(String xml) {
        try {
            if (xml.contains("i:nil=\"true\"")) {
                return null;
            }

            AmortizacionCredito amortizacion = new AmortizacionCredito();
            amortizacion.setId(soapClient.extractValue(xml, "a:Id", Integer.class));
            amortizacion.setCreditoBancoId(soapClient.extractValue(xml, "a:CreditoBancoId", Integer.class));
            amortizacion.setNumeroCuota(soapClient.extractValue(xml, "a:NumeroCuota", Integer.class));
            amortizacion.setValorCuota(soapClient.extractValue(xml, "a:ValorCuota", Double.class));
            amortizacion.setInteresPagado(soapClient.extractValue(xml, "a:InteresPagado", Double.class));
            amortizacion.setCapitalPagado(soapClient.extractValue(xml, "a:CapitalPagado", Double.class));
            amortizacion.setSaldoPendiente(soapClient.extractValue(xml, "a:SaldoPendiente", Double.class));
            amortizacion.setFechaPago(soapClient.extractValue(xml, "a:FechaPago", String.class));

            return amortizacion;
        } catch (Exception e) {
            System.err.println("Error al parsear amortización: " + e.getMessage());
            return null;
        }
    }

    public void close() {
        soapClient.close();
    }
}
