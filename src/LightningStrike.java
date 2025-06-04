import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class LightningStrike extends Movable {
    private int radius;

    // constructor, uses super to implement movable inheritance
    public LightningStrike(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int radius) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.radius = radius;
    }

    /**
     * @return radius of this strike
     * for the sake of this project, it us unused due to alterations of lightning strike to a line (the line will always affect 7 tiles)
     */
    public int getRadius() {
        return radius;
    }

    @Override
    public double getAnimationPeriod() {
        return .1; // Flash every 100ms if animated, a complete cycle is 1 sec
    }

    /**
     *
     * @param world world class
     * @param imageStore image library
     * @param scheduler event scheduler
     */
    //Executes activity for the lightning strike, makes sure it's removed from the event scheduler and world after execution
    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        world.removeEntity(scheduler, this);
        scheduler.unscheduleAllEvents(this);
    }
}


