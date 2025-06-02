import processing.core.PImage;

import java.util.List;

public class LightningStrike extends Movable implements Animatable {
    private int radius;

    public LightningStrike(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int radius) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

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

        // Remove lightning after short action period
//        scheduler.scheduleEvent(this,
//                new Activity(this, world, imageStore),
//                this.getActionPeriod());
    }
}
