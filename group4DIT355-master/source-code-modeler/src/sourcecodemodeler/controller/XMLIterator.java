package sourcecodemodeler.controller;

import sourcecodemodeler.model.XMLClass;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/*
    This class iterates over the tags of XML documents to retrieve data (data selection).
    The data is then used to create classes that can later be displayed in the produced class diagram.
 */
public class XMLIterator {
    private XMLClass[] xmlClasses;

    //===== Constructor(s) =====//
    public XMLIterator() {}

    //===== Getters & Setters =====//
    public XMLClass[] getXMLClasses() {
        return xmlClasses;
    }

    public String getStringifiedXMLClasses() {
        String classes = "";
        XMLClass[] xmlClasses = getXMLClasses();
        for (int i = 0; i < xmlClasses.length; i++) {
            classes += xmlClasses[i].toString();
        }

        String relations = "";

        for (XMLClass xmlclass : this.getXMLClasses()){
            for (String relation : xmlclass.relationsToString()){

                // make sure that this relationship hasn't been added before - to avoid duplication
                if (!relations.contains(relation + " -- " + xmlclass.getName())){
                    relations += xmlclass.getName() + " -- " + relation + "\n";
                }

            }
        }

        classes += relations;
        return classes;
    }

    //===== Methods =====//
    public void createXMLClasses(File[] files) {
        xmlClasses = new XMLClass[files.length];
        for (int i = 0; i < xmlClasses.length; i++) {
            xmlClasses[i] = createXMLClass(files[i]);
        }
        for (int i = 0; i < xmlClasses.length; i++) {
            setRelationships(xmlClasses[i]);
        }
    }

    // Creates a class (XMLClass) that will hold the data for th visualization.
    private XMLClass createXMLClass(File file) {
        XMLClass xmlClass = new XMLClass();
        setClassName(file, xmlClass);
        setAttributes(file, xmlClass);
        setMethods(file, xmlClass);
        return xmlClass;
    }

    // Iterates through the XML document to retrieve the class name.
    private void setClassName(File file, XMLClass xmlClass) {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);
            NodeList nodeList = doc.getElementsByTagName("name");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Node parent = node.getParentNode();
                if (parent.getNodeName().equalsIgnoreCase("class")) {
                    node = removeTag(node, "annotation");
                    node = removeTag(node, "comment");
                    String s = node.getTextContent();
                    s = prettyString(s).replace("+", "").replace(" ", "");
                    xmlClass.setName(s);
                }
            }
        } catch (ParserConfigurationException | org.xml.sax.SAXException | IOException e) {
            e.printStackTrace();
            System.out.println("Problem parsing XML file: " + file.getName());
        }
    }

    // Iterates through the XML document to retrieve attributes.
    private void setAttributes(File file, XMLClass xmlClass) {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);
            NodeList nodeList = doc.getElementsByTagName("decl_stmt"); // Tag for attributes.
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                node = removeTag(node, "annotation");
                node = removeTag(node,"init");
                node = removeTag(node, "comment");
                Node secondParent = node.getParentNode().getParentNode();
                if (secondParent.getNodeName() == "class") {
                    String s = node.getTextContent();
                    s = prettyString(s);
                    xmlClass.addAttribute(s);
                }
            }
        } catch (ParserConfigurationException | org.xml.sax.SAXException | IOException e) {
            e.printStackTrace();
            System.out.println("Problem parsing XML file: " + file.getName());
        }
    }

    // Iterates through a XML document to retrieve methods.
    private void setMethods(File file, XMLClass xmlClass) {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);
            NodeList nodeList = doc.getElementsByTagName("function"); // Tag for methods.
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                node = removeTag(node, "annotation");
                node = removeTag(node, "throws");
                node = removeTag(node, "parameter_list");
                node = removeTag(node, "specifier");
                node = removeTag(node, "type");
                node = removeTag(node, "comment");
                // Skip nodes that are children of expression nodes - overridden methods will be excluded.
                if (node.getParentNode().getParentNode().getParentNode().getNodeName() != "expr") {
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
            System.out.println("Problem parsing XML file: " + file.getName());
        }
    }

    private void setRelationships(XMLClass xmlClass) {
        List<String> attributes = xmlClass.getAttributes();
        // For each attribute, loop over class names and check if attribute matches class name.
        for (String attribute : attributes) {
            for (int i = 0; i < xmlClasses.length; i++) {
                if (attribute.toLowerCase().contains(xmlClasses[i].getName().toLowerCase())) {
                    // If attribute contains class name, add that class to relationships.
                    xmlClass.addRelationship(xmlClasses[i]);
                }
            }
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
                .replace("    ", " ")
                .replace("   ", " ")
                .replace("  ", " ");
        return s;
    }

}