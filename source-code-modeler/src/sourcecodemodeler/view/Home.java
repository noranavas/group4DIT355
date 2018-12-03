package sourcecodemodeler.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import sourcecodemodeler.controller.SourceCodeConverter;
import sourcecodemodeler.controller.XMLIterator;
import sourcecodemodeler.model.XMLClass;

import java.io.File;
import java.util.concurrent.TimeUnit;

/*
    This class handles the communication between JavaFX and the rest of the system.
    For example button events.
 */
public class Home {
    private SourceCodeConverter sourceCodeConverter;
    private XMLIterator xmlIterator;

    private File selectedFile;
    private StringProperty fileName;

    //===== Constructor(s) =====//
    public Home() {
        fileName = new SimpleStringProperty();
        xmlIterator = new XMLIterator();
        sourceCodeConverter = new SourceCodeConverter();
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

        // Defines selectable file extensions. In this case .java files.
        FileChooser.ExtensionFilter extFilters = new FileChooser.ExtensionFilter("Code files", "*.java");

        // Apply filters to the file chooser.
        fileChooser.getExtensionFilters().addAll(extFilters);

        // Open the JavaFX window.
        Node node = (Node)actionEvent.getSource();
        selectedFile = fileChooser.showOpenDialog(node.getScene().getWindow());

        // Update JavaFX to display name of selected file.
        try {
            setFileName(selectedFile.getName());
        } catch (NullPointerException e) {
            System.out.println("No file was selected.");
        }
    }

    // Allows the user to select a directory.
    public void selectDirectory(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a directory");
        Node node = (Node)actionEvent.getSource();
        selectedFile = directoryChooser.showDialog(node.getScene().getWindow());
        try {
            setFileName(selectedFile.getName());
        } catch (NullPointerException e) {
            System.out.println("No directory was selected.");
        }
    }

    /*
        Converts the code files to XML documents. // CODE CONVERSION.
        Creates XMLClass instances based on the XML documents. // DATA SELECTION.
        Uses the XMLClass instances to visualize the data. // VISUALIZATION.
     */
    public void visualize(ActionEvent actionEvent) {
        // Source Code Conversion.
        sourceCodeConverter.clearOutputDirectory();
        try {
            if (selectedFile.isDirectory()) {
                sourceCodeConverter.convertDirectoryToXML(selectedFile.getPath());
            } else {
                sourceCodeConverter.convertToXML(selectedFile.getName(), selectedFile.getPath());
            }
        } catch (NullPointerException e) {
            System.out.println("No file or directory selected.");
        }

        // Allow XML directory to update before trying to parse the XML documents.
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // XML Parsing.
        xmlIterator.createXMLClasses();

        // Call visualizer class and do visualization. Might need its own thread.
        // Ex: visualizer.visualize(xmlIterator.getXmlClasses());

    }

    //===== Temporary Test Methods =====//
    // Prints the formatted versions of all the files in the converted_xml folder.
    public void printFormattedXML(ActionEvent actionEvent) {
        xmlIterator.createXMLClasses();
        for (XMLClass xmlClass : xmlIterator.getXmlClasses()) {
            System.out.println(xmlClass.toString());
        }
    }

}
