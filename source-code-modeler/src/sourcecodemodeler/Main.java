package sourcecodemodeler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sourcecodemodeler.controller.SourceCodeConverter;
import sourcecodemodeler.controller.XMLIterator;
import sourcecodemodeler.model.XMLClass;
import sourcecodemodeler.network.NetworkConnection;
import sourcecodemodeler.network.Receiver;
import sourcecodemodeler.network.Sender;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class Main extends Application {
    private static final String PATH_TO_CSS = System.getProperty("user.dir") + "\\source-code-modeler\\resources\\css\\";

    private boolean isReceiver = false; // Switch to true/false depending on node (PC).
    private NetworkConnection connection = isReceiver ? createReceiver() : createSender();

    private SourceCodeConverter sourceCodeConverter = new SourceCodeConverter();
    private XMLIterator xmlIterator = new XMLIterator();

    private File selectedDirectory;

    //===== Network =====//
    @Override
    public void init() throws Exception {
        connection.startConnection();
    }
    @Override
    public void stop() throws Exception {
        connection.closeConnection();
    }
    public void sendData(Serializable data) {
        try {
            connection.send(data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error when sending data.");
        }
    }

    // Create receiver and sender whenever we want to send/receive data between the nodes.
    private Receiver createReceiver() {
        return new Receiver(Globals.PORT, data -> {
            // Give control back to the UI (JavaFX) thread.
            Platform.runLater(() -> {
                // Do stuff with received data.
                System.out.println("Receiver: " + data);
            });
        });
    }
    private Sender createSender() {
        return new Sender(Globals.PORT, "10.132.178.107", data -> {
            Platform.runLater(() -> {
                // Send data?
                System.out.println("Sender: " + data);
            });
        });
    }

    //===== JavaFX =====//
    // Launches the JavaFX window.
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set JavaFX content.
        Label title = new Label("Source Code Modeler");
        Button selectBTN = new Button("Select Directory");
        Button visualizeBTN = new Button("Visualize");
        Label selectedDirectoryName = new Label("");
        Button testPrint = new Button("Test Print");

        HBox hBoxButtons = new HBox(selectBTN, visualizeBTN);
        HBox hBoxSelectedDirectory = new HBox(selectedDirectoryName);
        HBox hBoxTestPrint = new HBox(testPrint);

        hBoxButtons.getStyleClass().add("Hbox");
        hBoxSelectedDirectory.getStyleClass().add("Hbox");
        hBoxTestPrint.getStyleClass().add("Hbox"); // TODO: Remove when done

        hBoxButtons.setId("Hbox");
        hBoxSelectedDirectory.setId("Hbox");
        hBoxTestPrint.setId("Hbox"); // TODO: Remove when done

        Pane pane = new TilePane();
        pane.getChildren().add(title);
        pane.getStyleClass().add("root");
        pane.getChildren().addAll(hBoxButtons, hBoxSelectedDirectory, hBoxTestPrint);
        ((TilePane) pane).setAlignment(Pos.CENTER);

        VBox.setVgrow(pane, Priority.ALWAYS);
        VBox vbox = new VBox(pane);
        vbox.setAlignment(Pos.CENTER);
        
        Scene scene = new Scene(vbox, 400, 255);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Source Code Modeler");

        // Apply all css files in the resources/css directory to the JavaFX scene.
        File[] cssFiles = new File(PATH_TO_CSS).listFiles();
        for (File cssFile : cssFiles) {
            scene.getStylesheets()
                    .add
                    (
                            new File(PATH_TO_CSS + cssFile.getName()).toURI().toURL().toExternalForm()
                    );
        }

        // Select directory event.
        selectBTN.setOnAction(actionEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select a directory");
            Node node = (Node) actionEvent.getSource();
            //final File selectedDirectory1 = directoryChooser.showDialog(node.getScene().getWindow());
            selectedDirectory = directoryChooser.showDialog(node.getScene().getWindow());
            try {
                selectedDirectoryName.setText(selectedDirectory.getName());
            } catch (NullPointerException e) {
                System.out.println("No directory was selected.");
            }
        });

        // Visualize event.
        // TODO: Separate the tasks, and execute them separately based on current node (PC).
        visualizeBTN.setOnAction(actionEvent -> {
            // Source Code Conversion.
            sourceCodeConverter.clearOutputDirectory();
            try {
                sourceCodeConverter.convertDirectoryToXML(selectedDirectory.getPath());
            } catch (NullPointerException e) {
                System.out.println("No directory selected.");
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
        });

        // Test print event. TODO: Remove when done.
        testPrint.setOnAction(actionEvent -> {
            if (xmlIterator.getXmlClasses().isEmpty() || xmlIterator.getXmlClasses() == null) {
                xmlIterator.createXMLClasses();
            }
            for (XMLClass xmlClass : xmlIterator.getXmlClasses()) {
                System.out.println(xmlClass.toString());
            }
        });

        primaryStage.show();
    }


    //===== Main =====//
    public static void main(String[] args) {
        // Runs the start() function.
        launch(args);
    }

}
