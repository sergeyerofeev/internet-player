package sample.extra;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ErrorAlert {

    private Stage errorWindow;

    public ErrorAlert(Stage parentStage, String contentText) {
        // ширина и высота окна
        Double errorWindowWidth = 352.0;
        Double errorWindowHeight = 88.0;

        Label label = new Label(contentText);
        label.setTextFill(Color.WHITE);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        label.setPrefWidth(errorWindowWidth);
        label.setPrefHeight(errorWindowHeight);
        AnchorPane.setRightAnchor(label, 15.0);
        AnchorPane.setLeftAnchor(label, 15.0);
        AnchorPane.setTopAnchor(label, 15.0);

        Separator separator = new Separator(Orientation.HORIZONTAL);
        AnchorPane.setRightAnchor(separator, 15.0);
        AnchorPane.setLeftAnchor(separator, 15.0);
        AnchorPane.setBottomAnchor(separator, 50.0);

        ImageView imageView = new ImageView("resources/attention.png");
        imageView.setFitWidth(40.0);
        imageView.setFitHeight(40.0);
        AnchorPane.setLeftAnchor(imageView, 15.0);
        AnchorPane.setBottomAnchor(imageView, 7.0);

        Button button = new Button("Cancel");
        button.setCancelButton(true);
        AnchorPane.setRightAnchor(button, 15.0);
        AnchorPane.setBottomAnchor(button, 15.0);

        AnchorPane anchorPane = new AnchorPane(label, separator, imageView, button);
        anchorPane.setPrefWidth(382.0);
        anchorPane.setPrefHeight(166.0);
        anchorPane.setStyle("-fx-background-color: #000033; -fx-background-radius: 3;");

        Scene scene = new Scene(anchorPane);
        scene.setFill(Color.TRANSPARENT);

        errorWindow = new Stage();
        errorWindow.setScene(scene);
        errorWindow.initStyle(StageStyle.TRANSPARENT);
        System.out.println("parentStage getXY: "+parentStage.getX()+"  x  "+parentStage.getY());
        System.out.println("parentStage getWidthHeight: "+parentStage.getWidth()+"  x  "+parentStage.getHeight());
        System.out.println("errorWindow getWidthHeight: "+errorWindow.getWidth()+"  x  "+errorWindow.getHeight());
        if(!Double.isNaN(parentStage.getX()) && !Double.isNaN(parentStage.getY())) {
            System.out.println("hello");
            errorWindow.initOwner(parentStage);
            // располагаем окно вывода ошибок по центру главного окна
            errorWindow.setX(parentStage.getX() + parentStage.getWidth() / 2 - errorWindowWidth / 2);
            errorWindow.setY(parentStage.getY() + parentStage.getHeight() / 2 - errorWindowHeight / 2);
        }
        errorWindow.initModality(Modality.WINDOW_MODAL);

        button.setOnAction(event -> errorWindow.close());
    }

    public Stage getErrorStage() {
        return errorWindow;
    }
}