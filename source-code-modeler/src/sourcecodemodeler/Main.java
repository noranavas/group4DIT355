package sourcecodemodeler;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sourcecodemodeler.controller.SocketNode;

public class Main extends Application {
    SocketNode socketNode = new SocketNode();

    Task task1 = new Task(){
        @Override
        protected Object call() throws Exception { //start the server
            System.out.println("Task 1 started");
            socketNode.startServer();
            return null;
        }};

    // Launches the interactive JavaFX window.
    @Override
    public void start(Stage primaryStage) throws Exception {
        //start the server socket as very first thing
        Thread thread1 = new Thread(task1);
       // thread1.start();


        Parent root = FXMLLoader.load(getClass().getResource("/fxml/home.fxml"));
        primaryStage.setScene(new Scene(root,900, 600));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // Main function.
    public static void main(String[] args) {
        launch(args);
    }

}
