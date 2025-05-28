import processing.core.PImage;

import java.util.List;

/**
 * There may be many entities that are both active and animated.
 * Instead of making each one implement both interfaces, which can get tedious,
 * we can use this common type that combines the two.
 * If we require entities that are animated but not active, or vice-versa,
 * the interfaces are available.
 */
public abstract class ActiveAnimatedEntity extends Entity implements Active, Animatable {

    private final double actionPeriod;
    private final double animationPeriod;

    public ActiveAnimatedEntity(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
       super(id, position, images);
       this.actionPeriod = actionPeriod;
       this.animationPeriod = animationPeriod;
    }

    @Override
    public double getActionPeriod() {
        return this.actionPeriod;
    }

    @Override
    public double getAnimationPeriod() {
        return this.animationPeriod;
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        Animatable.super.scheduleActions(scheduler, world, imageStore);
        Active.super.scheduleActions(scheduler, world, imageStore);
    }
}
