package project;

import jdk.jshell.spi.ExecutionControl;
import project.model.Simulation;

public class main {
    static void main(){
        // 2 sposoby stworzenia obiektu symulacji, chyba ten drugi jest czytelniejszy
        // łatwo o jednym zapomnieć  wtedy program rzuca błędem
//        Simulation simulation = new Simulation.SimulationBuilder()
//                .setMapHeight(10)
//                .setMapWidth(10)
//                .setFemaleNumber(3)
//                .setMaleNumber(3)
//                .setGenomLength(6)
//                .setEnergyBreadingLoss(0.1)
//                .setMinMutation(1)
//                .setMaxMutation(10)
//                .setHabsburgEnergyLoss(0.4)
//                .setEnergyStartLevel(100)
//                .setPlantStartingQuantity(5)
//                .setEnergyBreadingThreshold(50)
//                .setEnergyDailLoss(10)
//                .setPlantEnergyGain(15)
//                .setPlantDailyGrowth(10)
//                .build();
        Simulation simulation2 = new Simulation.SimulationBuilder()
                .setMap(3,3)
                .setHabsburgParameters(2,2,0.9)
                .setPlantParameters(2,2,20)
                .setGenomSpecification(0,0,6)
                .setEnergyDetails(100,10,50,0.3)
                .build();
        try {
            //simulation.run();
            simulation2.run();
        }
        catch (ExecutionControl.NotImplementedException e){
            e.printStackTrace();
        }



    }
}
