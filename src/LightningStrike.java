import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class LightningStrike extends Movable {
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
        world.removeEntity(scheduler, this);
        scheduler.unscheduleAllEvents(this);
        }
    }


