package sourcecodemodeler.controller;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;

public class Visualizer extends Application {

    //===== Variables =====//
    private static Stage stage;
    private BufferedImage diagram;
    private double scale;
    private Pane pane = new Pane();

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

    //===== Method =====//
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        System.out.println("In Visualiser.java");
        primaryStage.setTitle("Visualization");

        // ImageView will contain the diagram
        ImageView imageView = new ImageView();
        Image image = SwingFXUtils.toFXImage(diagram, null);
        imageView.setImage(image);

        ScrollPane scrollPane = new ScrollPane();
        BorderPane borderPane = new BorderPane();
        scrollPane.setContent(new Group(pane));
        scrollPane.setVvalue(0.5);
        scrollPane.setHvalue(0.5);

        // Scaling how the diagram is shown at first
        scale = 1;
        pane.setScaleX(scale);
        pane.setScaleY(scale);

        pane.getChildren().add(imageView);

        // Button for zooming out of the diagram
        Button but1 = new Button("+");
        but1.setOnAction((ActionEvent event) -> {
            scale*=2;
            pane.setScaleX(scale);
            pane.setScaleY(scale);

        });

        // Button for zooming in the diagram
        Button but2 = new Button("-");
        but2.setOnAction((ActionEvent event) -> {
            scale/=2;
            pane.setScaleX(scale);
            pane.setScaleY(scale);

        });

        // Displaying all the UI elements
        HBox buttons = new HBox(but1, but2);
        borderPane.setTop(buttons);
        borderPane.setCenter(scrollPane);
        pane.setStyle("-fx-background-color: #ffffff");
        Scene scene = new Scene(borderPane, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

}