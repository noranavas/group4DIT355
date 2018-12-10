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

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class Main extends Application {
    public static final int PORT = 5991;
    private static final String PATH_TO_CSS = System.getProperty("user.dir") + "\\source-code-modeler\\resources\\css\\";
    private static final String PATH_TO_XML_DIRECTORY = Globals.PATH_TO_XML_DIRECTORY;
    private static final String IP_ADDRESS_MIDDLEWARE_NODE = "95.80.14.65";
    private static final String IP_ADDRESS_XML_PARSER_NODE = "95.80.14.65";
    private static final String IP_ADDRESS_VISUALIZER_NODE = "95.80.14.65";
    private static String IP_ADDRESS_LOCAL;
    private static String IP_ADDRESS_NEXT_NODE;

    private SourceCodeConverter sourceCodeConverter = new SourceCodeConverter();
    private XMLIterator xmlIterator = new XMLIterator();

    private NetworkConnection receiver = createReceiver();
    private NetworkConnection sender = new NetworkConnection() {
        @Override
        protected boolean isReceiver() {
            return false;
        }

        @Override
        protected String getIP() {
            return "10.132.178.107"; //TODO: put the IP address you send to
        }

        @Override
        protected int getPort() {
            return PORT;
        }
    };

    private File selectedDirectory;

    //===== Network =====//
    @Override
    public void init() throws Exception {
        receiver.startConnection();
        sender.startConnection();
    }
    @Override
    public void stop() throws Exception {
        receiver.closeConnection();
    }
    public void sendData(Serializable data) {
        try {
            sender.send(data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error when sending data: " + data.toString());
        }
    }

    private Receiver createReceiver() {
        return new Receiver(PORT, data -> {
            // Give control back to the UI (JavaFX) thread.
            Platform.runLater(() -> {
                handleData(data);
            });
        });
    }
    private Sender createSender() {
        return new Sender(PORT, "10.0.109.129", data -> {
            Platform.runLater(() -> {
                System.out.println("Sender: " + data);
            });
        });
    }

    public void handleData(Serializable data) {
        Object object = data;

        // If data is String, it is a ip address.
        if (object instanceof String) {
            IP_ADDRESS_NEXT_NODE = (String)data;

        // If data is byte[][], it is the xml files. Do XML parsing.
        } else if (object instanceof byte[][]) {
            System.out.println("In XML Parser node...");
            parseXML(data);
            if (sender == null) sender = createSender();
            try {
                sender.startConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // TODO: Request ip address of next node from middleware?
            //sendData(IP_ADDRESS_NEXT_NODE);
            //sendData(xmlIterator.getXMLClasses());

        // If data is XMLClass[], it is the parsed xml. Do visualization.
        } else if (object instanceof XMLClass[]) {
            System.out.println("In Visualizer node...");
            // TODO: Do visualization. Send visualization to middleware, middleware send to XML parser node.
        } else {
            System.out.println("Unable to recognize data: " + data.toString());
        }
    }

    private void parseXML(Serializable data) {
        sourceCodeConverter.clearOutputDirectory();
        byte[][] encoded = (byte[][])data;
        for (int i = 0; i < encoded.length; i++) {
            try {
                Files.write(new File(PATH_TO_XML_DIRECTORY + "XMLFile" + i).toPath(), encoded[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File[] files = new File(PATH_TO_XML_DIRECTORY).listFiles();
        xmlIterator.createXMLClasses(files);

        // Test print
        XMLClass[] xmlClass = xmlIterator.getXMLClasses();
        for (int i = 0; i < xmlClass.length; i++) {
            System.out.println(xmlClass[i].toString());
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
            createSender();
            try {
                sender.startConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            sendData(IP_ADDRESS_LOCAL);
            sendData(encoded);
        });

        // Test print the parsed XML. TODO: Remove when done.
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
        try {
            IP_ADDRESS_LOCAL = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        launch(args);
    }

}
