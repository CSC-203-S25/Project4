public class Activity extends Action {
    public Activity(Entity entity, WorldModel model, ImageStore imageStore) {
        super(entity, model, imageStore);
    }

    @Override
    public void executeAction(EventScheduler scheduler) {
        ((Active) this.entity).executeActivity(this.world, this.imageStore, scheduler);
    }
}
