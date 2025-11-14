package ec.edu.espe.comercializadora.services;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.ArrayList;

public class SoapClient {

    private static final String BASE_URL = "http://localhost:62812/";
    private final DefaultHttpClient httpClient;

    public SoapClient() {
        this.httpClient = new DefaultHttpClient();
    }

    public String sendSoapRequest(String service, String soapAction, String soapBody) throws Exception {
        HttpPost httpPost = new HttpPost(BASE_URL + service);

        httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
        httpPost.setHeader("SOAPAction", soapAction);
        httpPost.setHeader("Accept-Charset", "UTF-8");

        StringEntity entity = new StringEntity(soapBody, "UTF-8");
        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost);
        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }

    public Document parseXmlResponse(String xmlResponse) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(xmlResponse.getBytes("UTF-8")));
    }

    public List<Element> getElementsByTagNameNS(Document doc, String namespace, String tagName) {
        List<Element> elements = new ArrayList<>();
        NodeList nodeList = doc.getElementsByTagNameNS(namespace, tagName);
        for (int i = 0; i < nodeList.getLength(); i++) {
            elements.add((Element) nodeList.item(i));
        }
        return elements;
    }

    public List<Element> getElementsByTagName(Document doc, String tagName) {
        List<Element> elements = new ArrayList<>();
        NodeList nodeList = doc.getElementsByTagName(tagName);
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i) instanceof Element) {
                elements.add((Element) nodeList.item(i));
            }
        }
        return elements;
    }

    public String getTextContent(Document doc, String tagName) {
        try {
            NodeList nodeList = doc.getElementsByTagName(tagName);
            if (nodeList.getLength() > 0) {
                return nodeList.item(0).getTextContent();
            }
        } catch (Exception e) {
            // Silent fail
        }
        return "";
    }

    public void close() {
        httpClient.getConnectionManager().shutdown();
    }
}
