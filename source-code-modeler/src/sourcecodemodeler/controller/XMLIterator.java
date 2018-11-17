package sourcecodemodeler.controller;

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
    private final String pathToXMLDirectory = "resources\\converted_xml\\";

    //===== Constructor(s) =====//
    public XMLIterator() {}

    //===== Methods =====//
    // This method prints the raw content of the latest converted file.
    public void printXMLFile(String fileName) {
        String pathToXMLFile = pathToXMLDirectory + fileName;
        ProcessBuilder pb = new ProcessBuilder(
                "cmd.exe",
                "/c",
                "more " + pathToXMLFile
        );
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = br.readLine();
                if (line == null) { break; }
                System.out.println(line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // Creates a class that only contains the data displayed in a class of a class diagram.
    public void createXMLClass(String name) {
        XMLClass xmlClass = new XMLClass();
        xmlClass.setName(name
                .replace(".xml", "")
                .replace(".c", "")
                .replace(".cpp", "")
                .replace(".java", "")
        );
        System.out.println("Class name: " + xmlClass.getName());
        setAttributes(name, xmlClass);
        System.out.println("Attributes: ");
        // TODO: Exclude local attributes inside methods.
        for (String attribute : xmlClass.getAttributes()) {
            System.out.println(attribute
                    .replace("public", "+")
                    .replace("private", "-")
            );
        }
        System.out.println("Methods: ");
        // TODO: Filter out everything except access modifier, return type, method name and parameters.
        for (String method : xmlClass.getMethods()) {
            System.out.println(method
                    .replace("public", "+")
                    .replace("private", "-")
            );
        }
    }


    private void setAttributes(String xmlFileName, XMLClass xmlClass) {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(pathToXMLDirectory + xmlFileName));

            NodeList nodeList = doc.getElementsByTagName("function");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    xmlClass.addMethod(node.getTextContent());
                }
            }

            nodeList = doc.getElementsByTagName("decl_stmt"); // Tag for attributes.
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    xmlClass.addAttribute(node.getTextContent());
                }
            }

        } catch (ParserConfigurationException | org.xml.sax.SAXException | IOException e) {
            e.printStackTrace();
        }
    }

}
