//src/main/java/battle/BattleSolver.java

package battle;

import java.util.ArrayList;
import java.util.List;

import battle.Node.GameState;
import battle.Node.Unit;

@SuppressWarnings("unused")
/**
 * BattleSolver is responsible for solving a two-player turn-based battle
 * using adversarial search (Minimax). It supports both plain Minimax and
 * Minimax with Alpha-Beta pruning. The solver expects a compact string
 * representation of the initial game state and returns a plan string, the
 * final utility (score) and the number of nodes expanded.
 *
 * Output format from solve(...) is: plan + ";" + score + ";" + nodesExpanded + ";"
 * - plan: comma-separated actions such as "A(0,1),B(2,0),..." (empty if no actions)
 * - score: integer utility of the terminal state
 * - nodesExpanded: how many nodes were visited during search
 */
public class BattleSolver {
    private int nodesExpanded; // how many tree nodes we expanded
    private boolean visualize; // whether to print a textual visualization of the resulting path
    public Node initialNode;

    public BattleSolver() {
        this.nodesExpanded = 0;
    }

    /**
     * Entry point for solving a battle instance.
     *
     * @param initialStateString compact representation: "h,d,h,d,...;h,d,...;P"
     *        where the first semicolon-separated part is army A pairs (health,damage),
     *        second is army B, and the third part is the starting player ('A' or 'B').
     * @param ab whether to use alpha-beta pruning or plain minimax
     * @param visualize whether to print a step-by-step visualization
     * @return a string containing the plan, score and nodesExpanded separated by semicolons
     */
    public String solve(String initialStateString, boolean ab, boolean visualize) {
        this.visualize = visualize;
        this.nodesExpanded = 0;

        // Parse the compact initial state into a GameState object
        GameState initialState = parseInitialState(initialStateString);
        Node rootNode = new Node(initialState, null, null, 0);
        this.initialNode = rootNode;

        // Run minimax (optionally with alpha-beta pruning)
        MinimaxResult result;
        if (ab) {
            result = minimaxAlphaBeta(rootNode, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        } else {
            result = minimax(rootNode, true);
        }

        // Build solution path (action sequence) from the terminal node returned
        String plan = buildPlan(result.terminalNode);
        int score = result.value;

        if (visualize) {
            visualizeSolution(result.terminalNode);
        }

        // Return plan;score;nodesExpanded; (trailing semicolon kept for compatibility)
        return plan + ";" + score + ";" + nodesExpanded + ";";
    }

    /**
     * Parse a compact state string into a GameState object.
     * Expected format: "h,d,h,d,...;h,d,h,d,...;P" where P is 'A' or 'B'.
     * This method is fairly strict and assumes valid well-formed input.
     */
    private GameState parseInitialState(String stateString) {
        String[] parts = stateString.split(";");

        // Parse army A (pairs of health,damage)
        String[] armyATokens = parts[0].split(",");
        List<Unit> armyA = new ArrayList<>();
        for (int i = 0; i < armyATokens.length; i += 2) {
            int health = Integer.parseInt(armyATokens[i]);
            int damage = Integer.parseInt(armyATokens[i + 1]);
            armyA.add(new Unit(health, damage));
        }

        // Parse army B
        String[] armyBTokens = parts[1].split(",");
        List<Unit> armyB = new ArrayList<>();
        for (int i = 0; i < armyBTokens.length; i += 2) {
            int health = Integer.parseInt(armyBTokens[i]);
            int damage = Integer.parseInt(armyBTokens[i + 1]);
            armyB.add(new Unit(health, damage));
        }

        // Parse starting player char (A or B)
        char startingPlayer = parts[2].charAt(0);

        // GameState constructor: (armyA, armyB, currentPlayer, startingPlayer)
        return new GameState(armyA, armyB, startingPlayer, startingPlayer);
    }

    /**
     * Plain recursive minimax. Returns a MinimaxResult containing the value and
     * a reference to the terminal node that yielded that value. The method
     * increments nodesExpanded for basic instrumentation.
     *
     * Note: this implementation returns the terminal node for the best value
     * found in each subtree (useful to reconstruct the final plan), not the
     * immediate child.
     */
    private MinimaxResult minimax(Node node, boolean maximizingPlayer) {
        nodesExpanded++;
        GameState state = node.getState();

        // Terminal test: if game over, return utility and this node as terminal
        if (state.isTerminal()) {
            MinimaxResult result = new MinimaxResult(state.getUtility(), node);
            return result;
        }

        List<Node> children = generateChildren(node);
        int bestValue = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Node bestTerminalNode = null;

        // Recurse over children and select best according to maximizing/minimizing
        for (Node child : children) {
            MinimaxResult result = minimax(child, !maximizingPlayer);
            if (maximizingPlayer && result.value > bestValue ||
                !maximizingPlayer && result.value < bestValue) {
                bestValue = result.value;
                // store the terminal node that produced this value
                bestTerminalNode = result.terminalNode;
            }
        }

        MinimaxResult finalResult = new MinimaxResult(bestValue, bestTerminalNode);
        return finalResult;
    }

    /**
     * Minimax with alpha-beta pruning. Similar to minimax(...) but maintains
     * alpha and beta bounds to prune branches. We also sort children using a
     * heuristic to improve pruning effectiveness (simple move ordering).
     */
    private MinimaxResult minimaxAlphaBeta(Node node, int alpha, int beta, boolean maximizingPlayer) {
        nodesExpanded++;
        GameState state = node.getState();

        if (state.isTerminal()) {
            MinimaxResult result = new MinimaxResult(state.getUtility(), node);
            return result;
        }

        List<Node> children = generateChildren(node);
        int bestValue = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Node bestTerminalNode = null;

        // Order moves using a cheap heuristic: move ordering helps optimize alpha-beta
        children.sort((a, b) -> {
            int aValue = heuristicEstimate(a.getState());
            int bValue = heuristicEstimate(b.getState());
            // Descending if maximizing, ascending if minimizing
            return maximizingPlayer ? bValue - aValue : aValue - bValue;
        });

        for (Node child : children) {
            MinimaxResult result = minimaxAlphaBeta(child, alpha, beta, !maximizingPlayer);
            if (maximizingPlayer) {
                if (result.value > bestValue) {
                    bestValue = result.value;
                    bestTerminalNode = result.terminalNode;
                }
                alpha = Math.max(alpha, bestValue);
            } else {
                if (result.value < bestValue) {
                    bestValue = result.value;
                    bestTerminalNode = result.terminalNode;
                }
                beta = Math.min(beta, bestValue);
            }
            // pruning condition
            if (beta <= alpha) break;
        }

        MinimaxResult finalResult = new MinimaxResult(bestValue, bestTerminalNode);
        return finalResult;
    }

    /**
     * Cheap heuristic used for move ordering: computes the total-health
     * advantage for the starting player of the given state. This is not the
     * search evaluation; it's only used to sort child nodes to increase the
     * chance of alpha-beta pruning earlier (performance optimizer).
     */
    private int heuristicEstimate(GameState s) {
        int totalA = s.getTotalHealth(s.getArmyA());
        int totalB = s.getTotalHealth(s.getArmyB());
        return s.getStartingPlayer() == 'A' ? (totalA - totalB) : (totalB - totalA);
    }

    /**
     * Generate all legal children (successor states) from a parent node.
     * The action representation used by this solver is CurrentPlayer(i,j)
     * where i is the index of the attacking unit in the current player's
     * army and j is the index of the target unit in the opponent's army.
     *
     * Important notes:
     * - We skip units that are already dead (health <= 0).
     * - When applying an attack we clone the GameState first to avoid
     *   mutating shared structures.
     * - After the attack, the turn switches to the other player.
     */
    private List<Node> generateChildren(Node parent) {
        List<Node> children = new ArrayList<>();
        GameState state = parent.getState(); // state before applying any action

        // Determine which lists correspond to current player and opponent
        List<Unit> currentArmy = state.getCurrentPlayer() == 'A' ? state.getArmyA() : state.getArmyB();
        List<Unit> opponentArmy = state.getCurrentPlayer() == 'A' ? state.getArmyB() : state.getArmyA();

        // For each alive attacker in current army
        for (int i = 0; i < currentArmy.size(); i++) {
            if (!currentArmy.get(i).isAlive()) continue; // dead units can't act

            // For each alive target in opponent army
            for (int j = 0; j < opponentArmy.size(); j++) {
                if (!opponentArmy.get(j).isAlive()) continue; // skip dead targets

                // Clone the state so changes don't affect other branches
                GameState newState = state.clone();

                // After cloning, grab the appropriate armies from the cloned state
                List<Unit> newCurrentArmy = state.getCurrentPlayer() == 'A' ? newState.getArmyA() : newState.getArmyB();
                List<Unit> newOpponentArmy = state.getCurrentPlayer() == 'A' ? newState.getArmyB() : newState.getArmyA();

                // Apply attack: reduce target health by attacker's damage (floor at 0)
                int damage = newCurrentArmy.get(i).damage;
                newOpponentArmy.get(j).health = Math.max(0, newOpponentArmy.get(j).health - damage);

                // Build action string e.g. "A(0,1)"
                String action = state.getCurrentPlayer() + "(" + i + "," + j + ")";

                // Switch current player for the new state
                newState.setCurrentPlayer(state.getCurrentPlayer() == 'A' ? 'B' : 'A');

                // Create child node with increased depth
                Node child = new Node(newState, parent, action, parent.getDepth() + 1);
                children.add(child);
            }
        }

        return children;
    }

    /**
     * Reconstruct action plan from a terminal node by walking up to the root.
     * Actions are returned as a comma-separated string. If the provided node is
     * null the empty string is returned.
     */
    private String buildPlan(Node goalNode) {
        if (goalNode == null) {
            return "";
        }

        List<String> actions = new ArrayList<>();
        Node current = goalNode;

        // Walk back to root collecting actions (ignore the root which has null action)
        while (current != null && current.getAction() != null) {
            actions.add(0, current.getAction());
            current = current.getParent();
        }

        return String.join(",", actions);
    }

    /**
     * Print a readable step-by-step visualization of the plan and each
     * intervening GameState. Helpful when the `visualize` flag is enabled.
     */
    private void visualizeSolution(Node goalNode) {
        if (goalNode == null) {
            System.out.println("No solution found.");
            return;
        }

        List<Node> path = new ArrayList<>();
        Node current = goalNode;

        // Build path from root to the goal node
        while (current != null) {
            path.add(0, current);
            current = current.getParent();
        }

        System.out.println("=== Solution Visualization ===");
        for (int i = 0; i < path.size(); i++) {
            Node node = path.get(i);
            GameState state = node.getState();

            System.out.println("\nStep " + i + ":");
            if (node.getAction() != null) {
                System.out.println("Action: " + node.getAction());
            }

            System.out.print("Army A: ");
            for (Unit u : state.getArmyA()) {
                System.out.print("[H:" + u.health + " D:" + u.damage + "] ");
            }

            System.out.print("\nArmy B: ");
            for (Unit u : state.getArmyB()) {
                System.out.print("[H:" + u.health + " D:" + u.damage + "] ");
            }

            System.out.println("\nCurrent Player: " + state.getCurrentPlayer());
            System.out.println("Total Health A: " + state.getTotalHealth(state.getArmyA()));
            System.out.println("Total Health B: " + state.getTotalHealth(state.getArmyB()));
        }

        System.out.println("\n=== End Visualization ===");
    }

    // Helper class to store minimax results
    private static class MinimaxResult {
        int value;           // minimax value of the subtree
        Node terminalNode;   // terminal node that produced this value (useful to reconstruct plan)

        MinimaxResult(int value, Node terminalNode) {
            this.value = value;
            this.terminalNode = terminalNode;
        }
    }
}