package project.start;

import java.io.File;
import java.io.IOException;

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
    @FXML private Button loadButton;
    @FXML private Button saveButton;
    @FXML private Button startButton;

    private Simulation simulation;

@FXML
    public void initialize() {
        initializeFields();
        
        startButton.setOnAction(event -> onStartClicked());
        saveButton.setOnAction(event -> onSaveClicked());
        loadButton.setOnAction(event -> onLoadClicked());
        fileExplorerButton.setOnAction(event -> setPathByFileExplorer());
    }

    private void initializeFields() {
        mapWidthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 20));
        mapWidthSpinner.setEditable(true);
        mapHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 20));
        mapHeightSpinner.setEditable(true);
        startGrassSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 500, 50));
        startGrassSpinner.setEditable(true);
        grassEnergySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 20));
        grassEnergySpinner.setEditable(true);
        dailyGrassGrowthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 10));
        dailyGrassGrowthSpinner.setEditable(true);
        maleNumberSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 10));
        maleNumberSpinner.setEditable(true);
        femaleNumberSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 10));
        femaleNumberSpinner.setEditable(true);
        startEnergySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 100));
        startEnergySpinner.setEditable(true);
        reproductionEnergySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500, 50));
        reproductionEnergySpinner.setEditable(true);
        reproductionCostSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1.0, 0.5));
        reproductionCostSpinner.setEditable(true);
        habsburgEnergyLossSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1.0, 0.2));
        habsburgEnergyLossSpinner.setEditable(true);
        moveCostSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50, 1));
        moveCostSpinner.setEditable(true);
        minMutationsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 1));
        minMutationsSpinner.setEditable(true);
        maxMutationsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 3));
        maxMutationsSpinner.setEditable(true);
        genomeLengthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 8));
        genomeLengthSpinner.setEditable(true);
        dayDurationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 5000, 200));
        dayDurationSpinner.setEditable(true);
        
        csvSaveCheckBox.setSelected(false);
    }

 
    private void onStartClicked() {
        try {
            simulation = buildSimulationFromUI();

            SimulationWindow sim_window = new SimulationWindow(simulation);
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

    private void onLoadClicked() {
        try {
            Simulation loadedSimulation = FileManager.loadSimulationParameters(filePathField.getText());
            loadSimulation(loadedSimulation);
            showAlert("Sukces", "Pomyślnie wczytano konfigurację.");
        } catch (Exception e) {
            showAlert("Błąd wczytywania", "Nie udało się wczytać parametrów: " + e.getMessage());
        }
    }

    private void loadSimulation(Simulation loadedSimulation) {
        mapWidthSpinner.getValueFactory().setValue(loadedSimulation.getMap().getWidth());
        mapHeightSpinner.getValueFactory().setValue(loadedSimulation.getMap().getHeight());
        startGrassSpinner.getValueFactory().setValue(loadedSimulation.getPlantStartingQuantity());
        grassEnergySpinner.getValueFactory().setValue(loadedSimulation.getPlantEnergyGain());
        dailyGrassGrowthSpinner.getValueFactory().setValue(loadedSimulation.getPlantDailyGrowth());
        maleNumberSpinner.getValueFactory().setValue(loadedSimulation.getMaleNumber());
        femaleNumberSpinner.getValueFactory().setValue(loadedSimulation.getFemaleNumber());
        startEnergySpinner.getValueFactory().setValue(loadedSimulation.getEnergyStartLevel());
        reproductionEnergySpinner.getValueFactory().setValue(loadedSimulation.getEnergyBreadingThreshold());
        reproductionCostSpinner.getValueFactory().setValue(loadedSimulation.getEnergyBreadingLoss());
        habsburgEnergyLossSpinner.getValueFactory().setValue(loadedSimulation.getHabsburgEnergyLoss());
        moveCostSpinner.getValueFactory().setValue(loadedSimulation.getEnergyDailLoss());
        minMutationsSpinner.getValueFactory().setValue(loadedSimulation.getMinMutation());
        maxMutationsSpinner.getValueFactory().setValue(loadedSimulation.getMaxMutation());
        genomeLengthSpinner.getValueFactory().setValue(loadedSimulation.getGenomLength());
        dayDurationSpinner.getValueFactory().setValue(loadedSimulation.getDayDurationMillis());
        csvSaveCheckBox.setSelected(loadedSimulation.isCsvSaveEnabled());
    }
    
    private void setPathByFileExplorer() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik do zapisu konfiguracji");
        File selectedFile = fileChooser.showSaveDialog(fileExplorerButton.getScene().getWindow());
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void onSavePreset(javafx.event.ActionEvent event) {
        Button btn = (Button) event.getSource();
        String slot = (String) btn.getUserData();
        String fileName = "preset_" + slot + ".config";
        try {
            FileManager.saveSimulationParameters(buildSimulationFromUI(), fileName);
            showAlert("Sukces", "Pomyślnie zapisano konfigurację.");
        }
        catch (IOException e) {
            showAlert("Błąd zapisu", "Nie udało się zapisać presetu: " + e.getMessage());
        }
    }

    @FXML
    private void onLoadPreset(javafx.event.ActionEvent event) {
        Button btn = (Button) event.getSource();
        String slot = (String) btn.getUserData();
        String fileName = "preset_" + slot + ".config";
        try {
            Simulation sim = FileManager.loadSimulationParameters(fileName);
            loadSimulation(sim);
            showAlert("Sukces", "Pomyślnie wczytano konfigurację.");
        } catch (IOException e) {
            showAlert("Błąd wczytywania", "Nie udało się wczytać presetu: " + e.getMessage());
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
                .setDayDurationMillis(dayDurationSpinner.getValue())
                .setCsvSave(csvSaveCheckBox.isSelected())
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