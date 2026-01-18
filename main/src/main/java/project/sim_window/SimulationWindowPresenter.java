package project.sim_window;

import project.model.Simulation;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import project.model.*;

public class SimulationWindowPresenter implements MapChangeListener {

    @FXML private GridPane mapGrid;
    @FXML private Label animalCountLabel;
    @FXML private Label grassCountLabel;
    @FXML private Label mostPopularGenotypeLabel;
    @FXML private Label freeFieldsLabel;
    @FXML private Label avgEnergyLabel;
    @FXML private Label avgLifespanLabel;
    @FXML private Label avgChildrenLabel;
    @FXML private Button pauseButton;
    @FXML private Button resumeButton;
    @FXML private Button closeButton;

    private Simulation simulation;
    private int dayDurationMillis;

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        Platform.runLater(() -> {
            drawMap(worldMap);
            updateStatistics();
        });
    }

    private void drawMap(WorldMap map) {
        clearGrid();
        int mapWidth = map.getWidth();
        int mapHeight = map.getHeight();

        double cellSize = Math.min(700.0 / mapWidth, 700.0 / mapHeight);

        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                Vector2d position = new Vector2d(x, y);
                WorldElement element = map.objectAt(position);

                Rectangle rect = new Rectangle(cellSize, cellSize);
                
                if (element instanceof Animal) {
                    rect.setFill(Color.BROWN); 
                } else if (element instanceof Plant) {
                    rect.setFill(Color.GREEN);
                } else {
                    rect.setFill(Color.LIGHTGRAY); 
                }

                mapGrid.add(rect, x, y);
            }
        }
    }

    private void updateStatistics() {
        animalCountLabel.setText("Zwierzęta: " + (simulation.getMaleNumber() + simulation.getFemaleNumber()));
        grassCountLabel.setText("Rośliny: " + simulation.getCurrentPlants());
        freeFieldsLabel.setText("Wolne pola: " + simulation.getFreeSpace());
        avgEnergyLabel.setText("Średnia energia: " + simulation.getAverageEnergy());
        avgLifespanLabel.setText("Średnia długość życia: " + simulation.getAverageDeadLifeLength());
        avgChildrenLabel.setText("Średnia liczba dzieci: " + simulation.getAverageChildNumber().orElse(0.0));
        mostPopularGenotypeLabel.setText("Dominujący genotyp: " + simulation.getMostPopularGenotype()
                + " (" + simulation.getMostPopularGenotypeOccurrenceNumber() + " wystąpień)");
    }

    private void clearGrid() {
        mapGrid.getChildren().clear();
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }
    
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public void setDayDurationMillis(int dayDurationMillis) {
        this.dayDurationMillis = dayDurationMillis;
    }

    public void pauseSimulation() {
        this.simulation.setPause(true);
        pauseButton.setDisable(true);
        resumeButton.setDisable(false);
    }

    public void resumeSimulation() {
        this.simulation.setPause(false);
        pauseButton.setDisable(false);
        resumeButton.setDisable(true);
    }

    public synchronized void closeSimulation() {
        this.simulation.closeSimulation();
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
