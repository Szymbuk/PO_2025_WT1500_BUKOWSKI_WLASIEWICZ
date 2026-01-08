package project.model;

public class IncorrectPositionException extends RuntimeException {
    //Zmieniono na Exception Unchecked, dlatego że zwierzęta w żadym wypadku nie powinny być generowane poza mapą\
    // Użytkownik nie podaje lokalizacji początkowej zwierząt
    public IncorrectPositionException(Vector2d position)
    {
        IO.println("Position %s is not correct.".formatted(position));
    }
}
