/**
 * JUnit tests for Sorts assignment.
 *
 * @author Paul Hatalsky
 * @author Ayaan Kazerouni
 * @version 11/17/2017 Developed for CPE 203 A* testing
 */

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

//Greta Gamble, Arjan Ellingson, Ross Poletti

//Our test cases can be found in PathingTestComplete file
//We pass all the tests that are in the files in our code
//Professor Kalathas said they don't need to pass the "Start" "End" "StartEnd" or "Reversed" test files
//However, our paths are the same except for the start, end, or reversed order ones which should not pass because of the A* logic
//All the SingleStepPathingStrategy implementations are commented out but can be implemented by uncommenting them and commenting out the A* Pathing ones that are labeled
//The Dude, Fairy, and testing files are where these 2 strategies are implemented and can be un-commented/commented out


public class AStarPathingStrategyAcceptanceTests {
    private static final PathingStrategy strategy = new AStarPathingStrategy();

    private static GridValues[][] grid;
    private static final int ROWS = 9;
    private static final int COLS = 9;

    private enum GridValues {BACKGROUND, OBSTACLE}

    @BeforeAll
    public static void before() {
        grid = new GridValues[ROWS][COLS];
        PathingStrategy.publicizePoint();
    }

    private static int getX(Point p) {
        return PathingStrategy.getX(p);
    }

    private static int getY(Point p) {
        return PathingStrategy.getY(p);
    }

    private static void initialize_grid() {
        for (GridValues[] gridValues : grid) {
            Arrays.fill(gridValues, GridValues.BACKGROUND);
        }
        grid[1][3] = GridValues.OBSTACLE;
        for (int row = 1; row < 6; row++) {
            grid[row][4] = GridValues.OBSTACLE;
        }
        grid[5][2] = GridValues.OBSTACLE;
        grid[5][3] = GridValues.OBSTACLE;
    }

    private static boolean withinBounds(Point p, GridValues[][] grid) {
        return getY(p) >= 0 && getY(p) < grid.length &&
                getX(p) >= 0 && getX(p) < grid[0].length;
    }

    private static boolean neighbors(Point p1, Point p2) {
        return getX(p1) + 1 == getX(p2) && getY(p1) == getY(p2) ||
                getX(p1) - 1 == getX(p2) && getY(p1) == getY(p2) ||
                getX(p1) == getX(p2) && getY(p1) + 1 == getY(p2) ||
                getX(p1) == getX(p2) && getY(p1) - 1 == getY(p2);
    }

    private static boolean two_doors_down(Point p1, Point p2) {
        return getX(p1) + 2 == getX(p2) && getY(p1) == getY(p2) ||
                getX(p1) - 2 == getX(p2) && getY(p1) == getY(p2) ||
                getX(p1) == getX(p2) && getY(p1) + 2 == getY(p2) ||
                getX(p1) == getX(p2) && getY(p1) - 2 == getY(p2) ||
                getX(p1) + 1 == getX(p2) && getY(p1) + 1 == getY(p2) ||
                getX(p1) + 1 == getX(p2) && getY(p1) - 1 == getY(p2) ||
                getX(p1) - 1 == getX(p2) && getY(p1) + 1 == getY(p2) ||
                getX(p1) - 1 == getX(p2) && getY(p1) - 1 == getY(p2);
    }

    @Test
    @Timeout(value=500, unit=TimeUnit.MILLISECONDS)
    public void test01_computePath01() {
        initialize_grid();
        Point start = new Point(3, 4);
        Point end = new Point(5, 4);
        List<Point> path =
                strategy.computePath(start, end,
                        p -> withinBounds(p, grid) && grid[getY(p)][getX(p)] != GridValues.OBSTACLE,
                        AStarPathingStrategyAcceptanceTests::neighbors,
                        PathingStrategy.CARDINAL_NEIGHBORS);

        List<Point> expected = List.of(new Point(2, 4), new Point(1, 4), new Point(1, 5), new Point(1, 6), new Point(2, 6),
                new Point(3, 6), new Point(4, 6), new Point(5, 6), new Point(5, 5));

        assertEquals(expected, path);
    }

    @Test
    @Timeout(value=500, unit=TimeUnit.MILLISECONDS)
    public void test02_computePath02() {
        initialize_grid();
        List<Point> path =
                strategy.computePath(new Point(5, 4),
                        new Point(3, 4),
                        p -> withinBounds(p, grid) && grid[getY(p)][getX(p)] != GridValues.OBSTACLE,
                        AStarPathingStrategyAcceptanceTests::neighbors,
                        PathingStrategy.CARDINAL_NEIGHBORS);

        List<Point> expected = List.of(new Point(5, 5), new Point(5, 6), new Point(4, 6), new Point(3, 6), new Point(2, 6),
                new Point(1, 6), new Point(1, 5), new Point(1, 4), new Point(2, 4));
        assertEquals(expected, path);
    }

    @Test
    @Timeout(value=500, unit=TimeUnit.MILLISECONDS)
    public void test03_computePath03() {
        initialize_grid();
        grid[5][1] = GridValues.OBSTACLE;
        grid[6][1] = GridValues.OBSTACLE;
        grid[3][2] = GridValues.OBSTACLE;
        List<Point> path =
                strategy.computePath(new Point(3, 4),
                        new Point(5, 4),
                        p -> withinBounds(p, grid) && grid[getY(p)][getX(p)] != GridValues.OBSTACLE,
                        AStarPathingStrategyAcceptanceTests::neighbors,
                        PathingStrategy.CARDINAL_NEIGHBORS);

        List<Point> expected = List.of(new Point(3, 3), new Point(3, 2), new Point(2, 2), new Point(2, 1), new Point(2, 0),
                new Point(3, 0), new Point(4, 0), new Point(5, 0), new Point(5, 1), new Point(5, 2), new Point(5, 3));
        assertEquals(expected, path);
    }

    @Test
    @Timeout(value=500, unit=TimeUnit.MILLISECONDS)
    public void test04_computePath04() {
        initialize_grid();
        grid[5][1] = GridValues.OBSTACLE;
        grid[6][1] = GridValues.OBSTACLE;
        grid[3][2] = GridValues.OBSTACLE;
        List<Point> path =
                strategy.computePath(new Point(5, 4),
                        new Point(3, 4),
                        p -> withinBounds(p, grid) && grid[getY(p)][getX(p)] != GridValues.OBSTACLE,
                        AStarPathingStrategyAcceptanceTests::neighbors,
                        PathingStrategy.CARDINAL_NEIGHBORS);

        List<Point> expected = List.of(new Point(5, 3), new Point(5, 2), new Point(5, 1), new Point(5, 0), new Point(4, 0),
                new Point(3, 0), new Point(2, 0), new Point(2, 1), new Point(2, 2), new Point(3, 2), new Point(3, 3));
        assertEquals(expected, path);
    }

    @Test
    @Timeout(value=500, unit=TimeUnit.MILLISECONDS)
    public void test05_computePath05() {
        initialize_grid();
        grid[5][0] = GridValues.OBSTACLE;
        grid[5][1] = GridValues.OBSTACLE;
        grid[0][5] = GridValues.OBSTACLE;
        List<Point> path =
                strategy.computePath(new Point(5, 4),
                        new Point(3, 4),
                        p -> withinBounds(p, grid) && grid[getY(p)][getX(p)] != GridValues.OBSTACLE,
                        AStarPathingStrategyAcceptanceTests::neighbors,
                        PathingStrategy.CARDINAL_NEIGHBORS);

        assertTrue(path == null || path.isEmpty());
    }

    @Test
    @Timeout(value=500, unit=TimeUnit.MILLISECONDS)
    public void test06_computePath06() {
        initialize_grid();
        grid[4][2] = GridValues.OBSTACLE;
        grid[3][3] = GridValues.OBSTACLE;
        List<Point> path =
                strategy.computePath(new Point(3, 4),
                        new Point(5, 3),
                        p -> withinBounds(p, grid) && grid[getY(p)][getX(p)] != GridValues.OBSTACLE,
                        AStarPathingStrategyAcceptanceTests::neighbors,
                        PathingStrategy.CARDINAL_NEIGHBORS);

        assertTrue(path == null || path.isEmpty());
    }

    @Test
    @Timeout(value=500, unit=TimeUnit.MILLISECONDS)
    public void test07_computePath07() {
        for (GridValues[] gridValues : grid) {
            Arrays.fill(gridValues, GridValues.BACKGROUND);
        }
        grid[1][1] = GridValues.OBSTACLE;
        grid[1][3] = GridValues.OBSTACLE;
        grid[1][5] = GridValues.OBSTACLE;
        grid[1][6] = GridValues.OBSTACLE;
        grid[1][7] = GridValues.OBSTACLE;
        grid[2][2] = GridValues.OBSTACLE;
        grid[2][4] = GridValues.OBSTACLE;
        grid[2][7] = GridValues.OBSTACLE;
        grid[3][2] = GridValues.OBSTACLE;
        grid[3][4] = GridValues.OBSTACLE;
        grid[3][7] = GridValues.OBSTACLE;
        grid[4][2] = GridValues.OBSTACLE;
        grid[4][4] = GridValues.OBSTACLE;
        grid[4][7] = GridValues.OBSTACLE;
        grid[5][1] = GridValues.OBSTACLE;
        grid[5][4] = GridValues.OBSTACLE;
        grid[5][5] = GridValues.OBSTACLE;
        grid[5][7] = GridValues.OBSTACLE;
        grid[6][2] = GridValues.OBSTACLE;
        grid[6][4] = GridValues.OBSTACLE;
        grid[6][7] = GridValues.OBSTACLE;
        grid[7][0] = GridValues.OBSTACLE;
        grid[7][2] = GridValues.OBSTACLE;
        grid[7][4] = GridValues.OBSTACLE;
        grid[7][6] = GridValues.OBSTACLE;
        grid[7][7] = GridValues.OBSTACLE;
        grid[8][0] = GridValues.OBSTACLE;
        grid[8][4] = GridValues.OBSTACLE;
        List<Point> path =
                strategy.computePath(new Point(3, 4),
                        new Point(5, 4),
                        p -> withinBounds(p, grid) && grid[getY(p)][getX(p)] != GridValues.OBSTACLE,
                        AStarPathingStrategyAcceptanceTests::neighbors,
                        PathingStrategy.CARDINAL_NEIGHBORS);

        List<Point> expected = List.of(new Point(3, 5), new Point(3, 6), new Point(3, 7), new Point(3, 8), new Point(2, 8), new Point(1, 8),
                new Point(1, 7), new Point(1, 6), new Point(0, 6), new Point(0, 5), new Point(0, 4), new Point(0, 3),
                new Point(0, 2), new Point(0, 1), new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(3, 0),
                new Point(4, 0), new Point(5, 0), new Point(6, 0), new Point(7, 0), new Point(8, 0), new Point(8, 1),
                new Point(8, 2), new Point(8, 3), new Point(8, 4), new Point(8, 5), new Point(8, 6), new Point(8, 7),
                new Point(8, 8), new Point(7, 8), new Point(6, 8), new Point(5, 8), new Point(5, 7), new Point(5, 6),
                new Point(6, 6), new Point(6, 5), new Point(6, 4));
        assertEquals(expected, path);
    }

    @Test
    @Timeout(value=500, unit=TimeUnit.MILLISECONDS)
    public void test08_computePath08() {
        for (GridValues[] gridValues : grid) {
            Arrays.fill(gridValues, GridValues.BACKGROUND);
        }
        grid[0][4] = GridValues.OBSTACLE;
        grid[1][1] = GridValues.OBSTACLE;
        grid[1][4] = GridValues.OBSTACLE;
        grid[4][0] = GridValues.OBSTACLE;
        grid[4][1] = GridValues.OBSTACLE;

        List<Point> path =
                strategy.computePath(new Point(0, 0),
                        new Point(8, 8),
                        p -> withinBounds(p, grid) && grid[getY(p)][getX(p)] != GridValues.OBSTACLE,
                        AStarPathingStrategyAcceptanceTests::neighbors,
                        PathingStrategy.CARDINAL_NEIGHBORS);


        assertEquals(15, path.size());
    }

    @Test
    @Timeout(value=500, unit=TimeUnit.MILLISECONDS)
    public void test09_computePath09() {
        for (GridValues[] gridValues : grid) {
            Arrays.fill(gridValues, GridValues.BACKGROUND);
        }
        grid[0][4] = GridValues.OBSTACLE;
        grid[1][1] = GridValues.OBSTACLE;
        grid[1][4] = GridValues.OBSTACLE;
        grid[4][0] = GridValues.OBSTACLE;
        grid[4][1] = GridValues.OBSTACLE;

        List<Point> path =
                strategy.computePath(new Point(8, 8),
                        new Point(0, 0),
                        p -> withinBounds(p, grid) && grid[getY(p)][getX(p)] != GridValues.OBSTACLE,
                        AStarPathingStrategyAcceptanceTests::neighbors,
                        PathingStrategy.CARDINAL_NEIGHBORS);


        assertEquals(15, path.size());
    }

    @Test
    @Timeout(value=500, unit=TimeUnit.MILLISECONDS)
    public void test10_computePath10() {
        for (GridValues[] gridValues : grid) {
            Arrays.fill(gridValues, GridValues.BACKGROUND);
        }
        grid[7][8] = GridValues.OBSTACLE;
        grid[8][7] = GridValues.OBSTACLE;
        List<Point> path =
                strategy.computePath(new Point(0, 0),
                        new Point(8, 8),
                        p -> withinBounds(p, grid) && grid[getY(p)][getX(p)] != GridValues.OBSTACLE,
                        AStarPathingStrategyAcceptanceTests::neighbors,
                        PathingStrategy.CARDINAL_NEIGHBORS);


        assertTrue(path == null || path.isEmpty());
    }

    @Test
    @Timeout(value=1000, unit=TimeUnit.MILLISECONDS)
    public void test11_computePath11() {
        GridValues[][] localGrid = new GridValues[100][100];
        for (int row = 0; row < 100; row++) {
            for (int col = 0; col < 100; col++) {
                localGrid[row][col] = GridValues.BACKGROUND;
            }
        }
        for (int i = 1; i < 99; i++)
            localGrid[i][i] = GridValues.OBSTACLE;
        List<Point> path =
                strategy.computePath(new Point(0, 99),
                        new Point(99, 0),
                        p -> withinBounds(p, localGrid) && localGrid[getY(p)][getX(p)] != GridValues.OBSTACLE,
                        AStarPathingStrategyAcceptanceTests::neighbors,
                        PathingStrategy.CARDINAL_NEIGHBORS);

        assertEquals(197, path.size());
    }

    @Test
    @Timeout(value=500, unit=TimeUnit.MILLISECONDS)
    public void test12_computePath12_diag_card_neighbors() {
        initialize_grid();
        grid[7][3] = GridValues.OBSTACLE;
        List<Point> path =
                strategy.computePath(new Point(5, 4),
                        new Point(3, 4),
                        p -> withinBounds(p, grid) && grid[getY(p)][getX(p)] != GridValues.OBSTACLE,
                        AStarPathingStrategyAcceptanceTests::neighbors,
                        PathingStrategy.DIAGONAL_CARDINAL_NEIGHBORS);

        Point[] expected = new Point[]{new Point(5, 5), new Point(4, 6), new Point(3, 6), new Point(2, 6),
                new Point(1, 5), new Point(2, 4)};
        assertArrayEquals(expected, path.toArray());
    }

    @Test
    @Timeout(value=500, unit=TimeUnit.MILLISECONDS)
    public void test13_computePath13_within_reach_ish() {
        initialize_grid();
        List<Point> path =
                strategy.computePath(new Point(5, 4),
                        new Point(2, 4),
                        p -> withinBounds(p, grid) && grid[getY(p)][getX(p)] != GridValues.OBSTACLE,
                        AStarPathingStrategyAcceptanceTests::two_doors_down,
                        PathingStrategy.CARDINAL_NEIGHBORS);

        List<Point> expected = List.of(new Point(5, 5), new Point(5, 6), new Point(4, 6),
                new Point(3, 6), new Point(2, 6));
        assertEquals(expected, path);
    }
}
