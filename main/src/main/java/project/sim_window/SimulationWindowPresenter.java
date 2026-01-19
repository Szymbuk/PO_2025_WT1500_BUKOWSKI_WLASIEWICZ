package project.sim_window;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import project.model.Animal;
import project.model.MapChangeListener;
import project.model.Plant;
import project.model.Simulation;
import project.model.Vector2d;
import project.model.WorldElement;
import project.model.WorldMap;

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

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        Platform.runLater(() -> {
                drawMap(worldMap);
                updateStatistics();
        });
    }

    private void drawMap(WorldMap map) {
        clearGrid();
        
        double displaySize = 700.0; 
        double cellSize = displaySize / Math.max(map.getWidth(), map.getHeight());

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Vector2d position = new Vector2d(x, y);
                WorldElement element = map.objectAt(position);

                if (element instanceof Animal animal) {
                    VBox container = new VBox(0);
                    container.setAlignment(Pos.CENTER);
                    container.setMinSize(cellSize, cellSize);
                    container.setMaxSize(cellSize, cellSize);

                    Rectangle animalRect = new Rectangle(cellSize, cellSize * 0.7); 
                    animalRect.setFill(Color.BROWN);

                    double energyPct = (double) animal.getEnergy() / simulation.getEnergyStartLevel();
                    
                    double ratio = Math.min(1.0, Math.max(0.0, energyPct));
                    
                    Rectangle track = new Rectangle(cellSize, cellSize * 0.2);
                    track.setFill(Color.web("#CCCCCC"));

                    Rectangle energyFill = new Rectangle(cellSize * ratio, cellSize * 0.2);
                    
                    if (energyPct < 0.3) {
                        energyFill.setFill(Color.RED);
                    } else if (energyPct < 0.7) {
                        energyFill.setFill(Color.YELLOW);
                    } else {
                        energyFill.setFill(Color.LIME);
                    }

                    StackPane energyBarContainer = new StackPane(track, energyFill);
                    energyBarContainer.setAlignment(Pos.CENTER_LEFT);

                    container.getChildren().addAll(animalRect, energyBarContainer);
                    mapGrid.add(container, x, y);

                } else {
                    Rectangle rect = new Rectangle(cellSize, cellSize);
                    if (element instanceof Plant) {
                        rect.setFill(Color.GREEN);
                    } else {
                        rect.setFill(Color.LIGHTGRAY);
                    }
                    mapGrid.add(rect, x, y);
                }
            }
        }
    }

    private synchronized void updateStatistics() {
        animalCountLabel.setText("Zwierzęta: " + (simulation.getMaleNumber() + simulation.getFemaleNumber()));
        grassCountLabel.setText("Rośliny: " + simulation.getCurrentPlants());
        freeFieldsLabel.setText("Wolne pola: " + simulation.getFreeSpace());
        avgEnergyLabel.setText("Średnia energia: " + simulation.getAverageEnergy());
        avgLifespanLabel.setText("Średnia długość życia: " + simulation.getAverageDeadLifeLength());
        avgChildrenLabel.setText("Średnia liczba dzieci: " + simulation.getAverageChildNumber().orElse(0.0));
        mostPopularGenotypeLabel.setText("Dominujący genotyp: \n" + simulation.getMostPopularGenotype()
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
