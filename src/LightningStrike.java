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
        return 100; // Flash every 100ms if animated
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        // Add to the world
        if (!world.isOccupied(this.getPosition())) {
            world.addEntity(this);
        }

        // Schedule flash animation (optional)
        scheduler.scheduleEvent(this,
                new Animation(this, world, imageStore, 0), // Animate one cycle
                this.getAnimationPeriod());

        // Schedule removal after short time
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.getActionPeriod());
    }
}
