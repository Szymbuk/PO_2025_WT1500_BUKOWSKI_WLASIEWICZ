package project.model.util;

import project.model.Vector2d;
import project.model.WorldElement;

import java.util.HashMap;
import java.util.List;

public class Simulation {
    private final int mapHeight;
    private final int mapWidth;
    private int maleNumber;
    private int femaleNumber;
    private final float energyLoss;
    private final int plantQuantity;
    private final int plantEnergyGain;
    private final int plantDailyGrowth;
    private final int energyStartLevel;
    private final int energyDailLoss;
    private final int energyBreadingThreshold;
    private final int energyBreadingLoss;
    private final int minMutation;
    private final int maxMutation;
    private final int genomLength;

    private final HashMap<Vector2d, List<WorldElement>> mapElements = new HashMap<>();

    public Simulation(SimulationBuilder builder){
        this.mapHeight = builder.mapHeight;
        this.mapWidth = builder.mapWidth;
        this.maleNumber = builder.maleNumber;
        this.femaleNumber = builder.femaleNumber;
        this.energyLoss = builder.energyLoss;
        this.plantQuantity = builder.plantQuantity;
        this.plantEnergyGain = builder.plantEnergyGain;
        this.plantDailyGrowth = builder.plantDailyGrowth;
        this.energyStartLevel = builder.energyStartLevel;
        this.energyDailLoss = builder.energyDailLoss;
        this.energyBreadingThreshold = builder.energyBreadingThreshold;
        this.energyBreadingLoss = builder.energyBreadingLoss;
        this.minMutation = builder.minMutation;
        this.maxMutation = builder.maxMutation;
        this.genomLength = builder.genomLength;

    }










    private class SimulationBuilder {
        private Integer mapHeight;
        private Integer mapWidth;
        private Integer maleNumber;
        private Integer femaleNumber;
        private Float energyLoss;
        private Integer plantQuantity;
        private Integer plantEnergyGain;
        private Integer plantDailyGrowth;
        private Integer energyStartLevel;
        private Integer energyDailLoss;
        private Integer energyBreadingThreshold;
        private Integer energyBreadingLoss;
        private Integer minMutation;
        private Integer maxMutation;
        private Integer genomLength;

        public SimulationBuilder setEnergyBreadingLoss(Integer energyBreadingLoss) {
            this.energyBreadingLoss = energyBreadingLoss;
            return this;
        }

        public SimulationBuilder setGenomLength(Integer genomLength) {
            this.genomLength = genomLength;
            return this;
        }

        public SimulationBuilder setMaxMutation(Integer maxMutation) {
            this.maxMutation = maxMutation;
            return this;
        }

        public SimulationBuilder setMinMutation(Integer minMutation) {
            this.minMutation = minMutation;
            return this;
        }

        public SimulationBuilder setEnergyBreadingThreshold(Integer energyBreadingThreshold) {
            this.energyBreadingThreshold = energyBreadingThreshold;
            return this;
        }

        public SimulationBuilder setEnergyDailLoss(Integer energyDailLoss) {
            this.energyDailLoss = energyDailLoss;
            return this;
        }

        public SimulationBuilder setEnergyStartLevel(Integer energyStartLevel) {
            this.energyStartLevel = energyStartLevel;
            return this;
        }

        public SimulationBuilder setPlantDailyGrowth(Integer plantDailyGrowth) {
            this.plantDailyGrowth = plantDailyGrowth;
            return this;
        }

        public SimulationBuilder setPlantEnergyGain(Integer plantEnergyGain) {
            this.plantEnergyGain = plantEnergyGain;
            return this;
        }

        public SimulationBuilder setPlantQuantity(Integer plantQuantity) {
            this.plantQuantity = plantQuantity;
            return this;
        }

        public SimulationBuilder setEnergyLoss(Float energyLoss) {
            this.energyLoss = energyLoss;
            return this;
        }

        public SimulationBuilder setFemaleNumber(Integer femaleNumber) {
            this.femaleNumber = femaleNumber;
            return this;
        }

        public SimulationBuilder setMapHeight(Integer mapHeight) {
            this.mapHeight = mapHeight;
            return this;
        }

        public SimulationBuilder setMapWidth(Integer mapWidth) {
            this.mapWidth = mapWidth;
            return this;
        }

        public SimulationBuilder setMaleNumber(Integer maleNumber) {
            this.maleNumber = maleNumber;
            return this;
        }
        public Simulation build(){
            if (mapHeight==null){
                throw new IllegalStateException("mapHeight is null");
            }
            if (mapWidth==null){
                throw new IllegalStateException("mapWidth is null");
            }
            if (maleNumber==null){
                throw new IllegalStateException("maleNumber is null");
            }
            if (femaleNumber==null){
                throw new IllegalStateException("femaleNumber is null");
            }
            if (energyLoss==null){
                throw new IllegalStateException("energyLoss is null");
            }
            if (plantQuantity==null){
                throw new IllegalStateException("plantQuantity is null");
            }
            if (plantEnergyGain==null){
                throw new IllegalStateException("plantEnergyGain is null");
            }
            if (plantDailyGrowth==null){
                throw new IllegalStateException("plantDailyGrowth is null");
            }
            if (energyStartLevel==null){
                throw new IllegalStateException("energyStartLevel is null");
            }
            if (energyDailLoss==null){
                throw new IllegalStateException("energyDailLoss is null");
            }
            if (energyBreadingThreshold==null){
                throw new IllegalStateException("energyBreadingThreshold is null");
            }
            if (energyBreadingLoss==null){
                throw new IllegalStateException("energyBreadingLoss is null");
            }
            if (minMutation==null){
                throw new IllegalStateException("minMutation is null");
            }
            if (maxMutation==null){
                throw new IllegalStateException("maxMutation is null");
            }
            if (genomLength==null){
                throw new IllegalStateException("genomLength is null");
            }


            return new Simulation(this);
        }
    }
}
