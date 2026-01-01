package project.model;


public class Animal implements WorldElement{
    private MapDirection orientation;
    private Vector2d position;

    public Animal(MapDirection orientation, Vector2d position){
        this.orientation = orientation;
        this.position = position;
    }
    public Animal(Vector2d position){
        this(MapDirection.NORTH,position);
    }

    public Animal(){this(new Vector2d(2,2));}

    @Override
    public String toString(){
        return switch (orientation){
            case NORTH -> "N";
            case SOUTH -> "S";
            case EAST -> "E";
            case WEST -> "W";
        };
    }

    @Override
    public boolean isAt(Vector2d position){
        return this.position.equals(position);
    }

    public void move(MoveDirection orientation, MoveValidator validator){
        this.orientation = switch(orientation){
            case RIGHT -> this.orientation.next();
            case LEFT -> this.orientation.previous();
            case FORWARD,BACKWARD -> this.orientation;
        };

        Vector2d moveVector = switch (orientation){
            case RIGHT,LEFT -> new Vector2d(0,0);
            case FORWARD -> this.orientation.toUnitVector();
            case BACKWARD -> this.orientation.toUnitVector().opposite();
        };
        Vector2d new_position = position.add(moveVector);
        if (validator.canMoveTo(new_position)) {
            position = new_position;
        }
    }

    @Override
    public Vector2d getPosition(){
        return position;
    }
    public MapDirection getOrientation(){
        return orientation;
    }

}
