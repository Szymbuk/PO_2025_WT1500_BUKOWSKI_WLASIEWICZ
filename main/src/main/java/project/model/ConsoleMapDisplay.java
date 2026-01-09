package project.model;

public class ConsoleMapDisplay implements MapChangeListener{
    private int numberOfChanges = 0;

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        synchronized (System.out) {
            IO.println();
            IO.println(worldMap.getId());
            IO.println(message);
            IO.println(worldMap.toString());
            numberOfChanges += 1;
            IO.println("Change number %d\n\n".formatted(numberOfChanges));
        }
    }
}
