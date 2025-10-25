//src/main/java/battle/Node.java

package battle;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
public class Node {
    private GameState state;
    private Node parent;
    private String action; // Format: "Player(attackerIndex,targetIndex)" , this is the action that led to this state
    private int depth;
    private List<Node> children;
    public int value;

    public Node(GameState state, Node parent, String action, int depth) {
        this.state = state;
        this.parent = parent;
        this.action = action;
        this.depth = depth;
        this.children = new ArrayList<>();
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    // Helper class to represent game state
    @SuppressWarnings("FieldMayBeFinal")
    public static class GameState {
        private List<Unit> armyA;
        private List<Unit> armyB;
        private char currentPlayer;
        private char startingPlayer;

        public GameState() {
            this.armyA = new ArrayList<>();
            this.armyB = new ArrayList<>();
            this.currentPlayer = 'A';
            this.startingPlayer = 'A';
        }

        public GameState(List<Unit> armyA, List<Unit> armyB, char currentPlayer, char startingPlayer) {
            this.armyA = new ArrayList<>();
            this.armyB = new ArrayList<>();

            // Deep copy armies
            for (Unit u : armyA) {
                this.armyA.add(new Unit(u.health, u.damage));
            }
            for (Unit u : armyB) {
                this.armyB.add(new Unit(u.health, u.damage));
            }

            this.currentPlayer = currentPlayer;
            this.startingPlayer = startingPlayer;
        }

        public List<Unit> getArmyA() {
            return armyA;
        }

        public List<Unit> getArmyB() {
            return armyB;
        }

        public char getCurrentPlayer() {
            return currentPlayer;
        }

        public char getStartingPlayer() {
            return startingPlayer;
        }

        public void setCurrentPlayer(char player) {
            this.currentPlayer = player;
        }

        public boolean isTerminal() {
            return getTotalHealth(armyA) == 0 || getTotalHealth(armyB) == 0;
        }

        public int getTotalHealth(List<Unit> army) {
            int total = 0;
            for (Unit u : army) {
                total += u.health;
            }
            return total;
        }

        public int getUtility() {
            int healthA = getTotalHealth(armyA);
            int healthB = getTotalHealth(armyB);

            if (startingPlayer == 'A') { //if we are maximizing A
                if (healthB == 0) return healthA; //if B is dead, A wins therefore return A's health since we are maximizing A
                if (healthA == 0) return -healthB; //if A is dead, B wins therefore return negative of B because it is bad for A
            } else { //if we are maximizing B
                if (healthA == 0) return healthB; //if A is dead, B wins therefore return B's health since we are maximizing B
                if (healthB == 0) return -healthA; //if B is dead, A wins therefore return negative of A because it is bad for B
            }
            return 0;
        }

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

            // ❌ don't call constructor that deep-copies again
            GameState newState = new GameState();
            newState.armyA = clonedArmyA;
            newState.armyB = clonedArmyB;
            newState.currentPlayer = this.currentPlayer;
            newState.startingPlayer = this.startingPlayer;
            return newState;
        }
        
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

    //helper class to represent units (soldiers)
    @SuppressWarnings("unused")
    public static class Unit {
        int health;
        int damage;

        public Unit(int health, int damage) {
            this.health = health;
            this.damage = damage;
        }

        public boolean isAlive() {
            return health > 0;
        }
    }
}