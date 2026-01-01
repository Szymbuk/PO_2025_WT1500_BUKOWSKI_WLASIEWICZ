package project.model;

public class IncorrectPositionException extends Exception {
    public IncorrectPositionException(Vector2d position)
    {
        IO.println("Position %s is not correct.".formatted(position));
    }
}
