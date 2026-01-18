package project.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import project.model.util.FamilyStructure;

import static org.junit.jupiter.api.Assertions.*;

class AnimalPairTest {

    @Nested
    class breed {
        @Test
        void unrelatedEnergyTest(){

            Animal a1 = new Animal(new Vector2d(1,1),8, Gender.Male,100);
            Animal a2 = new Animal(new Vector2d(1,1),8, Gender.Female,100);
            int energyBefore = a1.getEnergy()+a2.getEnergy();
            AnimalPair pair = new AnimalPair(a1,a2);
            FamilyStructure family = new FamilyStructure();
            family.addAnimal(a1,null);
            family.addAnimal(a2,null);
            Animal baby = pair.breed(8,0.1,0,0,10,10,family,0.0);
            assertEquals(energyBefore,a1.getEnergy()+a2.getEnergy()+ baby.getEnergy());
        }


        @Test
        void Habsburg0EnergyTest(){
            FamilyStructure family = new FamilyStructure();
            Animal p1 =new Animal(new Vector2d(5,5),8, Gender.Male,100);
            Animal p2 =new Animal(new Vector2d(5,5),8, Gender.Male,100);
            AnimalPair parents = new AnimalPair(p1,p2);
            family.addAnimal(p1,null);
            family.addAnimal(p2,null);



            Animal a1 = new Animal(new Vector2d(1,1),8, Gender.Male,100);
            Animal a2 = new Animal(new Vector2d(1,1),8, Gender.Female,100);
            int energyBefore = a1.getEnergy()+a2.getEnergy();
            AnimalPair pair = new AnimalPair(a1,a2);

            family.addAnimal(a1,parents);
            family.addAnimal(a2,parents);
            Animal baby = pair.breed(8,0.1,0,0,10,10,family,0.0);
            assertEquals(energyBefore,a1.getEnergy()+a2.getEnergy()+ baby.getEnergy());
        }


        @Test
        void HabsburgEnergyTest(){
            FamilyStructure family = new FamilyStructure();
            Animal p1 =new Animal(new Vector2d(5,5),8, Gender.Male,100);
            Animal p2 =new Animal(new Vector2d(5,5),8, Gender.Male,100);
            AnimalPair parents = new AnimalPair(p1,p2);
            family.addAnimal(p1,null);
            family.addAnimal(p2,null);



            Animal a1 = new Animal(new Vector2d(1,1),8, Gender.Male,100);
            Animal a2 = new Animal(new Vector2d(1,1),8, Gender.Female,100);
            int energyBefore = a1.getEnergy()+a2.getEnergy();
            AnimalPair pair = new AnimalPair(a1,a2);

            family.addAnimal(a1,parents);
            family.addAnimal(a2,parents);
            Animal baby = pair.breed(8,0.1,0,0,10,10,family,1.0);
            assertTrue(energyBefore>a1.getEnergy()+a2.getEnergy()+ baby.getEnergy());
        }


        @Test
        void genomLengthTest(){
            FamilyStructure family = new FamilyStructure();
            Animal a1 = new Animal(new Vector2d(1,1),8, Gender.Male,100);
            Animal a2 = new Animal(new Vector2d(1,1),8, Gender.Female,100);
            int energyBefore = a1.getEnergy()+a2.getEnergy();
            AnimalPair pair = new AnimalPair(a1,a2);

            family.addAnimal(a1,null);
            family.addAnimal(a2,null);
            Animal baby = pair.breed(8,0.1,0,0,10,10,family,1.0);
            assertEquals(a1.getGenotype().size(),baby.getGenotype().size());
            assertEquals(a2.getGenotype().size(),baby.getGenotype().size());
            assertEquals(a1.getGenotype().size(),a2.getGenotype().size());
        }


        @Test
        void correctGenderGeneTest(){
            FamilyStructure family = new FamilyStructure();
            Animal a1 = new Animal(new Vector2d(1,1),8, Gender.Male,100);
            Animal a2 = new Animal(new Vector2d(1,1),8, Gender.Female,100);
            int energyBefore = a1.getEnergy()+a2.getEnergy();
            AnimalPair pair = new AnimalPair(a1,a2);

            family.addAnimal(a1,null);
            family.addAnimal(a2,null);
            for (int i = 0; i<100000; i++) {
                Animal baby = pair.breed(8, 0.1, 0, 0, 10, 10, family, 1.0);
                assertTrue(baby.getGenotype().getLast() == 0 || baby.getGenotype().getLast() ==1);
            }
        }

        @Test
        void correctGenomMixTest(){
            FamilyStructure family = new FamilyStructure();
            Animal a1 = new Animal(new Vector2d(1,1),8, Gender.Male,100);
            Animal a2 = new Animal(new Vector2d(1,1),8, Gender.Female,100);
            AnimalPair pair = new AnimalPair(a1,a2);

            family.addAnimal(a1,null);
            family.addAnimal(a2,null);
            for (int i = 0; i<100000; i++) {
                Animal baby = pair.breed(8, 0.1, 0, 0, 10, 10, family, 1.0);
                assertTrue((baby.getGenotype().subList(0,4).equals(a1.getGenotype().subList(0,4)) && baby.getGenotype().subList(4,8).equals(a2.getGenotype().subList(4,8)))||
                        baby.getGenotype().subList(0,4).equals(a2.getGenotype().subList(0,4)) && baby.getGenotype().subList(4,8).equals(a1.getGenotype().subList(4,8)));
            }
        }
    }
}