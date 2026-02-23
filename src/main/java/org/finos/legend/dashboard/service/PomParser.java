package org.finos.legend.dashboard.service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class PomParser {

    public Map<String, String> extractProperties(String pomXml) {
        Map<String, String> properties = new HashMap<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(pomXml)));

            NodeList propertiesNodes = doc.getElementsByTagName("properties");
            if (propertiesNodes.getLength() > 0) {
                Element propsElement = (Element) propertiesNodes.item(0);
                NodeList children = propsElement.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        properties.put(child.getNodeName(), child.getTextContent().trim());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse pom.xml", e);
        }
        return properties;
    }
}
