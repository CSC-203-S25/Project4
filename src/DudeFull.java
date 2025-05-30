import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DudeFull extends Dude {
    /**
     * @param id name
     * @param position current position as a Point
     * @param images image list
     * @param actionPeriod how oftenn action should be scheduled
     * @param animationPeriod how often animation should be scheduled
     * @param resourceLimit
     */
    public DudeFull(String id, Point position, List<PImage> images, double actionPeriod,
                    double animationPeriod, int resourceLimit) {
        super(id, position, images, actionPeriod, animationPeriod, resourceLimit);
    }

    /**
     * @param world the world we are in
     * @param target the goal block to move to, one step at a time
     * @param scheduler event scheduler
     * @return
     */
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            return true;
        } else {
            return super.moveTo(world, target, scheduler);
        }
    }

    /**
     * @param world world we are in
     * @param imageStore image libary
     * @param scheduler event scheduler
     */
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        // Look for the nearest House to deposit resources

        Optional<Entity> fullTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(House.class)));

        // if can move to house, transform to dude not full
        if (fullTarget.isPresent() && this.moveTo(world, fullTarget.get(), scheduler)) {
            this.transform(world, scheduler, imageStore);
        } else {
            // Otherwise, schedule another attempt after our action period
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        }
    }

    /**
     * @param world world we are in
     * @param scheduler event scheduler
     * @param imageStore image libary
     * @return true when transformed
     */
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        // create new instance of dude not full and add it to world, remove dude full
        Movable dude = new DudeNotFull(this.getId(), this.getPosition(), this.getImages(),
                this.getActionPeriod(), this.getAnimationPeriod(), this.getResourceLimit(), 0);
        world.removeEntity(scheduler, this);

        world.addEntity(dude);
        dude.scheduleActions(scheduler, world, imageStore);

        return true;
    }

    @Override
    public boolean transformDude(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        // create new instance of dude not full and add it to world, remove dude full
        Movable fairy_hunter = new FairyHunter(this.getId(), this.getPosition(), this.getImages(),
                this.getActionPeriod(), this.getAnimationPeriod());
        world.removeEntity(scheduler, this);

        world.addEntity(fairy_hunter);
        fairy_hunter.scheduleActions(scheduler, world, imageStore);

        return true;
    }

}
