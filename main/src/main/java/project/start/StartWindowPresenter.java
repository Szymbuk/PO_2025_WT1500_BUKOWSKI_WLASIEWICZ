package project.start;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import project.model.Simulation;
import project.model.util.FileManager;
import project.sim_window.SimulationWindow;

public class StartWindowPresenter {
    @FXML private Spinner<Integer> mapWidthSpinner;
    @FXML private Spinner<Integer> mapHeightSpinner;
    @FXML private Spinner<Integer> startGrassSpinner;
    @FXML private Spinner<Integer> grassEnergySpinner;
    @FXML private Spinner<Integer> dailyGrassGrowthSpinner;
    @FXML private Spinner<Integer> maleNumberSpinner;
    @FXML private Spinner<Integer> femaleNumberSpinner;
    @FXML private Spinner<Integer> startEnergySpinner;
    @FXML private Spinner<Integer> reproductionEnergySpinner;
    @FXML private Spinner<Double> reproductionCostSpinner;
    @FXML private Spinner<Double> habsburgEnergyLossSpinner;
    @FXML private Spinner<Integer> moveCostSpinner;
    @FXML private Spinner<Integer> minMutationsSpinner;
    @FXML private Spinner<Integer> maxMutationsSpinner;
    @FXML private Spinner<Integer> genomeLengthSpinner;
    @FXML private Spinner<Integer> dayDurationSpinner;
    @FXML private CheckBox csvSaveCheckBox;
    @FXML private TextField filePathField;
    @FXML private Button fileExplorerButton;
    @FXML private Button saveButton;
    @FXML private Button startButton;

@FXML
    public void initialize() {
        initializeFields();
        
        startButton.setOnAction(event -> onStartClicked());
        saveButton.setOnAction(event -> onSaveClicked());
        fileExplorerButton.setOnAction(event -> setPathByFileExplorer());
    }

    private void initializeFields() {
        mapWidthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 20));
        mapHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 20));
        startGrassSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 500, 50));
        grassEnergySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 20));
        dailyGrassGrowthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 10));
        maleNumberSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 10));
        femaleNumberSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 10));
        startEnergySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 100));
        reproductionEnergySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500, 50));
        reproductionCostSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1.0, 0.5));
        habsburgEnergyLossSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1.0, 0.2));
        moveCostSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50, 1));
        minMutationsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 1));
        maxMutationsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 3));
        genomeLengthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 8));
        dayDurationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 5000, 200));
        
        csvSaveCheckBox.setSelected(false);
    }

 
    private void onStartClicked() {
        try {
            Simulation simulation = buildSimulationFromUI();

            SimulationWindow sim_window = new SimulationWindow(simulation, dayDurationSpinner.getValue());
            sim_window.show();
            
        } catch (IllegalStateException e) {
            showAlert("Błąd konfiguracji", "Nie udało się zbudować symulacji: " + e.getMessage());
        } catch (Exception e) {
            showAlert("Błąd", "Wystąpił nieoczekiwany błąd: " + e.getMessage());
        }
    }

    private void onSaveClicked() {
        try {
            FileManager.saveSimulationParameters(buildSimulationFromUI(), filePathField.getText());
        } catch (Exception e) {
            showAlert("Błąd zapisu", "Nie udało się zapisać parametrów: " + e.getMessage());
        }
    }
    
    private void setPathByFileExplorer() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik do zapisu konfiguracji");
        File selectedFile = fileChooser.showSaveDialog(fileExplorerButton.getScene().getWindow());
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private Simulation buildSimulationFromUI() {
        return new Simulation.SimulationBuilder()
                .setMap(mapWidthSpinner.getValue(), mapHeightSpinner.getValue())
                .setHabsburgParameters(
                        maleNumberSpinner.getValue(), 
                        femaleNumberSpinner.getValue(), 
                        habsburgEnergyLossSpinner.getValue()
                )
                .setPlantParameters(
                        startGrassSpinner.getValue(), 
                        dailyGrassGrowthSpinner.getValue(), 
                        grassEnergySpinner.getValue()
                )
                .setGenomSpecification(
                        minMutationsSpinner.getValue(), 
                        maxMutationsSpinner.getValue(), 
                        genomeLengthSpinner.getValue()
                )
                .setEnergyDetails(
                        startEnergySpinner.getValue(), 
                        moveCostSpinner.getValue(), 
                        reproductionEnergySpinner.getValue(), 
                        reproductionCostSpinner.getValue()
                )
                .build();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}