package project.sim_window;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SimulationWindow {
    private Stage stage;

    public SimulationWindow() {
        stage = new Stage();
        stage.setTitle("Secondary Window");

        Label label = new Label("I am a separate window!");
        StackPane root = new StackPane(label);

        stage.setScene(new Scene(root, 250, 150));
    }

    public void show() {
        stage.show(); // NON-BLOCKING
    }
}
