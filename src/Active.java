public interface Active {

    double getActionPeriod();
    default void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent((Entity) this, new Activity((Entity) this, world, imageStore), this.getActionPeriod());
    }

    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
}
