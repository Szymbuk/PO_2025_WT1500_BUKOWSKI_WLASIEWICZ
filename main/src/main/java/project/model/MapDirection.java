package project.model;

public enum MapDirection {NORTH,NORTHEAST, EAST,SOUTHEAST, SOUTH,SOUTHWEST, WEST, NORTHWEST;

    public String toString(){
        return switch(this){
            case NORTH -> "Północ";
            case NORTHEAST -> "Północny-wchód";
            case SOUTH -> "Południe";
            case SOUTHEAST -> "Południowy-wchód";
            case EAST -> "Wschód";
            case SOUTHWEST -> "Południowy-zachód";
            case WEST -> "Zachód";
            case NORTHWEST -> "Północny-zachód";
        };
    }

    public MapDirection rotate(int n){
        MapDirection res = this;
        for (int i = 0; i<n; i++){
            res = res.next();
        }
        return res;
    }

    public MapDirection next(){
        return switch(this){
            case NORTH -> NORTHEAST;
            case NORTHEAST -> EAST;
            case EAST -> SOUTHEAST;
            case SOUTHEAST -> SOUTH;
            case SOUTH -> SOUTHWEST;
            case SOUTHWEST -> WEST;
            case WEST -> NORTHWEST;
            case NORTHWEST -> NORTH;
        };
    }

    public Vector2d toUnitVector(){
        return switch(this){
            case NORTH -> new Vector2d(0,1);
            case NORTHEAST -> new Vector2d(1,1);
            case EAST -> new Vector2d(1,0);
            case SOUTHEAST -> new Vector2d(1,-1);
            case SOUTH -> new Vector2d(0,-1);
            case SOUTHWEST -> new Vector2d(-1,-1);
            case WEST -> new Vector2d(-1,0);
            case NORTHWEST -> new Vector2d(-1,1);
        };
    }
}


