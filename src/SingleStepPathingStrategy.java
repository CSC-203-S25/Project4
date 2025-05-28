//Ross Poletti, Arjan Ellingson, Greta Gamble
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SingleStepPathingStrategy implements PathingStrategy {
// Defines a class that implements the single step pathing algorithm
    public List<Point> computePath(
            Point start,
            Point end,
            Predicate<Point> canPassThrough,
            BiPredicate<Point, Point> withinReach,
            Function<Point, Stream<Point>> potentialNeighbors
    ) {
        // Returns an empty list if next point is within reach
        if (withinReach.test(start, end)) {
            return new ArrayList<>();
        }

        // Checks horizontally if there's potential neighbors
        Optional<Point> horizontalNext = potentialNeighbors.apply(start)
                .filter(p -> Math.abs(end.x - p.x) < Math.abs(end.x - start.x))
                .filter(canPassThrough)
                .min((p1, p2) -> Math.abs(end.x - p1.x) - Math.abs(end.x - p2.x));

        // Return a list containing the point if it exists
        if (horizontalNext.isPresent()) {
            Point neighbor = horizontalNext.get();

            // Return list
            List<Point> path = new ArrayList<>();
            path.add(neighbor);

            // Recursively add more points
            path.addAll(computePath(neighbor, end, canPassThrough, withinReach, potentialNeighbors));

            // Return the path
            return path;
        }

        Optional<Point> verticalNext = potentialNeighbors.apply(start)
                .filter(p -> Math.abs(end.y - p.y) < Math.abs(end.y - start.y))
                .filter(canPassThrough)
                .min((p1, p2) -> Math.abs(end.y - p1.y) - Math.abs(end.y - p2.y));

        // Return the neighboring points in a list
        if (verticalNext.isPresent()) {
            Point neighbor = verticalNext.get();

            // list of neighbors it can move to next
            List<Point> path = new ArrayList<>();
            path.add(neighbor);

            path.addAll(computePath(neighbor, end, canPassThrough, withinReach, potentialNeighbors));

            // Return the path
            return path;
        }

        // No valid positions
        return new ArrayList<>(); // Assumed empty
    }
}