import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SingleStepPathingStrategyTest {
    private final SingleStepPathingStrategy strategy = new SingleStepPathingStrategy();

    // 4-way cardinal neighbors
    private final Function<Point, Stream<Point>> neighbors = p -> Stream.of(
            new Point(p.x + 1, p.y),
            new Point(p.x - 1, p.y),
            new Point(p.x, p.y + 1),
            new Point(p.x, p.y - 1)
    );

    // “Within reach” when Manhattan distance ≤ 1
    private final BiPredicate<Point, Point> adjacent = (p1, p2) ->
            Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y) <= 1;

    // Default: nothing blocks movement
    private final Predicate<Point> alwaysPass = p -> true;

    @Test
    public void testAlreadyWithinReach() {
        Point start = new Point(0, 0);
        Point end   = new Point(0, 1);
        List<Point> path = strategy.computePath(
                start, end,
                alwaysPass, adjacent, neighbors
        );
        assertTrue(path.isEmpty(), "If start is within reach of end, path must be empty");
    }

    @Test
    public void testPureHorizontalAdvance() {
        Point start = new Point(0, 0);
        Point end   = new Point(3, 0);
        List<Point> path = strategy.computePath(
                start, end,
                alwaysPass, adjacent, neighbors
        );

        List<Point> expected = Arrays.asList(
                new Point(1, 0),
                new Point(2, 0)
        );
        assertEquals(expected, path, "Should step east until one away from end");
    }

    @Test
    public void testPureVerticalAdvance() {
        Point start = new Point(0, 0);
        Point end   = new Point(0, 3);
        List<Point> path = strategy.computePath(
                start, end,
                alwaysPass, adjacent, neighbors
        );

        List<Point> expected = Arrays.asList(
                new Point(0, 1),
                new Point(0, 2)
        );
        assertEquals(expected, path, "Should step north until one away from end");
    }

    @Test
    public void testObstacleForcesVerticalThenResumeHorizontal() {
        Point start = new Point(0, 0);
        Point end   = new Point(2, 2);

        // block the immediate horizontal move (1,0) but allow everything else
        Predicate<Point> canPassThrough = p ->
                !(p.x == 1 && p.y == 0);

        List<Point> path = strategy.computePath(
                start, end,
                canPassThrough, adjacent, neighbors
        );

        // Expect: first go up to (0,1), then (1,1), then (2,1)
        List<Point> expected = Arrays.asList(
                new Point(0, 1),
                new Point(1, 1),
                new Point(2, 1)
        );
        assertEquals(expected, path, "When (1,0) is blocked, should go vertical then resume horizontal");
    }

    @Test
    public void testNoValidMovesReturnsEmpty() {
        Point start = new Point(0, 0);
        Point end   = new Point(5, 5);

        // nothing is passable
        Predicate<Point> nowherePass = p -> false;

        List<Point> path = strategy.computePath(
                start, end,
                nowherePass, adjacent, neighbors
        );
        assertTrue(path.isEmpty(), "If no neighbors can pass, path must be empty");
    }
}
