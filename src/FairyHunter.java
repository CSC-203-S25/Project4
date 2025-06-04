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
        Optional<Entity> fairyTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(Fairy.class)));

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