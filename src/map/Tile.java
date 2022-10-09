/*
 * Copyright (c) Matej Kandráč
 */

package map;

/**
 * Tile record holds information with position and if tile is walkable
 * @param x
 * @param y
 * @param walkable
 */
public record Tile(int x, int y, boolean walkable) {

    /**
     * Creates new tile instance with offset. Used to create moving tiles (Sokoban, Box)
     * @param x offset (1,0,-1)
     * @param y offset (1,0,-1)
     * @return new instance of tile
     */
    public Tile copyOffset(int x, int y) {
        return new Tile(this.x + x, this.y + y, walkable);
    }

    /**
     * Returns tile on the right side of this tile. Can be null if it is the edge of the map
     * @param map current map
     * @return right child
     */
    public Tile right(Tile[][] map) {
        if (x == map.length - 1) {
            return null;
        }
        return map[y][x + 1];
    }

    public Tile left(Tile[][] map) {
        if (x == 0) {
            return null;
        }
        return map[y][x - 1];
    }

    public Tile bottom(Tile[][] map) {
        if (y == map.length - 1) {
            return null;
        }
        return map[y + 1][x];
    }

    public Tile top(Tile[][] map) {
        if (y == 0) {
            return null;
        }
        return map[y - 1][x];
    }

    /**
     * Checks if the position of this tile is the same as the one in parameter
     * @param object tile to compare
     * @return true if it is the same position
     */
    public boolean equals(Tile object) {
        return x == object.x() && y == object.y();
    }
}
