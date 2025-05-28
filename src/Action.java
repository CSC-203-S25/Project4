/**
 * An action that can be taken by an entity
 */
public abstract class Action {
    protected final Entity entity;
    protected final WorldModel world;
    protected final ImageStore imageStore;

    public Action(Entity entity, WorldModel world, ImageStore imageStore) {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }

    public abstract void executeAction(EventScheduler scheduler);
}
