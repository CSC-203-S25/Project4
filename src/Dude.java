import processing.core.PImage;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class Dude extends Movable {
    // constants moved during project 2
    public static final String DUDE_KEY = "dude";
    public static final int DUDE_ACTION_PERIOD = 0;
    public static final int DUDE_ANIMATION_PERIOD = 1;
    public static final int DUDE_LIMIT = 2;
    public static final int DUDE_NUM_PROPERTIES = 3;

    private final int resourceLimit;

    /**
     * @param id name
     * @param position current position as a Point
     * @param images image list
     * @param actionPeriod how oftenn action should be scheduled
     * @param animationPeriod how often animation should be scheduled
     * @param resourceLimit
     */
    public Dude(String id, Point position, List<PImage> images,
                double actionPeriod, double animationPeriod,
                int resourceLimit)
    {
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
    }

    public int getResourceLimit() {
        return resourceLimit;
    }

    // checks if move is invalid
    // criteria: invalid if occupied block and that block is not a stump
    @Override
    public boolean isInvalidMove(WorldModel world, Point dest) {
        return world.isOccupied(dest)
                && !(world.getOccupancyCell(dest) instanceof Stump);
    }

     /**
     Single-Step Pathing - invokes provided single step pathing class
     **/

//    @Override
//    public Point nextPosition(WorldModel world, Point destPos) {
//    // determines the next possible point (or list of possible moves)
//        Predicate<Point> canPassThrough = p ->
//                world.withinBounds(p) && !isInvalidMove(world, p);
//    // determines when or if we are at a point next to (adjacent) our target
//        BiPredicate<Point, Point> withinReach = Point::adjacent;
//    // north east south west points from current neighbors
//        Function<Point, Stream<Point>> neighbors = PathingStrategy.CARDINAL_NEIGHBORS;
//    // create single step pathing strategy using the above calculations,and calculate the best next position
//    // no valid path -> getPosition()
//        return new SingleStepPathingStrategy()
//                .computePath(getPosition(), destPos,
//                        canPassThrough, withinReach, neighbors)
//                .stream()
//                .findFirst()
//                .orElse(getPosition());
//    }


    /**
     * Compute the next step towards a target using A* pathfinding.
     *
     * @param world   the world grid in which we’re moving
     * @param destPos the target position we want to approach
     * @return the adjacent point to move into next, or current position if no path exists
     * @implNote uses default Manhattan‐distance heuristic and uniform cost (1 per move)
     */
    // A* PATHING
    @Override
    public Point nextPosition(WorldModel world, Point destPos) {
        // 1) Instantiate A* with default settings:
        //    – Heuristic: Manhattan distance
        //    – Cost per move: 1
        PathingStrategy aStar = new AStarPathingStrategy();

        // 2) canPassThrough: allow movement into any in-bounds cell that is either empty or a Stump
        Predicate<Point> canPassThrough = p ->
                world.withinBounds(p)
                        && (
                        !world.isOccupied(p)
                                || world.getOccupant(p)
                                .filter(e -> e instanceof Stump)
                                .isPresent()
                );

        // 3) withinReach: consider “arrived” when our position is adjacent (shares an edge) with destPos
        BiPredicate<Point, Point> withinReach = Point::adjacent;

        // 4) neighbors: explore only the 4 cardinal directions (N, E, S, W)
        Function<Point, Stream<Point>> neighbors =
                PathingStrategy.CARDINAL_NEIGHBORS;

        // 5) Compute the full A* path, then take only the very first step
        List<Point> path = aStar.computePath(
                getPosition(),
                destPos,
                canPassThrough,
                withinReach,
                neighbors
        );
        // no path found, stay
        if (path.isEmpty()){
            return this.getPosition();
        }
        // else, return given path next pos
        return path.getFirst();
    }



    // general move to func

    /**
     *
     * @param world the world we are in
     * @param target the goal block to move to, one step at a time
     * @param scheduler event scheduler
     * @return
     */
    @Override
    public boolean moveTo(WorldModel world,
                          Entity target,
                          EventScheduler scheduler)
    {
        // if the position is adjacent to the target, stop further actions and move
        if (getPosition().adjacent(target.getPosition())) {
            scheduler.unscheduleAllEvents(target);
            world.removeEntity(scheduler, target);
            return true;
        } else {
            // move the entity to the next position by calculating best post heuristically
            Point next = nextPosition(world, target.getPosition());

            //System.out.println("Dude moving from " + this.getPosition() + " to " + next);

            // may be at target
            if (!getPosition().equals(next)) {
                world.moveEntity(scheduler, this, next);
            }
            return false;
        }
    }

    /**
     * When moveTo() returns true, your subclass’s executeActivity()
     * should call this to swap NotFull↔Full (or whatever your logic is).
     */
    public abstract boolean transform(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore);
}
