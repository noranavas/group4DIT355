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
import net.sourceforge.plantuml.SourceStringReader;
import sourcecodemodeler.controller.SourceCodeConverter;
import sourcecodemodeler.controller.Visualizer;
import sourcecodemodeler.controller.XMLIterator;
import sourcecodemodeler.model.XMLClass;
import sourcecodemodeler.network.NetworkConnection;
import sourcecodemodeler.network.Receiver;
import sourcecodemodeler.network.Sender;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class Handler extends Application {
    public static final int PORT = 5991;
    private static final String PATH_TO_CSS = System.getProperty("user.dir") + "\\source-code-modeler\\resources\\css\\";
    private static final String PATH_TO_XML_DIRECTORY = Globals.PATH_TO_XML_DIRECTORY;
    private static String IP_ADDRESS_LOCAL;
    private static String IP_ADDRESS_NEXT_NODE = "192.168.1.110";
    private boolean hasVisual = false;

    private SourceCodeConverter sourceCodeConverter = new SourceCodeConverter();
    private XMLIterator xmlIterator = new XMLIterator();
    private NetworkConnection receiver = createReceiver();
    private NetworkConnection sender  = createSender();
    private File selectedDirectory;

    Button selectBTN = new Button("Select Directory");
    Button visualizeBTN = new Button("Visualize");

    //===== Network =====//
    @Override
    public void init() throws Exception {
        if (receiver != null) receiver.startConnection();
        if (sender != null) sender.startConnection();
    }
    @Override
    public void stop() throws Exception {
        if (receiver != null) receiver.closeConnection();
        if (sender != null) sender.closeConnection();
    }
    public void sendData(Serializable data) {
        try {
            sender.send(data);
        } catch (Exception e) {
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
        return new Sender(PORT, IP_ADDRESS_NEXT_NODE, data -> {
            Platform.runLater(() -> {
                System.out.println("Sender created.");
            });
        });
    }

    public void handleData(Serializable data) {
        Object object = data;

        // If data is String, it is a ip address.
        if (object instanceof String) {
            //IP_ADDRESS_NEXT_NODE = (String)data;
            System.out.println("Data received is of type: String. Received " + object.toString());

        // If data is byte[][], it is the xml files. Do XML parsing.
        } else if (object instanceof byte[][]) {
            System.out.println("In XML Parser node...");
            parseXML(data);

            // TODO: Request ip address of next node from middleware?
            try {
                TimeUnit.SECONDS.sleep(2);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("SENDING TO " + IP_ADDRESS_NEXT_NODE);
            sendData(IP_ADDRESS_NEXT_NODE);
            sendData(xmlIterator.getXMLClasses());

            selectBTN.setDisable(true);
            visualizeBTN.setDisable(true);

        // If data is XMLClass[], it is the parsed xml. Do visualization.
        } else if (object instanceof XMLClass[]) {
            System.out.println("In Visualizer node...");
            // TODO: Do visualization. Send visualization to middleware, middleware send to XML parser node.

            // Send the visualization.
            if (hasVisual) {
                // do nothing
            } else {
                visualize(data);
                sendData(data);
                hasVisual = true;
            }
        } else {
            System.out.println("Unable to recognize data: " + data.toString());
        }
    }

    // Node Tasks
    private void parseXML(Serializable data) {
        sourceCodeConverter.clearOutputDirectory();
        byte[][] encoded = (byte[][])data;
        for (int i = 0; i < encoded.length; i++) {
            try {
                Files.write(new File(PATH_TO_XML_DIRECTORY + "XMLFile" + i).toPath(), encoded[i]);
            } catch (IOException e) {
                System.out.println("Error when trying to recreate XML file. parseXML() in Main.");
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
    // TODO: Handle the received visualization data.
    private void visualize(Serializable data) {
        XMLClass[] xmlClasses = (XMLClass[])data;
        xmlIterator.setXMLClasses(xmlClasses);
        try {
            System.out.println("Trying visualization...");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            String source = "@startuml\n" +
                    "skinparam class {\n" +
                    "BorderColor black\n" +
                    "ArrowColor black\n" +
                    "BackgroundColor LightSkyBlue\n" +
                    "}\n";
            source += xmlIterator.getStringifiedXMLClasses() +
                    "@enduml\n";
            SourceStringReader reader = new SourceStringReader(source);
            reader.generateImage(out);
            byte[] byteData = out.toByteArray();
            InputStream in = new ByteArrayInputStream(byteData);
            BufferedImage diagram = ImageIO.read(in);
            Visualizer visualiser = new Visualizer(diagram);
            visualiser.start(Visualizer.getStage());
        } catch (IOException e) {
            System.out.println("Error in visualize() in Main.");
        }
    }

    //===== JavaFX =====//
    // Launches the JavaFX window.
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set JavaFX content.
        Label title = new Label("Source Code Modeler");
        Label selectedDirectoryName = new Label("");

        // ========== Test Buttons ==========//
        // TODO: Remove this section when done.
        Button testXMLParse = new Button("Test XML Parse");
        HBox hBoxTestXMLParse = new HBox(testXMLParse);
        hBoxTestXMLParse.getStyleClass().add("Hbox");
        hBoxTestXMLParse.setId("Hbox");

        Button testVisual = new Button("Test Visual Local");
        HBox hBoxTestVisual = new HBox(testVisual);
        hBoxTestVisual.getStyleClass().add("Hbox");
        hBoxTestVisual.setId("Hbox");
        //==============================//

        HBox hBoxButtons = new HBox(selectBTN, visualizeBTN);
        hBoxButtons.getStyleClass().add("Hbox");
        hBoxButtons.setId("Hbox");
        HBox hBoxSelectedDirectory = new HBox(selectedDirectoryName);
        hBoxSelectedDirectory.getStyleClass().add("Hbox");
        hBoxSelectedDirectory.setId("Hbox");

        Pane pane = new TilePane();
        pane.getChildren().add(title);
        pane.getStyleClass().add("root");
        pane.getChildren().addAll(hBoxButtons, hBoxSelectedDirectory, hBoxTestXMLParse, hBoxTestVisual);
        ((TilePane) pane).setAlignment(Pos.CENTER);

        VBox.setVgrow(pane, Priority.ALWAYS);
        VBox vbox = new VBox(pane);
        vbox.setAlignment(Pos.CENTER);
        
        Scene scene = new Scene(vbox, 400, 300);
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
            selectedDirectory = directoryChooser.showDialog(node.getScene().getWindow());
            try {
                selectedDirectoryName.setText(selectedDirectory.getName());
            } catch (NullPointerException e) {
                System.out.println("No directory was selected.");
            }
            visualizeBTN.setDisable(false);
        });

        // Visualize event.
        // TODO: Separate the tasks, and execute them separately based on current node (PC).
        visualizeBTN.setOnAction(actionEvent -> {
            sourceCodeConverter.clearOutputDirectory();
            //createSender(); // No longer needed.
            // Source Code Conversion.
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
            System.out.println(System.lineSeparator() + "Sending 'LOCAL IP: " + IP_ADDRESS_LOCAL + "' to " + IP_ADDRESS_NEXT_NODE);
            sendData(encoded);
            System.out.println("sending 'encoded' to " + IP_ADDRESS_NEXT_NODE + System.lineSeparator());

            visualizeBTN.setDisable(true);
            selectBTN.setDisable(true);
        });

        // Test print the parsed XML. TODO: Remove when done.
        testXMLParse.setOnAction(actionEvent -> {
            sourceCodeConverter.convertDirectoryToXML(selectedDirectory.getPath());
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            xmlIterator.createXMLClasses(new File(PATH_TO_XML_DIRECTORY).listFiles());
            XMLClass[] xmlClasses = xmlIterator.getXMLClasses();
            for (int i = 0; i < xmlIterator.getXMLClasses().length; i++) {
                System.out.println(xmlClasses[i].toString());
            }
        });

        // Test visualization locally. TODO: Remove when done.
        testVisual.setOnAction(actionEvent -> {
            sourceCodeConverter.convertDirectoryToXML(selectedDirectory.getPath());
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            xmlIterator.createXMLClasses(new File(PATH_TO_XML_DIRECTORY).listFiles());

            try {
                System.out.println("Trying visualization...");
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                String source = "@startuml\n" +
                        "skinparam class {\n" +
                        "BorderColor black\n" +
                        "ArrowColor black\n" +
                        "BackgroundColor LightSkyBlue\n" +
                        "}\n";
                source += xmlIterator.getStringifiedXMLClasses() +
                        "@enduml\n";
                SourceStringReader reader = new SourceStringReader(source);
                reader.generateImage(out);
                byte[] byteData = out.toByteArray();
                InputStream in = new ByteArrayInputStream(byteData);
                BufferedImage diagram = ImageIO.read(in);
                Visualizer visualiser = new Visualizer(diagram);
                visualiser.start(Visualizer.getStage());
            } catch (IOException e) {
                System.out.println("Error in visualize() in Main.");
            }
        });

        visualizeBTN.setDisable(true);
        primaryStage.show();
    }

    //===== Main =====//
    public static void main(String[] args) {
        try {
            IP_ADDRESS_LOCAL = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        // Runs the start() function.
        launch(args);
    }

}
