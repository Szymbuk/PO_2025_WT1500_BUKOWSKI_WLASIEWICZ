package project.model;

public interface WorldElement {

    String toString();

    Vector2d getPosition();

    boolean isAt(Vector2d position);

}
