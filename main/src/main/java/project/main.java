package project;

import project.model.*;

public class main {
    static void main(){
        Vector2d v1 = new Vector2d(3,4);
        Animal animal = new Animal(v1);
        WorldMap map = new WorldMap(10,10,1);
        try {
            map.place(animal);
        }
        catch (IncorrectPositionException e){
            e.printStackTrace();
        }
        IO.println(map);
    }
}
