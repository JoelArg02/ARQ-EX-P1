package ec.edu.arguello.service;

import ec.edu.arguello.config.ServiceConfig;
import ec.edu.arguello.model.Cuenta;
import ec.edu.arguello.util.SoapClient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CuentaService {
    private final SoapClient soapClient;

    public CuentaService() {
        this.soapClient = new SoapClient();
    }

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

            String response = soapClient.sendSoapRequest(
                ServiceConfig.CUENTA_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/ICuentaController/GetAllCuentas"
            );

            return parseCuentasFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener cuentas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Cuenta getCuentaById(int id) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetCuentaById>
                            <tem:id>%d</tem:id>
                        </tem:GetCuentaById>
                    </soap:Body>
                </soap:Envelope>
                """, id);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.CUENTA_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/ICuentaController/GetCuentaById"
            );

            return parseCuentaFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener cuenta por ID: " + e.getMessage());
            return null;
        }
    }

    public Cuenta getCuentaByNumeroCuenta(String numeroCuenta) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetCuentaByNumeroCuenta>
                            <tem:numeroCuenta>%s</tem:numeroCuenta>
                        </tem:GetCuentaByNumeroCuenta>
                    </soap:Body>
                </soap:Envelope>
                """, numeroCuenta);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.CUENTA_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/ICuentaController/GetCuentaByNumeroCuenta"
            );

            return parseCuentaFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener cuenta por número: " + e.getMessage());
            return null;
        }
    }

    public List<Cuenta> getCuentasByClienteBancoId(int clienteBancoId) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetCuentasByClienteBancoId>
                            <tem:clienteBancoId>%d</tem:clienteBancoId>
                        </tem:GetCuentasByClienteBancoId>
                    </soap:Body>
                </soap:Envelope>
                """, clienteBancoId);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.CUENTA_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/ICuentaController/GetCuentasByClienteBancoId"
            );

            return parseCuentasFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener cuentas por cliente: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Cuenta createCuenta(int clienteBancoId, String numeroCuenta, double saldo, int tipoCuenta) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
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
                """, clienteBancoId, numeroCuenta, saldo, tipoCuenta);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.CUENTA_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/ICuentaController/CreateCuenta"
            );

            return parseCuentaFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al crear cuenta: " + e.getMessage());
            return null;
        }
    }

    public Cuenta updateCuenta(int id, String numeroCuenta, double saldo, int tipoCuenta) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:UpdateCuenta>
                            <tem:id>%d</tem:id>
                            <tem:numeroCuenta>%s</tem:numeroCuenta>
                            <tem:saldo>%.2f</tem:saldo>
                            <tem:tipoCuenta>%d</tem:tipoCuenta>
                        </tem:UpdateCuenta>
                    </soap:Body>
                </soap:Envelope>
                """, id, numeroCuenta, saldo, tipoCuenta);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.CUENTA_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/ICuentaController/UpdateCuenta"
            );

            return parseCuentaFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al actualizar cuenta: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteCuenta(int id) {
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
                """, id);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.CUENTA_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/ICuentaController/DeleteCuenta"
            );

            return response.contains("<DeleteCuentaResult>true</DeleteCuentaResult>");
        } catch (Exception e) {
            System.err.println("Error al eliminar cuenta: " + e.getMessage());
            return false;
        }
    }

    private List<Cuenta> parseCuentasFromXml(String xml) {
        List<Cuenta> cuentas = new ArrayList<>();

        try {
            Pattern arrayPattern = Pattern.compile("<a:Cuenta>(.*?)</a:Cuenta>", Pattern.DOTALL);
            Matcher arrayMatcher = arrayPattern.matcher(xml);

            while (arrayMatcher.find()) {
                String cuentaXml = arrayMatcher.group(1);

                try {
                    Cuenta cuenta = new Cuenta();
                    cuenta.setId(soapClient.extractValue(cuentaXml, "a:Id", Integer.class));
                    cuenta.setClienteBancoId(soapClient.extractValue(cuentaXml, "a:ClienteBancoId", Integer.class));
                    cuenta.setNumeroCuenta(soapClient.extractValue(cuentaXml, "a:NumeroCuenta", String.class));
                    cuenta.setSaldo(soapClient.extractValue(cuentaXml, "a:Saldo", Double.class));
                    cuenta.setTipoCuenta(soapClient.extractValue(cuentaXml, "a:TipoCuenta", String.class));

                    cuentas.add(cuenta);
                } catch (Exception e) {
                    System.err.println("Error al parsear cuenta individual: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al parsear cuentas: " + e.getMessage());
        }

        return cuentas;
    }

    private Cuenta parseCuentaFromXml(String xml) {
        try {
            if (xml.contains("i:nil=\"true\"")) {
                return null;
            }

            Cuenta cuenta = new Cuenta();
            cuenta.setId(soapClient.extractValue(xml, "a:Id", Integer.class));
            cuenta.setClienteBancoId(soapClient.extractValue(xml, "a:ClienteBancoId", Integer.class));
            cuenta.setNumeroCuenta(soapClient.extractValue(xml, "a:NumeroCuenta", String.class));
            cuenta.setSaldo(soapClient.extractValue(xml, "a:Saldo", Double.class));
            cuenta.setTipoCuenta(soapClient.extractValue(xml, "a:TipoCuenta", String.class));

            return cuenta;
        } catch (Exception e) {
            System.err.println("Error al parsear cuenta: " + e.getMessage());
            return null;
        }
    }

    public void close() {
        soapClient.close();
    }
}
