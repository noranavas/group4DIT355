package sourcecodemodeler.controller;

import sourcecodemodeler.Globals;
import sourcecodemodeler.model.XMLClass;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/*
    This class iterates over the tags of XML documents to retrieve data (data selection).
    The data is then used to create classes that can later be displayed in the produced class diagram.
 */
public class XMLIterator {
    private final String pathToXMLDirectory = Globals.PATH_TO_XML_FILES;

    //===== Constructor(s) =====//
    public XMLIterator() {}

    //===== Methods =====//
    // Creates a class (XMLClass) that will hold the data for th visualization.
    public XMLClass createXMLClass(String name) {
        XMLClass xmlClass = new XMLClass();
        xmlClass.setName(name
                .replace(".xml", "")
                .replace(".java", "")
        );
        setAttributes(name, xmlClass);
        setMethods(name, xmlClass);
        //setRelationships();
        return xmlClass;
    }

    // Iterates through the XML document to retrieve attributes.
    private void setAttributes(String xmlFileName, XMLClass xmlClass) {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(pathToXMLDirectory + xmlFileName));

            NodeList nodeList = doc.getElementsByTagName("decl_stmt"); // Tag for attributes.
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Node secondParent = node.getParentNode().getParentNode();
                if (secondParent.getNodeName() == "class"/* && GetClassName(secondParent) == xmlClass.getName()*/) {
                    node = prettyAttribute(node);
                    String s = node.getTextContent();

                    s = prettyString(s);
                    xmlClass.addAttribute(s);
                }
            }
        } catch (ParserConfigurationException | org.xml.sax.SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    // Iterates through a XML document to retrieve methods.
    private void setMethods(String xmlFileName, XMLClass xmlClass) {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(pathToXMLDirectory + xmlFileName));
            NodeList nodeList = doc.getElementsByTagName("function"); // Tag for methods.
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                node = removeAnnotations(node);
                node = removeExceptions(node);
                // Skip nodes that are children of expression nodes - overridden methods will be excluded.
                if (node.getParentNode().getParentNode().getParentNode().getNodeName() != "expr") {
                    String s = node.getTextContent();
                    String body = s.substring(s.indexOf('{'), s.length());
                    if (!body.isEmpty()) {
                        s = s.replace(body, "");
                    }
                    s = prettyString(s);
                    xmlClass.addMethod(s);
                }
            }
        } catch (ParserConfigurationException | org.xml.sax.SAXException | IOException e) {
            e.printStackTrace();
            System.out.println("Problem parsing XML file: " + xmlFileName);
        }
    }

    //----- Remove Specific Tags -----//
    private Node removeTag(Node node, String tag) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            String childNodeName = childNode.getNodeName();
            // Check if this child should be removed by checking its name.
            if (childNodeName.equalsIgnoreCase(tag)) {
                childNodes.item(i).setTextContent("");
            }
            else {
                removeTag(childNode, tag); // Recursively call this function to remove children.
            }
        }
        return node;
    }
    private Node removeAnnotations(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            String childNodeName = childNodes.item(i).getNodeName();
            if (childNodeName.equalsIgnoreCase("annotation")) {
                childNodes.item(i).setTextContent("");
            }
        }
        return node;
    }
    private Node removeExceptions(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            String childNodeName = childNodes.item(i).getNodeName();
            if (childNodeName.equalsIgnoreCase("throws")) {
                childNodes.item(i).setTextContent("");
            }
        }
        return node;
    }

    private Node prettyAttribute(Node node) {
        node = removeTag(node, "init");
        node = removeTag(node, "comment");
        return node;
    }
    private String prettyString(String s) {
        s = s.replaceAll("\\s\\s", " "); //remove double empty space
        s = s.replaceAll("\\s,\\s+", ", "); //remove space before comma
        s = s.replaceAll("\\s*;", ""); // remove semi-column,
        if (!(s.contains("public") || s.contains("private") || s.contains("protected")))
        {
            s = "+ " + s.trim(); // set public as default visibility
        }

        s = s.trim()
                .replace("public", "+")
                .replace("private", "-")
                .replace("protected", "#")
                .replace("void", "")
                .replace("static", "")
                .replace("final", "")
                .replace("   ", " ")
                .replace("  ", " ");
        return s;
    }

}
