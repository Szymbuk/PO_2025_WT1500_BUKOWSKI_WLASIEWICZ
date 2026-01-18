package project.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorldMapTest {
    int width = 10;
    int height = 10;
    WorldMap map = new WorldMap(width,height,1);

    @Nested
    class canMoveToTest {
        @Test
        void canMoveMapCenter()
        {
            Vector2d position = new Vector2d(5,5);
            assertTrue(map.canMoveTo(position));
        }

        @Test
        void canMoveMapLowerBound()
        {
            Vector2d position = new Vector2d(4,0);
            assertTrue(map.canMoveTo(position));
        }

        @Test
        void canMoveMapLeftBound()
        {
            Vector2d position = new Vector2d(0,3);
            assertTrue(map.canMoveTo(position));
        }

        @Test
        void canMoveMapCorner()
        {
            Vector2d position = new Vector2d(9,9);
            assertTrue(map.canMoveTo(position));
        }

        @Test
        void cannotMoveUnderMap()
        {
            Vector2d position = new Vector2d(4,-1);
            assertFalse(map.canMoveTo(position));
        }

        @Test
        void cannotMoveRightOfMap()
        {
            Vector2d position = new Vector2d(11,10);
            assertFalse(map.canMoveTo(position));
        }

        @Test
        void cannotMoveOutOfCorner()
        {
            Vector2d position = new Vector2d(-1,11);
            assertFalse(map.canMoveTo(position));
        }




    }

    @Nested
    class CorrectPositionTests {
        @Test
        void shouldBeMovedToRightBound()
        {
            Vector2d v = new Vector2d(0, 0);
            Animal animal = new Animal(v, 4, Gender.Male, 100);
            Vector2d v2 = new Vector2d(-1, 0);
            MapDirection startingOrientation = animal.getOrientation();
            map.place(animal);

            Vector2d resultVector = map.correctPosition(v, v2, animal);
            assertEquals(new Vector2d(width-1, 0), resultVector);
            assertEquals(startingOrientation, animal.getOrientation());
        }

        @Test
        void shouldBounceOfLowerBound()
        {
            Vector2d v1 = new Vector2d(0, 0);
            Animal animal = new Animal(v1, 4, Gender.Male, 100);
            try {
                Field orientationField = Animal.class.getDeclaredField("orientation");
                orientationField.setAccessible(true);
                orientationField.set(animal, MapDirection.SOUTH);
            }
            catch(NoSuchFieldException| IllegalArgumentException| IllegalAccessException e){
                e.printStackTrace();
            }


            Vector2d v2 = new Vector2d(0, -1);
            MapDirection startingOrientation = animal.getOrientation();
            map.place(animal);

            Vector2d correctVector = new Vector2d(0,1);
            Vector2d resultVector = map.correctPosition(v1, v2, animal);
            assertEquals(correctVector, resultVector);
            assertEquals(startingOrientation.rotate(4), animal.getOrientation());
        }

        @Test
        void shouldMoveToLeftBound()
        {
            Vector2d v1 = new Vector2d(9, 0);
            Animal animal = new Animal(v1, 4, Gender.Male, 100);
            Vector2d v2 = new Vector2d(10, 0);
            MapDirection startingOrientation = animal.getOrientation();
            map.place(animal);

            Vector2d resultVector = map.correctPosition(v1, v2, animal);
            assertEquals(new Vector2d(0, 0), resultVector);
            assertEquals(startingOrientation, animal.getOrientation());
        }
        @Test
        void shouldBounceOfUpperBound()
        {
            Vector2d v1 = new Vector2d(0, 9);
            Animal animal = new Animal(v1, 4, Gender.Male, 100);
            try {
                Field orientationField = Animal.class.getDeclaredField("orientation");
                orientationField.setAccessible(true);
                orientationField.set(animal, MapDirection.NORTH);
            }
            catch(NoSuchFieldException| IllegalArgumentException| IllegalAccessException e){
                e.printStackTrace();
            }

            Vector2d v2 = new Vector2d(0, 10);
            MapDirection startingOrientation4 = animal.getOrientation();
            map.place(animal);
            IO.println();
            Vector2d correctVector = new Vector2d(0,8);
            Vector2d resultVector = map.correctPosition(v1, v2, animal);
            assertEquals(correctVector, resultVector);
            assertEquals(startingOrientation4.rotate(4), animal.getOrientation());
        }

        @Test
        void shouldNotChangeXPosition()
        {
            Vector2d v1 = new Vector2d(8, 8);
            Animal animal = new Animal(v1, 4, Gender.Male, 100);
            Vector2d v2 = new Vector2d(9, 9);
            MapDirection startingOrientation = animal.getOrientation();
            map.place(animal);

            Vector2d resultVector5 = map.correctPosition(v1, v2, animal);
            assertEquals(v2, resultVector5);
            assertEquals(startingOrientation,animal.getOrientation());
        }
    }

    @Nested
    class MoveTests {
        @Test
        void shouldMoveAnimalToNewPosition() {
            WorldMap map = new WorldMap(10, 10, 1);
            Vector2d startPos = new Vector2d(5, 5);
            Animal animal = new Animal(startPos, 4, Gender.Male, 100);
            map.place(animal);

            map.move(animal);

            assertNotEquals(startPos, animal.getPosition());
            assertEquals(animal, map.objectAt(animal.getPosition()));
        }

        @Test
        void shouldRemoveAnimalFromOldPosition() {
            WorldMap map = new WorldMap(10, 10, 1);
            Vector2d startPos = new Vector2d(5, 5);
            Animal animal = new Animal(startPos, 4, Gender.Male, 100);
            map.place(animal);

            map.move(animal);

            assertNull(map.objectAt(startPos));
        }
    }

    @Nested
    class GeneratePlantsTests {
        @Test
        void shouldGenerateExactNumberOfPlants() {
            WorldMap map = new WorldMap(20, 20, 1);
            map.generatePlants(15);
            assertEquals(15, map.getPlants().size());
        }

        @Test
        void shouldNotGenerateMorePlantsThanMapSize() {
            WorldMap map = new WorldMap(5, 5, 1);
            assertThrows(IllegalStateException.class , () ->map.generatePlants(30));
            assertEquals(25, map.getPlants().size());
        }
    }

    @Nested
    class ConsumePlantsTests {
        @Test
        void animalShouldEatPlant() {
            WorldMap map = new WorldMap(10, 10, 1);
            Vector2d pos = new Vector2d(2, 2);
            Animal animal = new Animal(pos, 4, Gender.Male, 50);
            map.place(animal);
            map.getPlants().put(pos, new Plant(pos));

            map.consumePlants(20);

            assertEquals(70, animal.getEnergy());
            assertFalse(map.getPlants().containsKey(pos));
        }

        @Test
        void strongestAnimalShouldEatPlant() {
            WorldMap map = new WorldMap(10, 10, 1);
            Vector2d pos = new Vector2d(2, 2);
            Animal weak = new Animal(pos, 4, Gender.Male, 50);
            Animal strong = new Animal(pos, 4, Gender.Female, 100);
            map.place(weak);
            map.place(strong);
            map.getPlants().put(pos, new Plant(pos));

            map.consumePlants(20);

            assertEquals(120, strong.getEnergy());
            assertEquals(50, weak.getEnergy());
        }
    }

    @Nested
    class ProcreateTests {
        @Test
        void shouldCreatePairIfConditionsMet() {
            WorldMap map = new WorldMap(10, 10, 1);
            Vector2d pos = new Vector2d(3, 3);
            Animal male = new Animal(pos, 4, Gender.Male, 100);
            Animal female = new Animal(pos, 4, Gender.Female, 100);
            map.place(male);
            map.place(female);

            List<AnimalPair> pairs = map.procreate(50);


            assertEquals(1, pairs.size());
            assertEquals(male, pairs.get(0).male());
            assertEquals(female, pairs.get(0).female());
        }

        @Test
        void shouldNotProcreateIfEnergyTooLow() {
            WorldMap map = new WorldMap(10, 10, 1);
            Vector2d pos = new Vector2d(3, 3);
            Animal male = new Animal(pos, 4, Gender.Male, 20);
            Animal female = new Animal(pos, 4, Gender.Female, 20);
            map.place(male);
            map.place(female);

            List<AnimalPair> pairs = map.procreate(50);
            assertTrue(pairs.isEmpty());
        }

        @Test
        void shouldNotProcreateSameGender() {
            WorldMap map = new WorldMap(10, 10, 1);
            Vector2d pos = new Vector2d(3, 3);
            Animal male1 = new Animal(pos, 4, Gender.Male, 100);
            Animal male2 = new Animal(pos, 4, Gender.Male, 100);
            map.place(male1);
            map.place(male2);

            List<AnimalPair> pairs = map.procreate(50);

            assertTrue(pairs.isEmpty());
        }
    }


    @Nested
    class isOccupiedTest {
        @Test
        void falseOnEmptyField(){
            assertFalse(map.isOccupied(new Vector2d(0,0)));
        }


        @Test
        void trueWhenAnimal(){
            Vector2d v = new Vector2d(0,0);
            Animal animal = new Animal(v,8,Gender.Male,50);
            map.place(animal);
            assertTrue(map.isOccupied(new Vector2d(0,0)));
        }

        @Test
        void trueWhenPlant(){
            map.generatePlants(width*height);
            assertTrue(map.isOccupied(new Vector2d(0,0)));
        }

        @Test
        void trueWhenPlantAndAnimal(){
            map.generatePlants(width*height);
            Vector2d v = new Vector2d(0,0);
            Animal animal = new Animal(v,8,Gender.Male,50);
            map.place(animal);
            assertTrue(map.isOccupied(new Vector2d(0,0)));

        }

    }
}