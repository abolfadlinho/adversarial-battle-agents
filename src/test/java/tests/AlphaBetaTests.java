package tests;

import org.junit.jupiter.api.Test;

import battle.BattleSolver;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;

import java.time.Duration;

public class AlphaBetaTests {

//-------------------------------alphabeta tests-----------------------------------------------------


    @Test
    public void test_alphabeta_a() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {

            String initialState = "5,3;4,1;A;";
            BattleSolver b = new BattleSolver();
            String sol = b.solve(initialState, true, false);

            int expectedScore = 4;

            BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, true);
            assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);

        });

    }


    @Test
    public void test_alphabeta_b() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {

            String initialState = "5,3;4,1;B;";
            BattleSolver b = new BattleSolver();
            String sol = b.solve(initialState, true, false);

            int expectedScore = 3;

            BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, true);
            assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);
        });

    }

    @Test
    public void test_alphabeta_c() {

        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {


            String initialState = "1,1,2,3,6,7;5,5,3,1;A;";
            BattleSolver b = new BattleSolver();
            String sol = b.solve(initialState, true, false);

            int expectedScore = 8;
            BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, true);
            assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);
        });

    }


    @Test
    public void test_alphabeta_d() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {

            String initialState = "1,1,2,3,6,7;5,5,3,1;B;";
            BattleSolver b = new BattleSolver();
            String sol = b.solve(initialState, true, false);

            int expectedScore = 3;


            BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, true);
            assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);
        });

    }


    @Test
    public void test_alphabeta_e() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {

            String initialState = "7,3,11,9;2,8,4,10,1,6,5,1;A;";
            BattleSolver b = new BattleSolver();
            String sol = b.solve(initialState, true, false);

            int expectedScore = 3;


            BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, true);
            assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);
        });

    }


    @Test
    public void test_alphabeta_f() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {

            String initialState = "7,3,11,9;2,8,4,10,1,6,5,1;B;";
            BattleSolver b = new BattleSolver();
            String sol = b.solve(initialState, true, false);

            int expectedScore = -3;

            BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, true);
            assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);
        });

    }


    @Test
    public void test_alphabeta_g() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {

            String initialState = "11,2,12,3,9,8,13,14;1,4,6,10,7,9,5,2;A;";
            BattleSolver b = new BattleSolver();
            String sol = b.solve(initialState, true, false);

            int expectedScore = 30;


            BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, true);
            assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);
        });

    }


    @Test
    public void test_alphabeta_h() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {

            String initialState = "1,1,1,1,2,3,6,7;5,5,10,1;B;";
            BattleSolver b = new BattleSolver();
            String sol = b.solve(initialState, true, false);

            int expectedScore = -2;
            BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, true);
            assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);
        });

    }


    @Test
    public void test_alphabeta_i() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {

            String initialState = "1,10,5,2,3,5;6,7,3,1,13,4;A;";
            BattleSolver b = new BattleSolver();
            String sol = b.solve(initialState, true, false);
            System.out.println(sol);

            int expectedScore = -4;

            BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, true);
            assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);

        });

    }

    @Test
    public void test_alphabeta_j() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(100), () -> {

            String initialState = "1,1,1,1,2,3,6,7;5,5,10,1;A;";
            BattleSolver b = new BattleSolver();
            String sol = b.solve(initialState, true, false);

            int expectedScore = 8;

            BattleGameChecker.ValidationResult validation = BattleGameChecker.validateSolution(initialState, sol, expectedScore, true);
            assertTrue(validation.isValid, "Valid solution should pass validation: " + validation.errorMessage);
        });

    }
    
}
