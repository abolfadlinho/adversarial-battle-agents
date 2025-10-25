package tests;

import org.junit.jupiter.api.Test;

import battle.BattleSolver;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;

import java.time.Duration;

public class MiniMaxTests {
    @Test
    public void test_plan_a() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {

            String initialState = "5,3;4,1;A;";
            BattleSolver b = new BattleSolver();
            String sol = b.solve(initialState, false, false);
            int expectedScore = 12345;

            BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, false);
            assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);

        });
    }


    @Test
    public void test_plan_b() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {

            String initialState = "5,3;4,1;B;";
            BattleSolver b = new BattleSolver();
            String sol = b.solve(initialState, false, false);
            int expectedScore = 12345;

            BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, false);
            assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);

        });
    }


    //--------------------------minimax only tests----------------------------------------
    @Test
    public void test_minimax_a() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {

            String initialState = "5,3;4,1;A;";
            BattleSolver b = new BattleSolver();
            String sol = b.solve(initialState, false, false);

            int expectedScore = 4;


            BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, true);
            assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);

        });
    }


    @Test
    public void test_minimax_b() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {

            String initialState = "5,3;4,1;B;";
            BattleSolver b = new BattleSolver();
            String sol = b.solve(initialState, false, false);

            int expectedScore = 3;


            BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, true);
            assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);

        });
    }


    @Test
    public void test_minimax_c() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {

            String initialState = "1,1,2,3,6,7;5,5,3,1;A;";
        BattleSolver b = new BattleSolver();
        String sol = b.solve(initialState, false, false);

        int expectedScore = 8;


        BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, true);
        assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);

        });
    }

    @Test
    public void test_minimax_d() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {

            String initialState = "1,1,2,3,6,7;5,5,3,1;A;";
        BattleSolver b = new BattleSolver();
        String sol = b.solve(initialState, false, false);

        int expectedScore = 8;
        BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, true);
        assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);

        });
        }

    @Test
    public void test_minimax_e() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {


        String initialState = "7,3,11,9;2,8,4,10,1,6,5,1;A;";
        BattleSolver b = new BattleSolver();
        String sol = b.solve(initialState, false, false);

        int expectedScore = 3;


        BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, true);
        assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);

        });
    }


    @Test
    public void test_minimax_f() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {

            String initialState = "7,3,11,9;2,8,4,10,1,6,5,1;B;";
        BattleSolver b = new BattleSolver();
        String sol = b.solve(initialState, false, false);

        int expectedScore = -3;


        BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, true);
        assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);

        });
    }


    @Test
    public void test_minimax_g() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {
            String initialState = "1,1,2,3,6,7;5,5,10,1;A;";
            BattleSolver b = new BattleSolver();
            String sol = b.solve(initialState, false, false);

            int expectedScore = 7;


            BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, true);
            assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);
        });

    }


    @Test
    public void test_minimax_i() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {
            String initialState = "1,10,5,2,3,5;6,7,3,1,13,4;A;";
            BattleSolver b = new BattleSolver();
            String sol = b.solve(initialState, false, true);
            System.out.println(sol);
            int expectedScore = -4;


            BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, true);
            assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);

        });
    }


    @Test
    public void test_minimax_j() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {
            String initialState = "1,10,5,2,3,5;6,7,3,1,13,4;B;";
            BattleSolver b = new BattleSolver();
            String sol = b.solve(initialState, false, false);

            int expectedScore = -14;


            BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, true);
            assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);

        });

    }
    
}
