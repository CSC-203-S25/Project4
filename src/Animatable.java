public interface Animatable {
    double getAnimationPeriod();

    default void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent((Entity) this, new Animation((Entity) this, world, imageStore, 0), getAnimationPeriod());
    }
}
