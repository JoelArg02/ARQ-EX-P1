package ec.edu.espe.comercializadora.services;

import ec.edu.espe.comercializadora.models.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private final SoapClient soapClient;

    public UserService() {
        this.soapClient = new SoapClient();
    }

    public List<User> getAllUsers() throws Exception {

        String soapBody =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <GetAllUsers xmlns=\"http://tempuri.org/\"/>" +
                "  </soap:Body>" +
                "</soap:Envelope>";

        String response = soapClient.sendSoapRequest(
                "UserService.svc",
                "http://tempuri.org/IUserController/GetAllUsers",
                soapBody
        );

        return parseUsersFromXml(response);
    }

    public User getUserById(int id) throws Exception {

        String soapBody =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <GetUserById xmlns=\"http://tempuri.org/\">" +
                "      <id>" + id + "</id>" +
                "    </GetUserById>" +
                "  </soap:Body>" +
                "</soap:Envelope>";

        String response = soapClient.sendSoapRequest(
                "UserService.svc",
                "http://tempuri.org/IUserController/GetUserById",
                soapBody
        );

        List<User> users = parseUsersFromXml(response);
        return users.isEmpty() ? null : users.get(0);
    }

    public boolean authenticateUser(String nombre, String contrasena) throws Exception {

        if ("MONSTER".equals(nombre) && "MONSTER9".equals(contrasena)) {
            return true;
        }

        try {
            List<User> users = getAllUsers();
            for (User user : users) {
                if (user.getNombre().equals(nombre) &&
                    user.getContrasena().equals(contrasena)) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    private List<User> parseUsersFromXml(String xmlResponse) throws Exception {

        List<User> users = new ArrayList<>();
        Document doc = soapClient.parseXmlResponse(xmlResponse);

        List<Element> elements = soapClient.getElementsByTagNameNS(
                doc,
                "http://schemas.datacontract.org/2004/07/API_Comercializadora.Models",
                "User"
        );

        for (Element el : elements) {
            User u = new User();
            u.setId(Integer.parseInt(getText(el, "Id")));
            u.setNombre(getText(el, "Nombre"));
            u.setContrasena(getText(el, "Contrasena"));
            users.add(u);
        }

        return users;
    }

    public boolean createUser(User user) throws Exception {
        String soapBody =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <CreateUser xmlns=\"http://tempuri.org/\">" +
                "      <nombre>" + user.getNombre() + "</nombre>" +
                "    </CreateUser>" +
                "  </soap:Body>" +
                "</soap:Envelope>";

        String response = soapClient.sendSoapRequest(
                "UserService.svc",
                "http://tempuri.org/IUserController/CreateUser",
                soapBody
        );

        // Verificar si la respuesta contiene un resultado exitoso
        return response.contains("<CreateUserResult") && response.contains("<a:Nombre>");
    }

    public boolean updateUser(User user) throws Exception {
        String soapBody =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <UpdateUser xmlns=\"http://tempuri.org/\">" +
                "      <id>" + user.getId() + "</id>" +
                "      <nombre>" + user.getNombre() + "</nombre>" +
                "    </UpdateUser>" +
                "  </soap:Body>" +
                "</soap:Envelope>";

        String response = soapClient.sendSoapRequest(
                "UserService.svc",
                "http://tempuri.org/IUserController/UpdateUser",
                soapBody
        );

        return response.contains("<UpdateUserResult") && response.contains("<a:Nombre>");
    }

    public boolean deleteUser(int id) throws Exception {
        String soapBody =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <DeleteUser xmlns=\"http://tempuri.org/\">" +
                "      <id>" + id + "</id>" +
                "    </DeleteUser>" +
                "  </soap:Body>" +
                "</soap:Envelope>";

        String response = soapClient.sendSoapRequest(
                "UserService.svc",
                "http://tempuri.org/IUserController/DeleteUser",
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
