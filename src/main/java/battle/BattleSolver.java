//src/main/java/battle/BattleSolver.java

package battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import battle.Node.GameState;
import battle.Node.Unit;

@SuppressWarnings("unused")
public class BattleSolver {
    private int nodesExpanded;
    private boolean visualize;
    public Node initialNode;
    private Map<Node.GameState, MinimaxResult> memo;

    public BattleSolver() {
        this.nodesExpanded = 0;
        this.memo = new HashMap<>();
    }

    public String solve(String initialStateString, boolean ab, boolean visualize) {
        this.memo.clear();
        this.visualize = visualize;
        this.nodesExpanded = 0;

        // Parse initial state
        GameState initialState = parseInitialState(initialStateString);
        Node rootNode = new Node(initialState, null, null, 0);

        // Run minimax
        MinimaxResult result;
        if (ab) {
            result = minimaxAlphaBeta(rootNode, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        } else {
            result = minimax(rootNode, true);
        }

        // Build solution path from the terminal node
        String plan = buildPlan(result.terminalNode);
        int score = result.value;

        if (visualize) {
            visualizeSolution(result.terminalNode);
        }

        return plan + ";" + score + ";" + nodesExpanded + ";";
    }

    private GameState parseInitialState(String stateString) {
        String[] parts = stateString.split(";");

        // Parse army A
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

        // Parse starting player
        char startingPlayer = parts[2].charAt(0);

        return new GameState(armyA, armyB, startingPlayer, startingPlayer);
    }

    private MinimaxResult minimax(Node node, boolean maximizingPlayer) {
        nodesExpanded++;
        GameState state = node.getState();

        // ✅ Memoization check
        if (memo.containsKey(state)) {
            return memo.get(state);
        }

        if (state.isTerminal()) {
            MinimaxResult result = new MinimaxResult(state.getUtility(), node);
            memo.put(state, result);
            return result;
        }

        List<Node> children = generateChildren(node);
        int bestValue = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Node bestTerminalNode = null;

        for (Node child : children) {
            MinimaxResult result = minimax(child, !maximizingPlayer);
            if (maximizingPlayer && result.value > bestValue ||
                !maximizingPlayer && result.value < bestValue) {
                bestValue = result.value;
                bestTerminalNode = result.terminalNode;
            }
        }

        MinimaxResult finalResult = new MinimaxResult(bestValue, bestTerminalNode);
        memo.put(state, finalResult); // ✅ Store in cache
        return finalResult;
    }

    private MinimaxResult minimaxAlphaBeta(Node node, int alpha, int beta, boolean maximizingPlayer) {
        nodesExpanded++;
        GameState state = node.getState();

        // ✅ Memoization check
        /*if (memo.containsKey(state)) {
            return memo.get(state);
        }*/

        if (state.isTerminal()) {
            MinimaxResult result = new MinimaxResult(state.getUtility(), node);
            //memo.put(state, result);
            return result;
        }

        List<Node> children = generateChildren(node);
        int bestValue = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Node bestTerminalNode = null;

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
            if (beta <= alpha) break;
        }

        MinimaxResult finalResult = new MinimaxResult(bestValue, bestTerminalNode);
        //memo.put(state, finalResult); // ✅ Store in cache
        return finalResult;
    }

    private int heuristicEstimate(GameState s) {
        // Simple: total health advantage of current player
        int totalA = s.getTotalHealth(s.getArmyA());
        int totalB = s.getTotalHealth(s.getArmyB());
        return s.getStartingPlayer() == 'A' ? (totalA - totalB) : (totalB - totalA);
    }

    private List<Node> generateChildren(Node parent) {
        List<Node> children = new ArrayList<>();
        GameState state = parent.getState(); //state before applying any action

        List<Unit> currentArmy = state.getCurrentPlayer() == 'A' ? state.getArmyA() : state.getArmyB(); //the player's army before moving from parent to child
        List<Unit> opponentArmy = state.getCurrentPlayer() == 'A' ? state.getArmyB() : state.getArmyA();

        // For each alive unit in current army
        for (int i = 0; i < currentArmy.size(); i++) {
            if (!currentArmy.get(i).isAlive()) continue; //skip unit if he is dead, he cant do anything

            // For each alive unit in opponent army
            for (int j = 0; j < opponentArmy.size(); j++) {
                if (!opponentArmy.get(j).isAlive()) continue; //if unit is already dead then skip him

                // Create new state
                GameState newState = state.clone();

                List<Unit> newCurrentArmy = state.getCurrentPlayer() == 'A' ? newState.getArmyA() : newState.getArmyB();
                List<Unit> newOpponentArmy = state.getCurrentPlayer() == 'A' ? newState.getArmyB() : newState.getArmyA();

                // Apply attack
                int damage = newCurrentArmy.get(i).damage;
                newOpponentArmy.get(j).health = Math.max(0, newOpponentArmy.get(j).health - damage);

                // Create action string
                String action = state.getCurrentPlayer() + "(" + i + "," + j + ")";

                // Switch player
                newState.setCurrentPlayer(state.getCurrentPlayer() == 'A' ? 'B' : 'A');

                // Create child node
                Node child = new Node(newState, parent, action, parent.getDepth() + 1);
                children.add(child);
            }
        }

        return children;
    }

    private String buildPlan(Node goalNode) {
        if (goalNode == null) {
            return "";
        }

        List<String> actions = new ArrayList<>();
        Node current = goalNode;

        while (current != null && current.getAction() != null) {
            actions.add(0, current.getAction());
            current = current.getParent();
        }

        return String.join(",", actions);
    }

    private void visualizeSolution(Node goalNode) {
        if (goalNode == null) {
            System.out.println("No solution found.");
            return;
        }

        List<Node> path = new ArrayList<>();
        Node current = goalNode;

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

    // Helper class to store minimax result
    private static class MinimaxResult {
        int value;
        Node terminalNode;  // Changed from bestNode to terminalNode

        MinimaxResult(int value, Node terminalNode) {
            this.value = value;
            this.terminalNode = terminalNode;
        }
    }
}