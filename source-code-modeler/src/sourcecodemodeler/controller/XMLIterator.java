package sourcecodemodeler.controller;

import sourcecodemodeler.Globals;
import sourcecodemodeler.model.XMLClass;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
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
    public void createXMLClasses() {
        File file = new File(pathToXMLDirectory);
        File[] files = file.listFiles();
        for (File mFile : files) {
            xmlClasses.add(createXMLClass(mFile));
        }
    }

    public void createXMLClasses(File[] files) {
        for (File file : files) {
            xmlClasses.add(createXMLClass(file));
        }
    }

    // Creates a class (XMLClass) that will hold the data for th visualization.
    private XMLClass createXMLClass(File file) {
        XMLClass xmlClass = new XMLClass();
        xmlClass.setName(file.getName()
                .replace(".xml", "")
                .replace(".java", "")
        );
        setAttributes(file, xmlClass);
        setMethods(file, xmlClass);
        //setRelationships();
        return xmlClass;
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
                if (secondParent.getNodeName() == "class" /* && GetClassName(secondParent) == xmlClass.getName()*/ ) {
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
            System.out.println("Problem parsing XML file: " + file.getName());
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
