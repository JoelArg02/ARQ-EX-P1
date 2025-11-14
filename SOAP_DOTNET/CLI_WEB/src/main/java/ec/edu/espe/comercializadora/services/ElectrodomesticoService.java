package ec.edu.espe.comercializadora.services;

import ec.edu.espe.comercializadora.models.Electrodomestico;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.List;

public class ElectrodomesticoService {
    private SoapClient soapClient;

    public ElectrodomesticoService() {
        this.soapClient = new SoapClient();
    }

    public List<Electrodomestico> getAllElectrodomesticos() throws Exception {
        String soapBody =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <GetAllElectrodomesticos xmlns=\"http://tempuri.org/\"/>" +
                "  </soap:Body>" +
                "</soap:Envelope>";

        String response = soapClient.sendSoapRequest(
                "ElectrodomesticoService.svc",
                "http://tempuri.org/IElectrodomesticoController/GetAllElectrodomesticos",
                soapBody
        );
        return parseElectrodomesticosFromXml(response);
    }

    public List<Electrodomestico> getElectrodomesticosActivos() throws Exception {
        List<Electrodomestico> todos = getAllElectrodomesticos();
        List<Electrodomestico> activos = new ArrayList<>();
        for (Electrodomestico electrodomestico : todos) {
            if (electrodomestico.isActivo()) {
                activos.add(electrodomestico);
            }
        }
        return activos;
    }

    public boolean createElectrodomestico(Electrodomestico electrodomestico) throws Exception {
        String soapBody =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <CreateElectrodomestico xmlns=\"http://tempuri.org/\">" +
                "      <nombre>" + electrodomestico.getNombre() + "</nombre>" +
                "      <descripcion>" + (electrodomestico.getDescripcion() != null ? electrodomestico.getDescripcion() : "") + "</descripcion>" +
                "      <marca>" + (electrodomestico.getMarca() != null ? electrodomestico.getMarca() : "") + "</marca>" +
                "      <precioVenta>" + electrodomestico.getPrecioVenta() + "</precioVenta>" +
                "      <activo>" + electrodomestico.isActivo() + "</activo>" +
                "    </CreateElectrodomestico>" +
                "  </soap:Body>" +
                "</soap:Envelope>";

        String response = soapClient.sendSoapRequest(
                "ElectrodomesticoService.svc",
                "http://tempuri.org/IElectrodomesticoController/CreateElectrodomestico",
                soapBody
        );
        return response.contains("<CreateElectrodomesticoResult") && response.contains("<a:Nombre>");
    }

    private List<Electrodomestico> parseElectrodomesticosFromXml(String xmlResponse) throws Exception {
        List<Electrodomestico> electrodomesticos = new ArrayList<>();
        Document doc = soapClient.parseXmlResponse(xmlResponse);
        
        List<Element> electrodomesticoElements = soapClient.getElementsByTagNameNS(
                doc,
                "http://schemas.datacontract.org/2004/07/API_Comercializadora.Models",
                "Electrodomestico"
        );
        
        for (Element electrodomesticoElement : electrodomesticoElements) {
            Electrodomestico electrodomestico = new Electrodomestico();
            electrodomestico.setId(Integer.parseInt(getText(electrodomesticoElement, "Id")));
            electrodomestico.setNombre(getText(electrodomesticoElement, "Nombre"));
            electrodomestico.setDescripcion(getText(electrodomesticoElement, "Descripcion"));
            electrodomestico.setMarca(getText(electrodomesticoElement, "Marca"));
            electrodomestico.setPrecioVenta(Double.parseDouble(getText(electrodomesticoElement, "PrecioVenta")));
            electrodomestico.setActivo(Boolean.parseBoolean(getText(electrodomesticoElement, "Activo")));
            electrodomestico.setFechaRegistro(getText(electrodomesticoElement, "FechaRegistro"));
            electrodomesticos.add(electrodomestico);
        }
        
        return electrodomesticos;
    }

    public boolean updateElectrodomestico(Electrodomestico electrodomestico) throws Exception {
        String soapBody =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <UpdateElectrodomestico xmlns=\"http://tempuri.org/\">" +
                "      <id>" + electrodomestico.getId() + "</id>" +
                "      <nombre>" + electrodomestico.getNombre() + "</nombre>" +
                "      <descripcion>" + (electrodomestico.getDescripcion() != null ? electrodomestico.getDescripcion() : "") + "</descripcion>" +
                "      <marca>" + (electrodomestico.getMarca() != null ? electrodomestico.getMarca() : "") + "</marca>" +
                "      <precioVenta>" + electrodomestico.getPrecioVenta() + "</precioVenta>" +
                "      <activo>" + electrodomestico.isActivo() + "</activo>" +
                "    </UpdateElectrodomestico>" +
                "  </soap:Body>" +
                "</soap:Envelope>";

        String response = soapClient.sendSoapRequest(
                "ElectrodomesticoService.svc",
                "http://tempuri.org/IElectrodomesticoController/UpdateElectrodomestico",
                soapBody
        );
        return response.contains("<UpdateElectrodomesticoResult") && response.contains("<a:Nombre>");
    }

    public boolean deleteElectrodomestico(int id) throws Exception {
        String soapBody =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <DeleteElectrodomestico xmlns=\"http://tempuri.org/\">" +
                "      <id>" + id + "</id>" +
                "    </DeleteElectrodomestico>" +
                "  </soap:Body>" +
                "</soap:Envelope>";

        String response = soapClient.sendSoapRequest(
                "ElectrodomesticoService.svc",
                "http://tempuri.org/IElectrodomesticoController/DeleteElectrodomestico",
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

    private String getChildElementText(Element parent, String childTagName) {
        try {
            return parent.getElementsByTagName(childTagName).item(0).getTextContent();
        } catch (Exception e) {
            return "";
        }
    }
}