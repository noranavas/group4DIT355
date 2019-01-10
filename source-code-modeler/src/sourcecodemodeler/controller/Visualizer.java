package sourcecodemodeler.controller;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;

public class Visualizer extends Application {
    private static Stage stage;
    private BufferedImage diagram;

    //===== Constructor(s) =====//
    public Visualizer(BufferedImage diagram) { //Takes a bufferedImage as parameter
        this.diagram = diagram;
        stage = new Stage();
    }

    //===== Getters & Setters =====//
    public static Stage getStage() {
        return stage;
    }
    public BufferedImage getDiagram() {
        return diagram;
    }

    //===== Methods =====//
    @Override
    public void start(Stage primaryStage) {
        //Shows the diagram as a scene in the stage.
        stage = primaryStage;
        System.out.println("In Visualiser.java");

        primaryStage.setTitle("Visualization");
        ImageView imageView = new ImageView();
        Image image = SwingFXUtils.toFXImage(diagram, null);
        imageView.setImage(image);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #ffffff");
        root.getChildren().add(imageView);
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();

    }

}