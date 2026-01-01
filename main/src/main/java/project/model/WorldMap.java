package project.model;

import project.model.util.MapVisualizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The interface responsible for interacting with the map of the world.
 * Assumes that Vector2d and MoveDirection classes are defined.
 *
 * @author apohllo, idzik
 */
public class WorldMap implements MoveValidator {

    private final int width;
    private final int height;
    private final int id;
    private final HashMap<Vector2d, Animal> animals = new HashMap<>();
    private final List<MapChangeListener> listeners = new ArrayList<>();


    public WorldMap(int width,int height,int id){
        this.id = id;
        this.height = height;
        this.width = width;
    }

    public void place(Animal animal) throws IncorrectPositionException{
        Vector2d beginning = new Vector2d(0,0);
        Vector2d end = new Vector2d(width,height);
        Vector2d position = animal.getPosition();
        if (!position.precedes(end) || !position.follows(beginning)){
            throw new IncorrectPositionException(position);
        }
        else{
            if (animals.containsKey(position)) {
                throw new IncorrectPositionException(position);
            } else {
                String message = "Dodano zwierze";
                animals.put(position, animal);
                mapChanged(message);
            }
        }
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        Vector2d beginning = new Vector2d(0,0);
        Vector2d end = new Vector2d(width,height);
        return (!isOccupied(position) &&  position.precedes(end) && position.follows(beginning));
    }


    public Boundary getCurrentBounds() {
        return new Boundary(new Vector2d(0,0),new Vector2d(width,height));
    }


    public void move(Animal animal, MoveDirection direction) {
        Vector2d previousPosition = animal.getPosition();
        if (!(animals.containsKey(previousPosition) && animals.get(previousPosition).equals(animal))) {
            return;
        }
        animal.move(direction, this);
        Vector2d newPosition = animal.getPosition();
        animals.remove(previousPosition, animal);
        animals.put(newPosition, animal);

        if (!previousPosition.equals(newPosition)) {
            String message = "Zwierze poruszylo sie";
            mapChanged(message);
        } else if (direction == MoveDirection.LEFT || direction == MoveDirection.RIGHT) {
            String message = "Zwierze obrocilo sie";
            mapChanged(message);
        }
    }


    public boolean isOccupied(Vector2d position) {
        return (objectAt(position) != null);
    }


    public WorldElement objectAt(Vector2d position) {
        return animals.getOrDefault(position, null);
    }


    public HashMap<Vector2d, WorldElement> getElements() {
        return new HashMap<>(animals);
    }


    public void addListener(MapChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MapChangeListener listener) {
        listeners.remove(listener);
    }

    public void mapChanged(String message) {
        for (MapChangeListener listener : listeners) {
            listener.mapChanged(this, message);
        }
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return new MapVisualizer(this).draw(getCurrentBounds().lowerLeft(), getCurrentBounds().upperRight());
    }
}

