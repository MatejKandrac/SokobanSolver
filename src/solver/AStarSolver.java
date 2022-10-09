/*
 * Copyright (c) Matej Kandráč
 */

package solver;

import map.Tile;

import java.util.*;

/**
 * AStartSolver class contains ACTIONS which sokoban can do as well as algorithm to solve the problem.
 * Algorithm used is A* which uses heuristic and general values to determine best Node.
 */

public class AStarSolver implements SolverCommons {

    // ACTION values
    private static final String ACTION_PUSH_LEFT = "PUSH_LEFT";
    private static final String ACTION_PUSH_RIGHT = "PUSH_RIGHT";
    private static final String ACTION_PUSH_DOWN = "PUSH_DOWN";
    private static final String ACTION_PUSH_UP = "PUSH_UP";
    private static final String ACTION_WALK_LEFT = "WALK_LEFT";
    private static final String ACTION_WALK_RIGHT = "WALK_RIGHT";
    private static final String ACTION_WALK_UP = "WALK_UP";
    private static final String ACTION_WALK_DOWN = "WALK_DOWN";

    // Action weights can be modified to alter sokoban solution
    private static final int PUSH_ACTION_WEIGHT = 1;
    private static final int MOVE_ACTION_WEIGHT = 1;

    // Map which will be solved
    private final Tile[][] map;

    // Finish tile
    private final Tile finish;

    // Queue which contains unvisited nodes. Uses comparator which compares FCosts of each Node.
    private final PriorityQueue<Node> unvisitedNodes;

    // Final node. Can be null if there is no solution.
    private Node finalNode;

    // Set used to handle same states
    private final HashSet<String> existedStates;

    /**
     * Base constructor initializes fields and sets comparator.
     * @param map static map tiles
     * @param finish static finish node
     */
    public AStarSolver(Tile[][] map, Tile finish) {
        this.map = map;
        this.finish = finish;
        unvisitedNodes = new PriorityQueue<>(Comparator.comparingInt(Node::getFCost));
        existedStates = new HashSet<>();
    }

    /**
     * Solve method of problem.
     * @param sokoban Start sokoban position
     * @param box Start box position
     */
    @Override
    public void solve(Tile sokoban, Tile box) {
        // All required fields have to be defined
        if (box == null || sokoban == null || finish == null) {
            throw new IllegalStateException("Some required fields are missing on map.");
        }
        // Creates initial Nodes and adds them to unvisited list
        for (Tile destinationTile : getDestinations(box, sokoban)) {
            Node node = new Node(destinationTile, null, cleanMap(), sokoban, box, 0, null, finish);
            unvisitedNodes.add(node);
            existedStates.add(node.stateString());
        }
        // MAIN LOOP
        // Loops until solution is found or there is nothing to do anymore
        while (unvisitedNodes.size() > 0 && finalNode == null) {
            // Gets best node to to visit
            Node nodeToVisit = unvisitedNodes.poll();
            // Visits the node
            visitNode(nodeToVisit);
        }
    }


    /**
     * Simple function which is used to determine if STEP ACTION will be used or PUSH ACTION
     * @param node to visit
     */
    void visitNode(Node node) {
        if (node.getDestination().equals(node.getSokoban())) pushBox(node);
        else walk(node);
    }

    /**
     * Walk function marks node position as visited (uses same instance for all nodes which have that destination)
     * and makes new Nodes where it is possible.
     * @param node Node to visit
     */
    void walk(Node node) {
        boolean[][] newMap = node.getVisitMap();
        newMap[node.getSokoban().y()][node.getSokoban().x()] = true;

        // Attempt to walk in every direction
        walkAction(node, node.getSokoban().left(map), newMap, node.getSokoban().copyOffset(-1, 0), ACTION_WALK_LEFT);

        walkAction(node, node.getSokoban().right(map), newMap, node.getSokoban().copyOffset(1, 0), ACTION_WALK_RIGHT);

        walkAction(node, node.getSokoban().top(map), newMap, node.getSokoban().copyOffset(0, -1), ACTION_WALK_UP);

        walkAction(node, node.getSokoban().bottom(map), newMap, node.getSokoban().copyOffset(0, 1), ACTION_WALK_DOWN);
    }

    /**
     * Walk function determines if walk is possible and if so, creates new node.
     * @param parentNode Parent node
     * @param neighbor Tile where sokoban should move
     * @param newMap boolean map of visited nodes
     * @param sokoban New sokoban position
     * @param action Action which will be used
     */
    void walkAction(Node parentNode, Tile neighbor, boolean[][] newMap, Tile sokoban, String action) {
        // If there is a neighbor (is not edge of map) and is walkable and it is not the box (we cant walk on box)
        if (neighbor != null && neighbor.walkable() && !neighbor.equals(parentNode.getBox())) {
            // if the node is not visited yet and was not handled before, add it to unvisited nodes and increment G-cost
            if (!parentNode.getVisitMap()[neighbor.y()][neighbor.x()]){
                Node newNode = new Node(
                        parentNode.getDestination(),
                        parentNode,
                        newMap,
                        sokoban,
                        parentNode.getBox(),
                        parentNode.getGCost()+MOVE_ACTION_WEIGHT,
                        action + " ",
                        finish);
                unvisitedNodes.add(newNode);
            }
        }
    }

    /**
     * Push action determines to which direction box should be pushed
     * @param node node from which you are pushing the box
     */
    void pushBox(Node node) {
        // Gets neighbor on left
        Tile neighbor = node.getSokoban().left(map);
        // If neighbor exists and it is the box
        if (neighbor != null && neighbor.equals(node.getBox())) {
            // Create new box tile moved by offset
            Tile newBox = node.getBox().copyOffset(-1, 0);
            // Create new sokoban tile moved by same offset
            Tile newSokoban = node.getSokoban().copyOffset(-1, 0);
            // Push the box with action
            pushWithAction(node, newBox, newSokoban, ACTION_PUSH_LEFT);

            // THIS APPLIES FOR ALL OTHER CASES BUT WITH DIFFERENT ACTION AND OFFSET
        }
        else  if ((neighbor = node.getSokoban().right(map)) != null && neighbor.equals(node.getBox())) {
            Tile newBox = node.getBox().copyOffset(1, 0);
            Tile newSokoban = node.getSokoban().copyOffset(1, 0);
            pushWithAction(node, newBox, newSokoban, ACTION_PUSH_RIGHT);
        }
        else if ((neighbor = node.getSokoban().top(map)) != null && neighbor.equals(node.getBox())) {
            Tile newBox = node.getBox().copyOffset(0, -1);
            Tile newSokoban = node.getSokoban().copyOffset(0, -1);
            pushWithAction(node, newBox, newSokoban, ACTION_PUSH_UP);
        }
        else if ((neighbor = node.getSokoban().bottom(map)) != null && neighbor.equals(node.getBox())) {
            Tile newBox = node.getBox().copyOffset(0, 1);
            Tile newSokoban = node.getSokoban().copyOffset(0, 1);
            pushWithAction(node, newBox, newSokoban, ACTION_PUSH_DOWN);
        }

        // To simplify remaining cases, remove all destinations which have the same destination and box position from unvisited nodes
        unvisitedNodes.removeIf(node1 -> node.getDestination().equals(node1.getDestination()) && node.getBox().equals(node1.getBox()));
    }


    /**
     * Executes push action and adds it to new nodes . If the box is on finish. Set final node and break the function.
     * @param parentNode node from which this node was created
     * @param newBox new position of box
     * @param newSokoban new position of sokoban
     * @param action description of action taken
     */
    private void pushWithAction(Node parentNode, Tile newBox, Tile newSokoban, String action) {
        if (newBox.equals(finish)) {
            finalNode = new Node(
                    parentNode,
                    action,
                    newSokoban,
                    newBox
            );
            return;
        }
        for (Tile destination : getDestinations(newBox, newSokoban)) {
            Node newNode = new Node(
                    destination,
                    parentNode,
                    cleanMap(),
                    newSokoban,
                    newBox,
                    parentNode.getGCost() + PUSH_ACTION_WEIGHT,
                    action + " ",
                    finish
            );
            if (!existedStates.contains(newNode.stateString())){
                unvisitedNodes.add(newNode);
                existedStates.add(newNode.stateString());
            }
        }
    }

    /**
     * Gets all destinations around box tile. Action is required for {@link #checkToAdd(Tile, Tile, Tile, Tile)} function.
     * @param boxTile box position
     * @param sokoban required for {@link #checkToAdd(Tile, Tile, Tile, Tile)} function
     * @return List of adjacent destinations
     */
    List<Tile> getDestinations(Tile boxTile, Tile sokoban) {
        List<Tile> nodes = new ArrayList<>();

        Tile right = boxTile.right(map);
        Tile left = boxTile.left(map);
        Tile bottom = boxTile.bottom(map);
        Tile top = boxTile.top(map);

        if (checkToAdd(right, left, boxTile, sokoban)) nodes.add(right);

        if (checkToAdd(left, right, boxTile, sokoban)) nodes.add(left);

        if (checkToAdd(bottom, top, boxTile, sokoban)) nodes.add(bottom);

        if (checkToAdd(top, bottom, boxTile, sokoban)) nodes.add(top);

        return nodes;
    }

    /**
     * Checks whether the destination should be added
     * @param current current tile position
     * @param opposing tile on the opposite side of the box
     * @param box position of box
     * @param sokoban position of sokoban
     * @return if tile should be added to destinations
     */
    boolean checkToAdd(Tile current, Tile opposing, Tile box, Tile sokoban) {
        // if tile exists and is walkable
        if (current != null && current.walkable()) {
            // if there is a space on the other side of box and box is not on a finish tile (we have a solution so there is no point to add destination)
            // Also opposite is not a corner (ignore this if it is a finish tile)
            // And the tile is not blocked from all sides (destination is not reachable).
            // However this case should be ignored if sokoban is currently at this position (has nowhere else to go)
            return (opposing != null && opposing.walkable() && !box.equals(finish)) &&
                    (!isCorner(opposing) || opposing.equals(finish)) &&
                    (!isBlocked(current, box) || current.equals(sokoban));
        }
        return false;
    }

    /**
     * Determines whether tile is a corner.
     * Corner is every tile which has two blocks (wall or edge of map) next to each other.
     * @param tile tile to determine if it is a corner
     * @return whether tile is a corner
     */
    boolean isCorner(Tile tile) {
        boolean topBlocked = false;
        boolean rightBlocked = false;
        if (tile.y() == 0 || !map[tile.y() - 1][tile.x()].walkable()) {
            topBlocked = true;
        }
        if (tile.x() == map.length - 1 || !map[tile.y()][tile.x() + 1].walkable()) {
            rightBlocked = true;
        }
        if (topBlocked && rightBlocked) {
            return true;
        }
        boolean bottomBlocked = tile.y() == map.length - 1 || !map[tile.y() + 1][tile.x()].walkable();
        if (rightBlocked && bottomBlocked) {
            return true;
        }
        boolean leftBlocked = tile.x() == 0 || !map[tile.y()][tile.x() - 1].walkable();
        return (leftBlocked && bottomBlocked) || (topBlocked && leftBlocked);
    }

    /**
     * Checks around the tile and checks if tile is blocked. It is blocked when every surrounding tiles are either edges
     * of map, walls or box.
     * @param tile tile to determine
     * @param box position of box
     * @return whether tile is blocked from all sides
     */
    boolean isBlocked(Tile tile, Tile box) {
        int counter = 0;
        if (tile.y() == 0 || !map[tile.y() - 1][tile.x()].walkable() || map[tile.y() - 1][tile.x()].equals(box)) counter++;
        if (tile.x() == map.length - 1 || !map[tile.y()][tile.x() + 1].walkable() || map[tile.y()][tile.x() + 1].equals(box)) counter++;
        if (tile.y() == map.length - 1 || !map[tile.y() + 1][tile.x()].walkable() || map[tile.y() + 1][tile.x()].equals(box)) counter++;
        if (tile.x() == 0 || !map[tile.y()][tile.x() - 1].walkable() || map[tile.y()][tile.x() - 1].equals(box)) counter++;
        return counter == 4;
    }

    /**
     * Creates new empty boolean map which is used by nodes to determine visited tiles.
     * @return 2D boolean array witch all values false
     */
    public boolean[][] cleanMap() {
        return new boolean[map.length][map.length];
    }

    /**
     * Gets the string result of path in actions taken.
     * Uncomment commented lines if you want to print the maps of final nodes.
     * @return String containing the path
     */
    @Override
    public String getResult() {
        if (finalNode == null) {
            return "There is no solution";
        }
        StringBuilder path = new StringBuilder();
        Node current = finalNode;
//        List<Node> nodes = new ArrayList<>();
        while (current != null) {
//            nodes.add(current);
            if (current.getStep() != null) {
                path.insert(0, current.getStep());
            }
            current = current.getParent();
        }
//        for (int i = nodes.size()-1; i >= 0; i--) {
//            nodes.get(i).printMap(map, finish);
//        }
        return path.toString();
    }
}
