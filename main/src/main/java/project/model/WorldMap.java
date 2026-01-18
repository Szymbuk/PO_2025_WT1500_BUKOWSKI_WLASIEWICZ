package project.model;

import project.model.util.MapVisualizer;

import java.util.*;
import java.util.stream.Collectors;

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
    private final HashMap<List<Integer>, Integer> genotypeCountMap = new HashMap<>();

    public WorldMap(int width,int height,int id){

        this.id = id;
        this.height = height;
        this.width = width;
        this.beginning= new Vector2d(0,0);
        this.end = new Vector2d(width-1,height-1);

        double theoreticalJungleSize = (height) * 0.2;
        int jungleLength = Math.toIntExact(Math.round(theoreticalJungleSize));
        if (!(jungleLength%2 == (height)%2)){
            jungleLength = (int) theoreticalJungleSize;
            if (!(jungleLength%2 == (height)%2)){
                jungleLength +=1;
            }
        }
        int margin = (height-jungleLength)/2;
        jungleBeginning = new Vector2d(0, margin);
        jungleEnd = new Vector2d(end.x(), margin + jungleLength-1);
    }


    public void place(Animal animal){
        Vector2d position = animal.getPosition();
        if (!position.precedes(end) || !position.follows(beginning)){
            throw new IncorrectPositionException(position);
        }
        else{
                if (! animals.containsKey(position)){
                    animals.put(position,new ArrayList<>());
                }
                animals.get(animal.getPosition()).add(animal);
                //zwieksz licznik wystepowan genotypu
                genotypeCountMap.put(animal.getGenotype(), genotypeCountMap.getOrDefault(animal.getGenotype(), 0) + 1);
            }
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return (position.precedes(end) && position.follows(beginning));
    }

    @Override
    public Vector2d correctPosition(Vector2d oldPosition,Vector2d newPositon,Animal animal) {
        if (newPositon.y() <beginning.y() || newPositon.y() > end.y()){
            animal.turnAround();
            return animal.getOrientation().toUnitVector().add(oldPosition);
        }
        int x;
        if (newPositon.x() < beginning.x()){
            x = end.x();
        } else if (newPositon.x() > end.x()) {
            x = beginning.x();
        }
        else{
            x = newPositon.x();
        }
        return new Vector2d(x,newPositon.y());
    }


    public Boundary getCurrentBounds() {
        return new Boundary(new Vector2d(0,0),new Vector2d(width-1,height-1));
    }


    public void move(Animal animal) {
        Vector2d previousPosition = animal.getPosition();
        if (!(animals.containsKey(previousPosition) && animals.get(previousPosition).contains(animal))) {
            return;
        }
        animal.move(this);
        Vector2d newPosition = animal.getPosition();
        animals.get(previousPosition).remove(animal);
        if (animals.get(previousPosition).isEmpty()){
            animals.remove(previousPosition);
        }
        if (! animals.containsKey(newPosition)){
            animals.put(newPosition,new ArrayList<>());
        }
        animals.get(newPosition).add(animal);
    }

    public int consumePlants(int energyGain){
        List<Vector2d> plantsPositionsToDelete = new ArrayList<>();
        int plantsConsumed = 0;
        for (Vector2d position:  plants.keySet()){
            List<Animal> hungryAnimals = animals.getOrDefault(position,null);
            if (hungryAnimals == null || hungryAnimals.isEmpty()){
                continue;
            }
            hungryAnimals.sort(Comparator.comparing(Animal::getEnergy).thenComparing(Animal::getAge).thenComparing(Animal::getChildrenNumber));
            Animal winner = hungryAnimals.getLast();
            winner.eat(energyGain);
            plantsConsumed +=1;
            plantsPositionsToDelete.add(position);
        }
        for (Vector2d position : plantsPositionsToDelete){
            plants.remove(position);
        }
        if (plantsConsumed > 0) {
            mapChanged("Zjedzono %d roslin".formatted(plantsConsumed));
        }
        return plantsConsumed;
    }
    
    public List<AnimalPair> procreate(int energyBreadingThreshold){
        // Funkcja zwraca pary zwierząt, które będą się ze sobą rozmnażać
        List<AnimalPair> pairList = new ArrayList<>();
        for (Vector2d position : animals.keySet()){
            List<Animal> animalsToBreed = animals.get(position);
            animalsToBreed.stream()
                    .filter(animal -> animal.getEnergy()>= energyBreadingThreshold)
                    .collect(Collectors.toList())
                    .sort(Comparator.comparing(Animal::getEnergy)
                            .thenComparing(Animal::getAge)
                            .thenComparing(Animal::getChildrenNumber));

            Animal strongestMale = null;
            Animal strongestFemale = null;
            int i = animalsToBreed.size()-1;
            while ((strongestFemale == null || strongestMale ==null) && i>0){
                if (animalsToBreed.get(i).getGender().equals(Gender.Male) && strongestMale==null){
                    strongestMale = animalsToBreed.get(i);
                }
                else if(animalsToBreed.get(i).getGender().equals(Gender.Female) && strongestFemale==null){
                    strongestFemale = animalsToBreed.get(i);
                }
                i-=1;
            }
            if (strongestFemale == null || strongestMale == null){
                continue;
            }
            pairList.add(new AnimalPair(strongestMale,strongestFemale));
        }
        return pairList;
    }


    public void removeAnimal(Animal animal){
        animals.get(animal.getPosition()).remove(animal);
        if (animals.get(animal.getPosition()).isEmpty()){
            animals.remove(animal.getPosition());
        }
        genotypeCountMap.put(animal.getGenotype(), genotypeCountMap.getOrDefault(animal.getGenotype(), 0) - 1);
        assert genotypeCountMap.get(animal.getGenotype()) >= 0;
    }


    public boolean isOccupied(Vector2d position) {
        return (animals.containsKey(position)||plants.containsKey(position));
    }

    public WorldElement objectAt(Vector2d position) {
        if (animals.containsKey(position)) {
            if (!animals.get(position).isEmpty())
            {
                List<Animal> strongestAnimals = animals.get(position);
                strongestAnimals.sort(Comparator.comparing(Animal::getEnergy).thenComparing(Animal::getAge).thenComparing(Animal::getChildrenNumber));
                return strongestAnimals.getLast();
            }
        }
        return plants.getOrDefault(position,null);
    }

    public int getEmptyFieldsCount() {
//        HashMap<Vector2d,List<WorldElement>> elements = new HashMap<>();
//        for (Vector2d position: animals.keySet()){
//            elements.put(position, new ArrayList<>(animals.get(position)));
//            }
//
//        for (Vector2d position: plants.keySet()){
//            if (!elements.containsKey(position)) {
//                elements.put(position, new ArrayList<>());
//            }
//            elements.get(position).add(plants.get(position));
//        }
        HashSet<Vector2d> elements = new HashSet<>(animals.keySet());
        elements.addAll(plants.keySet());
        return width * height - elements.size();
    }

    public void generatePlants(Integer numberOfPlants){
        Random random = new Random();
        int desert = 0;
        int jungle = 0;
        // Mapa musi dostać taką liczbę roślin, jaką będzie mogła wygenerować,
        // Zabezpieczenie jest w Simulation
        while (numberOfPlants>0) {
            //pustynia
            if (random.nextDouble() < 0.2) {
                if (placeDesert()) {
                    numberOfPlants -= 1;
                    desert +=1;
                } else if (placeJungle()) {
                    numberOfPlants -= 1;
                    jungle +=1;
                } else {
                    System.err.println("zostalo do posadzenia %d roslin".formatted(numberOfPlants));
                    throw new IllegalStateException("Podano złą liczbę roślin do posadzenia");
                }
            } else {
                //dżungla
                if (placeJungle()) {
                    numberOfPlants -= 1;
                    jungle +=1;
                } else if (placeDesert()) {
                    numberOfPlants -= 1;
                    desert +=1;
                } else {
                    System.err.println("zostalo do posadzenia %d roslin".formatted(numberOfPlants));
                    throw new IllegalStateException("Podano złą liczbę roślin do posadzenia");
                }
            }


        }
        String message = "posadzono rośliny: %d w dzungli, %d na pustyni".formatted(jungle,desert);
        mapChanged(message);
    }

    private boolean placeJungle(){
        // Jeżeli jest miejsce to umieszcza roślinę w dżungli, w przeciwnym razie zwraca fałsz
        Random random = new Random();
        int stepCounter = 0;
        boolean flag = false;
        while (stepCounter<(width)*(height) && stepCounter<100) {


            int x;
            int y;
            if (jungleBeginning.x() == jungleEnd.x()){
                x = jungleBeginning.x();
            }
            else{
                //random działa na przedziale lewostronnie domkniętym stąd +1
                x = random.nextInt(jungleBeginning.x(), jungleEnd.x()+1);
            }
            if (jungleBeginning.y() == jungleEnd.y()){
                y = jungleBeginning.y();
            }
            else{
                y = random.nextInt(jungleBeginning.y(), jungleEnd.y()+1);
            }


            Vector2d vector = new Vector2d(x,y);
            if (!plants.containsKey(vector)){
                flag =true;
                plants.put(vector,new Plant(vector));
                break;
            }
            stepCounter+=1;
        }

        int x = jungleBeginning.x();
        while (!flag && x<= jungleEnd.x()){
            int y = beginning.y();
            while (y<= jungleEnd.y()){
                Vector2d vector = new Vector2d(x,y);
                if (!plants.containsKey(vector)){
                    flag = true;
                    plants.put(vector,new Plant(vector));
                    break;
                }
                y+=1;
            }
            x+=1;
        }
        return flag;
    }

    private boolean placeDesert(){
        // Jeżeli może to umieszcza roślinę na pustyni. Robi to losowo, chyba że przekroczono limit poszukiwań to przeszukuje każdy indeks mapy. Jeżeli się nie uda-zwraca fałsz.
        Random random = new Random();
        int stepCounter = 0;
        boolean flag = false;
        while (stepCounter<(width)*(height) && stepCounter<100) {
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
            int x;
            int y;
            if (dessertBeginning.x() == dessertEnd.x()){
                x = dessertBeginning.x();
            }
            else{
                x = random.nextInt(dessertBeginning.x(), dessertEnd.x()+1);
            }
            if (dessertBeginning.y() == dessertEnd.y()){
                y = dessertBeginning.y();
            }
            else{
                y = random.nextInt(dessertBeginning.y(), dessertEnd.y()+1);
            }

            Vector2d vector = new Vector2d(x,y);
            if (!plants.containsKey(vector)){
                plants.put(vector,new Plant(vector));
                flag = true;
                break;
            }
            stepCounter+=1;
        }

        int x = beginning.x();
        while (!flag && x<= end.x()){
            int y = beginning.y();
            while (y<jungleBeginning.y()){
                Vector2d vector = new Vector2d(x,y);
                if (!plants.containsKey(vector)){
                    flag =true;
                    plants.put(vector,new Plant(vector));
                    break;
                }
                y+=1;
            }
            y = jungleEnd.y()+1;
            while (!flag && y<= end.y()){
                Vector2d vector = new Vector2d(x,y);
                if (!plants.containsKey(vector)){
                    flag = true;
                    plants.put(vector,new Plant(vector));
                }
                y+=1;
            }
            x+=1;
        }
        return flag;
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return new MapVisualizer(this).draw(getCurrentBounds().lowerLeft(), getCurrentBounds().upperRight());
    }

    public HashMap<Vector2d,Plant> getPlants(){
        return plants;
    }

    public List<Map.Entry<List<Integer>, Integer>> getMostPopularGenotypes(int n) {
        List<Map.Entry<List<Integer>, Integer>> list = new ArrayList<>(genotypeCountMap.entrySet());
        // to rzucało błąd, dlatego podmieniłem
        // list.sort((Comparator<? super Map.Entry<List<Integer>, Integer>>) Map.Entry.comparingByValue().reversed());
        list.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        return list.subList(0, n);
    }

    public int getAverageEnergy() {
        int sum = 0;
        int n = 0;
        for (Map.Entry<Vector2d, List<Animal>> entry: animals.entrySet()) {
            for (Animal animal : entry.getValue()) {
                sum += animal.getEnergy();
                n += 1;
            }
        }
        return sum/n;
    }
}

