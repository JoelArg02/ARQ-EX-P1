package ec.edu.arguello.service;

import ec.edu.arguello.config.ServiceConfig;
import ec.edu.arguello.model.User;
import ec.edu.arguello.util.SoapClient;

public class UserService {
    private final SoapClient soapClient;

    public UserService() {
        this.soapClient = new SoapClient();
    }

    /**
     * Valida las credenciales del usuario
     */
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

    public void close() {
        soapClient.close();
    }
}
