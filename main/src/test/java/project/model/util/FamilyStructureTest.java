package project.model.util;

import org.junit.jupiter.api.Test;
import project.model.Animal;
import project.model.AnimalPair;
import project.model.Gender;
import project.model.Vector2d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FamilyStructureTest {

    @Test
    void relationshipLevelTest() {
        Animal a1 = new Animal(new Vector2d(1,1),8, Gender.Male,100);
        Animal a2 = new Animal(new Vector2d(1,1),8, Gender.Female,100);
        AnimalPair pair = new AnimalPair(a1,a2);
        FamilyStructure family = new FamilyStructure();
        family.addAnimal(a1,null);
        family.addAnimal(a2,null);
        assertNull(family.relationshipLevel(pair).orElse(null));


        Animal baby1 = pair.breed(8,0.1,0,0,10,10,family,0.1);
        Animal baby2 = pair.breed(8,0.1,0,0,10,10,family,0.1);
        family.addAnimal(baby1,pair);
        family.addAnimal(baby2,pair);
        assertEquals(2,family.relationshipLevel(new AnimalPair(baby1,baby2)).orElse(100));
        assertEquals(1,family.relationshipLevel(new AnimalPair(baby1,a1)).orElse(100));
    }
}