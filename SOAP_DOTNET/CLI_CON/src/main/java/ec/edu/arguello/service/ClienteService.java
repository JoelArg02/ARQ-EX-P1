package ec.edu.arguello.service;

import ec.edu.arguello.config.ServiceConfig;
import ec.edu.arguello.model.ClienteBanco;
import ec.edu.arguello.util.SoapClient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClienteService {
    private final SoapClient soapClient;

    public ClienteService() {
        this.soapClient = new SoapClient();
    }

    /**
     * Obtiene todos los clientes
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

            String response = soapClient.sendSoapRequest(
                ServiceConfig.CLIENTE_BANCO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IClienteBancoController/GetAllClientesBanco"
            );

            return parseClientesFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener clientes: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene un cliente por ID
     */
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

            String response = soapClient.sendSoapRequest(
                ServiceConfig.CLIENTE_BANCO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IClienteBancoController/GetClienteBancoById"
            );

            if (response.contains("<a:Id>")) {
                ClienteBanco cliente = new ClienteBanco();
                cliente.setId(soapClient.extractValue(response, "a:Id", Integer.class));
                cliente.setCedula(soapClient.extractValue(response, "a:Cedula", String.class));
                cliente.setNombreCompleto(soapClient.extractValue(response, "a:NombreCompleto", String.class));
                cliente.setEstadoCivil(soapClient.extractValue(response, "a:EstadoCivil", String.class));
                cliente.setFechaNacimiento(soapClient.extractValue(response, "a:FechaNacimiento", String.class));
                String creditoActivo = soapClient.extractValue(response, "a:TieneCreditoActivo", String.class);
                cliente.setTieneCreditoActivo("true".equalsIgnoreCase(creditoActivo));
                return cliente;
            }

            return null;
        } catch (Exception e) {
            System.err.println("Error al obtener cliente: " + e.getMessage());
            return null;
        }
    }

    /**
     * Crea un nuevo cliente
     */
    public ClienteBanco createCliente(String cedula, String nombreCompleto, String estadoCivil, String fechaNacimiento) {
        try {
            // Limpiar fecha si viene con hora
            if (fechaNacimiento.contains("T")) {
                fechaNacimiento = fechaNacimiento.split("T")[0];
            }
            fechaNacimiento = fechaNacimiento.trim();
            
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:CreateClienteBanco>
                            <tem:dto xmlns:a="http://schemas.datacontract.org/2004/07/API_BANCO.Application.DTOs.Clientes" xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
                                <a:Cedula>%s</a:Cedula>
                                <a:EstadoCivil>%s</a:EstadoCivil>
                                <a:FechaNacimiento>%sT00:00:00</a:FechaNacimiento>
                                <a:NombreCompleto>%s</a:NombreCompleto>
                            </tem:dto>
                        </tem:CreateClienteBanco>
                    </soap:Body>
                </soap:Envelope>
                """, cedula, estadoCivil, fechaNacimiento, nombreCompleto);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.CLIENTE_BANCO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IClienteBancoController/CreateClienteBanco"
            );

            if (response.contains("<a:Id>")) {
                ClienteBanco cliente = new ClienteBanco();
                cliente.setId(soapClient.extractValue(response, "a:Id", Integer.class));
                cliente.setCedula(soapClient.extractValue(response, "a:Cedula", String.class));
                cliente.setNombreCompleto(soapClient.extractValue(response, "a:NombreCompleto", String.class));
                cliente.setEstadoCivil(soapClient.extractValue(response, "a:EstadoCivil", String.class));
                cliente.setFechaNacimiento(soapClient.extractValue(response, "a:FechaNacimiento", String.class));
                return cliente;
            }

            return null;
        } catch (Exception e) {
            System.err.println("Error al crear cliente: " + e.getMessage());
            return null;
        }
    }

    public ClienteBanco updateCliente(int id, String cedula, String nombreCompleto, String estadoCivil, String fechaNacimiento) {
        try {
            if (fechaNacimiento.contains("T")) {
                fechaNacimiento = fechaNacimiento.split("T")[0];
            }
            
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:UpdateClienteBanco>
                            <tem:id>%d</tem:id>
                            <tem:cedula>%s</tem:cedula>
                            <tem:nombreCompleto>%s</tem:nombreCompleto>
                            <tem:estadoCivil>2</tem:estadoCivil>
                            <tem:fechaNacimiento>%s</tem:fechaNacimiento>
                            <tem:tieneCreditoActivo>false</tem:tieneCreditoActivo>
                        </tem:UpdateClienteBanco>
                    </soap:Body>
                </soap:Envelope>
                """, id, cedula, nombreCompleto, fechaNacimiento);

            
            String response = soapClient.sendSoapRequest(
                ServiceConfig.CLIENTE_BANCO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IClienteBancoController/UpdateClienteBanco"
            );

            if (response.contains("<a:Id>")) {
                ClienteBanco cliente = new ClienteBanco();
                cliente.setId(soapClient.extractValue(response, "a:Id", Integer.class));
                cliente.setCedula(soapClient.extractValue(response, "a:Cedula", String.class));
                cliente.setNombreCompleto(soapClient.extractValue(response, "a:NombreCompleto", String.class));
                cliente.setEstadoCivil(soapClient.extractValue(response, "a:EstadoCivil", String.class));
                cliente.setFechaNacimiento(soapClient.extractValue(response, "a:FechaNacimiento", String.class));
                return cliente;
            }

            return null;
        } catch (Exception e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Elimina un cliente
     */
    public boolean deleteCliente(int id) {
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
                """, id);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.CLIENTE_BANCO_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IClienteBancoController/DeleteClienteBanco"
            );

            return response.contains("true");
        } catch (Exception e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }

    /**
     * Parsea la respuesta XML para extraer la lista de clientes
     */
    private List<ClienteBanco> parseClientesFromXml(String xml) {
        List<ClienteBanco> clientes = new ArrayList<>();

        try {
            Pattern arrayPattern = Pattern.compile("<a:ClienteBanco>(.*?)</a:ClienteBanco>", Pattern.DOTALL);
            Matcher arrayMatcher = arrayPattern.matcher(xml);

            while (arrayMatcher.find()) {
                String clienteXml = arrayMatcher.group(1);

                try {
                    ClienteBanco cliente = new ClienteBanco();
                    cliente.setId(soapClient.extractValue(clienteXml, "a:Id", Integer.class));
                    cliente.setCedula(soapClient.extractValue(clienteXml, "a:Cedula", String.class));
                    cliente.setNombreCompleto(soapClient.extractValue(clienteXml, "a:NombreCompleto", String.class));
                    cliente.setEstadoCivil(soapClient.extractValue(clienteXml, "a:EstadoCivil", String.class));
                    cliente.setFechaNacimiento(soapClient.extractValue(clienteXml, "a:FechaNacimiento", String.class));
                    String creditoActivo = soapClient.extractValue(clienteXml, "a:TieneCreditoActivo", String.class);
                    cliente.setTieneCreditoActivo("true".equalsIgnoreCase(creditoActivo));

                    clientes.add(cliente);
                } catch (Exception e) {
                    System.err.println("Error al parsear cliente individual: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al parsear clientes: " + e.getMessage());
        }

        return clientes;
    }

    public void close() {
        soapClient.close();
    }
}
