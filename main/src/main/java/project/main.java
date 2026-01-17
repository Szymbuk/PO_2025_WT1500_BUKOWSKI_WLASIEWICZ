package project;

import jdk.jshell.spi.ExecutionControl;
import project.model.Simulation;
import project.start.StartWindow;
import javafx.application.Application;

public class main {
    public static void main(String[] args) {
        Application.launch(StartWindow.class, args);
    // 2 sposoby stworzenia obiektu symulacji, chyba ten drugi jest czytelniejszy
    // łatwo o jednym zapomnieć  wtedy program rzuca błędem
    //    Simulation simulation = new Simulation.SimulationBuilder()
    //            .setHabsburgEnergyLoss(0.4)
    //            .build();
    //    try {
    //        //simulation.run();
    //        simulation2.run();
    //    }
    //    catch (ExecutionControl.NotImplementedException e){
    //        e.printStackTrace();
    //    }
    }
}
