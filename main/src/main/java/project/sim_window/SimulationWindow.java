package project.sim_window;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.model.Simulation;

public class SimulationWindow {
    private Stage window;

    public SimulationWindow(Simulation simulation, int dayDurationMillis) {
        window = new Stage();
        window.setTitle("Okno symulacji");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("simulationwindow.fxml"));
        try {
            BorderPane viewRoot = loader.load();
            loader.getController();
            SimulationWindowPresenter presenter = loader.getController();
            presenter.setSimulation(simulation);
            presenter.setDayDurationMillis(dayDurationMillis);
            simulation.getMap().addListener(presenter);
            window.setScene(new Scene(viewRoot));
            Thread sim_thread = new Thread(simulation);
            sim_thread.start();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void show() {
        window.show();
    }
}
