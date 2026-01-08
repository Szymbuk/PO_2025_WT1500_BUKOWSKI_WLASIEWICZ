package project.model;

public class Plant implements WorldElement{
    private final Vector2d position;

    public Plant(Vector2d position){
        this.position = position;
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    @Override
    public boolean isAt(Vector2d position){
        return this.position.equals(position);
    }

    @Override
    public String toString(){
        return "*";
    }
}
