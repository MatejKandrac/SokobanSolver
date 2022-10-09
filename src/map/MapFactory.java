/*
 * Copyright (c) Matej Kandráč
 */

package map;

import java.io.*;

/**
 * MapFactory class generates static map tiles and if loading from file, sets sokoban, box and finish positions.
 */
public class MapFactory {

    // currently generated x position
    static int x = 0;

    // currently generated y position
    static int y = 0;

    // size of map
    static int size = 0;

    // if loading from file, position of sokoban
    public static Tile lastSokoban;

    // if loading from file, position of box
    public static Tile lastBox;

    // if loading from file, position from finish
    public static Tile lastFinish;

    // Since this is a static class, make constructor private to disable instance creation
    private MapFactory(){}

    /**
     * Generates static map tiles. Used when you want to enter map by hand. {@link #loadFromFile(String)} should be used instead.
     * @param size size of map
     * @return array of map tiles
     */
    public static Tile[][] generateMap(int size) {
        MapFactory.size = size;
        MapFactory.lastFinish = new Tile(6, 6 , true);
        MapFactory.lastBox = new Tile(4, 1, true);
        MapFactory.lastSokoban = new Tile(0, 3, true);
        return new Tile[][]{
                {walkable(true), walkable(true), walkable(true), walkable(true), walkable(true), walkable(false), walkable(false)},
                {walkable(false), walkable(true), walkable(true), walkable(true), walkable(true), walkable(true), walkable(false)},
                {walkable(false), walkable(false), walkable(true), walkable(true), walkable(true), walkable(true), walkable(false)},
                {walkable(true), walkable(true), walkable(true), walkable(true), walkable(true), walkable(true), walkable(true)},
                {walkable(true), walkable(true), walkable(true), walkable(false), walkable(false), walkable(true), walkable(true)},
                {walkable(true), walkable(true), walkable(true), walkable(false), walkable(false), walkable(true), walkable(true)},
                {walkable(true), walkable(true), walkable(true), walkable(true), walkable(true), walkable(true), walkable(true)}
        };
    }

    /**
     * Loads map from file. See example map1.txt
     * @param path Path of file
     * @return array of map tiles
     * @throws IOException thrown when map fails to load
     */
    public static Tile[][] loadFromFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        // First line contains size of map
        size = Integer.parseInt(br.readLine());
        y = 0;
        Tile[][] map = new Tile[size][size];
        String line;
        // Read file line by line...
        while ((line = br.readLine()) != null) {
            // Read line character by character and create field
            for (char c : line.toCharArray()) {
                if (c == 'X') {
                    // Walls are not walkable
                    map[y][x] = walkable(false);
                } else if (c == '-') {
                    // Everything else is walkable
                    map[y][x] = walkable(true);
                } else if (c == 'F') {
                    lastFinish = walkable(true);
                    // Corner case: box is on bottom right corner (x:size y:size). Since y and x auto increments we need to
                    // set them manually. This applies for every special field.
                    if (y == size) {
                        y--;
                        x = size-1;
                        map[y][x] = lastFinish;
                    }
                    else {
                        map[y][x-1] = lastFinish;
                    }
                } else if (c == 'S') {
                    lastSokoban = walkable(true);
                    if (y == size) {
                        y--;
                        x = size-1;
                        map[y][x] = lastSokoban;
                    } else {
                        map[y][x - 1] = lastSokoban;
                    }
                } else if (c == 'B') {
                    lastBox = walkable(true);
                    if (y == size) {
                        y--;
                        x = size-1;
                        map[y][x] = lastBox;
                    } else {
                        map[y][x-1] = lastBox;
                    }
                } else {
                    throw new IllegalStateException("INVALID CHARACTER IN MAP FILE: " + c);
                }
            }
        }
        br.close();
        return map;
    }

    /**
     * Creates tile and auto increments x and y positions.
     * @param walkable whether sokoban can or cannot walk on this tile
     * @return generated tile
     */
    private static Tile walkable(boolean walkable) {
        Tile tile = new Tile(x, y, walkable);
        x++;
        if (x == size) {
            x = 0;
            y++;
        }
        if (y == size && x == 1) {
            throw new IllegalStateException("Map too large: x=" + x + ", y=" + y);
        }
        return tile;
    }

}
