package project.model;

import jdk.jshell.spi.ExecutionControl;

import java.util.ArrayList;
import java.util.List;

public class Simulation {
    private final int mapHeight;
    private final int mapWidth;
    private int maleNumber;
    private int femaleNumber;
    private final double habsburgEnergyLoss;
    private final int plantStartingQuantity;
    private final int plantEnergyGain;
    private final int plantDailyGrowth;
    private final int energyStartLevel;
    private final int energyDailLoss;
    private final int energyBreadingThreshold;
    private final int energyBreadingLoss;
    private final int minMutation;
    private final int maxMutation;
    private final int genomLength;

    private final WorldMap map;
    private final List<Animal> animals = new ArrayList<>();
    private final List<Animal> deadAnimals = new ArrayList<>();
    private int currentPlants;

    private Simulation(SimulationBuilder builder){
        this.mapHeight = builder.mapHeight;
        this.mapWidth = builder.mapWidth;
        this.maleNumber = builder.maleNumber;
        this.femaleNumber = builder.femaleNumber;
        this.habsburgEnergyLoss = builder.habsburgEnergyLoss;
        this.plantStartingQuantity = builder.plantStartingQuantity;
        this.plantEnergyGain = builder.plantEnergyGain;
        this.plantDailyGrowth = builder.plantDailyGrowth;
        this.energyStartLevel = builder.energyStartLevel;
        this.energyDailLoss = builder.energyDailLoss;
        this.energyBreadingThreshold = builder.energyBreadingThreshold;
        this.energyBreadingLoss = builder.energyBreadingLoss;
        this.minMutation = builder.minMutation;
        this.maxMutation = builder.maxMutation;
        this.genomLength = builder.genomLength;
        map = new WorldMap(mapWidth,mapHeight,1);


    }
    public void run() throws ExecutionControl.NotImplementedException {

        createAnimals();
        int numberOfPlants = howManyPlants(plantStartingQuantity);
        map.generatePlants(numberOfPlants);
        currentPlants += numberOfPlants;

        for (int i=0; i<10; i++){
            deleteDeathAnimals();
            moveAnimals();
            plantConsumption();
            animalsProcreation();

            numberOfPlants = howManyPlants(plantDailyGrowth);
            map.generatePlants(numberOfPlants);
            currentPlants += numberOfPlants;


        }

    }

    private void createAnimals() throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Niezaimplementowano createAnimals");
    }

    private void deleteDeathAnimals() throws ExecutionControl.NotImplementedException {
        for (Animal animal : animals){
            if (animal.getEnergy()<=0){
                map.removeAnimal(animal);
            }
        }
        //zapewniona podstawowa funkcjonalność
        throw new ExecutionControl.NotImplementedException("Niezaimplementowano deleteDeathAnimals");
    }

    private void moveAnimals() throws ExecutionControl.NotImplementedException {
       for (Animal animal : animals){
           map.move(animal);
           animal.addAge();
           animal.nextGen();
       }
        //zapewniona podstawowa funkcjonalność
        //throw new ExecutionControl.NotImplementedException("Niezaimplementowano moveAnimals");
    }

    private void plantConsumption() throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Niezaimplementowano plantConsumption");
    }

    private void animalsProcreation() throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Niezaimplementowano animalsProcreation");
    }

    private int howManyPlants(int plants){
        int maxPlants = mapWidth*mapHeight - currentPlants;
        return Math.min(plants, maxPlants);

    }










    public static class SimulationBuilder {
        private Integer mapHeight;
        private Integer mapWidth;
        private Integer maleNumber;
        private Integer femaleNumber;
        private Double habsburgEnergyLoss;
        private Integer plantStartingQuantity;
        private Integer plantEnergyGain;
        private Integer plantDailyGrowth;
        private Integer energyStartLevel;
        private Integer energyDailLoss;
        private Integer energyBreadingThreshold;
        private Integer energyBreadingLoss;
        private Integer genomLength;
        private Integer minMutation = 0;
        private Integer maxMutation;



        public SimulationBuilder setEnergyBreadingLoss(Integer energyBreadingLoss) {
            this.energyBreadingLoss = energyBreadingLoss;
            return this;
        }

        public SimulationBuilder setGenomLength(Integer genomLength) {
            this.genomLength = genomLength;
            if (maxMutation==null) {maxMutation = genomLength;}
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

        public SimulationBuilder setPlantStartingQuantity(Integer plantStartingQuantity) {
            this.plantStartingQuantity = plantStartingQuantity;
            return this;
        }

        public SimulationBuilder setHabsburgEnergyLoss(Double habsburgEnergyLoss) {
            this.habsburgEnergyLoss = habsburgEnergyLoss;
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

        public SimulationBuilder setMap(Integer width, Integer height){
            this.mapHeight = height;
            this.mapWidth = width;
            return this;
        }

        public SimulationBuilder setHabsburgParameters(Integer maleNumber,Integer femaleNumber, Double habsburgEnergyLoss){
            this.maleNumber = maleNumber;
            this.femaleNumber = femaleNumber;
            this.habsburgEnergyLoss = habsburgEnergyLoss;
            return this;
        }

        public SimulationBuilder setPlantParameters(Integer plantQuantity, Integer plantDailyGrowth, Integer plantEnergyGain){
            this.plantStartingQuantity = plantQuantity;
            this.plantDailyGrowth = plantDailyGrowth;
            this.plantEnergyGain = plantEnergyGain;
            return this;
        }

        public SimulationBuilder setGenomSpecification(Integer minMutation, Integer maxMutation, Integer genomLength){
            this.minMutation = minMutation;
            this.maxMutation = maxMutation;
            this.genomLength = genomLength;
            return this;
        }

        public SimulationBuilder setEnergyDetails(Integer energyStartLevel, Integer energyDailLoss, Integer energyBreadingThreshold, Integer energyBreadingLoss){
            this.energyStartLevel = energyStartLevel;
            this.energyDailLoss = energyDailLoss;
            this.energyBreadingThreshold = energyBreadingThreshold;
            this.energyBreadingLoss = energyBreadingLoss;
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
            if (habsburgEnergyLoss ==null){
                throw new IllegalStateException("energyLoss is null");
            }
            if (plantStartingQuantity ==null){
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
