package ec.edu.espe.arguello.repository;

import ec.edu.espe.arguello.models.User;
import ec.edu.espe.arguello.config.ServiceConfig;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Logger;

@ApplicationScoped
public class UserRepository implements Serializable {
    private static final Logger logger = Logger.getLogger(UserRepository.class.getName());
    
    public User validateUser(String username, String password) {
        try {
            String soapRequest = buildValidateUserRequest(username, password);
            String response = sendSoapRequest(soapRequest, ServiceConfig.USER_SERVICE_URL);
            
            if (response != null && response.contains("<a:Estado>ok</a:Estado>")) {
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                user.setStatus("ok");
                return user;
            }
            
            return null;
        } catch (Exception e) {
            logger.severe("Error validando usuario: " + e.getMessage());
            return null;
        }
    }
    
    private String buildValidateUserRequest(String username, String password) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
               "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
               "               xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" \n" +
               "               xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
               "  <soap:Body>\n" +
               "    <ValidateUser xmlns=\"http://tempuri.org/\">\n" +
               "      <username>" + username + "</username>\n" +
               "      <password>" + password + "</password>\n" +
               "    </ValidateUser>\n" +
               "  </soap:Body>\n" +
               "</soap:Envelope>";
    }
    
    private String sendSoapRequest(String soapRequest, String serviceUrl) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
                    
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serviceUrl))
                    .header("Content-Type", "text/xml; charset=utf-8")
                    .header("SOAPAction", "http://tempuri.org/IUserService/ValidateUser")
                    .POST(HttpRequest.BodyPublishers.ofString(soapRequest))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            logger.severe("Error enviando solicitud SOAP: " + e.getMessage());
            return null;
        }
    }
}