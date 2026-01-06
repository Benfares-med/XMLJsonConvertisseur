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
        // Configuration spécifique pour XML
        XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
        // Permet de gérer les namespaces sans casser la structure JSON
        xmlInputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);

        xmlMapper = new XmlMapper(xmlInputFactory);
    }

    public String xmlToJsonAuto(String xml) {
        try {
            // Jackson gère nativement le CDATA en extrayant le texte contenu dedans
            JsonNode node = xmlMapper.readTree(xml.getBytes());
            return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (Exception e) {
            return "ERREUR JACKSON (XML -> JSON) : \n" + e.getMessage();
        }
    }

    public String jsonToXmlAuto(String json) {
        try {
            JsonNode node = jsonMapper.readTree(json);
            // withRootName "root" assure qu'on a toujours une racine XML valide
            return xmlMapper.writerWithDefaultPrettyPrinter().withRootName("root").writeValueAsString(node);
        } catch (Exception e) {
            return "ERREUR JACKSON (JSON -> XML) : \n" + e.getMessage();
        }
    }
}
