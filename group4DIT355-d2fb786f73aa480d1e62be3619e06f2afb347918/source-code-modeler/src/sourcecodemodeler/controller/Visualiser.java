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
       // System.out.println(selectedFile.getName());
/*
        classStage = primaryStage;
        System.out.println("In Visualiser.java");

        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get("C:\\Users\\melin\\Documents\\mockup.txt")), "UTF-8");
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
*/

/*
        Group root = new Group();
        Line line1 = new Line();
        line1.setStartX(10);
        line1.setStartY(10);
        line1.setEndX(150);
        line1.setEndY(150);
*/

/*
        @startuml
        Class01 "1" *-- "many" Class02 : contains
        Class03 o-- Class011 : agregation
        Class05 --> "1" Class06
        @enduml

*/
/*
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);


        final HBox root = new HBox(5);

        final Text text = new Text(content);
        final Rectangle border = new Rectangle(0, 0, Color.TRANSPARENT);
        border.setStroke(Color.BLACK);
        border.setManaged(false);
        text.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {

            @Override
            public void changed(ObservableValue<? extends Bounds> observable,
                                Bounds oldValue, Bounds newValue) {
                border.setLayoutX(text.getBoundsInParent().getMinX());
                border.setLayoutY(text.getBoundsInParent().getMinY());
                border.setWidth(text.getBoundsInParent().getWidth()+2.5);
                border.setHeight(text.getBoundsInParent().getHeight());
            }

        });
        root.getChildren().addAll(text);
        root.getChildren().add(border);


        gridPane.add(root, 1, 1);



        Scene scene = new Scene(gridPane, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
*/

            classStage = primaryStage;
            System.out.println("In Visualiser.java");

            primaryStage.setTitle("Visualisation");

            // PaintUML paint = new PaintUML();
            ImageView imageView = new ImageView();
//            BufferedImage img = null;
/*
            try
            {
                img = ImageIO.read(new File("C:\\Users\\melin\\Desktop\\image.png"));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
*/
            Image image = SwingFXUtils.toFXImage(diagram, null);
            imageView.setImage(image);

            StackPane root = new StackPane();
            root.setStyle("-fx-background-color: #ffffff");
            root.getChildren().add(imageView);
            primaryStage.setScene(new Scene(root, 900, 600));
            primaryStage.show();

    }


}
