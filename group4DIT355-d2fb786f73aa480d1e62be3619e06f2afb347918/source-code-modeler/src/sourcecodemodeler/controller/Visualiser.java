package sourcecodemodeler.controller;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;

public class Visualiser extends Application {

    public static Stage classStage = new Stage();
    BufferedImage diagram;

    public Visualiser(BufferedImage diagram) {
        this.diagram = diagram;
    }

    @Override
    public void start(Stage primaryStage) {

            classStage = primaryStage;
            System.out.println("In Visualiser.java");

            primaryStage.setTitle("Visualisation");

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
