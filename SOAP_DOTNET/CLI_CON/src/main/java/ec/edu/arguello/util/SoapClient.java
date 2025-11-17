package ec.edu.arguello.util;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoapClient {
    private final CloseableHttpClient httpClient;

    public SoapClient() {
        this.httpClient = HttpClients.createDefault();
    }

    /**
     * Envía una petición SOAP
     */
    public String sendSoapRequest(String url, String soapXml, String soapAction) throws Exception {
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

    /**
     * Extrae un valor del XML usando regex
     */
    public String extractValueRegex(String xml, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(xml);
        return m.find() ? m.group(1) : "";
    }

    /**
     * Extrae un valor del XML y lo convierte al tipo especificado
     */
    @SuppressWarnings("unchecked")
    public <T> T extractValue(String xml, String tagName, Class<T> type) {
        String value = extractValueRegex(xml, "<" + tagName + ">(.*?)</" + tagName + ">");

        if (value.isEmpty()) {
            if (type == Integer.class) return (T) Integer.valueOf(0);
            if (type == Boolean.class) return (T) Boolean.FALSE;
            if (type == Double.class) return (T) Double.valueOf(0.0);
            return (T) "";
        }

        try {
            if (type == Integer.class) return (T) Integer.valueOf(value);
            if (type == Boolean.class) return (T) Boolean.valueOf(value);
            if (type == Double.class) return (T) Double.valueOf(value);
            return (T) value;
        } catch (Exception e) {
            if (type == Integer.class) return (T) Integer.valueOf(0);
            if (type == Boolean.class) return (T) Boolean.FALSE;
            if (type == Double.class) return (T) Double.valueOf(0.0);
            return (T) "";
        }
    }

    public void close() {
        try {
            httpClient.close();
        } catch (Exception e) {
            // Ignorar errores al cerrar
        }
    }
}
