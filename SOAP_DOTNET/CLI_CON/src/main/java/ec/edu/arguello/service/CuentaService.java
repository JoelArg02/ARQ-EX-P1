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

    /**
     * Crea un movimiento (depósito o retiro)
     */
    public boolean createMovimiento(int cuentaId, String tipoMovimiento, double monto) {
        try {
            int tipo = "Deposito".equals(tipoMovimiento) ? 1 : 2;

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

            String response = soapClient.sendSoapRequest(
                ServiceConfig.MOVIMIENTO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IMovimientoController/CreateMovimiento"
            );

            return response.contains("<a:Id>");
        } catch (Exception e) {
            System.err.println("Error al crear movimiento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Parsea la respuesta XML para extraer la lista de cuentas
     */
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

    public void close() {
        soapClient.close();
    }
}
