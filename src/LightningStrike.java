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
        return 0;
    }



    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

    }
}
