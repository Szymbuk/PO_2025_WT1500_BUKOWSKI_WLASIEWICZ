package project.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {
    WorldMap map = new WorldMap(10,10,1);


    @Test
    void alwaysChangePositionAfterMove() {
        for (int i = 0;i<10;i++) {
            for (int j = 0; j<10; j++){
                Vector2d v1 = new Vector2d(i, j);
                Animal animal = new Animal(v1, 6, Gender.Male, 100);
                map.place(animal);
                animal.move(map);
                assertNotEquals(v1, animal.getPosition());
            }
        }
    }


    @Test
    void nextGen() {
        Vector2d v1 = new Vector2d(4, 4);
        int numberOfGens = 6;
        Animal animal = new Animal(v1, numberOfGens, Gender.Male, 100);
        int prevGenInd = animal.getCurrentGeneIndex();
        for (int i =0; i<10; i++){
            animal.nextGen();
            int newGeneInd = animal.getCurrentGeneIndex();
            assertNotEquals(prevGenInd,newGeneInd);
            assertNotEquals(numberOfGens-1,newGeneInd);
            assertTrue(prevGenInd == newGeneInd-1 || newGeneInd == 0);
            prevGenInd = newGeneInd;
        }
    }

    @Test
    void breed() {

        Vector2d v1 = new Vector2d(4, 4);
        int numberOfGens = 6;
        Animal animal = new Animal(v1, numberOfGens, Gender.Male, 100);
        int energyLoss = 40;
        int childNumber = animal.getChildrenNumber();
        int startEnergy = animal.getEnergy();

        animal.breed(true,2,energyLoss, false);

        assertEquals(startEnergy-energyLoss, animal.getEnergy());
        assertEquals(childNumber+1,animal.getChildrenNumber());
        

    }

}