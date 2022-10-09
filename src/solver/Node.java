/*
 * Copyright (c) Matej Kandráč
 */

package solver;

import map.Tile;

/**
 * Node class represents one state of entire solution.
 */
public class Node {

    // F-cost (sum of g-cost and h-cost of node)
    private final int fCost;
    // G-cost (distance from start)
    private final int gCost;
    // Destination which sokoban is trying to reach
    private final Tile destination;
    // Parent node of this node
    private final Node parent;
    // Visit map contains boolean map with visited nodes (This array is different for each new destination)
    private final boolean[][] visitMap;
    // Sokoban tile
    private final Tile sokoban;
    // Box tile
    private final Tile box;
    // String step. Is only one of static members in AStarSolver
    private final String step;

    // Default constructor
    public Node(Tile destination, Node parent, boolean[][] visitMap, Tile sokoban, Tile box, int gCost, String step, Tile end) {
        this.destination = destination;
        this.parent = parent;
        this.visitMap = visitMap;
        this.sokoban = sokoban;
        this.box = box;
        this.step = step;
        this.gCost = gCost;
        this.fCost = gCost + getHValue(end);
    }

    // Constructor for last node. Some information are obsolete.
    public Node(Node parent, String step, Tile sokoban, Tile box) {
        destination = null;
        this.parent = parent;
        visitMap = null;
        this.sokoban = sokoban;
        this.box = box;
        this.step = step;
        fCost = 0;
        gCost = 0;
    }

    public String getStep() {
        return step;
    }

    public int getGCost() {
        return gCost;
    }

    public Node getParent() {
        return parent;
    }

    public Tile getDestination() {
        return destination;
    }

    public int getFCost() {
        return fCost;
    }

    public boolean[][] getVisitMap() {
        return visitMap;
    }

    public Tile getBox() {
        return box;
    }

    public Tile getSokoban() {
        return sokoban;
    }

    /**
     * Converts node to position string. Used as key in Set.
     * @return formatted positions of box and destination
     */
    public String stateString() {
        return Integer.toString(box.x()) + Integer.toString(box.y()) +
                Integer.toString(destination.x()) + Integer.toString(destination.y()) +
                Integer.toString(sokoban.x()) + Integer.toString(sokoban.y());
    }

    /**
     * Returns heuristic value of this node
     * @param end final tile
     * @return heuristic cost (H-cost)
     */
    private int getHValue(Tile end) {
        int sokobanDistance = Math.abs(sokoban.x() - destination.x()) + Math.abs(sokoban.y() - destination.y());
        int finishDistance = Math.abs(end.x() - box.x()) + Math.abs(end.y() - box.y()) * 2;
        return sokobanDistance + finishDistance;
    }

    /**
     * Prints map based on node.
     * @param map Map tiles
     * @param finish Finish tile
     */
    public void printMap(Tile[][] map, Tile finish) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map.length; x++) {
                if (x == sokoban.x() && y == sokoban.y()) {
                    System.out.print("S");
                } else if (x == box.x() && y == box.y()) {
                    System.out.print("B");
                } else if (x == finish.x() && y == finish.y()) {
                    System.out.print("F");
                } else if (destination != null && x == destination.x() && y == destination.y()) {
                    System.out.print("D");
                }
                else if (map[y][x].walkable()) {
                    System.out.print("-");
                } else {
                    System.out.print("X");
                }
            }
            System.out.println();
        }
        System.out.println("\n\n");
    }

}
