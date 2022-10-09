/*
 * Copyright (c) Matej Kandráč
 */

package solver;

import map.Tile;

/**
 * Solver commons interface.
 * Contains common methods which every solver should have.
 */
public interface SolverCommons {

    void solve(Tile sokoban, Tile box);

    String getResult();

}
