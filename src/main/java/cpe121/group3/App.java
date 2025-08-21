package cpe121.group3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        scene = new Scene(loadFXML("mainmenu"), 1000, 600);
        stage.setTitle("Appointment Manager");

        Image icon = new Image(getClass().getResourceAsStream("/cpe121/group3/assets/Appointment-Manager-Logo.png"));
        stage.getIcons().add(icon);
        
        stage.initStyle(StageStyle.UNDECORATED); // disable native title bar
        stage.setResizable(true);
        stage.setMinWidth(800); // Minimum window width
        stage.setMinHeight(400); // Minimum window height
        stage.setScene(scene);
        stage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}