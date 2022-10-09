/*
 * Copyright (c) Matej Kandráč
 */

import map.MapFactory;
import map.Tile;
import solver.AStarSolver;

import java.io.IOException;

/**
 * Main class of Sokoban solver. Only used for launching the app.
 */

public class Main {

    /**
     * Main function runs the app.
     * @param args (not used)
     * @throws IOException Thrown when map generating fails.
     */
    public static void main(String[] args) throws IOException {
        // Generate map tiles
        Tile[][] map = MapFactory.loadFromFile("map3.txt");
        AStarSolver solver = new AStarSolver(map, MapFactory.lastFinish);
        // Solve problem
        solver.solve(MapFactory.lastSokoban, MapFactory.lastBox);
        // Print final result of solver
        System.out.println(solver.getResult());
    }

}