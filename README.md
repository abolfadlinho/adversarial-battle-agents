### Creating the Maven project

```bash
mvn "archetype:generate" "-DgroupId=com.battle" "-DartifactId=maven-battle" "-DarchetypeArtifactId=maven-archetype-quickstart" "-DinteractiveMode=false"
```

### Compile before running

```bash
mvn clean compile
```

### Run test files individually

```bash
mvn -Dtest=battleTestsPublic test
mvn -Dtest=MiniMaxTests test
mvn -Dtest=AlphaBetaTests test
```

### Run ALL compiled test files

```bash
mvn test
```

## Project README — Detailed Documentation and Report

This section documents the `maven-battle` project in detail. It preserves the short usage instructions above and expands with design notes, algorithm details, API documentation for the main classes, example usage, complexity discussion, known caveats, and ideas for future improvements.

### Project overview

This project implements a simple two-player deterministic turn-based battle solver using search algorithms (Minimax and Alpha-Beta pruning) over a small game state. The core logic is implemented in:

- `src/main/java/battle/BattleSolver.java` — orchestrates parsing, search (minimax / alpha-beta), heuristics, and solution reconstruction/visualization.
- `src/main/java/battle/Node.java` — defines the search tree node and contains the nested `GameState` and `Unit` helper classes.

The test suite in `src/test/java/tests` contains unit tests (MiniMax, AlphaBeta, and public battle tests) that validate behaviour.

### Game model and input format

- Armies: Each player has a list of units. Each unit has integer `health` and `damage` values.
- Turns: Players alternate moves. On a player's turn, one of their alive units attacks one of the opponent's alive units.
- Terminal condition: The game ends when the total health of one army becomes zero.

Input string format accepted by `BattleSolver.solve`:

`<armyA_pairs>;<armyB_pairs>;<startingPlayer>`

Where `<armyX_pairs>` is a comma-separated list of integer pairs `health,damage` for each unit and `<startingPlayer>` is `A` or `B`.

Example (two units per army, starting with A):

```
10,3,5,2;8,2,6,1;A
```

This encodes: Army A = [(health=10,dmg=3),(5,2)] and Army B = [(8,2),(6,1)], starting player A.

### What the solver returns

The public API method `BattleSolver.solve(String initialStateString, boolean ab, boolean visualize)` returns a single string with the following semicolon-separated format:

```
<plan>;<score>;<nodesExpanded>;
```

- `<plan>` — comma-separated actions like `A(0,1)` meaning player A's unit #0 attacked enemy unit #1. If no actions exist (edge cases), it may be empty.
- `<score>` — integer utility value for the terminal node as computed by the solver.
- `<nodesExpanded>` — number of nodes expanded by the search.

Example call in code:

```
BattleSolver s = new BattleSolver();
String result = s.solve("10,3,5,2;8,2,6,1;A", true, false);
// => something like: "A(0,1),B(1,0),...;12;345;"
```

### High-level design and algorithm

- Search node: `Node` holds a `GameState`, a reference to the parent node, the action that produced it, depth and a children list.
- GameState: contains `armyA`, `armyB`, `currentPlayer`, and `startingPlayer`. Utility is calculated from the perspective of `startingPlayer`.
- Actions: an action is always an attack: `currentPlayer(attackerIndex,targetIndex)`. After the action the target unit's health is reduced by attacker's damage, and the turn switches.

Search implemented:

- Plain Minimax (`minimax`): recursive minimax with memoization (a `Map<GameState, MinimaxResult>`). When a terminal state is reached, `GameState.getUtility()` provides the leaf value. The memo cache reduces repeated work.
- Alpha-Beta (`minimaxAlphaBeta`): recursive alpha-beta pruning with node ordering (children are sorted using `heuristicEstimate`) to attempt better cutoffs. Note: this implementation does not use memoization (commented out), but it uses ordering heuristics for pruning improvement.

Heuristic used for ordering: simple total-health advantage for the starting player (difference between sum of healths).

### Important implementation details (method-level)

- BattleSolver.solve(initialStateString, ab, visualize)

  - Parses the input, creates the root node, runs either minimax or alpha-beta depending on `ab`, builds the action plan from the terminal node, optionally prints a visualization, and returns `plan;score;nodesExpanded;`.

- parseInitialState(stateString)

  - Splits input by `;`, parses army A tokens and B tokens into lists of `Unit` objects, and sets the starting player.

- minimax(node, maximizingPlayer)

  - Expands `node`, checks memo map; if terminal, returns utility. Otherwise recurses on children, tracks best value and the corresponding terminal node. Stores results in `memo` keyed by `GameState`.

- minimaxAlphaBeta(node, alpha, beta, maximizingPlayer)

  - Similar structure to `minimax`, but uses alpha-beta bounds and sorts children using `heuristicEstimate` before recursion for better pruning.

- generateChildren(parent)

  - Enumerates each alive attacker in current player's army and each alive target in opponent's army, clones the `GameState`, applies damage, flips `currentPlayer`, and constructs a child `Node` with the action string.

- Node.GameState.clone()
  - Performs a deep copy of armies and copies `currentPlayer` and `startingPlayer`.

### Classes & public methods (quick reference)

- battle.BattleSolver

  - BattleSolver() — constructor
  - String solve(String initialStateString, boolean ab, boolean visualize)

- battle.Node
  - Node(GameState state, Node parent, String action, int depth)
  - GameState (nested): holds armies, currentPlayer, startingPlayer. Key methods: `isTerminal()`, `getTotalHealth(List<Unit>)`, `getUtility()`, `clone()`, `equals()`, `hashCode()`.
  - Unit (nested): fields `health`, `damage`, method `isAlive()`.

### Example runs / commands

Run a single test class (already in the short README above):

```powershell
mvn -Dtest=battleTestsPublic test
mvn -Dtest=MiniMaxTests test
mvn -Dtest=AlphaBetaTests test
```

Run all tests:

```powershell
mvn test
```

Programmatic example (from another Java class):

```java
BattleSolver solver = new BattleSolver();
String result = solver.solve("10,3,5,2;8,2,6,1;A", true, true);
System.out.println(result); // prints plan;score;nodesExpanded;
```

### Complexity and performance notes

- Branching factor: approximately (#alive attackers) \* (#alive opponents). For two armies of n units, branching factor is O(n^2) in the worst case.
- Depth: number of actions until one army's total health reaches zero; can be up to O(n \* maxHealth) in pathological views but practically bounded by the number of units times how many hits required to kill.
- Overall complexity: exponential in the depth; naive worst-case O(b^d) where b is branching factor and d is depth.

Optimizations present:

- Memoization: plain minimax stores visited `GameState` -> `MinimaxResult` in `memo`.
- Alpha-beta ordering: `minimaxAlphaBeta` sorts children by a heuristic estimate (health advantage) to improve pruning.

Tradeoffs / limitations:

- The alpha-beta implementation has memoization commented out. Combining memoization with alpha-beta requires care (transposition table, storing bounds) but could yield improvements.
- `GameState.equals`/`hashCode` currently consider `currentPlayer` and armies but do not include `startingPlayer` consistently in `hashCode` (the `equals` method likewise does not check `startingPlayer`). Because `getUtility()` depends on `startingPlayer`, memoization keyed by `GameState` may conflate states that differ only by `startingPlayer`. This can be a subtle bug affecting correctness of cached results when the same board is reached with different `startingPlayer` values.

### Known caveats and suggested fixes

1. Memoization vs starting player

   - Problem: `startingPlayer` affects utility calculation, but is not included in `equals/hashCode`.
   - Fix: include `startingPlayer` in both `hashCode()` and `equals()` comparisons so cached results are safe.

2. Consider using a transposition table for alpha-beta

   - The alpha-beta branch currently doesn't use the `memo` map (it's commented out). A full implementation could use a transposition table that stores upper/lower bounds along with exact values when known.

3. Heuristic improvements

   - The current heuristic for ordering is simple total-health advantage. More accurate heuristics (e.g., considering potential overkill, targeting priority, or unit DPS/time-to-kill) could improve pruning and move ordering.

4. Move-generation pruning
   - Consider dominance pruning heuristics: e.g., identical units or symmetric targets could be collapsed to reduce branching.

### Tests and validation

The repository contains tests under `src/test/java/tests` (MiniMaxTests, AlphaBetaTests, battleTestsPublic). Run them with `mvn test` or the single-test commands above. The README's top section already shows how to compile and run tests.

### Next steps & extension ideas

- Add full transposition table for alpha-beta and store bounds.
- Add iterative deepening to allow anytime behaviour and to improve move ordering with previous depth results.
- Add unit tests specifically targeting memoization correctness with different `startingPlayer` values.
- Add a CLI runner that accepts an input string and prints a more human-friendly breakdown of the plan and step-by-step visualization (instead of the single-line result).

### Appendix: quick pointers into the source

- `BattleSolver.generateChildren` creates children by cloning `GameState`, applying damage, flipping `currentPlayer`, and creating a `Node` with action string `Player(attackerIndex,targetIndex)`.
- `Node.GameState.getUtility()` returns a positive number for a win from the perspective of `startingPlayer` (value is the winner's remaining total health) or negative when the starting player loses.

---

If you'd like, I can:

- open a PR that 1) adds `startingPlayer` to `hashCode/equals`, 2) enable memoization consistently for alpha-beta with a simple transposition table, and 3) add a few tests to assert memoization correctness. Mark which you'd prefer and I'll implement the changes.
