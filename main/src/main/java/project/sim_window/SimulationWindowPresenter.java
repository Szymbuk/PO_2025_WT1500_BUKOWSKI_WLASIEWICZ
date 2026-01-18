package project.sim_window;

import project.model.Simulation;

public class SimulationWindowPresenter {
    private Simulation simulation;
    private int dayDurationMillis;
    
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public void setDayDurationMillis(int dayDurationMillis) {
        this.dayDurationMillis = dayDurationMillis;
    }
}
