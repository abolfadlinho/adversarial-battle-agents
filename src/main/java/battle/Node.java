//src/main/java/battle/Node.java

package battle;

import java.util.ArrayList;
import java.util.List;

/**
 * Node represents a single state in the game tree for adversarial search.
 * Each node contains a GameState, a reference to its parent, the action that
 * led to this state, the depth in the tree, and a list of children nodes.
 * This class is used by BattleSolver to build and traverse the search tree.
 */
@SuppressWarnings("FieldMayBeFinal")
public class Node {

    private GameState state; // the game state at this node
    private Node parent; // parent node in the search tree (null for root)
    private String action; // action string that led to this state, e.g., "A(0,1)" meaning player A attacked unit 0 on unit 1
    private int depth; // at root -> 0
    private List<Node> children; // list of child nodes (successor states)
    public int value; // value assigned by minimax

    /**
     * Constructor for creating a new Node.
     *
     * @param state  the GameState at this node
     * @param parent the parent Node (null for root)
     * @param action the action string that led to this state (null for root)
     * @param depth  the depth in the tree
     */
    public Node(GameState state, Node parent, String action, int depth) {
        this.state = state;
        this.parent = parent;
        this.action = action;
        this.depth = depth;
        this.children = new ArrayList<>();
    }

    // Getter for state
    public GameState getState() {
        return state;
    }

    // Setter for state
    public void setState(GameState state) {
        this.state = state;
    }

    // Getter for parent
    public Node getParent() {
        return parent;
    }

    // Setter for parent
    public void setParent(Node parent) {
        this.parent = parent;
    }

    // Getter for action
    public String getAction() {
        return action;
    }

    // Setter for action
    public void setAction(String action) {
        this.action = action;
    }

    // Getter for depth
    public int getDepth() {
        return depth;
    }

    // Setter for depth
    public void setDepth(int depth) {
        this.depth = depth;
    }

    // Getter for children list
    public List<Node> getChildren() {
        return children;
    }

    // Add a child node to this node's children list
    public void addChild(Node child) {
        this.children.add(child);
    }

    /**
     * GameState represents the current state of the battle game.
     * It includes the two armies (lists of Units), the current player,
     * and the starting player. Provides methods to check terminal conditions,
     * calculate utilities, and clone the state.
     */
    @SuppressWarnings("FieldMayBeFinal")
    public static class GameState {
        
        private List<Unit> armyA; // list of units for army A
        private List<Unit> armyB; // list of units for army B
        private char currentPlayer; // current player ('A' or 'B')
        private char startingPlayer;

        /**
         * Default constructor, initializes empty armies and sets players to 'A'.
         * Only needed for cloning purposes.
         */
        public GameState() {
            this.armyA = new ArrayList<>();
            this.armyB = new ArrayList<>();
            this.currentPlayer = 'A';
            this.startingPlayer = 'A';
        }

        /**
         * Constructor with deep copy of armies to avoid shared mutable state.
         *
         * @param armyA         list of units for army A
         * @param armyB         list of units for army B
         * @param currentPlayer current player
         * @param startingPlayer starting player
         */
        public GameState(List<Unit> armyA, List<Unit> armyB, char currentPlayer, char startingPlayer) {
            this.armyA = new ArrayList<>();
            this.armyB = new ArrayList<>();

            // Deep copy armies to prevent mutations from affecting other states
            for (Unit u : armyA) {
                this.armyA.add(new Unit(u.health, u.damage));
            }
            for (Unit u : armyB) {
                this.armyB.add(new Unit(u.health, u.damage));
            }

            this.currentPlayer = currentPlayer;
            this.startingPlayer = startingPlayer;
        }

        // Getter for army A
        public List<Unit> getArmyA() {
            return armyA;
        }

        // Getter for army B
        public List<Unit> getArmyB() {
            return armyB;
        }

        // Getter for current player
        public char getCurrentPlayer() {
            return currentPlayer;
        }

        // Getter for starting player
        public char getStartingPlayer() {
            return startingPlayer;
        }

        // Setter for current player
        public void setCurrentPlayer(char player) {
            this.currentPlayer = player;
        }

        /**
         * Checks if the game is in a terminal state (one army has zero total health).
         *
         * @return true if the game is over, false otherwise
         */
        public boolean isTerminal() {
            return getTotalHealth(armyA) == 0 || getTotalHealth(armyB) == 0;
        }

        /**
         * Calculates the total health of an army.
         *
         * @param army the list of units
         * @return sum of health values of all units
         */
        public int getTotalHealth(List<Unit> army) {
            int total = 0;
            for (Unit u : army) {
                total += u.health;
            }
            return total;
        }

        /**
         * Computes the utility value for this state, relative to the starting player.
         * Positive values favor the starting player, negative values favor the opponent.
         * Used as the evaluation utility function in minimax.
         *
         * @return utility value
         */
        public int getUtility() {
            int healthA = getTotalHealth(armyA);
            int healthB = getTotalHealth(armyB);

            if (startingPlayer == 'A') { // maximizing A
                if (healthB == 0) return healthA; // A wins, return A's remaining health
                if (healthA == 0) return -healthB; // B wins, negative of B's health (bad for A)
            } else { // maximizing B
                if (healthA == 0) return healthB; // B wins, return B's remaining health
                if (healthB == 0) return -healthA; // A wins, negative of A's health (bad for B)
            }
            return 0; // non-terminal state, neutral
        }

        /**
         * Creates a deep clone of this GameState to avoid mutating shared state.
         * Manually clones armies to ensure independence.
         *
         * @return a new GameState instance with copied data
         */
        @Override
        public GameState clone() {
            List<Unit> clonedArmyA = new ArrayList<>();
            for (Unit u : this.armyA) {
                clonedArmyA.add(new Unit(u.health, u.damage));
            }

            List<Unit> clonedArmyB = new ArrayList<>();
            for (Unit u : this.armyB) {
                clonedArmyB.add(new Unit(u.health, u.damage));
            }

            // Avoid calling the deep-copy constructor again to prevent double-copying
            GameState newState = new GameState();
            newState.armyA = clonedArmyA;
            newState.armyB = clonedArmyB;
            newState.currentPlayer = this.currentPlayer;
            newState.startingPlayer = this.startingPlayer;
            return newState;
        }

        /**
         * Computes a hash code for this GameState, including all fields.
         * Used for hashing in collections like HashMap or HashSet.
         *
         * @return hash code
         */
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + currentPlayer;
            for (Unit u : armyA) {
                hash = 31 * hash + u.health;
                hash = 31 * hash + u.damage;
            }
            for (Unit u : armyB) {
                hash = 31 * hash + u.health;
                hash = 31 * hash + u.damage;
            }
            return hash;
        }

        /**
         * IMPORTANT: was needed for a previous implementation (no longer needed)
         * Checks equality with another object. Two GameStates are equal if
         * they have the same currentPlayer, army sizes, and unit health/damage.
         *
         * @param obj the object to compare
         * @return true if equal, false otherwise
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            GameState other = (GameState) obj;
            if (this.currentPlayer != other.currentPlayer) return false;
            if (this.armyA.size() != other.armyA.size() || this.armyB.size() != other.armyB.size()) return false;
            for (int i = 0; i < armyA.size(); i++) {
                if (armyA.get(i).health != other.armyA.get(i).health ||
                    armyA.get(i).damage != other.armyA.get(i).damage) return false;
            }
            for (int i = 0; i < armyB.size(); i++) {
                if (armyB.get(i).health != other.armyB.get(i).health ||
                    armyB.get(i).damage != other.armyB.get(i).damage) return false;
            }
            return true;
        }

    }

    /**
     * Unit represents a single soldier in the battle, with health and damage attributes.
     * Units are immutable once created (no setters), and provide a method to check if alive.
     */
    @SuppressWarnings("unused")
    public static class Unit {
        
        int health; // health points of the unit
        int damage; // damage dealt by the unit when attacking

        /**
         * Constructor for Unit.
         *
         * @param health initial health
         * @param damage damage value
         */
        public Unit(int health, int damage) {
            this.health = health;
            this.damage = damage;
        }

        /**
         * Checks if the unit is alive (has positive health).
         *
         * @return true if health > 0, false otherwise
         */
        public boolean isAlive() {
            return health > 0;
        }
    }
}