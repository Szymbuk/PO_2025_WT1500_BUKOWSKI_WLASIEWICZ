package project.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
            Vector2d position = new Vector2d(10,10);
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
            assertEquals(new Vector2d(width, 0), resultVector);
            assertEquals(startingOrientation, animal.getOrientation());
        }

        @Test
        void shouldBounceOfLowerBound()
        {
            Vector2d v1 = new Vector2d(0, 0);
            Animal animal = new Animal(v1, 4, Gender.Male, 100);
            Vector2d v2 = new Vector2d(0, -1);
            MapDirection startingOrientation = animal.getOrientation();
            map.place(animal);

            Vector2d resultVector = map.correctPosition(v1, v2, animal);
            assertEquals(v1, resultVector);
            assertEquals(startingOrientation.rotate(4), animal.getOrientation());
        }

        @Test
        void shouldMoveToLeftBound()
        {
            Vector2d v1 = new Vector2d(10, 0);
            Animal animal = new Animal(v1, 4, Gender.Male, 100);
            Vector2d v2 = new Vector2d(11, 0);
            MapDirection startingOrientation = animal.getOrientation();
            map.place(animal);

            Vector2d resultVector = map.correctPosition(v1, v2, animal);
            assertEquals(new Vector2d(0, 0), resultVector);
            assertEquals(startingOrientation, animal.getOrientation());
        }
        @Test
        void shouldBounceOfUpperBound()
        {
            Vector2d v1 = new Vector2d(0, 10);
            Animal animal = new Animal(v1, 4, Gender.Male, 100);
            Vector2d v2 = new Vector2d(0, 11);
            MapDirection startingOrientation4 = animal.getOrientation();
            map.place(animal);

            Vector2d resultVector = map.correctPosition(v1, v2, animal);
            assertEquals(v1, resultVector);
            assertEquals(startingOrientation4.rotate(4), animal.getOrientation());
        }

        @Test
        void shouldNotChangeXPosition()
        {
            Vector2d v1 = new Vector2d(10, 10);
            Animal animal = new Animal(v1, 4, Gender.Male, 100);
            Vector2d v2 = new Vector2d(11, 11);
            MapDirection startingOrientation = animal.getOrientation();
            map.place(animal);

            Vector2d resultVector5 = map.correctPosition(v1, v2, animal);
            assertEquals(v1, resultVector5);
            assertEquals(startingOrientation.rotate(4), animal.getOrientation());
        }
    }

    @Test
    void move() {
    }

    @Test
    void generatePlants() {
    }

    @Test
    void consumePlants() {
    }

    @Test
    void procreate() {
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