import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HunterDestroyer extends Movable{
    public HunterDestroyer(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    @Override
    public boolean moveTo(WorldModel model, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            return true;
        } else {
            return super.moveTo(model, target, scheduler);
        }    }

    /*@Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        // Look for the nearest House to deposit resources

        Optional<Entity> fullTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(FairyHunter.class)));

        // if can move to house, transform to dude not full
        if (fullTarget.isPresent() && this.moveTo(world, fullTarget.get(), scheduler)) {
            // this.transform(world, scheduler, imageStore); replace this w something else
        } else {
            // Otherwise, schedule another attempt after our action period
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        }
    }*/
    @Override
    public double getAnimationPeriod() {
        return .1; // Flash every 100ms if animated
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        // Add to the world
        if (!world.isOccupied(this.getPosition())) {
            world.addEntity(this);
        }

        // Animate through all frames exactly once
        int totalFrames = this.getImages().size();
        scheduler.scheduleEvent(this,
                new Animation(this, world, imageStore, totalFrames),
                this.getAnimationPeriod());

        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.getActionPeriod());


    }}
