package project.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Random;

import javafx.scene.chart.XYChart;
import project.model.util.FamilyStructure;
import project.model.util.FileManager;
import project.model.util.StatsToFile;

public class Simulation implements Runnable {
    private final int mapHeight;
    private final int mapWidth;
    private int startMaleNumber;
    private int startFemaleNumber;
    private final double habsburgEnergyLoss;
    private final int plantStartingQuantity;
    private final int plantEnergyGain;
    private final int plantDailyGrowth;
    private final int energyStartLevel;
    private final int energyDailLoss;
    private final int energyBreadingThreshold;
    private final double energyBreadingLoss;
    private final int minMutation;
    private final int maxMutation;
    private final int genomLength;
    private final boolean trackDescendants;

    private final int dayDurationMillis;
    private final boolean csvSave;
    private boolean isRunning = true;
    private boolean pauseSimulation = false;

    private final WorldMap map;
    private final List<Animal> animals = new ArrayList<>();
    private final List<Animal> deadAnimals = new ArrayList<>();
    private int currentPlants = 0;
    private final FamilyStructure familyStructure = new FamilyStructure();
    private int day = 1;
    private int maleNumber;
    private int femaleNumber;

    private final Map<String, List<XYChart.Data<Number, Number>>> graphHistory = new HashMap<>();

    private Simulation(SimulationBuilder builder){
        this.mapHeight = builder.mapHeight;
        this.mapWidth = builder.mapWidth;
        this.startMaleNumber = builder.startMaleNumber;
        this.startFemaleNumber = builder.startFemaleNumber;
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
        this.maleNumber = startMaleNumber;
        this.femaleNumber = startFemaleNumber;
        this.dayDurationMillis = builder.dayDurationMillis;
        this.csvSave = builder.csvSave;
        this.trackDescendants = builder.trackDescendants;
        String[] statNames = {
            "Liczba zwierząt", 
            "Liczba roślin", 
            "Wolne pola", 
            "Średnia energia", 
            "Średnia długość życia", 
            "Średnia liczba dzieci"
        };
        for (String name : statNames) {
            this.graphHistory.put(name, new ArrayList<>());
        }

        map = new WorldMap(mapWidth,mapHeight,1);


    }
    public void run(){
        createAnimals();
        int numberOfPlants = howManyPlants(plantStartingQuantity);
        map.generatePlants(numberOfPlants);
        currentPlants += numberOfPlants;
        while (isRunning){
            try {
                synchronized (this) {
                    while (pauseSimulation){
                        wait(1000);
                    }
                }
                if (csvSave) {
                    saveDayToCsv();
                }
                performDayCycle();
                saveGraphHistory();
                Thread.sleep(dayDurationMillis);
            } catch (InterruptedException e) {
                System.err.println("Symulacja przerwana.");
                isRunning = false;
                Thread.currentThread().interrupt();
            }
        }
    }

    private synchronized void performDayCycle(){
        deleteDeathAnimals();
        moveAnimals();
        currentPlants -= map.consumePlants(plantEnergyGain);
        animalsProcreation();

        int numberOfPlants = howManyPlants(plantDailyGrowth);
        map.generatePlants(numberOfPlants);
        currentPlants += numberOfPlants;

        animalsDailyEnergyLoss();
        day+=1;
    }
    
    public void closeSimulation() {
        isRunning = false;
    }

    public synchronized void setPause(boolean pauseSimulation) {
        this.pauseSimulation = pauseSimulation;
    }

    public WorldMap getMap() { return this.map; }

    public Map<String, List<XYChart.Data<Number, Number>>> getGraphHistory() {
        return this.graphHistory;
    }

    public int getEnergyStartLevel() {
        return energyStartLevel;
    }

    private void saveDayToCsv() {
        try {
            StatsToFile.saveToFile(this, "daystats.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveGraphHistory() {
        synchronized (graphHistory) {
        graphHistory.get("Liczba zwierząt").add(new XYChart.Data<Number, Number>(day, animals.size()));
        graphHistory.get("Liczba roślin").add( new XYChart.Data<Number, Number>(day, getCurrentPlants()));
        graphHistory.get("Wolne pola").add( new XYChart.Data<Number, Number>(day, getFreeSpace()));
        graphHistory.get("Średnia energia").add( new XYChart.Data<Number, Number>(day, getAverageEnergy()));
        graphHistory.get("Średnia długość życia").add( new XYChart.Data<Number, Number>(day, getAverageDeadLifeLength()));
        graphHistory.get("Średnia liczba dzieci").add( new XYChart.Data<Number, Number>(day, getAverageChildNumber().orElse(0.0)));
        }
    }

    private void createAnimals(){
        Random random = new Random();
        for (int i =0; i< maleNumber; i++){
            int x = random.nextInt(0,mapWidth);
            int y = random.nextInt(0,mapHeight);
            Vector2d position = new Vector2d(x,y);
            Animal animal = new Animal(position,genomLength,Gender.Male,energyStartLevel);
            animals.add(animal);
            map.place(animal);
            familyStructure.addAnimal(animal,null);
        }
        for (int i =0; i< femaleNumber; i++){
            int x = random.nextInt(0,mapWidth);
            int y = random.nextInt(0,mapHeight);
            Vector2d position = new Vector2d(x,y);
            Animal animal = new Animal(position,genomLength,Gender.Female,energyStartLevel);
            animals.add(animal);
            map.place(animal);
            familyStructure.addAnimal(animal,null);
        }
        map.mapChanged("Dodano początkowe zwierzęta");
    }

    private void deleteDeathAnimals(){
        boolean flag = false;
        List<Animal> animalsToRemove = new ArrayList<>();
        synchronized (animals) {
            for (Animal animal : animals){
                if (animal.getEnergy()<=0){
                    animal.setDayOfDeath(day);
                    synchronized (map) {
                        map.removeAnimal(animal);
                    }
                    animalsToRemove.add(animal);
                    deadAnimals.add(animal);
                    flag = true;
                    if (animal.getGender() ==Gender.Male){
                        maleNumber-=1;
                    }
                    else{
                        femaleNumber -=1;
                    }
                }
            }
            for (Animal animal : animalsToRemove){
                animals.remove(animal);
            }
        }
        if (flag){map.mapChanged("Usunięto martwe zwierzęta");}
    }

    private void moveAnimals(){
       for (Animal animal : animals){
           map.move(animal);
           animal.addAge();
           animal.nextGen();
       }
       if (!animals.isEmpty()) {
           map.mapChanged("Zwierzęta poruszyły się");
       }
    }

    private synchronized void animalsProcreation(){
        List<AnimalPair> animalsToBreed= map.procreate(energyBreadingThreshold);
//        Random random = new Random();
        for (AnimalPair animalPair : animalsToBreed){
            //Przeniesiono logikę z simulation do animalPair

//            Animal male = animalPair.male();
//            Animal female = animalPair.female();
//            int totalEnergy = male.getEnergy() + female.getEnergy();
//            double malePart = (double) male.getEnergy() /totalEnergy;
//            int maleGens = (int) (genomLength*malePart);
//            int femaleGens = genomLength-maleGens;
//            List<Integer> newGenotype = new ArrayList<>();
//            int maleEnergyLoss = (int) (male.getEnergy()*energyBreadingLoss);
//            int femaleEnergyLoss = (int) (female.getEnergy()*energyBreadingLoss);
//
//            if (random.nextBoolean()){
//                newGenotype.addAll(male.breed(true,maleGens,maleEnergyLoss));
//                newGenotype.addAll(female.breed(false,femaleGens,femaleEnergyLoss));
//            }
//            else{
//                newGenotype.addAll(female.breed(true,femaleGens,femaleEnergyLoss));
//                newGenotype.addAll(male.breed(false,maleGens,maleEnergyLoss));
//            }
//            int numberOfMutations = random.nextInt(minMutation,maxMutation+1);
//            for (int i = 0; i<numberOfMutations; i+=1){
//                // losujemy który z genów zmieniamy (poza genem płci)
//                int randomIndex = random.nextInt(0,genomLength-1);
//                // losujemy na jaki gen zmieniamy
//                newGenotype.set(randomIndex,random.nextInt(0,8));
//            }
//            int x = random.nextInt(0,mapWidth);
//            int y = random.nextInt(0,mapHeight);
//            Vector2d position = new Vector2d(x,y);
//
//            Gender newGender = switch(newGenotype.getLast()){
//                case 0 -> Gender.Male;
//                case 1 -> Gender.Female;
//                case null, default -> throw new IllegalStateException("Nieprawidłowy gen płci w nowym genotypie");
//            };
//            int energyOfNewAnimal = (int) ((maleEnergyLoss + femaleEnergyLoss) * habsburgEnergyLossCalculation(animalPair));
//            Animal newAnimal = new Animal(position,newGenotype,newGender, energyOfNewAnimal);
            Animal newAnimal = animalPair.breed(genomLength,energyBreadingLoss,minMutation,maxMutation,mapWidth,mapHeight,familyStructure,habsburgEnergyLoss, trackDescendants);
            map.place(newAnimal);
            animals.add(newAnimal);
            familyStructure.addAnimal(newAnimal,animalPair);

            if (newAnimal.getGender() == Gender.Male){
                maleNumber+=1;
            }
            else{
                femaleNumber +=1;
            }
        }
        if (!animalsToBreed.isEmpty()){
            map.mapChanged("W wyniku rozmnażania powstały nowe zwierzaki");
        }
    }

    private int howManyPlants(int plants){
        int maxPlants = (mapWidth)*(mapHeight) - currentPlants;
        return Math.min(plants, maxPlants);
    }

    private void animalsDailyEnergyLoss(){
        for (Animal animal: animals){
            animal.dailyEnergyLoss(energyDailLoss);
        }
    }

    private void saveToFile(String path) throws IOException {
        FileManager.saveSimulationDataToCSV(this,path);
    }

    public synchronized int getMaleNumber(){
        return maleNumber;
    }

    public synchronized int getFemaleNumber(){
        return femaleNumber;
    }

    public int getPlantStartingQuantity() {
        return plantStartingQuantity;
    }

    public int getPlantEnergyGain() {
        return plantEnergyGain;
    }

    public int getPlantDailyGrowth() {
        return plantDailyGrowth;
    }

    public double getHabsburgEnergyLoss() {
        return habsburgEnergyLoss;
    }

    public int getEnergyDailLoss() {
        return energyDailLoss;
    }

    public int getEnergyBreadingThreshold() {
        return energyBreadingThreshold;
    }

    public double getEnergyBreadingLoss() {
        return energyBreadingLoss;
    }

    public int getMinMutation() {
        return minMutation;
    }

    public int getMaxMutation() {
        return maxMutation;
    }

    public int getGenomLength() {
        return genomLength;
    }

    public int getDayDurationMillis(){
        return dayDurationMillis;
    }

    public boolean isCsvSaveEnabled(){
        return csvSave;
    }

    public synchronized int getCurrentPlants(){
        return currentPlants;
    }

    public synchronized List<Integer> getMostPopularGenotype(){
        return map.getMostPopularGenotypes(1).getFirst().getKey();
    }

    public synchronized int getMostPopularGenotypeOccurrenceNumber(){
        return map.getMostPopularGenotypes(1).getFirst().getValue();
    }

    public synchronized int getFreeSpace(){
        return map.getEmptyFieldsCount();
    }

    public synchronized int getAverageEnergy(){
        return map.getAverageEnergy();
    }

    public synchronized int getAverageDeadLifeLength(){
        return deadAnimals.stream().map(Animal::getAge).mapToInt(i -> i).sum();
    }

    public synchronized OptionalDouble getAverageChildNumber() {
        return animals.stream().map(Animal::getChildrenNumber).mapToInt(i->i).average();
    }

    public int getDay(){
        return day;
    }



    public static class SimulationBuilder {
        private Integer mapHeight;
        private Integer mapWidth;
        private Integer startMaleNumber;
        private Integer startFemaleNumber;
        private Double habsburgEnergyLoss;
        private Integer plantStartingQuantity;
        private Integer plantEnergyGain;
        private Integer plantDailyGrowth;
        private Integer energyStartLevel;
        private Integer energyDailLoss;
        private Integer energyBreadingThreshold;
        private Double energyBreadingLoss;
        private Integer genomLength;
        private Integer minMutation = 0;
        private Integer maxMutation;
        private Integer dayDurationMillis;
        private boolean csvSave;
        private boolean trackDescendants;



        public SimulationBuilder setEnergyBreadingLoss(Double energyBreadingLoss) {
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

        public SimulationBuilder setStartFemaleNumber(Integer startFemaleNumber) {
            this.startFemaleNumber = startFemaleNumber;
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

        public SimulationBuilder setStartMaleNumber(Integer startMaleNumber) {
            this.startMaleNumber = startMaleNumber;
            return this;
        }
        
        public SimulationBuilder setDayDurationMillis(Integer dayDurationMillis){
            this.dayDurationMillis = dayDurationMillis;
            return this;
        }

        public SimulationBuilder setCsvSave(boolean csvSave){
            this.csvSave = csvSave;
            return this;
        }

        public SimulationBuilder setMap(Integer width, Integer height){
            this.mapHeight = height;
            this.mapWidth = width;
            return this;
        }
        
        public SimulationBuilder setTrackDescendants(boolean track) {
            this.trackDescendants = track;
            return this;
        }

        public SimulationBuilder setHabsburgParameters(Integer maleNumber,Integer femaleNumber, Double habsburgEnergyLoss){
            this.startMaleNumber = maleNumber;
            this.startFemaleNumber = femaleNumber;
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

        public SimulationBuilder setEnergyDetails(Integer energyStartLevel, Integer energyDailLoss, Integer energyBreadingThreshold, Double energyBreadingLoss){
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
            if (startMaleNumber ==null){
                throw new IllegalStateException("startMaleNumber is null");
            }
            if (startFemaleNumber ==null){
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
