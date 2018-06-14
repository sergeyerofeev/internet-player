package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.controllers.MainController;
import sample.extra.ErrorAlert;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class Main extends Application {
    // определяем порт который будет прослушиваться для предотвращения запуска копии приложения
    private final int PORT = 39999;
    private ServerSocket socket;
    //окно редактирования
    private FXMLLoader editLoader;
    private Parent editRoot;
    // главное окно
    private FXMLLoader mainLoader;
    private Parent mainRoot;

    //private Stage errorStage;

    private double xOffset;
    private double yOffset;

    @Override
    public void start(Stage primaryStage) {

        try {
            // если порт занят, приложение уже запущено, выходим
            socket = new ServerSocket(PORT,0,InetAddress.getByAddress(new byte[] {127,0,0,1}));
        } catch (IOException e) {
            ErrorAlert errorAlert = new ErrorAlert(primaryStage,"Попытка запустить копию прложения!");
            (errorAlert.getErrorStage()).showAndWait();
            System.exit(1);
        }

        try {
            editLoader = new FXMLLoader(getClass().getResource("view/editWindow.fxml"));
            editRoot = editLoader.load();
        } catch (Exception e) {
            ErrorAlert errorAlert = new ErrorAlert(primaryStage,"Произошла ошибка при загрузке окна редактирования!");
            (errorAlert.getErrorStage()).showAndWait();
            System.exit(1);
        }

        try {
            mainLoader = new FXMLLoader(getClass().getResource("view/mainWindow.fxml"));
            mainRoot = mainLoader.load();
        } catch (Exception e) {
            ErrorAlert errorAlert = new ErrorAlert(primaryStage,"Произошла ошибка при загрузке главного окна приложения!");
            (errorAlert.getErrorStage()).showAndWait();
            System.exit(1);
        }
        MainController mainController = mainLoader.getController();
        mainController.setStage(primaryStage);

        mainController.setEditController(editLoader.getController(), editRoot);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(mainRoot);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add("resources/style.css");

        scene.addEventHandler(MouseEvent.ANY, event ->{
            if(event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                xOffset = primaryStage.getX() - event.getScreenX();
                yOffset = primaryStage.getY() - event.getScreenY();
            }
            if(event.getEventType() == MouseEvent.MOUSE_DRAGGED){
                primaryStage.setX(event.getScreenX() + xOffset);
                primaryStage.setY(event.getScreenY() + yOffset);
            }
        });

        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("resources/playIcon.png"));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
