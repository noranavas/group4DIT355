package sourcecodemodeler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import sourcecodemodeler.controller.SourceCodeConverter;
import sourcecodemodeler.controller.XMLIterator;
import sourcecodemodeler.network.NetworkConnection;
import sourcecodemodeler.network.Receiver;
import sourcecodemodeler.network.Sender;

import java.io.IOException;
import java.io.Serializable;

public class Main extends Application {
    private boolean isReceiver = false; // Switch to true/false depending on node (PC).
    private NetworkConnection connection = isReceiver ? createReceiver() : createSender();
    private SourceCodeConverter sourceCodeConverter = new SourceCodeConverter();
    private XMLIterator xmlIterator = new XMLIterator();

    //===== JavaFX Content =====//
    private Button selectFile = new Button("Select File");
    private Button selectDirectory = new Button("Select Directory");
    private Button visualize = new Button("Visualize");
    private Label selectedFile = new Label();
    private Button testPrint = new Button("Test Print");

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
        }
    }

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
        /*Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/home.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setScene(new Scene(root,500, 225));*/

        Button selectBTN = new Button("Select Directory");
        Button visualizeBTN = new Button("Visualize");
        Label selectedDirectory = new Label();
        HBox hBox = new HBox(selectBTN, selectedDirectory, visualizeBTN);
        Scene scene = new Scene(hBox);
        scene.getStylesheets().add(Main.class.getResource("main.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Source Code Modeler");
        primaryStage.show();
    }

    //===== Main =====//
    public static void main(String[] args) {
        // Runs the start() function.
        launch(args);
    }

}
