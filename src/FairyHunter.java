import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FairyHunter extends Movable{
    public FairyHunter(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    @Override
    public boolean moveTo(WorldModel model, Entity target, EventScheduler scheduler) {
        //Change this moveTo to move to the nearest Fairy
        if (this.getPosition().adjacent(target.getPosition())) {
            return true;
        } else {
            return super.moveTo(model, target, scheduler);
        }    }

//    @Override
//    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
//        // Look for the nearest House to deposit resources
//
//        Optional<Entity> fairyTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(Fairy.class)));
//
//        // if can move to house, transform to dude not full
//        if (fairyTarget.isPresent() && this.moveTo(world, fairyTarget.get(), scheduler)) {
//            // this.transform(world, scheduler, imageStore); replace this w something else
//        } else {
//            // Otherwise, schedule another attempt after our action period
//            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
//        }
//    }

    //TODO Change this to deal with getting rid of the fairies in the path
    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        // finds the nearest stump to go to
        Optional<Entity> fairyTarget = world.findNearest(this.getPosition(), new ArrayList<>(List.of(Stump.class)));

        // if stump, and can go to stump, create sapling where stump was
        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (this.moveTo(world, fairyTarget.get(), scheduler)) {

                Sapling sapling = new Sapling(Sapling.SAPLING_KEY + "_" + fairyTarget.get().getId(), tgtPos,
                        imageStore.getImageList(Sapling.SAPLING_KEY), Sapling.SAPLING_ACTION_ANIMATION_PERIOD,
                        Sapling.SAPLING_ACTION_ANIMATION_PERIOD, 0, Sapling.SAPLING_HEALTH_LIMIT);

                world.addEntity(sapling);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }
        // if cant try again
        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
    }
}
