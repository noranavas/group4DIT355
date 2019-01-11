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
    public void setXMLClasses(XMLClass[] xmlClasses) {
        this.xmlClasses = xmlClasses;
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
        this.xmlClasses = new XMLClass[files.length];

        for (int i=0; i < files.length; i++) {
            xmlClasses[i] = createXMLClass(files[i]);
        }

        for (XMLClass xmlClass : xmlClasses) {
            discoverComposition(xmlClass, xmlClasses);
        }
    }

    private void discoverComposition(XMLClass xmlClass, XMLClass[] classes) {
        List<String> attributes = xmlClass.getAttributes();
        //List<String> classes = getListOfClasses();

        for (String attribute : attributes) {
            // for each attribute loop over class names and check if attribute contains class name
            for (XMLClass otherClass : classes) {
                if (xmlClass != otherClass && attribute != null && otherClass.getName() != null) {
                    if (otherClass.getName() != null && attribute.toLowerCase().contains(otherClass.getName().toLowerCase())) {
                        // attribute contains class name, so make an instance of XMLClass and add it to relationships
                        xmlClass.addRelationship(otherClass);
                    }
                }
            }
        }
    }

    // Creates a class (XMLClass) that will hold the data for th visualization.
    public XMLClass createXMLClass(File file) {
        XMLClass xmlClass = new XMLClass();

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);

            NodeList nodeList = doc.getElementsByTagName("class");

            if (nodeList.getLength() == 0) {
                nodeList = doc.getElementsByTagName("enum");
            }

            if (nodeList.getLength() > 0) {

                // Set xmlClass name to the first class we find in the xml file
                NodeList classChildNodes = nodeList.item(0).getChildNodes();
                for (int i = 0; i < classChildNodes.getLength(); i++) {
                    Node node = classChildNodes.item(i);
                    if (node.getNodeName() == "name") {
                        xmlClass.setName(node.getTextContent());
                        break;
                    }
                }

                // If there are more classes in the file, remove them to avoid inner class attributes and methods
                if (nodeList.getLength() > 1) {
                    for (int i = 1; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);
                        node.setTextContent("");
                    }
                }

                setAttributes(doc, xmlClass);
                setMethods(doc, xmlClass);
                discoverInheritance(doc, xmlClass);
            }

        } catch (ParserConfigurationException | org.xml.sax.SAXException | IOException e) {
            e.printStackTrace();
            System.out.println("Problem parsing XML file: " + file.getName());
        }

        return xmlClass;
    }

    private void discoverInheritance(Document doc, XMLClass xmlClass) {
        NodeList nodeList = doc.getElementsByTagName("extends"); // Tag for methods.

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            // node "extends" contains node "name", so take all children and find child node "name"
            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node child = childNodes.item(j);
                if (child.getNodeName() == "name") {
                    xmlClass.addRelationship(new XMLClass(child.getTextContent()));
                    break;
                }
            }
        }
    }

    // Iterates through the XML document to retrieve attributes.
    private void setAttributes(Document doc, XMLClass xmlClass) {

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
    }

    // Iterates through a XML document to retrieve methods.
    private void setMethods(Document doc, XMLClass xmlClass) {
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
                node = removeTag(node, "comment");

                String s = node.getTextContent();
                String body = s.substring(s.indexOf('{'), s.length());
                if (!body.isEmpty()) {
                    s = s.replace(body, "");
                }
                s = prettyString(s) + "()";
                xmlClass.addMethod(s);
            }
        }
    }

    //----- Remove Specific Tags -----//
    private Node removeTag(Node node, String tag) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);

            // recursively call removeTag method to remove tag from all children
            if (childNode.hasChildNodes()) {
                childNode = removeTag(childNode, tag);
            }

            // this actually removes the tag by setting its content to empty string
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

}