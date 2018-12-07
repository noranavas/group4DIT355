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
import java.util.ArrayList;
import java.util.List;

/*
    This class iterates over the tags of XML documents to retrieve data (data selection).
    The data is then used to create classes that can later be displayed in the produced class diagram.
 */
public class XMLIterator {
    private final String pathToXMLDirectory = Globals.PATH_TO_XML_FILES;
    private List<XMLClass> xmlClasses;

    //===== Constructor(s) =====//
    public XMLIterator() {
        this.xmlClasses = new ArrayList<>();
    }

    //===== Getters & Setters =====//
    public List<XMLClass> getXmlClasses() {
        return xmlClasses;
    }

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
        setRelationships(name, xmlClass);
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
                node = removeTag(node, "annotation");
                node = removeTag(node,"init");
                node = removeTag(node, "comment");
                Node secondParent = node.getParentNode().getParentNode();
                if (secondParent.getNodeName() == "class" /* && GetClassName(secondParent) == xmlClass.getName()*/ ) {
                    String s = node.getTextContent();
                    s = prettyString(s);
                    xmlClass.addAttribute(s);
                }
            }
        } catch (ParserConfigurationException | org.xml.sax.SAXException | IOException e) {
            e.printStackTrace();
            System.out.println("Problem parsing XML file: " + xmlFileName);
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

                // Skip nodes that are children of expression nodes - overridden methods will be excluded.
                if (node.getParentNode().getParentNode().getParentNode().getNodeName() != "expr") {
                    node = removeTag(node, "annotation");
                    node = removeTag(node, "throws");
                    node = removeTag(node, "parameter_list");
                    node = removeTag(node, "specifier");
                    node = removeTag(node, "type");

                    String s = node.getTextContent();
                    String body = s.substring(s.indexOf('{'), s.length());
                    if (!body.isEmpty()) {
                        s = s.replace(body, "");
                    }
                    s = prettyString(s) + "()";
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
            if (childNode.hasChildNodes()) {
                childNode = removeTag(childNode, tag);
            }
            String childNodeName = childNodes.item(i).getNodeName();
            if (childNodeName.equalsIgnoreCase(tag)) {
                childNodes.item(i).setTextContent("");
            }
        }
        return node;
    }

    private String prettyString(String s) {
        // Remove double spaces, space before comma and semi-column.
        s = s.replaceAll("\\s\\s", " ")
                .replaceAll("\\s,\\s+", ", ")
                .replaceAll("\\s*;", "");

        if (!(s.contains("public") || s.contains("private") || s.contains("protected"))) {
            s = "+ " + s.trim(); // Set public as default visibility.
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

    private List<String> getListOfClasses() {
        File[] files = new File(pathToXMLDirectory).listFiles(); // List of all xml files
        List<String> classes = new ArrayList<String>();

        // Loop over all files and save their name as a string
        for (int i = 0; i < files.length; i++) {
            classes.add(files[i].getName().replace(".java.xml", ""));
        }

        return classes;
    }

    private void discoverComposition(XMLClass xmlClass) {
        List<String> attributes = xmlClass.getAttributes();
        List<String> classes = getListOfClasses();

        for (String attribute : attributes) {
            // for each attribute loop over class names and check if attribute contains class name
            for (String className : classes) {
                if (attribute.toLowerCase().contains(className.toLowerCase())) {
                    // attribute contains class name, so make an instance of XMLClass and add it to relationships
                    xmlClass.addRelationship(new XMLClass(className));
                }
            }
        }
    }

    private void discoverInheritance(String xmlFileName, XMLClass xmlClass) {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(pathToXMLDirectory + xmlFileName));
            NodeList nodeList = doc.getElementsByTagName("extends"); // Tag for methods.

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                // node "extends" contains node "name", so take all children and find child node "name"
                NodeList childNodes = node.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node child = childNodes.item(j);
                    if (child.getNodeName() == "name"){
                        xmlClass.addRelationship(new XMLClass(child.getTextContent()));
                        break;
                    }
                }
            }


        } catch (ParserConfigurationException | org.xml.sax.SAXException | IOException e) {
            e.printStackTrace();
            System.out.println("Problem parsing XML file: " + xmlFileName);
        }
    }

    // Iterates through the XML document to retrieve relationships.
    private void setRelationships(String xmlFileName, XMLClass xmlClass) {
        discoverComposition(xmlClass);
        discoverInheritance(xmlFileName, xmlClass);
    }
}
