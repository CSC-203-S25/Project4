//Ross Poletti, Arjan Ellingson, Greta Gamble
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * The A* algorithm includes additional information about the goal vertex the
 * algorithm is trying to reach, and it forms a heuristic function to find its
 * path. A* will reach the goal faster than the Dijkstra’s algorithm, however
 * sometimes it may not be the shortest path. That depends on the heuristic
 * function.
 * Code based on: https://en.wikipedia.org/wiki/A*_search_algorithm#Pseudocode 
 */
public class AStarPathingStrategy implements PathingStrategy {

    //Initializes new lists to keep track of the open set (points that need to be evaluated)
    private List<Point> openSet = new ArrayList<>();
    //Initializes a list of neighbors of the current point that will need to be evaluated as valid or not
    private List<Point> neighborlist = new ArrayList<>();
    //Target and begin variables initialized (start and where it's trying to get to)
    private Point target;
    private Point begin;
    //Hashmaps to keep track of where it came from and the g score (real cost from the start to the target) which is important to the A* algorithm
    private Map<Point, Point> cameFrom = new HashMap<>();
    private Map<Point, Integer> gscore = new HashMap<>();


    //Heuristic created, this is how we are implementing the Heuristic part of the algorithm
    //Estimated the distance between two points in the grid that is the virtual world
    //Helps with finding the shortest path quicker
    private int hScore(Point pt) {
        int dx = Math.abs(PathingStrategy.getX(pt) - PathingStrategy.getX(target));
        int dy = Math.abs(PathingStrategy.getY(pt) - PathingStrategy.getY(target));
        return dx + dy;
    }

    //The f score is the estimated cost for the dude or fairy to reach its target point
    //The g score is the real cost from the start to the target
    //The h score is the Heuristic estimation of how much it will take to get from the start to the target
    //f = g + h
    private int fScore(Point pt) {
        return gscore.getOrDefault(pt, Integer.MAX_VALUE) + hScore(pt);
    }
    //This searched through a list of points of where it could go
    private Point findSmallestPoint() {
        Point best = openSet.get(0);
        //Searches each point in the list to find the best
        for (Point p : openSet) {
            if (fScore(p) < fScore(best)) {
                best = p;
            }
        }
        //Returns the point with the lowest f-score finding the best path
        return best;
    }

    //Backtracks through cameFrom to reconstruct the path that the entity is going to take
    private List<Point> pathReconstruction(Point pt) {
        List<Point> path = new ArrayList<>();
        while (!pt.equals(begin)) {
            path.add(pt);
            pt = cameFrom.get(pt);
        }
        //Then it reverses the path to give the correct path that the Entity will take to get to its target
        Collections.reverse(path);
        return path;
    }

    //Overrides this from PathingStrategy to compute the best path for the entity to take
    //Adapts the one from the interface to allow its use in A*
    @Override
    public List<Point> computePath(
            Point start,
            Point end,
            Predicate<Point> canPassThrough,
            BiPredicate<Point, Point> withinReach,
            Function<Point, Stream<Point>> potentialNeighbors
    ) {

        //Resets all the variables and lists to give a clean slate
        this.begin = start;
        this.target = end;
        openSet.clear();
        neighborlist.clear();
        cameFrom.clear();
        gscore.clear();

        //Initializes the gscore and openSet (points that need to be evaluated)
        gscore.put(start, 0);
        openSet.add(start);

        //Main loop to use the A* algorithm
        //While open set has points in it, it loops
        while (!openSet.isEmpty()) {
            //Initializes the smallest point to be hte current one, and will update through the loop
            Point current = findSmallestPoint();
            //Removes from open set so it doesn't get evaluated again
            openSet.remove(current);

            //Checks if current is close enough to the target
            if (withinReach.test(current, end)) {
                return pathReconstruction(current);
            }

            //Otherwise it puts the current point in the list of neighbors that have been evaluated
            neighborlist.add(current); //Don't want to revisit this

            //Expand neighbors by checking for the current point's neighbors
            potentialNeighbors.apply(current)
                    //Uses a filter to see if the neighboring point can be passed through by the entity
                    .filter(canPassThrough)
                    //For each VALID neighbor, it does the following
                    .forEach(neighbor -> {
                        //Only does the following if the point neighbors with current is NOT already in the neighbor list
                        if (!neighborlist.contains(neighbor)) {
                            //The tentative is created with 1+ the current g-score because it's taking another step toward the target
                            //(distance from start point to neighbor through the current point)
                            int tentativeG = gscore.get(current) + 1;
                            //If this g-score is better (less) than any previously recorded paths
                            if (tentativeG < gscore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                                //Update the path from current to go to the neighbor
                                cameFrom.put(neighbor, current);
                                //Record the new g-score for this neighboring point
                                gscore.put(neighbor, tentativeG);
                                //If neighbor isn't in the open set that needs to be evaluated, then add the neighbor to it
                                if (!openSet.contains(neighbor)) {
                                    openSet.add(neighbor);
                                }
                            }
                        }
                    });
        }

        //If no path is found by the searching algorithm, an empty list is returned
        return Collections.emptyList();
    }
}
