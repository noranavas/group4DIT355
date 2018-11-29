package sourcecodemodeler;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sourcecodemodeler.network.Receiver;

public class Main extends Application {

    Task receiverTask = new Task() {
        @Override
        protected Object call() throws Exception {
            System.out.println("Receiver task started.");
            new Receiver().start(5991);
            return null;
        }};

    // Launches the interactive JavaFX window.
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Start the receiver(socket) as very first thing.
        Thread thread = new Thread(receiverTask);
        thread.start();

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/home.fxml"));
        primaryStage.setScene(new Scene(root,900, 900));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // Main function.
    public static void main(String[] args) {
        launch(args);
    }

}
