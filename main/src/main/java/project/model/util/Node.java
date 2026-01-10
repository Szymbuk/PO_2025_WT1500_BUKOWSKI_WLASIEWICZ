package project.model.util;

import project.model.Animal;
import project.model.AnimalPair;

import java.util.ArrayList;
import java.util.List;

public class  Node{
    Animal item;
    AnimalPair parents;
    List<Animal> children = new ArrayList<>();
    Node(Animal animal, AnimalPair parents){
        this.item = animal;
        animal.setFamilyStructureNode(this);
        this.parents=parents;
        if (parents != null){
            parents.female().getFamilyStructureNode().addChild(animal);
            parents.male().getFamilyStructureNode().addChild(animal);
        }
    }

    private void addChild(Animal animal){
        children.add(animal);
    }




}
