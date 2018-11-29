package sourcecodemodeler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    // Launches the interactive JavaFX window.
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Start the receiver(socket) as very first thing.
        ThreadHandler threadHandler = new ThreadHandler();
        Thread thread = new Thread(threadHandler.startReceiver);
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
