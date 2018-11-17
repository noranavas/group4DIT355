package sourcecodemodeler.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.FileChooser;

import java.io.File;

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
    // Allows a user to select a file.
    public void selectFile(ActionEvent actionEvent) {
        Node node = (Node)actionEvent.getSource();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a code file"); // Title of the window in which the user selects the file.

        // Defines selectable file extensions. Should be file extensions supported by srcML.
        // TODO: Add all file extensions supported by srcML.
        FileChooser.ExtensionFilter extFilters =
                new FileChooser.ExtensionFilter(
                        "Code files",
                        "*.c",
                        "*.cpp",
                        "*.java");

        // Apply filters to the file chooser.
        fileChooser.getExtensionFilters().addAll(extFilters);
        // Open the window in which the user can select a file.
        selectedFile = fileChooser.showOpenDialog(node.getScene().getWindow());
        // Update JavaFX to display name of selected file.
        setFileName(selectedFile.getName());

    }

    // Uses SourceCodeConverter class to convert selected file to XML.
    public void convertToXML(ActionEvent actionEvent) {
        sourceCodeConverter.convertToXML(selectedFile.getName(), selectedFile.getPath());
    }

    // For TESTING.
    // Prints the content of the latest converted file to the console.
    public void printXMLContent(ActionEvent actionEvent) {
        xmlIterator.printXMLFile(selectedFile.getName() + ".xml");
    }

    // For TESTING.
    public void printFormattedXML(ActionEvent actionEvent) {
        xmlIterator.createXMLClass(selectedFile.getName() + ".xml");
    }

}
