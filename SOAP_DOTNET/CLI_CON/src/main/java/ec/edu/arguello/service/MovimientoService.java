package ec.edu.arguello.service;

import ec.edu.arguello.config.ServiceConfig;
import ec.edu.arguello.model.Movimiento;
import ec.edu.arguello.util.SoapClient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MovimientoService {
    private final SoapClient soapClient;

    public MovimientoService() {
        this.soapClient = new SoapClient();
    }

    public List<Movimiento> getAllMovimientos() {
        try {
            String soapRequest = """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetAllMovimientos/>
                    </soap:Body>
                </soap:Envelope>
                """;

            String response = soapClient.sendSoapRequest(
                ServiceConfig.MOVIMIENTO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IMovimientoController/GetAllMovimientos"
            );

            return parseMovimientosFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener movimientos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Movimiento getMovimientoById(int id) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetMovimientoById>
                            <tem:id>%d</tem:id>
                        </tem:GetMovimientoById>
                    </soap:Body>
                </soap:Envelope>
                """, id);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.MOVIMIENTO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IMovimientoController/GetMovimientoById"
            );

            return parseMovimientoFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener movimiento por ID: " + e.getMessage());
            return null;
        }
    }

    public List<Movimiento> getMovimientosByCuentaId(int cuentaId) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetMovimientosByCuentaId>
                            <tem:cuentaId>%d</tem:cuentaId>
                        </tem:GetMovimientosByCuentaId>
                    </soap:Body>
                </soap:Envelope>
                """, cuentaId);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.MOVIMIENTO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IMovimientoController/GetMovimientosByCuentaId"
            );

            return parseMovimientosFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener movimientos por cuenta: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Movimiento createMovimiento(int cuentaId, int tipo, double monto) {
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

            String response = soapClient.sendSoapRequest(
                ServiceConfig.MOVIMIENTO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IMovimientoController/CreateMovimiento"
            );

            return parseMovimientoFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al crear movimiento: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteMovimiento(int id) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:DeleteMovimiento>
                            <tem:id>%d</tem:id>
                        </tem:DeleteMovimiento>
                    </soap:Body>
                </soap:Envelope>
                """, id);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.MOVIMIENTO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IMovimientoController/DeleteMovimiento"
            );

            return response.contains("<DeleteMovimientoResult>true</DeleteMovimientoResult>");
        } catch (Exception e) {
            System.err.println("Error al eliminar movimiento: " + e.getMessage());
            return false;
        }
    }

    private List<Movimiento> parseMovimientosFromXml(String xml) {
        List<Movimiento> movimientos = new ArrayList<>();

        try {
            Pattern arrayPattern = Pattern.compile("<a:Movimiento>(.*?)</a:Movimiento>", Pattern.DOTALL);
            Matcher arrayMatcher = arrayPattern.matcher(xml);

            while (arrayMatcher.find()) {
                String movimientoXml = arrayMatcher.group(1);

                try {
                    Movimiento movimiento = new Movimiento();
                    movimiento.setId(soapClient.extractValue(movimientoXml, "a:Id", Integer.class));
                    movimiento.setCuentaId(soapClient.extractValue(movimientoXml, "a:CuentaId", Integer.class));
                    movimiento.setTipoMovimiento(soapClient.extractValue(movimientoXml, "a:Tipo", String.class));
                    movimiento.setMonto(soapClient.extractValue(movimientoXml, "a:Monto", Double.class));
                    movimiento.setFecha(soapClient.extractValue(movimientoXml, "a:Fecha", String.class));

                    movimientos.add(movimiento);
                } catch (Exception e) {
                    System.err.println("Error al parsear movimiento individual: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al parsear movimientos: " + e.getMessage());
        }

        return movimientos;
    }

    private Movimiento parseMovimientoFromXml(String xml) {
        try {
            if (xml.contains("i:nil=\"true\"")) {
                return null;
            }

            Movimiento movimiento = new Movimiento();
            movimiento.setId(soapClient.extractValue(xml, "a:Id", Integer.class));
            movimiento.setCuentaId(soapClient.extractValue(xml, "a:CuentaId", Integer.class));
            movimiento.setTipoMovimiento(soapClient.extractValue(xml, "a:Tipo", String.class));
            movimiento.setMonto(soapClient.extractValue(xml, "a:Monto", Double.class));
            movimiento.setFecha(soapClient.extractValue(xml, "a:Fecha", String.class));

            return movimiento;
        } catch (Exception e) {
            System.err.println("Error al parsear movimiento: " + e.getMessage());
            return null;
        }
    }

    public void close() {
        soapClient.close();
    }
}
