package ec.edu.espe.arguello.service;

import ec.edu.espe.arguello.config.ServiceConfig;
import ec.edu.espe.arguello.model.Cuenta;
import ec.edu.espe.arguello.model.Movimiento;
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

public class CuentaService {
    private final HttpClient httpClient;

    public CuentaService() {
        this.httpClient = HttpClients.createDefault();
    }

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

            String response = sendSoapRequest(ServiceConfig.CUENTA_SERVICE_URL, soapRequest,
                "http://tempuri.org/ICuentaController/GetAllCuentas");

            return parseCuentasFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener cuentas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Cuenta> getCuentasByClienteId(int clienteId) {
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
                """, clienteId);

            String response = sendSoapRequest(ServiceConfig.CUENTA_SERVICE_URL, soapRequest,
                "http://tempuri.org/ICuentaController/GetCuentasByClienteBancoId");

            return parseCuentasFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener cuentas del cliente: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Movimiento realizarMovimiento(int cuentaId, int tipo, double monto) {
        try {
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
                """, cuentaId, tipo, monto);

            String response = sendSoapRequest(ServiceConfig.MOVIMIENTO_SERVICE_URL, soapRequest,
                "http://tempuri.org/IMovimientoController/CreateMovimiento");

            return parseMovimientoFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al realizar movimiento: " + e.getMessage());
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

    private List<Cuenta> parseCuentasFromXml(String xml) {
        List<Cuenta> cuentas = new ArrayList<>();
        try {
            Pattern arrayPattern = Pattern.compile("<a:Cuenta>(.*?)</a:Cuenta>", Pattern.DOTALL);
            Matcher arrayMatcher = arrayPattern.matcher(xml);

            while (arrayMatcher.find()) {
                String cuentaXml = arrayMatcher.group(1);
                try {
                    Cuenta cuenta = new Cuenta();
                    cuenta.setId(Integer.parseInt(extractValue(cuentaXml, "a:Id")));
                    cuenta.setClienteBancoId(Integer.parseInt(extractValue(cuentaXml, "a:ClienteBancoId")));
                    cuenta.setNumeroCuenta(extractValue(cuentaXml, "a:NumeroCuenta"));
                    cuenta.setSaldo(Double.parseDouble(extractValue(cuentaXml, "a:Saldo")));
                    cuenta.setTipoCuenta(extractValue(cuentaXml, "a:TipoCuenta"));
                    cuentas.add(cuenta);
                } catch (Exception e) {
                    // Skip invalid cuenta
                }
            }
        } catch (Exception e) {
            System.err.println("Error al parsear cuentas: " + e.getMessage());
        }
        return cuentas;
    }

    private Movimiento parseMovimientoFromXml(String xml) {
        Movimiento movimiento = new Movimiento();
        try {
            movimiento.setId(Integer.parseInt(extractValue(xml, "a:Id")));
            movimiento.setCuentaId(Integer.parseInt(extractValue(xml, "a:CuentaId")));
            movimiento.setTipoMovimiento(extractValue(xml, "a:Tipo"));
            movimiento.setMonto(Double.parseDouble(extractValue(xml, "a:Monto")));
            movimiento.setFecha(extractValue(xml, "a:Fecha"));
        } catch (Exception e) {
            return null;
        }
        return movimiento;
    }

    private String extractValue(String xml, String tagName) {
        Pattern pattern = Pattern.compile("<" + tagName + ">(.*?)</" + tagName + ">");
        Matcher matcher = pattern.matcher(xml);
        return matcher.find() ? matcher.group(1) : "";
    }
}
