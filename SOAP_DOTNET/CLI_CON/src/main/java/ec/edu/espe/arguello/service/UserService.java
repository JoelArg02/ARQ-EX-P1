package ec.edu.espe.arguello.service;

import ec.edu.espe.arguello.config.ServiceConfig;
import ec.edu.espe.arguello.model.User;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserService {
    private final HttpClient httpClient;
    private final ServiceConfig config;

    public UserService() {
        this.httpClient = HttpClients.createDefault();
        this.config = new ServiceConfig();
    }

    public User login(String username, String password) {
        try {
            String soapRequest = String.format("""
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:Login>
                            <tem:userName>%s</tem:userName>
                            <tem:password>%s</tem:password>
                        </tem:Login>
                    </soap:Body>
                </soap:Envelope>
                """, username, password);

            String response = sendSoapRequest(config.USER_SERVICE_URL, soapRequest,
                "http://tempuri.org/IUserController/Login");

            return parseUserFromXml(response);
        } catch (Exception e) {
            System.err.println("Error al iniciar sesión: " + e.getMessage());
            return null;
        }
    }

    private String sendSoapRequest(String url, String soapXml, String soapAction) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "text/xml; charset=utf-8");
        httpPost.setHeader("SOAPAction", "\"" + soapAction + "\"");

        HttpEntity entity = new StringEntity(soapXml);
        httpPost.setEntity(entity);

        return httpClient.execute(httpPost, response -> {
            HttpEntity responseEntity = response.getEntity();
            return EntityUtils.toString(responseEntity);
        });
    }

    private User parseUserFromXml(String xml) {
        User user = new User();
        try {
            user.setId(Integer.parseInt(extractValue(xml, "a:Id")));
            user.setUserName(extractValue(xml, "a:UserName"));
            user.setPassword(extractValue(xml, "a:Password"));
            user.setRol(extractValue(xml, "a:Rol"));
        } catch (Exception e) {
            return null;
        }
        return user;
    }

    private String extractValue(String xml, String tagName) {
        Pattern pattern = Pattern.compile("<" + tagName + ">(.*?)</" + tagName + ">");
        Matcher matcher = pattern.matcher(xml);
        return matcher.find() ? matcher.group(1) : "";
    }
}
