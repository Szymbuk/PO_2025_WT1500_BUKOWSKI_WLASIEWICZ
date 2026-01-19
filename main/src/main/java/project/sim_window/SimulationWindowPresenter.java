package project.sim_window;

import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import project.model.Animal;
import project.model.Gender;
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

    @FXML private Label trackedGenotypeLabel;
    @FXML private Label trackedActiveGeneLabel;
    @FXML private Label trackedEnergyLabel;
    @FXML private Label trackedGrassEatenLabel;
    @FXML private Label trackedChildrenLabel;
    @FXML private Label trackedDescendantsLabel;
    @FXML private Label trackedAgeLabel;
    @FXML private Label trackedDeathDayLabel;
    @FXML private Label trackedGenderLabel;

    @FXML private Button stopTrackingButton;
    @FXML private Button highlightPreferredFieldsButton;
    @FXML private Button highlightDominantGenotypeButtton;

    @FXML private LineChart<Number, Number> statChart;
    @FXML private ComboBox<String> statChoiceBox;
    private final XYChart.Series<Number, Number> series = new XYChart.Series<>();
    private final String[] statNames = {
        "Liczba zwierząt", 
        "Liczba roślin", 
        "Wolne pola", 
        "Średnia energia", 
        "Średnia długość życia", 
        "Średnia liczba dzieci"
    };

    private boolean highlightDominantGenotype = false;

    private Simulation simulation;
    private Animal trackedAnimal;

    @FXML
    public void initialize() {
        statChoiceBox.getItems().addAll(statNames);
        statChoiceBox.setValue(statNames[0]);
        statChart.getData().add(series);
        series.setName(statNames[0]);

        statChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateActiveSeries(newVal);
            }
        });

    }

    private void updateActiveSeries(String selectedStat) {
        series.setName(selectedStat);
        series.getData().clear();
        series.getData().addAll(simulation.getGraphHistory().get(selectedStat));
    }

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        Platform.runLater(() -> {
                drawMap(worldMap);
                updateStatistics();
                updateTrackedAnimalUI();
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

                if (trackedAnimal != null && trackedAnimal.getPosition().equals(position)) {
                    drawAnimalInCell(trackedAnimal, x, y, true, cellSize);
                } else {

                if (element instanceof Animal animal) {
                    drawAnimalInCell(animal, x, y, false, cellSize);
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
    }

    private void drawAnimalInCell(Animal animal, int x, int y, boolean isTracked, double cellSize) {
        VBox container = new VBox(0);
        container.setAlignment(Pos.CENTER);

        Rectangle animalRect = new Rectangle(cellSize, cellSize * 0.7);
        if (animal.getGender().equals(Gender.Male)) {
            animalRect.setFill(Color.PINK); 
        } else {
            animalRect.setFill(Color.BLUE); 
        }

        if (isTracked) {
            animalRect.setStroke(Color.BLACK);
            animalRect.setStrokeWidth(3);
            animalRect.setStrokeType(StrokeType.INSIDE);
            
            javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow();
            glow.setColor(Color.BLACK);
            glow.setRadius(10);
            animalRect.setEffect(glow);
        }
        
        if (highlightDominantGenotype && animal.getGenotype().equals(simulation.getMostPopularGenotype())) {
            DropShadow yellowGlow = new DropShadow();
            yellowGlow.setColor(Color.YELLOW);
            yellowGlow.setRadius(15);
            yellowGlow.setSpread(0.7);
            animalRect.setEffect(yellowGlow);
        }

        double energyPct = (double) animal.getEnergy() / simulation.getEnergyStartLevel();
        Rectangle energyBar = new Rectangle(cellSize * Math.min(1.0, energyPct), cellSize * 0.2);
        energyBar.setFill(energyPct < 0.3 ? Color.RED : (energyPct < 0.7 ? Color.YELLOW : Color.LIME));

        container.getChildren().addAll(animalRect, energyBar);
        
        container.setOnMouseClicked(event -> {
            this.trackedAnimal = animal;
            updateTrackedAnimalUI();
            drawMap(simulation.getMap()); 
        });

        mapGrid.add(container, x, y);
    }

    private synchronized void updateStatistics() {
        animalCountLabel.setText("Zwierzęta: " + (simulation.getMaleNumber() + simulation.getFemaleNumber()));
        grassCountLabel.setText("Rośliny: " + simulation.getCurrentPlants());
        freeFieldsLabel.setText("Wolne pola: " + simulation.getFreeSpace());
        avgEnergyLabel.setText("Średnia energia: " + simulation.getAverageEnergy());
        avgLifespanLabel.setText("Średnia długość życia: " + simulation.getAverageDeadLifeLength());
        avgChildrenLabel.setText("Średnia liczba dzieci: %.2f".formatted(simulation.getAverageChildNumber().orElse(0.0)));
        mostPopularGenotypeLabel.setText("Dominujący genotyp: \n" + simulation.getMostPopularGenotype()
                + " (" + simulation.getMostPopularGenotypeOccurrenceNumber() + " wystąpień)");

        String selected = statChoiceBox.getValue();
        List<XYChart.Data<Number, Number>> currentHistory = simulation.getGraphHistory().get(selected);

        int displayedCount = series.getData().size();

        if (displayedCount < currentHistory.size()) {
            List<XYChart.Data<Number, Number>> newPoints = currentHistory.subList(displayedCount, currentHistory.size());
            series.getData().addAll(newPoints);
        }
    }

    private void updateTrackedAnimalUI() {
        if (trackedAnimal == null) {
            trackedGenderLabel.setText("Płeć: -");
            trackedGenotypeLabel.setText("Genom: -");
            trackedActiveGeneLabel.setText("Indeks genu: -");
            trackedEnergyLabel.setText("Energia: -");
            trackedAgeLabel.setText("Wiek: -");
            trackedChildrenLabel.setText("Dzieci: -");
            trackedDeathDayLabel.setText("Dzień śmierci: -");
            trackedGrassEatenLabel.setText("Zjedzona trawa: -");
            trackedDescendantsLabel.setText("Potomkowie: -");
            return;
        }

        trackedGenderLabel.setText("Płeć: " + trackedAnimal.getGender().toString());
        trackedGenotypeLabel.setText("Genom: " + trackedAnimal.getGenotype().toString());
        trackedEnergyLabel.setText("Energia: " + trackedAnimal.getEnergy());
        trackedAgeLabel.setText("Wiek: " + trackedAnimal.getAge());
        trackedChildrenLabel.setText("Dzieci: " + trackedAnimal.getChildrenNumber());
        trackedDescendantsLabel.setText("Potomkowie: " + trackedAnimal.getDescendantsNumber());
        trackedActiveGeneLabel.setText("Indeks genu: " + trackedAnimal.getCurrentGeneIndex());
        trackedGrassEatenLabel.setText("Zjedzona trawa: " + trackedAnimal.getGrassEaten());
        
        if (trackedAnimal.getEnergy() <= 0){
            trackedDeathDayLabel.setText("Dzień śmierci: " + trackedAnimal.getDayOfDeath());
        } else {
            trackedDeathDayLabel.setText("Dzień śmierci: -");
        }
    }

    private void clearGrid() {
        mapGrid.getChildren().clear();
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    public void switchHighlightGenotypeButton() {
        highlightDominantGenotype = !highlightDominantGenotype;
        drawMap(simulation.getMap()); 
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

    @FXML
    private void stopTracking() {
        this.trackedAnimal = null;
        updateTrackedAnimalUI();
    }
}
