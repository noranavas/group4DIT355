package sourcecodemodeler.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.sourceforge.plantuml.SourceStringReader;
import sourcecodemodeler.controller.SourceCodeConverter;
import sourcecodemodeler.controller.Visualiser;
import sourcecodemodeler.controller.XMLIterator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/*
    This class handles the communication between JavaFX and the rest of the system.
    For example button events.
 */
public class Home {
    private SourceCodeConverter sourceCodeConverter = new SourceCodeConverter();
    private XMLIterator xmlIterator = new XMLIterator();

    private File selectedFile;
    private StringProperty fileName;

    //===== Constructor(s) =====//
    public Home() {
        this.fileName = new SimpleStringProperty();
    }

    //===== Getters & Setters =====//
    public String getFileName() {
        return fileName.get();
    }

    // This method is needed to update the element in the fxml file.
    public StringProperty fileNameProperty() {
        return fileName;
    }

    public void setFileName(String text) {
        fileName.set(text);
    }

    //===== Methods =====//
    // Allows the user to select a file.
    public void selectFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a code file"); // Title of the JavaFX window.

        // Defines selectable file extensions. Should be file extensions supported by srcML.
        // TODO: Add all file extensions supported by srcML.
        FileChooser.ExtensionFilter extFilters = new FileChooser.ExtensionFilter
                ("Code files",
                        "*.c",
                        "*.cpp",
                        "*.java"
                );

        // Apply filters to the file chooser.
        fileChooser.getExtensionFilters().addAll(extFilters);
        // Open the JavaFX window.
        Node node = (Node) actionEvent.getSource();
        selectedFile = fileChooser.showOpenDialog(node.getScene().getWindow());
        // Update JavaFX to display name of selected file.
        setFileName(selectedFile.getName());
    }

    // Allows the user to select a directory.
    public void selectDirectory(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a directory");
        Node node = (Node) actionEvent.getSource();
        selectedFile = directoryChooser.showDialog(node.getScene().getWindow());
        setFileName(selectedFile.getName());
    }

    // Uses SourceCodeConverter class to convert selected file/directory to XML.
    public void convertToXML(ActionEvent actionEvent) {
        //sourceCodeConverter.clearOutputDirectory();
        try {
            if (selectedFile.isDirectory()) {
                sourceCodeConverter.convertDirectoryToXML(selectedFile.getParent());
            } else {
                sourceCodeConverter.convertToXML(selectedFile.getName(), selectedFile.getPath());
            }
        } catch (NullPointerException e) {
            System.out.println("No file or directory selected.");
            e.printStackTrace();
        }
    }

    //===== Temporary Test Methods =====//
    // Prints the content of the latest converted file to the console.
    public void printXMLContent(ActionEvent actionEvent) {
        System.out.println("PATH " + selectedFile.getPath());
        if (selectedFile.isDirectory()) {
            System.out.println("Can not print a directory.");
        } else {
            xmlIterator.printXMLFile(selectedFile.getName() + ".xml");
        }
    }

    public void printFormattedXML(ActionEvent actionEvent) {
        if (selectedFile.isDirectory()) {
            System.out.println("Can not print a directory.");
        } else {
            xmlIterator.createXMLClass(selectedFile.getName() + ".xml");
        }
    }

    public void visualiseXML(ActionEvent actionEvent) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String source = "@startuml\n" +
                "skinparam class {\n" +
                "BorderColor black\n" +
                "ArrowColor black\n" +
                "BackgroundColor LightSkyBlue\n" +
                "}\n";

        source += "class Person {\n" +
                "String name\n" +
                "int age\n" +
                "int money\n" +
                "String getName()\n" +
                "void setName(String name)\n" +
                "}\n" +
                "class ShoppingList{\n" +
                "int id\n" +
                "String getItems()\n" +
                "}\n" +
                "Person -- ShoppingList\n" +
                "@enduml\n";

        SourceStringReader reader = new SourceStringReader(source);

        String desc = reader.generateImage(outputStream);
        byte[] data = outputStream.toByteArray();

        InputStream inputStream = new ByteArrayInputStream(data);
        BufferedImage diagram = ImageIO.read(inputStream);

        Visualiser visualiser = new Visualiser(diagram);
        visualiser.start(Visualiser.classStage);

    }

}
