package ec.edu.arguello.service;

import ec.edu.arguello.config.ServiceConfig;
import ec.edu.arguello.model.User;
import ec.edu.arguello.util.SoapClient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserService {
    private final SoapClient soapClient;

    public UserService() {
        this.soapClient = new SoapClient();
    }

    public User validateCredentials(String username, String password) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:Login>
                            <tem:nombre>%s</tem:nombre>
                            <tem:contrasena>%s</tem:contrasena>
                        </tem:Login>
                    </soap:Body>
                </soap:Envelope>
                """, username, password);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.USER_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IUserController/Login"
            );

            if (response.contains("<a:Id>")) {
                User user = new User();
                user.setId(soapClient.extractValue(response, "a:Id", Integer.class));
                user.setNombre(soapClient.extractValue(response, "a:Nombre", String.class));
                user.setRol(soapClient.extractValue(response, "a:Rol", String.class));
                return user;
            }

            return null;
        } catch (Exception e) {
            System.err.println("Error al validar credenciales: " + e.getMessage());
            return null;
        }
    }

    public List<User> getAllUsers() {
        try {
            String soapRequest = """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetAllUsers/>
                    </soap:Body>
                </soap:Envelope>
                """;

            String response = soapClient.sendSoapRequest(
                ServiceConfig.USER_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IUserController/GetAllUsers"
            );

            return parseUsersFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public User getUserById(int id) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetUserById>
                            <tem:id>%d</tem:id>
                        </tem:GetUserById>
                    </soap:Body>
                </soap:Envelope>
                """, id);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.USER_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IUserController/GetUserById"
            );

            return parseUserFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
            return null;
        }
    }

    public User createUser(String nombre) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:CreateUser>
                            <tem:nombre>%s</tem:nombre>
                        </tem:CreateUser>
                    </soap:Body>
                </soap:Envelope>
                """, nombre);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.USER_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IUserController/CreateUser"
            );

            return parseUserFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            return null;
        }
    }

    public User updateUser(int id, String nombre) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:UpdateUser>
                            <tem:id>%d</tem:id>
                            <tem:nombre>%s</tem:nombre>
                        </tem:UpdateUser>
                    </soap:Body>
                </soap:Envelope>
                """, id, nombre);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.USER_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IUserController/UpdateUser"
            );

            return parseUserFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteUser(int id) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:DeleteUser>
                            <tem:id>%d</tem:id>
                        </tem:DeleteUser>
                    </soap:Body>
                </soap:Envelope>
                """, id);

            String response = soapClient.sendSoapRequest(
                ServiceConfig.USER_SERVICE_URL,
                soapRequest,
                "http://tempuri.org/IUserController/DeleteUser"
            );

            return response.contains("<DeleteUserResult>true</DeleteUserResult>");
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    private List<User> parseUsersFromXml(String xml) {
        List<User> users = new ArrayList<>();

        try {
            Pattern arrayPattern = Pattern.compile("<a:User>(.*?)</a:User>", Pattern.DOTALL);
            Matcher arrayMatcher = arrayPattern.matcher(xml);

            while (arrayMatcher.find()) {
                String userXml = arrayMatcher.group(1);

                try {
                    User user = new User();
                    user.setId(soapClient.extractValue(userXml, "a:Id", Integer.class));
                    user.setNombre(soapClient.extractValue(userXml, "a:Nombre", String.class));
                    user.setRol(soapClient.extractValue(userXml, "a:Rol", String.class));

                    users.add(user);
                } catch (Exception e) {
                    System.err.println("Error al parsear usuario individual: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al parsear usuarios: " + e.getMessage());
        }

        return users;
    }

    private User parseUserFromXml(String xml) {
        try {
            if (xml.contains("i:nil=\"true\"")) {
                return null;
            }

            User user = new User();
            user.setId(soapClient.extractValue(xml, "a:Id", Integer.class));
            user.setNombre(soapClient.extractValue(xml, "a:Nombre", String.class));
            user.setRol(soapClient.extractValue(xml, "a:Rol", String.class));

            return user;
        } catch (Exception e) {
            System.err.println("Error al parsear usuario: " + e.getMessage());
            return null;
        }
    }

    public void close() {
        soapClient.close();
    }
}
