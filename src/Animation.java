public class Animation extends Action {

    public int repeatCount;

    public Animation(Entity entity, WorldModel world, ImageStore imageStore, int repeatCount) {
        super(entity, world, imageStore);
        this.repeatCount = repeatCount;
    }

    @Override
    public void executeAction(EventScheduler scheduler) {
        this.entity.nextImage();

        if (this.repeatCount != 1) {
            scheduler.scheduleEvent(this.entity, new Animation(this.entity, this.world, this.imageStore, Math.max(this.repeatCount - 1, 0)),
                    ((Animatable) this.entity).getAnimationPeriod());
        }
    }

}
