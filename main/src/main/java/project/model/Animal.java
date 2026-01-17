package project.model;


import project.model.util.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Animal implements WorldElement{
    private MapDirection orientation;
    private Vector2d position;
    private int energy;
    private final List<Integer> genotype = new ArrayList<>();
    private int currentGeneIndex;
    private final Gender gender;
    private int childrenNumber = 0;
    private int age = 0;
    private Node familyStructureNode;


    public Animal(Vector2d position,int genNumber, Gender gender, int energy){
        //używany do tworzenia początkowych zwierząt (nieznany genotyp)
        this(position,gender,energy);
        Random rand = new Random();

        //gen number oznacza liczbę genów wraz z genem płci
        for (int i =0; i<genNumber-1; i++){
            genotype.add(rand.nextInt(7));
        }
        currentGeneIndex = rand.nextInt(0,genotype.size()-1); // przed dodaniem genu płci

        int lastGen = switch (gender){
            case Male -> 0;
            case Female -> 1;
        };

        genotype.add(lastGen);
    }

    public Animal(Vector2d position, List<Integer> genotype, Gender gender, int energy){
        // używany do tworzenia zwierząt w wyniku rozmnażania (znany genotyp)
        this(position,gender,energy);

        this.genotype.addAll(genotype);
        Random rand = new Random();
        currentGeneIndex = rand.nextInt(0,genotype.size()-1); // przed dodaniem genu płci
        int lastGen = switch (gender){
            case Male -> 0;
            case Female -> 1;
        };
        genotype.add(lastGen);
    }

    public Animal(Vector2d position, Gender gender, int energy){
        this.position = position;
        this.gender = gender;
        this.energy = energy;
        Random rand = new Random();
        this.orientation = MapDirection.NORTH.rotate(rand.nextInt(0,7));
    }

    @Override
    public String toString(){
        return switch (orientation){
            case NORTH -> " N ";
            case NORTHEAST -> "NE ";
            case NORTHWEST -> "NW ";
            case SOUTH -> " S ";
            case SOUTHEAST -> "SE ";
            case SOUTHWEST -> "SW ";
            case EAST -> " E ";
            case WEST -> " W ";
        };
    }

    @Override
    public boolean isAt(Vector2d position){
        return this.position.equals(position);
    }

    public void move(MoveValidator validator){
        Vector2d previousPosition = position;
        Vector2d moveVector = orientation.toUnitVector();
        Vector2d newPosition = position.add(moveVector);
        if (validator.canMoveTo(newPosition)) {
            position = newPosition;
        }
        else{
            position = validator.correctPosition(previousPosition,newPosition,this);
        }
    }

    @Override
    public Vector2d getPosition(){
        return position;
    }

    public MapDirection getOrientation(){
        return orientation;
    }

    public void turnAround(){
        orientation = orientation.rotate(4);
    }

    public void nextGen(){
        //aktywuje kolejny gen (pomija ostatni gen odpowiedzialny za płeć)
        currentGeneIndex = (currentGeneIndex +1)%(genotype.size()-1);
        Integer gene = genotype.get(currentGeneIndex);
        orientation = orientation.rotate(gene);
    }

    public List<Integer> breed(boolean leftside, int n,int energyLoss){
        energy -= energyLoss;
        childrenNumber +=1;

        if (leftside){
            return genotype.subList(0,n);
        }
        else{
            return genotype.subList(genotype.size()-n,genotype.size());
        }
    }

    public void eat(int energyGained){
        energy += energyGained;
    }

    public int getEnergy() {
        return energy;
    }

    public Gender getGender(){
        return gender;
    }

    public int getChildrenNumber() {
        return childrenNumber;
    }

    public int getAge() {
        return age;
    }

    public void addAge(){
        age+=1;
    }

    public void dailyEnergyLoss(int energyLost){
        energy -= energyLost;
    }

    public void setFamilyStructureNode(Node node){
        familyStructureNode = node;
    }

    public Node getFamilyStructureNode(){
        return familyStructureNode;
    }

    public List<Integer> getGenotype() { return genotype; }

    public int getCurrentGeneIndex(){ return currentGeneIndex;}
}
