package project.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Animal implements WorldElement{
    private MapDirection orientation;
    private Vector2d position;
    private int energy;
    private final List<Integer> genotype = new ArrayList<>();
    private int currentGeneIndex;
    private Gender gender;
    private int childrenNumber = 0;
    private int age = 0;

    public Animal(MapDirection orientation, Vector2d position,int genNumber){
        this.orientation = orientation;
        this.position = position;

        Random rand = new Random();
        for (int i =0; i<genNumber; i++){

            genotype.add(rand.nextInt(7));
        }
        genotype.add(rand.nextInt(1));
        gender = switch (genotype.getLast()){
            case 0 -> Gender.Male;
            case 1 -> Gender.Female;
            case null, default -> throw new IllegalStateException("Last gene must be 1 or 0");
        };
    }
    public Animal(Vector2d position,int genNumber){
        this(MapDirection.NORTH,position,genNumber);
    }

    public Animal(int genNumber){this(new Vector2d(2,2),genNumber);}

    @Override
    public String toString(){
        return switch (orientation){
            case NORTH -> "N";
            case NORTHEAST -> "NE";
            case NORTHWEST -> "NW";
            case SOUTH -> "S";
            case SOUTHEAST -> "SE";
            case SOUTHWEST -> "SW";
            case EAST -> "E";
            case WEST -> "W";
        };
    }

    @Override
    public boolean isAt(Vector2d position){
        return this.position.equals(position);
    }

    public void move(MoveValidator validator){
        Vector2d moveVector = orientation.toUnitVector();
        Vector2d new_position = position.add(moveVector);
        if (validator.canMoveTo(new_position)) {
            position = new_position;
        }
        else{
            position = validator.correctPosition(position,this);
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
        currentGeneIndex = (currentGeneIndex +1)%(genotype.size()-2);
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
}
