import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class HunterDestroyer extends Movable {

    public HunterDestroyer(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }


    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.getActionPeriod());

        scheduler.scheduleEvent(this,
                new Animation(this, world, imageStore, Integer.MAX_VALUE), // loop animation forever
                this.getAnimationPeriod());
    }

    @Override
    public Point nextPosition(WorldModel world, Point destPos) {
        PathingStrategy aStar = new AStarPathingStrategy();

        Predicate<Point> canPassThrough = p ->
                world.withinBounds(p)
                        && (
                        !world.isOccupied(p)
                                || world.getOccupant(p)
                                .filter(e -> e instanceof LightningStrike)
                                .isPresent()
                );

        BiPredicate<Point, Point> withinReach = Point::adjacent;

        Function<Point, Stream<Point>> neighbors = PathingStrategy.CARDINAL_NEIGHBORS;

        List<Point> path = aStar.computePath(
                getPosition(),
                destPos,
                canPassThrough,
                withinReach,
                neighbors
        );

        if (path.isEmpty()) {
            return this.getPosition();
        }
        return path.get(0);
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            scheduler.unscheduleAllEvents(target);
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(FairyHunter.class)));

        if (fairyTarget.isPresent()) {
            Point targetPos = fairyTarget.get().getPosition();

            if (this.moveTo(world, fairyTarget.get(), scheduler)) {

            }
        }

        // Schedule next activity (keep hunting!)
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.getActionPeriod());
    }

}

//import processing.core.PImage;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.function.BiPredicate;
//import java.util.function.Function;
//import java.util.function.Predicate;
//import java.util.stream.Stream;
//
//public class HunterDestroyer extends Movable{
//    public HunterDestroyer(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
//        super(id, position, images, actionPeriod, animationPeriod);
//    }
//
//    @Override
//    public Point nextPosition(WorldModel world, Point destPos) {
//        // Instantiate A*
//        PathingStrategy aStar = new AStarPathingStrategy();
//
//        // Pass‐through test: in‐bounds AND (empty OR stump)
//        Predicate<Point> canPassThrough = p ->
//                world.withinBounds(p)
//                        && (
//                        !world.isOccupied(p)
//                );
//
//        // We’re “within reach” when adjacent
//        BiPredicate<Point, Point> withinReach = Point::adjacent;
//
//        // 4‐way neighbors (NESW)
//        Function<Point, Stream<Point>> neighbors =
//                PathingStrategy.CARDINAL_NEIGHBORS;
//
//        // Compute the full A* path, but take only the very first step
//        List<Point> path = aStar.computePath(
//                getPosition(),
//                destPos,
//                canPassThrough,
//                withinReach,
//                neighbors
//        );
//
//        if (path.isEmpty()){
//            return this.getPosition();
//        }
//        return path.getFirst();
//    }
//
//    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
//        if (this.getPosition().adjacent(target.getPosition())) {
//            scheduler.unscheduleAllEvents(target);
//            world.removeEntity(scheduler, target);
//            return true;
//        } else {
//            // Compute the next step via SingleStepPathingStrategy:
//            Point nextPos = this.nextPosition(world, target.getPosition());
//            //System.out.println("Fairy moving from " + this.getPosition() + " to " + nextPos);
//
//
//            if (!this.getPosition().equals(nextPos)) {
//                world.moveEntity(scheduler, this, nextPos);
//            }
//            return false;
//        }
//    }
//
//    /*@Override
//    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
//        // Look for the nearest House to deposit resources
//
//        Optional<Entity> fullTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(FairyHunter.class)));
//
//        // if can move to house, transform to dude not full
//        if (fullTarget.isPresent() && this.moveTo(world, fullTarget.get(), scheduler)) {
//            // this.transform(world, scheduler, imageStore); replace this w something else
//        } else {
//            // Otherwise, schedule another attempt after our action period
//            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
//        }
//    }*/
//
//
//    @Override
//    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
//        // finds the nearest stump to go to
//        Optional<Entity> fairyTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(Tree.class)));
//
//        // if stump, and can go to stump, create sapling where stump was
//        if (fairyTarget.isPresent()) {
//            Point tgtPos = fairyTarget.get().getPosition();
//
//            if (this.moveTo(world, fairyTarget.get(), scheduler)) {
//                world.removeEntityAt(tgtPos);
//            }
//        }
//        // if cant try again
//        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
//    }
////    @Override
////    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
////
////        Optional<Entity> hunterTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(Tree.class)));
////        System.out.println(hunterTarget);
////
////        if (hunterTarget.isPresent()) {
////            Point tgtPos = hunterTarget.get().getPosition();
////            world.removeEntityAt(tgtPos);
////        }
////
////        // Add to the world
////        if (!world.isOccupied(this.getPosition())) {
////            world.addEntity(this);
////        }
////
////        // Animate through all frames exactly once
////        int totalFrames = this.getImages().size();
////        scheduler.scheduleEvent(this,
////                new Animation(this, world, imageStore, totalFrames),
////                this.getAnimationPeriod());
////
////        scheduler.scheduleEvent(this,
////                new Activity(this, world, imageStore),
////                this.getActionPeriod());
////
////
////    }
//}
