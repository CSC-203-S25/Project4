//Ross Poletti, Arjan Ellingson, Greta Gamble
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.util.List;

public class PathingTestComplete {

    @BeforeAll
    public static void before() {
        PathingStrategy.publicizePoint();
    }


    @Test
    public void testSingleStepNoObstacles() {
        boolean[][] grid = {
                { true, true, true },
                { true, true, true },
                { true, true, true }
        };

        Point start = new Point(1, 0);
        Point end   = new Point(1, 2);

        PathingStrategy single = new SingleStepPathingStrategy();
        List<Point> path = single.computePath(
                start, end,
                p -> withinBounds(p, grid) && grid[p.y][p.x],
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        List<Point> expected = List.of(new Point(1, 1));
        assertEquals(expected, path);
    }

    @Test
    // Write more tests including obstacles and other edge cases.
    public void testSingleStepWithObstacleBetween() {
        boolean[][] grid = {
                { true, true, true },
                { true, false, true },
                { true, true, true }
        };

        Point start = new Point(1, 0);
        Point end   = new Point(1, 2);

        PathingStrategy single = new SingleStepPathingStrategy();
        List<Point> path = single.computePath(
                start, end,
                p -> withinBounds(p, grid) && grid[p.y][p.x],
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        // obstacle at (1,1) blocks the only step
        assertTrue(path.isEmpty());
    }

    @Test
    public void testSingleStepSurroundedByObstacles() {
        boolean[][] grid = {
                { false, false, false },
                { false, true,  false },
                { false, false, false }
        };

        Point start = new Point(1, 1);
        Point end   = new Point(2, 1);

        PathingStrategy single = new SingleStepPathingStrategy();
        List<Point> path = single.computePath(
                start, end,
                p -> withinBounds(p, grid) && grid[p.y][p.x],
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        // no accessible neighbor
        assertTrue(path.isEmpty());
    }


 /* A-Star Tests */
    @Test
    public void testAStarSimpleNoObstacles() {
        boolean[][] grid = {
                { true, true, true },
                { true, true, true },
                { true, true, true }
        };

        Point start = new Point(0, 0);
        Point end   = new Point(2, 2);

        PathingStrategy astar = new AStarPathingStrategy();
        List<Point> path = astar.computePath(
                start, end,
                p -> withinBounds(p, grid) && grid[p.y][p.x],
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        // should be 3 steps (e.g., down→down→right or right→right→down)
        assertTrue(isValidPath(path, 3, start, end));
    }


    @Test
    public void testAStarNoPathWhenBlocked() {
        boolean[][] grid = {
                { true, false, true },
                { false, false, false },
                { true, false, true }
        };

        Point start = new Point(0, 0);
        Point end   = new Point(2, 2);

        PathingStrategy astar = new AStarPathingStrategy();
        List<Point> path = astar.computePath(
                start, end,
                p -> withinBounds(p, grid) && grid[p.y][p.x],
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        assertTrue(path == null || path.isEmpty());
    }

    /*
     * Properties of a correct a-star path. You don't know how your PriorityQueue will behave
     * regarding equally-good nodes. So instead checking the exact path returned, you could
     * instead check that the path has the following properties.
     *
     * 1. path length
     * 2. path starts at the start point and ends at the goal
     * 3. path actually contains contiguous nodes
     */

    // property based testing
    private static boolean isValidPath(List<Point> path, int expectedLength, Point start, Point end) {
        if (path == null || path.size() != expectedLength) {
            return false;
        }
        // first step adjacent to start
        if (!start.adjacent(path.get(0))) {
            return false;
        }
        // last step adjacent to end
        if (!end.adjacent(path.get(path.size() - 1))) {
            return false;
        }
        // contiguous
        Point prev = null;
        for (Point p : path) {
            if (prev != null && !p.adjacent(prev)) {
                return false;
            }
            prev = p;
        }
        return true;
    }

    private static boolean withinBounds(Point p, boolean[][] grid) {
        return p.y >= 0 && p.y < grid.length &&
                p.x >= 0 && p.x < grid[0].length;
    }
}
