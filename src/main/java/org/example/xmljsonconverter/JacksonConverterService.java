package org.example.xmljsonconverter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.core.JsonParser;
import javax.xml.stream.XMLInputFactory;

public class JacksonConverterService {

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final XmlMapper xmlMapper;

    public JacksonConverterService() {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
        xmlInputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);

        xmlMapper = new XmlMapper(xmlInputFactory);
    }

    public String xmlToJsonAuto(String xml) {
        try {
            JsonNode node = xmlMapper.readTree(xml.getBytes());
            return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (Exception e) {
            return "ERREUR JACKSON (XML -> JSON) : \n" + e.getMessage();
        }
    }

    public String jsonToXmlAuto(String json) {
        try {
            JsonNode node = jsonMapper.readTree(json);
            return xmlMapper.writerWithDefaultPrettyPrinter().withRootName("root").writeValueAsString(node);
        } catch (Exception e) {
            return "ERREUR JACKSON (JSON -> XML) : \n" + e.getMessage();
        }
    }
}
