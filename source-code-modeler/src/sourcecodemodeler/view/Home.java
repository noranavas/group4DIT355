package sourcecodemodeler.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import sourcecodemodeler.controller.SourceCodeConverter;
import sourcecodemodeler.controller.XMLIterator;

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
        setFileName(selectedFile.getName());
    }

    // Allows the user to select a directory.
    public void selectDirectory(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a directory");
        Node node = (Node)actionEvent.getSource();
        selectedFile = directoryChooser.showDialog(node.getScene().getWindow());
        setFileName(selectedFile.getName());
    }

    // Calls the conversion methods from the SourceCodeConverterClass.
    public void convertToXML(ActionEvent actionEvent) {
        sourceCodeConverter.clearOutputDirectory();
        try {
            if (selectedFile.isDirectory()) {
                sourceCodeConverter.convertDirectoryToXML(selectedFile.getPath());
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
        if (selectedFile.isDirectory()) {
            System.out.println("Can not print a directory.");
        } else {
            xmlIterator.printXMLFile( selectedFile.getName() + ".xml");
        }
    }

    // Prints the formatted versions of all the files in the converted_xml folder. For testing.
    public void printFormattedXML(ActionEvent actionEvent) {
        File file = new File(System.getProperty("user.dir") + "\\source-code-modeler\\resources\\converted_xml\\");
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            System.out.println(xmlIterator.createXMLClass(files[i].getName())
                    .toString());
        }
    }

}
