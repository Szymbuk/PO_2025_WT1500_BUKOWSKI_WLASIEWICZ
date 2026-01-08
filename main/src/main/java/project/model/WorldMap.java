package project.model;

import jdk.jshell.spi.ExecutionControl;
import project.model.util.MapVisualizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

public class WorldMap implements MoveValidator {
    private final int width;
    private final int height;
    private final int id;
    private final HashMap<Vector2d, List<Animal>> animals = new HashMap<>();
    private final HashMap<Vector2d, Plant> plants = new HashMap<>();
    private final List<MapChangeListener> listeners = new ArrayList<>();
    private final Vector2d beginning;
    private final Vector2d end;
    private final Vector2d jungleBeginning;
    private final Vector2d jungleEnd;


    public WorldMap(int width,int height,int id){
        this.id = id;
        this.height = height;
        this.width = width;
        this.beginning= new Vector2d(0,0);
        this.end = new Vector2d(width,height);

        double theoreticalJungleSize = width * 0.2;
        int jungleLength = Math.toIntExact(Math.round(theoreticalJungleSize));
        if (!(jungleLength%2 == height%2)){
            jungleLength = (int) theoreticalJungleSize;
            if (!(jungleLength%2 == height%2)){
                jungleLength +=1;
            }
        }
        int margin = (width-jungleLength)/2;
        jungleBeginning = new Vector2d(0, margin);
        jungleEnd = new Vector2d(width,margin + jungleLength);
    }


    public void place(Animal animal){
        Vector2d position = animal.getPosition();
        if (!position.precedes(end) || !position.follows(beginning)){
            //chcemy wyłapać ewentualne błędne działanie programu
            throw new IncorrectPositionException(position);
        }
        else{
                if (! animals.containsKey(position)){
                    animals.put(position,new ArrayList<>());
                }
                String message = "Dodano zwierze";
                animals.get(animal.getPosition()).add(animal);
                mapChanged(message);
            }
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return (!isOccupied(position) &&  position.precedes(end) && position.follows(beginning));
    }

    @Override
    public Vector2d correctPosition(Vector2d position,Animal animal) {
        if (position.y() <beginning.y() || position.y() > end.y()){
            animal.turnAround();
            return position;
        }

        int x;
        if (position.x() < beginning.x()){
            x = end.x();
        } else if (position.x() > end.x()) {
            x = beginning.x();
        }
        else{
            x = position.x();
        }
        return new Vector2d(x,position.y());
    }


    public Boundary getCurrentBounds() {
        return new Boundary(new Vector2d(0,0),new Vector2d(width,height));
    }


    public void move(Animal animal) {
        Vector2d previousPosition = animal.getPosition();
        if (!(animals.containsKey(previousPosition) && animals.get(previousPosition).equals(animal))) {
            return;
        }
        animal.move(this);
        Vector2d newPosition = animal.getPosition();
        animals.get(animal.getPosition()).remove(animal);
        animals.get(newPosition).add(animal);

        if (!previousPosition.equals(newPosition)) {
            String message = "Zwierze poruszylo sie na pozycję %s".formatted(newPosition);
            mapChanged(message);
        } else{
            String message = "Zwierze odbilo sie, jego aktualny zwrot to %s".formatted(animal.getOrientation());
            mapChanged(message);
        }
    }

    public void generatePlants(Integer numberOfPlants){
        Random random = new Random();

        List<Vector2d> positionList = new ArrayList<>();

        int desert = 0;
        int jungle = 0;
        // Mapa musi dostać taką liczbę roślin, jaką będzie mogła wygenerować,
        // Należy zabezpieczyć się w Simulation
        while (numberOfPlants>0) {
            //pustynia
            if (random.nextDouble() < 0.2) {
                if (placeDesert(positionList)) {
                    numberOfPlants -= 1;
                    desert +=1;
                } else if (placeJungle(positionList)) {
                    numberOfPlants -= 1;
                    jungle +=1;
                } else {
                    throw new IllegalStateException("Podano złą liczbę roślin do posadzenia");
                }



            } else {
                //dżungla
                if (placeJungle(positionList)) {
                    numberOfPlants -= 1;
                    jungle +=1;
                } else if (placeDesert(positionList)) {
                    numberOfPlants -= 1;
                    desert +=1;
                } else {
                    throw new IllegalStateException("Podano złą liczbę roślin do posadzenia");
                }
            }


        }
        String message = "posadzono rośliny: %d w dzungli, %d na pustyni".formatted(jungle,desert);
        for (Vector2d vector : positionList) {
            Plant plant = new Plant(vector);
            plants.put(vector, plant);
        }
        mapChanged(message);
        //throw new ExecutionControl.NotImplementedException("Niezaimplementowano generatePlants");
    }

    private boolean placeJungle(List<Vector2d> positionList){
        // Jeżeli jest miejsce to umieszcza roślinę w dżungli, w przeciwnym razie zwraca fałsz
        Random random = new Random();
        int stepCounter = 0;
        boolean flag = false;
        while (stepCounter<width*height) {
            int x = random.nextInt(jungleBeginning.x(), jungleEnd.x());
            int y = random.nextInt(jungleBeginning.y(), jungleEnd.y());
            Vector2d vector = new Vector2d(x,y);
            if (!plants.containsKey(vector)){
                flag =true;
                positionList.add(vector);
                break;
            }
            stepCounter+=1;
        }

        int x = jungleBeginning.x();
        while (!flag && x<= jungleEnd.x()){
            int y = jungleBeginning.y();
            while (y<= jungleEnd.y()){
                Vector2d vector = new Vector2d(x,y);
                if (!plants.containsKey(vector)){
                    flag = true;
                    positionList.add(vector);
                    break;
                }
                y+=1;
            }
            x+=1;
        }
        return flag;
    }

    private boolean placeDesert(List<Vector2d> positionList){
        // Jeżeli może to umieszcza roślinę na pustyni. Robi to losowo, chyba że przekroczono limit poszukiwań to przeszukuje każdy indeks mapy. Jeżeli się nie uda zwraca fałsz.
        Random random = new Random();
        int stepCounter = 0;
        boolean flag = false;
        while (stepCounter<width*height) {
            Vector2d dessertBeginning;
            Vector2d dessertEnd;
            if (random.nextBoolean()){
                dessertBeginning = new Vector2d(beginning.x(), beginning.y());
                dessertEnd = new Vector2d(end.x(), jungleBeginning.y()-1 ) ;
            }
            else{
                dessertBeginning = new Vector2d(beginning.x(), jungleEnd.y()+1);
                dessertEnd = new Vector2d(end.x(), end.y()) ;
            }
            int x = random.nextInt(dessertBeginning.x(), dessertEnd.x());
            int y = random.nextInt(dessertBeginning.y(), dessertEnd.y());
            Vector2d vector = new Vector2d(x,y);
            if (!plants.containsKey(vector)){
                positionList.add(vector);
                flag = true;
                break;
            }
            stepCounter+=1;
        }

        int x = beginning.x();
        while (!flag && x<= jungleEnd.x()){
            int y = jungleBeginning.y();
            while (y<= jungleEnd.y()){
                Vector2d vector = new Vector2d(x,y);
                if (!plants.containsKey(vector)){
                    flag =true;
                    positionList.add(vector);
                }
                y+=1;
            }
            x+=1;
        }
        return flag;
    }


    public void removeAnimal(Animal animal){
        animals.get(animal.getPosition()).remove(animal);
    }


    public boolean isOccupied(Vector2d position) {
        return (animals.containsKey(position)||plants.containsKey(position));
    }

//      Chyba niepotrzebne poza tym jak to zaimplementować?
//    public WorldElement objectAt(Vector2d position) {
//        return animals.getOrDefault(position, null);
//    }


    public HashMap<Vector2d, List<WorldElement>> getElements() {
        HashMap<Vector2d,List<WorldElement>> elements = new HashMap<>();
        for (Vector2d position: animals.keySet()){
            elements.put(position, new ArrayList<>(animals.get(position)));
            }

        for (Vector2d position: plants.keySet()){
            if (!elements.containsKey(position)) {
                elements.put(position, new ArrayList<>());
            }
            elements.get(position).add(plants.get(position));
        }
        return elements;
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

