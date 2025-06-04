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

    //Schedules the actions for the hunter destroyer, so that it will actually do somethinf
    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.getActionPeriod());

        scheduler.scheduleEvent(this,
                new Animation(this, world, imageStore, Integer.MAX_VALUE), // loop animation forever
                this.getAnimationPeriod());
    }

    //Uses A* pathing to find the next position which will be heading towards a fairy hunter
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


    //Gets the hunter destroyer moving to its next target and uses the scheduler to make sure it's appearing correctly
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

    //Gets the hunter destroyer moving toward the nearest fairy hunter
    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyHunterTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(FairyHunter.class)));
        //Finds the nearest fairy hunter to try and attack
        if (fairyHunterTarget.isPresent()) {
            Point targetPos = fairyHunterTarget.get().getPosition();
            //Moves to the point where the fairy hunter is present
            if (this.moveTo(world, fairyHunterTarget.get(), scheduler)) {

            }
        }

        // Schedule next activity (keeps hunting the fairy hunters)
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.getActionPeriod());
    }

}