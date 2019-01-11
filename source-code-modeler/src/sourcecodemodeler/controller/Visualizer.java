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
    public Visualizer(BufferedImage diagram) {
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
        stage = primaryStage;
        System.out.println("In Visualiser.java");

        primaryStage.setTitle("Visualization");
        System.out.println("A");
        ImageView imageView = new ImageView();
        System.out.println("B");
        Image image = SwingFXUtils.toFXImage(diagram, null);
        System.out.println("C");
        imageView.setImage(image);
        System.out.println("D");

        StackPane root = new StackPane();
        System.out.println("E");
        root.setStyle("-fx-background-color: #ffffff");
        System.out.println("F");
        root.getChildren().add(imageView);
        System.out.println("G");
        primaryStage.setScene(new Scene(root, 900, 600));
        System.out.println("H");
        primaryStage.show();
        System.out.println("I");

    }

}