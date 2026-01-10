package project.model.util;

import project.model.Animal;
import project.model.AnimalPair;

import java.util.*;

public class FamilyStructure {
    HashSet<Animal> animals = new HashSet<>();
    public void addAnimal(Animal animal, AnimalPair parents){
        animals.add(animal);
        new Node(animal, parents);
    }

    public Optional<Integer> relationshipLevel(AnimalPair parents){
        HashMap<Animal,Integer> distance = new HashMap<>();
        for(Animal animal: animals){
            distance.put(animal,null);
        }
        ArrayDeque<Animal> queue = new ArrayDeque<>();
        queue.add(parents.male());
        distance.replace(parents.male(),0);
        //szukamy odległości dla parents.female()

        while (!queue.isEmpty()){
            Animal currentAnimal = queue.pop();
            Integer currentDistance = distance.get(currentAnimal);

            if (currentDistance >4){
                return Optional.empty();
            }

            for (Animal child : currentAnimal.getFamilyStructureNode().children){
                if (distance.get(child) != null){
                    continue;
                }
                distance.replace(child,currentDistance+1);
                if (child == parents.female()){
                    return distance.get(child).describeConstable();
                }
                queue.add(child);
            }

            Animal parent1 = null;
            Animal parent2 = null;

            if (currentAnimal.getFamilyStructureNode().parents != null) {
                parent1 = currentAnimal.getFamilyStructureNode().parents.female();
                parent2 = currentAnimal.getFamilyStructureNode().parents.male();


                if (distance.get(parent1) == null) {
                    distance.replace(parent1, currentDistance + 1);
                    if (parent1 == parents.female()) {
                        return distance.get(parent1).describeConstable();
                    }
                    queue.add(parent1);
                }

                if (distance.get(parent2) == null) {
                    distance.replace(parent2, currentDistance + 1);
                    if (parent2 == parents.female()) {
                        return distance.get(parent2).describeConstable();
                    }
                    queue.add(parent2);
                }
            }
        }
        return Optional.empty();
    }


}
