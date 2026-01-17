package project;

import jdk.jshell.spi.ExecutionControl;
import project.model.Simulation;
import project.model.util.StatsToFile;

import java.io.IOException;
import java.util.Arrays;

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
                .setMap(10,10)
                .setHabsburgParameters(20,20,0.2)
                .setPlantParameters(10,5,100)
                .setGenomSpecification(0,0,7)
                .setEnergyDetails(900,10,110,0.4)
                .build();

        //simulation.run();
        simulation2.run();




    }
}
