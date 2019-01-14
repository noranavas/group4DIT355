package sourcecodemodeler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    public static final int PORT = 5991; // TCP Port.
    private static final String PATH_TO_CSS = System.getProperty("user.dir") + "\\source-code-modeler\\resources\\css\\";
    private static final String PATH_TO_XML_DIRECTORY = Globals.PATH_TO_XML_DIRECTORY;
    private static String IP_ADDRESS_NEXT_NODE;
    private boolean hasVisual = false; // Tracks wether the node has diagram visualization or not.

    private SourceCodeConverter sourceCodeConverter = new SourceCodeConverter();
    private XMLIterator xmlIterator = new XMLIterator();
    private NetworkConnection receiver = createReceiver();
    private NetworkConnection sender;
    private File selectedDirectory;
    private IPRepository ipRepository = new IPRepository();

    // JavaFX buttons, elements etc that need to be accessed in non-local scopes.
    Button selectBTN = new Button("Select Directory");
    Button visualizeBTN = new Button("Distributed Visualization");
    Button visualLocalBTN = new Button("Local Visualization");
    TextField[] textFieldIP = new TextField[3];

    //===== Network =====//
    @Override
    public void init() throws Exception {
        if (receiver != null) receiver.startConnection();
    }
    @Override
    public void stop() throws Exception {
        if (receiver != null) receiver.closeConnection();
        if (sender != null) sender.closeConnection();
    }
    public void sendData(Serializable data) {
        try {
            sender.send(data);
            System.out.println("Sending to: " + IP_ADDRESS_NEXT_NODE);
        } catch (Exception e) {
            System.out.println("Error when sending data to: " + IP_ADDRESS_NEXT_NODE);
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
    private void initSender() {
        if (sender == null) {
            sender = createSender();
            try {
                sender.startConnection();
            } catch (Exception e) {
                System.out.println("Error when starting sender connection");
            }
        }
    }

    // This method handles received data in a certain way depending on its type.
    public void handleData(Serializable data) {
        Object object = data;

        // If data is byte[][], it is the xml documents. Do XML parsing.
        if (object instanceof byte[][]) {
            parseXML(data);


            try {
                TimeUnit.SECONDS.sleep(2);
            }
            catch (InterruptedException e) {}

            initSender();
            try {TimeUnit.SECONDS.sleep(1);} // Allow sender to finish creation before sending data.
            catch (InterruptedException e) {}

            if (sender.canConnectToNextNode()) {
                System.out.println("Connection OK. Sending data to next node.");
                // Send the list of IPs and the xml classes, then disable the GUI  buttons.
                sendData(ipRepository);
                sendData(xmlIterator.getXMLClasses());
            } else {
                System.out.println("Next node not found. Doing local visualization.");
                visualizeLocal();
            }

            selectBTN.setDisable(true);
            visualizeBTN.setDisable(true);

        // If data is XMLClass[], it is the parsed xml. Do visualization.
        } else if (object instanceof XMLClass[]) {
            // Send the visualization.
            if (!hasVisual) {
                hasVisual = true;
                initSender();
                try {TimeUnit.SECONDS.sleep(1);} // Allow sender to finish creation before sending data.
                catch (InterruptedException e) {}

                if (sender.canConnectToNextNode()) {
                    System.out.println("Connection OK. Sending data to next node.");
                    sendData(ipRepository);
                    sendData(data);
                } else {
                    System.out.println("Next node not found. Doing local visualization.");
                }

                visualize(data);
            }

        // If the received data is an ip repository object, set appropriate node index.
        } else if (object instanceof IPRepository) {
            ipRepository = (IPRepository)data;
            System.out.println("ipRepo node nr (pre incr): " + ipRepository.getNodeNumber());
            ipRepository.incrementNodeNumber();
            System.out.println("ipRepo node nr (post incr): " + ipRepository.getNodeNumber());
            int nodeNumber = ipRepository.getNodeNumber();
            int nodeIPAddress = 0;
            if (nodeNumber == 0) {
                nodeIPAddress = 1;
            } else if (nodeNumber == 1) {
                nodeIPAddress = 2;
            } else if (nodeNumber == 2) {
                nodeIPAddress = 0;
            }
            IP_ADDRESS_NEXT_NODE = ipRepository.getIpAddress()[nodeIPAddress];
            System.out.println("IP Address next node: " + IP_ADDRESS_NEXT_NODE);
        } else {
            System.out.println("Unable to recognize received data: " + data.toString());
        }
    }

    // Re-create the xml sent from another node in bytes into xml Files.
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
    }
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
        Button testXMLParse = new Button("Test XML Parse");
        HBox hBoxTestXMLParse = new HBox(testXMLParse);
        hBoxTestXMLParse.getStyleClass().add("Hbox");
        hBoxTestXMLParse.setId("Hbox");
        //==============================//

        HBox hbVisualBTNs = new HBox(visualizeBTN, visualLocalBTN);
        hbVisualBTNs.getStyleClass().add("Hbox");
        hbVisualBTNs.setId("Hbox");

        HBox hbSelectBTN = new HBox(selectBTN);
        hbSelectBTN.getStyleClass().add("Hbox");
        hbSelectBTN.setId("Hbox");
        HBox hbSelectedDir = new HBox(selectedDirectoryName);
        hbSelectedDir.getStyleClass().add("Hbox");
        hbSelectedDir.setId("Hbox");

        Pane pane = new TilePane();
        pane.getChildren().add(title);
        pane.getChildren().add(hbSelectBTN);
        pane.getChildren().add(hbSelectedDir);

        Label[] ipLabel = new Label[3];
        HBox[] ipLabelHBox = new HBox[3];
        for (int i = 0; i < 3; i++) {
            textFieldIP[i] = new TextField();
            textFieldIP[i].setPrefWidth(200);
            ipLabel[i] = new Label("Node " + (i + 1) + " IP Address");
            ipLabelHBox[i] = new HBox();
            ipLabelHBox[i].getStyleClass().add("textField");
            ipLabelHBox[i].setId("textField");
            ipLabelHBox[i].getChildren().addAll(ipLabel[i], textFieldIP[i]);
            pane.getChildren().add(ipLabelHBox[i]);
        }

        pane.getStyleClass().add("root");
        pane.getChildren().add(hbVisualBTNs);

        ((TilePane) pane).setAlignment(Pos.CENTER);

        VBox.setVgrow(pane, Priority.ALWAYS);
        VBox vbox = new VBox(pane);
        vbox.setAlignment(Pos.CENTER);
        
        Scene scene = new Scene(vbox, 500, 435);
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

        // Select directory button event.
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
            visualLocalBTN.setDisable(false);
        });

        // Visualize distributed button event.
        visualizeBTN.setOnAction(actionEvent -> {
            sourceCodeConverter.clearOutputDirectory();
            // Source Code Conversion.
            try {
                sourceCodeConverter.convertDirectoryToXML(selectedDirectory.getPath());
            } catch (NullPointerException e) {
                System.out.println("No directory selected or selected directory invalid.");
            }

            // Allow output directory to update before doing anything else.
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {e.printStackTrace();}

            File[] files = new File(PATH_TO_XML_DIRECTORY).listFiles();
            byte[][] encoded = new byte[files.length][];
            for (int i = 0; i < encoded.length; i++) {
                try {
                    encoded[i] = Files.readAllBytes(Paths.get(files[i].getPath()));
                } catch (IOException e) {
                    System.out.println("Problem reading code files when pressing visualize button.");
                }
            }
            sourceCodeConverter.clearOutputDirectory();

            setIPAddresses();
            IP_ADDRESS_NEXT_NODE = ipRepository.getIpAddress()[1];
            initSender();
            try {TimeUnit.SECONDS.sleep(1);} // Allow sender to finish creation before sending data.
            catch (InterruptedException e) {}

            if (sender.canConnectToNextNode()) {
                System.out.println("Connection OK. Sending data to next node.");
                sendData(ipRepository);
                sendData(encoded);
            } else {
                System.out.println("Next node not found. Doing local visualization.");
                visualizeLocal();
            }

            visualizeBTN.setDisable(true);
            visualLocalBTN.setDisable(true);
            selectBTN.setDisable(true);
        });

        // Test print the parsed XML.
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

        // Do visualization locally button event.
        visualLocalBTN.setOnAction(actionEvent -> {
            visualizeLocal();
        });

        visualizeBTN.setDisable(true);
        visualLocalBTN.setDisable(true);
        primaryStage.show();
    }

    private void visualizeLocal() {
        sourceCodeConverter.clearOutputDirectory();
        if (ipRepository.getNodeNumber() != 1) {
            sourceCodeConverter.convertDirectoryToXML(selectedDirectory.getPath());
        }
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
    }

    private void setIPAddresses() {
        String[] textFieldContent = new String[3];
        for (int i = 0; i < 3; i++) {
            if (textFieldIP[i].getText().isEmpty()) {
                textFieldContent[i] = "No IP";
            } else {
                textFieldContent[i] = textFieldIP[i].getText();
            }
        }
        ipRepository.setIPAddress(textFieldContent);
    }

    //===== Main =====//
    public static void main(String[] args) {
        // Runs the start() function.
        launch(args);
    }

}
