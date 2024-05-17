package org.iesvdm.sudoku;

//import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
public class SudokuTest {

    @Test
    void failTest() {
        Sudoku sudoku = new Sudoku();
        sudoku.fillBoardBasedInCluesRandomlySolvable();
        sudoku.fillBoardBasedInCluesRandomly();
        sudoku.printBoard();
    }

        @Test
        void fillBoardRandomlyTest() {
            Sudoku sudoku = new Sudoku();
            sudoku.fillBoardRandomly();

        }

        @Test
        void fillBoardBasedInCluesRandomlyTest() {
            Sudoku sudoku = new Sudoku();
            sudoku.fillBoardBasedInCluesRandomly();

        }

        @Test
        void fillBoardBasedInCluesRandomlySolvableTest() {
            Sudoku sudoku = new Sudoku();
            sudoku.fillBoardBasedInCluesRandomlySolvable();

        }

        @Test
        void fillBoardSolvableTest() {
            Sudoku sudoku = new Sudoku();
            sudoku.fillBoardSolvable();

        }

        @Test
        void fillBoardUnsolvableTest() {
            Sudoku sudoku = new Sudoku();
            sudoku.fillBoardUnsolvable();

        }

        @Test
        void solveBoardTest() {
            Sudoku sudoku = new Sudoku();

            sudoku.fillBoardSolvable();
            assertThat(sudoku.solveBoard()).isTrue();

            sudoku.fillBoardUnsolvable();
            assertThat(sudoku.solveBoard()).isFalse();
        }
    }

