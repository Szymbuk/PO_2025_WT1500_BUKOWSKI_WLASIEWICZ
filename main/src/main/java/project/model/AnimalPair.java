package project.model;

import project.model.util.FamilyStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public record AnimalPair(Animal male, Animal female) {
    public Animal breed(int genomLength, double energyBreadingLoss,int minMutation, int maxMutation, int mapWidth, int mapHeight, FamilyStructure familyStructure,double habsburgEnergyLoss, boolean trackDescendants){
        Random random = new Random();
        int totalEnergy = male.getEnergy() + female.getEnergy();
        double malePart = (double) male.getEnergy() /totalEnergy;
        int maleGens = (int) (genomLength*malePart);
        int femaleGens = genomLength-maleGens;
        List<Integer> newGenotype = new ArrayList<>();
        int maleEnergyLoss = (int) (male.getEnergy()*energyBreadingLoss);
        int femaleEnergyLoss = (int) (female.getEnergy()*energyBreadingLoss);
        if (random.nextBoolean()){
            newGenotype.addAll(male.breed(true,maleGens,maleEnergyLoss, trackDescendants));
            newGenotype.addAll(female.breed(false,femaleGens,femaleEnergyLoss, trackDescendants));
        }
        else{
            newGenotype.addAll(female.breed(true,femaleGens,femaleEnergyLoss, trackDescendants));
            newGenotype.addAll(male.breed(false,maleGens,maleEnergyLoss, trackDescendants));
        }
        int numberOfMutations = random.nextInt(minMutation,maxMutation+1);
        for (int i = 0; i<numberOfMutations; i+=1){
            // losujemy który z genów zmieniamy (poza genem płci)
            int randomIndex = random.nextInt(0,genomLength-1);
            // losujemy na jaki gen zmieniamy
            newGenotype.set(randomIndex,random.nextInt(0,8));
        }
        int x = random.nextInt(0,mapWidth);
        int y = random.nextInt(0,mapHeight);
        Vector2d position = new Vector2d(x,y);

        Gender newGender = switch(newGenotype.getLast()){
            case 0 -> Gender.Male;
            case 1 -> Gender.Female;
            case null, default -> throw new IllegalStateException("Nieprawidłowy gen płci w nowym genotypie");
        };
        int energyOfNewAnimal = (int) ((maleEnergyLoss + femaleEnergyLoss) * habsburgEnergyLossCalculation(familyStructure,habsburgEnergyLoss));
        return new Animal(position,newGenotype,newGender, energyOfNewAnimal);
    }

    private double habsburgEnergyLossCalculation(FamilyStructure familyStructure,double habsburgEnergyLoss){
        Optional<Integer> relationshipLevel = familyStructure.relationshipLevel(this);
        return relationshipLevel.map(integer -> (1 - (1.0 / integer) * habsburgEnergyLoss)).orElse(1.0);
    }
}
