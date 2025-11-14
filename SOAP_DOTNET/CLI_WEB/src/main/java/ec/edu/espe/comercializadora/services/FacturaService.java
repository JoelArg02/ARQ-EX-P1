package ec.edu.espe.comercializadora.services;

import ec.edu.espe.comercializadora.models.Factura;
import ec.edu.espe.comercializadora.models.ElegibilidadCredito;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.List;

public class FacturaService {

    private final SoapClient soapClient;

    public FacturaService() {
        this.soapClient = new SoapClient();
    }

    public List<Factura> getAllFacturas() throws Exception {

        String soapBody =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <GetAllFacturas xmlns=\"http://tempuri.org/\"/>" +
                "  </soap:Body>" +
                "</soap:Envelope>";

        String response = soapClient.sendSoapRequest(
                "FacturaService.svc",
                "http://tempuri.org/IFacturaController/GetAllFacturas",
                soapBody
        );

        return parseFacturasFromXml(response);
    }

    public ElegibilidadCredito verificarElegibilidadCredito(String cedula, double monto) throws Exception {

        String soapBody =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <VerificarElegibilidadCredito xmlns=\"http://tempuri.org/\">" +
                "      <cedula>" + cedula + "</cedula>" +
                "    </VerificarElegibilidadCredito>" +
                "  </soap:Body>" +
                "</soap:Envelope>";

        String response = soapClient.sendSoapRequest(
                "FacturaService.svc",
                "http://tempuri.org/IFacturaController/VerificarElegibilidadCredito",
                soapBody
        );

        return parseElegibilidadFromXml(response);
    }

    public String crearFacturaConValidacion(int clienteId, List<Integer> electrodomesticoIds, List<Integer> cantidades, int formaPago, Integer numeroCuotas) throws Exception {

        StringBuilder detallesXml = new StringBuilder();

        for (int i = 0; i < electrodomesticoIds.size(); i++) {
            detallesXml.append("<api:DetalleFacturaDto>")
                    .append("<api:Cantidad>").append(cantidades.get(i)).append("</api:Cantidad>")
                    .append("<api:ElectrodomesticoId>").append(electrodomesticoIds.get(i)).append("</api:ElectrodomesticoId>")
                    .append("</api:DetalleFacturaDto>");
        }

        String numeroCuotasXml = "";
        if (numeroCuotas != null) {
            numeroCuotasXml = "<api:NumeroCuotas>" + numeroCuotas + "</api:NumeroCuotas>";
        } else {
            numeroCuotasXml = "<api:NumeroCuotas i:nil=\"true\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"/>";
        }

        String soapBody =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <CrearFacturaConValidacion xmlns=\"http://tempuri.org/\">" +
                "      <request xmlns:a=\"http://schemas.datacontract.org/2004/07/API_Comercializadora.Application.DTOs\" " +
                "               xmlns:b=\"http://schemas.datacontract.org/2004/07/API_Comercializadora.Models.Enums\" " +
                "               xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "        <a:ClienteId>" + clienteId + "</a:ClienteId>" +
                "        <a:Detalles>" + detallesXml.toString() + "</a:Detalles>" +
                "        <a:FormaPago>" + formaPago + "</a:FormaPago>" +
                "        " + numeroCuotasXml +
                "      </request>" +
                "    </CrearFacturaConValidacion>" +
                "  </soap:Body>" +
                "</soap:Envelope>";

        String response = soapClient.sendSoapRequest(
                "FacturaService.svc",
                "http://tempuri.org/IFacturaController/CrearFacturaConValidacion",
                soapBody
        );

        // Extraer información de la respuesta
        Document doc = soapClient.parseXmlResponse(response);
        
        // Buscar el elemento resultado principal
        List<Element> resultElements = soapClient.getElementsByTagNameNS(
                doc,
                "http://tempuri.org/",
                "CrearFacturaConValidacionResult"
        );
        
        if (!resultElements.isEmpty()) {
            List<Element> exitosoElements = soapClient.getElementsByTagNameNS(
                    doc,
                    "http://schemas.datacontract.org/2004/07/API_Comercializadora.Application.DTOs",
                    "Exitoso"
            );
            
            if (!exitosoElements.isEmpty() && "true".equals(exitosoElements.get(0).getTextContent())) {
                List<Element> mensajeElements = soapClient.getElementsByTagNameNS(
                        doc,
                        "http://schemas.datacontract.org/2004/07/API_Comercializadora.Application.DTOs",
                        "Mensaje"
                );
                if (!mensajeElements.isEmpty()) {
                    return mensajeElements.get(0).getTextContent();
                }
                return "Factura creada exitosamente";
            } else {
                List<Element> mensajeElements = soapClient.getElementsByTagNameNS(
                        doc,
                        "http://schemas.datacontract.org/2004/07/API_Comercializadora.Application.DTOs",
                        "Mensaje"
                );
                if (!mensajeElements.isEmpty()) {
                    throw new Exception("Error al crear factura: " + mensajeElements.get(0).getTextContent());
                }
                throw new Exception("Error al crear factura - respuesta sin mensaje");
            }
        } else {
            throw new Exception("Error al crear factura - respuesta inválida del servicio");
        }
    }

    // Método simplificado para crear factura con un solo producto
    public String crearFactura(int clienteId, int electrodomesticoId, int cantidad, int formaPago) throws Exception {
        List<Integer> electrodomesticoIds = new ArrayList<>();
        electrodomesticoIds.add(electrodomesticoId);
        
        List<Integer> cantidades = new ArrayList<>();
        cantidades.add(cantidad);
        
        return crearFacturaConValidacion(clienteId, electrodomesticoIds, cantidades, formaPago, null);
    }

    private List<Factura> parseFacturasFromXml(String xmlResponse) throws Exception {

        List<Factura> facturas = new ArrayList<>();
        Document doc = soapClient.parseXmlResponse(xmlResponse);

        List<Element> facturaElements = soapClient.getElementsByTagNameNS(
                doc,
                "http://schemas.datacontract.org/2004/07/API_Comercializadora.Application.DTOs",
                "FacturaListDto"
        );

        for (Element element : facturaElements) {
            Factura factura = new Factura();
            
            String idStr = getTextDTO(element, "Id");
            String clienteIdStr = getTextDTO(element, "ClienteId");
            String subtotalStr = getTextDTO(element, "Subtotal");
            String ivaStr = getTextDTO(element, "Iva");
            String totalStr = getTextDTO(element, "Total");
            String cantidadStr = getTextDTO(element, "CantidadProductos");
            
            factura.setId(idStr != null && !idStr.isEmpty() ? Integer.parseInt(idStr) : 0);
            factura.setClienteId(clienteIdStr != null && !clienteIdStr.isEmpty() ? Integer.parseInt(clienteIdStr) : 0);
            factura.setNombreCliente(getTextDTO(element, "NombreCliente"));
            factura.setCedulaCliente(getTextDTO(element, "CedulaCliente"));
            factura.setFechaEmision(getTextDTO(element, "FechaEmision"));
            factura.setSubtotal(subtotalStr != null && !subtotalStr.isEmpty() ? Double.parseDouble(subtotalStr) : 0.0);
            factura.setIva(ivaStr != null && !ivaStr.isEmpty() ? Double.parseDouble(ivaStr) : 0.0);
            factura.setTotal(totalStr != null && !totalStr.isEmpty() ? Double.parseDouble(totalStr) : 0.0);
            factura.setFormaPago(getTextDTO(element, "FormaPago"));
            factura.setCantidadProductos(cantidadStr != null && !cantidadStr.isEmpty() ? Integer.parseInt(cantidadStr) : 0);
            facturas.add(factura);
        }

        return facturas;
    }

    private ElegibilidadCredito parseElegibilidadFromXml(String xmlResponse) throws Exception {

        Document doc = soapClient.parseXmlResponse(xmlResponse);

        // Buscar por el tag correcto VerificarElegibilidadCreditoResult en el namespace correcto
        List<Element> elements = soapClient.getElementsByTagNameNS(
                doc,
                "http://tempuri.org/",
                "VerificarElegibilidadCreditoResult"
        );

        if (!elements.isEmpty()) {

            Element el = elements.get(0);
            ElegibilidadCredito elegibilidad = new ElegibilidadCredito();

            String esElegibleStr = getTextDTO(el, "EsElegible");
            String montoMaximoStr = getTextDTO(el, "MontoMaximoCredito");
            
            elegibilidad.setEsElegible(esElegibleStr != null && !esElegibleStr.isEmpty() ? Boolean.parseBoolean(esElegibleStr) : false);
            elegibilidad.setMensaje(getTextDTO(el, "Mensaje"));
            elegibilidad.setMontoMaximoCredito(montoMaximoStr != null && !montoMaximoStr.isEmpty() ? Double.parseDouble(montoMaximoStr) : 0.0);

            return elegibilidad;
        }

        // Si no encuentra el elemento, crear un objeto con valores por defecto
        ElegibilidadCredito elegibilidad = new ElegibilidadCredito();
        elegibilidad.setEsElegible(false);
        elegibilidad.setMensaje("Error al procesar la respuesta del servicio");
        elegibilidad.setMontoMaximoCredito(0.0);
        return elegibilidad;
    }

    private String getTextDTO(Element parent, String tag) {
        try {
            return parent.getElementsByTagNameNS(
                    "http://schemas.datacontract.org/2004/07/API_Comercializadora.Application.DTOs",
                    tag
            ).item(0).getTextContent();
        } catch (Exception e) {
            return "";
        }
    }
}
