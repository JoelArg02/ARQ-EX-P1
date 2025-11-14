package ec.edu.espe.comercializadora.services;

import ec.edu.espe.comercializadora.models.Cliente;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.List;

public class ClienteService {
    private SoapClient soapClient;

    public ClienteService() {
        this.soapClient = new SoapClient();
    }

    public List<Cliente> getAllClientes() throws Exception {
        String soapBody =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <GetAllClientes xmlns=\"http://tempuri.org/\"/>" +
                "  </soap:Body>" +
                "</soap:Envelope>";

        String response = soapClient.sendSoapRequest(
                "ClienteService.svc",
                "http://tempuri.org/IClienteController/GetAllClientes",
                soapBody
        );

        return parseClientesFromXml(response);
    }

    public Cliente getClienteByCedula(String cedula) throws Exception {
        List<Cliente> clientes = getAllClientes();
        for (Cliente cliente : clientes) {
            if (cliente.getCedula().equals(cedula)) {
                return cliente;
            }
        }
        return null;
    }

    public boolean createCliente(Cliente cliente) throws Exception {
        String soapBody =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <CreateCliente xmlns=\"http://tempuri.org/\">" +
                "      <cedula>" + cliente.getCedula() + "</cedula>" +
                "      <nombreCompleto>" + cliente.getNombreCompleto() + "</nombreCompleto>" +
                "      <correo>" + (cliente.getCorreo() != null ? cliente.getCorreo() : "") + "</correo>" +
                "      <telefono>" + (cliente.getTelefono() != null ? cliente.getTelefono() : "") + "</telefono>" +
                "      <direccion>" + (cliente.getDireccion() != null ? cliente.getDireccion() : "") + "</direccion>" +
                "    </CreateCliente>" +
                "  </soap:Body>" +
                "</soap:Envelope>";

        String response = soapClient.sendSoapRequest(
                "ClienteService.svc",
                "http://tempuri.org/IClienteController/CreateCliente",
                soapBody
        );

        return response.contains("<CreateClienteResult") && response.contains("<a:Cedula>");
    }

    private List<Cliente> parseClientesFromXml(String xmlResponse) throws Exception {
        List<Cliente> clientes = new ArrayList<>();
        Document doc = soapClient.parseXmlResponse(xmlResponse);

        List<Element> elements = soapClient.getElementsByTagNameNS(
                doc,
                "http://schemas.datacontract.org/2004/07/API_Comercializadora.Models",
                "Cliente"
        );

        for (Element el : elements) {
            Cliente c = new Cliente();
            c.setId(Integer.parseInt(getText(el, "Id")));
            c.setCedula(getText(el, "Cedula"));
            c.setNombreCompleto(getText(el, "NombreCompleto"));
            c.setCorreo(getText(el, "Correo"));
            c.setTelefono(getText(el, "Telefono"));
            c.setDireccion(getText(el, "Direccion"));
            c.setFechaRegistro(getText(el, "FechaRegistro"));
            clientes.add(c);
        }

        return clientes;
    }

    public boolean updateCliente(Cliente cliente) throws Exception {
        String soapBody =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <UpdateCliente xmlns=\"http://tempuri.org/\">" +
                "      <id>" + cliente.getId() + "</id>" +
                "      <cedula>" + cliente.getCedula() + "</cedula>" +
                "      <nombreCompleto>" + cliente.getNombreCompleto() + "</nombreCompleto>" +
                "      <correo>" + (cliente.getCorreo() != null ? cliente.getCorreo() : "") + "</correo>" +
                "      <telefono>" + (cliente.getTelefono() != null ? cliente.getTelefono() : "") + "</telefono>" +
                "      <direccion>" + (cliente.getDireccion() != null ? cliente.getDireccion() : "") + "</direccion>" +
                "    </UpdateCliente>" +
                "  </soap:Body>" +
                "</soap:Envelope>";

        String response = soapClient.sendSoapRequest(
                "ClienteService.svc",
                "http://tempuri.org/IClienteController/UpdateCliente",
                soapBody
        );

        return response.contains("<UpdateClienteResult") && response.contains("<a:Cedula>");
    }

    public boolean deleteCliente(int id) throws Exception {
        String soapBody =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <DeleteCliente xmlns=\"http://tempuri.org/\">" +
                "      <id>" + id + "</id>" +
                "    </DeleteCliente>" +
                "  </soap:Body>" +
                "</soap:Envelope>";

        String response = soapClient.sendSoapRequest(
                "ClienteService.svc",
                "http://tempuri.org/IClienteController/DeleteCliente",
                soapBody
        );

        return response.contains("true");
    }

    private String getText(Element parent, String tag) {
        try {
            return parent.getElementsByTagNameNS(
                    "http://schemas.datacontract.org/2004/07/API_Comercializadora.Models",
                    tag
            ).item(0).getTextContent();
        } catch (Exception e) {
            return "";
        }
    }
}
