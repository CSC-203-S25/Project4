import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DudeNotFull extends Dude {

    private int resourceCount;

    /**
     * @param id name
     * @param position current position as a Point
     * @param images image list
     * @param actionPeriod how oftenn action should be scheduled
     * @param animationPeriod how often animation should be scheduled
     * @param resourceLimit
     */
    public DudeNotFull(String id, Point position, List<PImage> images, double actionPeriod,
                       double animationPeriod, int resourceLimit, int resourceCount) {
        super(id, position, images, actionPeriod, animationPeriod, resourceLimit);
        this.resourceCount = resourceCount;
    }

    /**
     *
     * @param world the world we are in
     * @param imageStore img library
     * @param scheduler event scheduler
     */
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(this.getPosition(), new ArrayList<>(Arrays.asList(Tree.class, Sapling.class)));

        if (target.isEmpty() || !this.moveTo(world, target.get(), scheduler) || !this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        }
    }

    /**
     * @param world the world we are in
     * @param target the goal block to move to, one step at a time
     * @param scheduler event scheduler
     * @return
     */
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        // if next to target (probably a plant) increase our resource count and decrease the plants health
        if (this.getPosition().adjacent(target.getPosition())) {
            this.resourceCount += 1;
            ((Plant) target).decreaseHealth();
            return true;
        } else {
            // fallback to default (inherited class)
            return super.moveTo(world, target, scheduler);
        }
    }

    /**
     * @param world world we are in
     * @param scheduler event scheduler
     * @param imageStore image libary
     * @return true when transformed
     */
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        // create dude full and transform self into that instance
        if (this.resourceCount >= this.getResourceLimit()) {
            Movable dude = new DudeFull(this.getId(), this.getPosition(), this.getImages(), this.getActionPeriod(),
                    this.getAnimationPeriod(), this.getResourceLimit());

            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dude);
            dude.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public boolean transformToHunter(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        // create new instance of fairy hunter and add it to world, remove dude full
        Movable fairy_hunter = new FairyHunter("hunter_" + System.currentTimeMillis(),
                this.getPosition(),
                imageStore.getImageList("run"), // hammer image
                0.5,  // actionPeriod
                0.5 );
        world.removeEntity(scheduler, this);

        world.addEntity(fairy_hunter);
        fairy_hunter.scheduleActions(scheduler, world, imageStore);

        return true;
    }


}
