package sourcecodemodeler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class Main extends Application {
    public static final int PORT = 5991;
    private static final String PATH_TO_CSS = System.getProperty("user.dir") + "/resources/css/";
    private static final String PATH_TO_XML_DIRECTORY = Globals.PATH_TO_XML_DIRECTORY;
    private static final String[] IP_ADDRESS = Globals.IP_ADDRESS;

    private NetworkConnection receiver = createReceiver();
    private NetworkConnection sender = createSender();

    private SourceCodeConverter sourceCodeConverter = new SourceCodeConverter();
    private XMLIterator xmlIterator = new XMLIterator();

    private File selectedDirectory;
    private int nodeNumber = 1;

    //===== Network =====//
    @Override
    public void init() throws Exception {
        receiver.startConnection();
        sender.startConnection();
    }
    @Override
    public void stop() throws Exception {
        receiver.closeConnection();
        sender.closeConnection();
    }
    public void sendData(Serializable data) {
        try {
            sender.send(data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error when sending data: " + data.toString());
        }
    }

    // Create receiver and sender whenever we want to send/receive data between the nodes.
    private Receiver createReceiver() {
        return new Receiver(PORT, data -> {
            // Give control back to the UI (JavaFX) thread.
            Platform.runLater(() -> {

                if (nodeNumber == 1) {
                    System.out.println("In node: " + nodeNumber);
                    parseXML(data);
                    nodeNumber++;
                    //===== NEW STUFF
                    File[] files = new File(PATH_TO_XML_DIRECTORY).listFiles();
                    xmlIterator.createXMLClasses(files);
                    XMLClass[] xmlClassArray = xmlIterator.getXMLClasses();
                    createSender();
                    sendData(xmlClassArray);
                    //=====
                } else if (nodeNumber == 2) {
                    System.out.println("In node: " + nodeNumber);
                    // TODO: Do visualization?
                    nodeNumber++;
                } else if (nodeNumber == 3) {
                    System.out.println("In node: " + nodeNumber);
                    // TODO: Send visualization to all nodes?
                    nodeNumber++;
                } else {
                    System.out.println("In node: " + nodeNumber);
                    // TODO: ???
                }

            });
        });
    }
    private Sender createSender() {
        return new Sender(PORT, "192.168.1.110", data -> {
            Platform.runLater(() -> {
                System.out.println("Sender: " + data);
            });
        });
    }

    //===== Node Tasks =====//
    // Node 1
    private void parseXML(Serializable data) {
        sourceCodeConverter.clearOutputDirectory();
        byte[][] encoded = (byte[][])data;
        for (int i = 0; i < encoded.length; i++) {
            try {
                Files.write(new File(PATH_TO_XML_DIRECTORY + i).toPath(), encoded[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File[] files = new File(PATH_TO_XML_DIRECTORY).listFiles();
        xmlIterator.createXMLClasses(files);

        // Test Print
        XMLClass[] xmlClasses = xmlIterator.getXMLClasses();
        for (int i = 0; i < xmlIterator.getXMLClasses().length; i++) {
            System.out.println(xmlClasses[i].toString());
        }

    }

    //===== JavaFX =====//
    // Launches the JavaFX window.
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set JavaFX content.
        Label title = new Label("Source Code Modeler");
        Button selectBTN = new Button("Select Directory");
        Button visualizeBTN = new Button("Visualize");
        Label selectedDirectoryName = new Label("PLACEHOLDER");
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
        
        System.out.println(PATH_TO_CSS);
        
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
            createSender();
            sourceCodeConverter.clearOutputDirectory();
            try {
                sourceCodeConverter.convertDirectoryToXML(selectedDirectory.getPath());
            } catch (NullPointerException e) {
                System.out.println("No directory selected or selected directory invalid.");
            }

            // Allow output directory to update before doing anything else.
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            File[] files = new File(PATH_TO_XML_DIRECTORY).listFiles();
            byte[][] encoded = new byte[files.length][];
            for (int i = 0; i < encoded.length; i++) {
                try {
                    encoded[i] = Files.readAllBytes(Paths.get(files[i].getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            sourceCodeConverter.clearOutputDirectory();
            sendData(encoded);
            nodeNumber++;

        });

        // Test print event. TODO: Remove when done.
        testPrint.setOnAction(actionEvent -> {
            File[] files = new File(PATH_TO_XML_DIRECTORY).listFiles();
            if (xmlIterator.getXMLClasses() == null || xmlIterator.getXMLClasses().length == 0) {
                xmlIterator.createXMLClasses(files);
            }
            XMLClass[] xmlClasses = xmlIterator.getXMLClasses();
            for (int i = 0; i < xmlIterator.getXMLClasses().length; i++) {
                System.out.println(xmlClasses[i].toString());
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
