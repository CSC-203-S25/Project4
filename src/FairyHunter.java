import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FairyHunter extends Movable{
    public FairyHunter(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }


    //Overrides the scheduleActions function to work specifically for the fairy hunter entity
    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.getActionPeriod());

        scheduler.scheduleEvent(this,
                new Animation(this, world, imageStore, Integer.MAX_VALUE), // loop animation forever
                this.getAnimationPeriod());
    }

    //Uses A* pathing to find the next point that the entity will move to
    @Override
    public Point nextPosition(WorldModel world, Point destPos) {
        PathingStrategy aStar = new AStarPathingStrategy();
        
        //This ensures that it doesn't get stuck on any lightning strike entities
        //Uses a filter to find these points where it could potentially get stuck
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

        //Computes A* path for the entity
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

    //Gets the entity moving towards its target (the fairies) and uses the scheduler to make sure it's appearing correctly
    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            scheduler.unscheduleAllEvents(target);
            world.removeEntity(scheduler, target);
            return true;
        } else {
            //Keeps moving with the target's next position to get closer and closer
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    //This executeActivity override gets the nearest fairy for the fairy hunter to target
    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(Fairy.class)));
        //If the nearest fairy is present, it gets its position and sets that to be the target
        if (fairyTarget.isPresent()) {
            Point targetPos = fairyTarget.get().getPosition();
            //Starts moving toward the fairy it's trying to target
            if (this.moveTo(world, fairyTarget.get(), scheduler)) {

            }
        }

        // Schedule next activity (keeps hunting for the fairies)
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.getActionPeriod());
    }

}