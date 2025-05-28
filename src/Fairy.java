import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;


public class Fairy extends Movable {

    // constants from proj 2 refactoring
    public static final String FAIRY_KEY = "fairy";
    public static final int FAIRY_ANIMATION_PERIOD = 0;
    public static final int FAIRY_ACTION_PERIOD = 1;
    public static final int FAIRY_NUM_PROPERTIES = 2;

    /**
     * @param id name
     * @param position current position as a Point
     * @param images image list
     * @param actionPeriod how oftenn action should be scheduled
     * @param animationPeriod how often animation should be scheduled
     */
    public Fairy(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }
    /**
     * Single-Step Pathing
     * Computes the very next step toward a destination using a simple one-move pathfinder.
     */
//    @Override
//    public Point nextPosition(WorldModel world, Point destPos) {
//        // Instantiate the pathing strategy that only returns one step
//        SingleStepPathingStrategy strategy = new SingleStepPathingStrategy();
//
//        // Define which points we can move through: must be in bounds and unoccupied
//        Predicate<Point> canPassThrough = p ->
//                world.withinBounds(p) && !world.isOccupied(p);
//
//        // Define when we’ve “reached” the target: when adjacent (shares an edge)
//        BiPredicate<Point, Point> withinReach = Point::adjacent;
//
//        // Define how to generate neighbors (N, E, S, W)
//        Function<Point, Stream<Point>> neighbors = PathingStrategy.CARDINAL_NEIGHBORS;
//
//        //Compute the one-step path, then take the first (and only) move if it exists
//        List<Point> path = strategy.computePath(
//                this.getPosition(),  // start
//                destPos,             // goal
//                canPassThrough,      // pass-through test
//                withinReach,         // reach test
//                neighbors            // neighbor generator
//        );
//
//        // If there’s a valid next step, return it; otherwise stay in place
//        return path.stream()
//                .findFirst()
//                .orElse(this.getPosition());
//    }


    /**
     A-Star Pathing
     **/
    @Override
    public Point nextPosition(WorldModel world, Point destPos) {
        // Instantiate A*
        PathingStrategy aStar = new AStarPathingStrategy();

        // Pass‐through test: in‐bounds AND (empty OR stump)
        Predicate<Point> canPassThrough = p ->
                world.withinBounds(p)
                        && (
                        !world.isOccupied(p)
                );

        // We’re “within reach” when adjacent
        BiPredicate<Point, Point> withinReach = Point::adjacent;

        // 4‐way neighbors (NESW)
        Function<Point, Stream<Point>> neighbors =
                PathingStrategy.CARDINAL_NEIGHBORS;

        // Compute the full A* path, but take only the very first step
        List<Point> path = aStar.computePath(
                getPosition(),
                destPos,
                canPassThrough,
                withinReach,
                neighbors
        );

        if (path.isEmpty()){
            return this.getPosition();
        }
        return path.getFirst();
    }


    /**
     * @param world world we are in
     * @param imageStore image library
     * @param scheduler event scheduler
     */
    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        // finds the nearest stump to go to
        Optional<Entity> fairyTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(Stump.class)));

        // if stump, and can go to stump, create sapling where stump was
        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (this.moveTo(world, fairyTarget.get(), scheduler)) {

                Sapling sapling = new Sapling(Sapling.SAPLING_KEY + "_" + fairyTarget.get().getId(), tgtPos,
                        imageStore.getImageList(Sapling.SAPLING_KEY), Sapling.SAPLING_ACTION_ANIMATION_PERIOD,
                        Sapling.SAPLING_ACTION_ANIMATION_PERIOD, 0, Sapling.SAPLING_HEALTH_LIMIT);

                world.addEntity(sapling);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }
        // if cant try again
        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
    }



    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            scheduler.unscheduleAllEvents(target);
            world.removeEntity(scheduler, target);
            return true;
        } else {
            // Compute the next step via SingleStepPathingStrategy:
            Point nextPos = this.nextPosition(world, target.getPosition());
            //System.out.println("Fairy moving from " + this.getPosition() + " to " + nextPos);


            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

}