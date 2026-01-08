package project;

import jdk.jshell.spi.ExecutionControl;
import project.model.Simulation;

public class main {
    static void main(){
        // 2 sposoby stworzenia obiektu symulacji, chyba ten drugi jest czytelniejszy
        // łatwo o jednym zapomnieć  wtedy program rzuca błędem
        Simulation simulation = new Simulation.SimulationBuilder()
                .setMapHeight(10)
                .setMapWidth(10)
                .setFemaleNumber(3)
                .setMaleNumber(3)
                .setGenomLength(6)
                .setEnergyBreadingLoss(5)
                .setMinMutation(1)
                .setMaxMutation(10)
                .setHabsburgEnergyLoss(0.4)
                .setEnergyStartLevel(100)
                .setPlantStartingQuantity(5)
                .setEnergyBreadingThreshold(50)
                .setEnergyDailLoss(10)
                .setPlantEnergyGain(15)
                .build();
        Simulation simulation2 = new Simulation.SimulationBuilder()
                .setHabsburgParameters(5,5,0.6)
                .setPlantParameters(20,10,15)
                .setGenomSpecification(0,0,6)
                .setEnergyDetails(100,10,50,20)
                .build();
        try {
            simulation.run();
            simulation2.run();
        }
        catch (ExecutionControl.NotImplementedException e){
            e.printStackTrace();
        }



    }
}
