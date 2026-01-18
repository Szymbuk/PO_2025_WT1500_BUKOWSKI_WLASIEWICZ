package project.start;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class StartWindow extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("startwindow.fxml"));
        ScrollPane viewRoot = loader.load();
        loader.getController();

        StartWindowPresenter presenter = loader.getController();

        configureStage(primaryStage, viewRoot);
        primaryStage.show();
    }

    private void configureStage(Stage primaryStage, ScrollPane viewRoot) {
        var scene = new Scene(viewRoot);

        primaryStage.setScene(scene);

        primaryStage.setTitle("Okno startowe");
    }
}
