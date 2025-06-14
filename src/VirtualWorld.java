import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Random;


import org.junit.jupiter.api.BeforeAll;
import processing.core.*;


//Greta Gamble, Arjan Ellingson, Ross Poletti
//We made our lightning strike a line instead of a circle around the point clicked as we thought it made more sense visually
//The lightning effects 7 tiles and at each, if there's a dude (full or not full) present, it will be transformed into a FairyHunter
//Decided to do many of the actions in this file because they trigger on mousePressed(), rather than having duplicate code
public final class VirtualWorld extends PApplet {
    private static String[] ARGS;

    public static final int VIEW_WIDTH = 640;
    public static final int VIEW_HEIGHT = 480;
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;

    public static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    public static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;

    public static final String IMAGE_LIST_FILE_NAME = "imagelist";
    public static final String DEFAULT_IMAGE_NAME = "background_default";
    public static final int DEFAULT_IMAGE_COLOR = 0x808080;

    public static final String FAST_FLAG = "-fast";
    public static final String FASTER_FLAG = "-faster";
    public static final String FASTEST_FLAG = "-fastest";
    public static final double FAST_SCALE = 0.5;
    public static final double FASTER_SCALE = 0.25;
    public static final double FASTEST_SCALE = 0.10;

    public String loadFile = "world.sav";
    public long startTimeMillis = 0;
    public double timeScale = 1.0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        PathingStrategy.publicizePoint();
        parseCommandLine(ARGS);
        loadImages(IMAGE_LIST_FILE_NAME);
        loadWorld(loadFile, this.imageStore);

        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler();
        this.startTimeMillis = System.currentTimeMillis();
        this.scheduleActions(world, scheduler, imageStore);
    }

    public void draw() {
        double appTime = (System.currentTimeMillis() - startTimeMillis) * 0.001;
        double frameTime = (appTime - scheduler.getCurrentTime()) / timeScale;
        this.update(frameTime);
        view.drawViewport();
    }

    public void update(double frameTime) {
        scheduler.updateOnTime(frameTime);
    }

    // Just for debugging and for P5
    // Be sure to refactor this method as appropriate

    // Arjan, Ross, Greta

    //We added our lightning event to this function, instead of having duplicated code where another method is triggered when a point is clicked
    public void mousePressed() {
        Point pressed = mouseToPoint();

        System.out.println("CLICK! " + pressed.x + ", " + pressed.y);

        Optional<Entity> entityOptional = world.getOccupant(pressed);
        if (entityOptional.isPresent()) {
            Entity entity = entityOptional.get();
            System.out.println(entity.getId() + ": " + entity.getClass());
        }

        // calculate all neighbours or adjacent tiles to the point pressed
        List<Point> neighbors = new ArrayList<>();
        int count = 0;
        Point light = new Point(pressed.x, pressed.y);

        // Start direction: down-right
        int downx = 1;
        int downy = 1;

        //while less than 7 tiles are affected, it adds more points to the list of neighbors that will be affected by the strike
        while (neighbors.size() < 7) {
            int newX = light.x + count * downx;
            int newY = light.y + count * downy;

            //Checks bounds for the corner points to make sure 7 tiles are always affected
            if (newX < 0 || newX > 39) {
                // Out of x-bounds — reverse x direction
                downx = -downx;
                newX = light.x + count * downx;
            }
            if (newY < 0 || newY > 29) {
                // Out of y-bounds — reverse y direction
                downy = -downy;
                newY = light.y + count * downy;
            }

            Point attemptedPoint = new Point(newX, newY);

            // Check again: only add if still in bounds
            if (newX >= 0 && newX <= 39 && newY >= 0 && newY <= 29) {
                neighbors.add(attemptedPoint);
            }

            count++;
        }

        // affect 7 tiles
        for (Point neighbor : neighbors) {
            // Lightning Strike on click
            LightningStrike lightning = new LightningStrike(
                    "lightning_" + System.currentTimeMillis(),
                    neighbor //Used to be pressed
                    , imageStore.getImageList("lightning"),
                    100, // action period (lifetime) of lightning
                    100, // animation period (time between frames)
                    7// radius not important here
            );

            if (world.getOccupancyCell(neighbor) instanceof DudeFull || world.getOccupancyCell(neighbor) instanceof DudeNotFull) {
                ((Dude) world.getOccupancyCell(neighbor)).transformToHunter(this.world, this.scheduler, this.imageStore);
            }
            world.addEntity(lightning);

            // schedule lightning events and actions
            scheduler.scheduleEvent(lightning,
                    new Animation(lightning, world, imageStore, lightning.getImages().size()), // flash through all frames once
                    lightning.getAnimationPeriod());

            scheduler.scheduleEvent(lightning,
                    new Activity(lightning, world, imageStore),
                    lightning.getActionPeriod());

            // set bg of affected tile to be burnt
            Background b = new Background("burnt_earth_tile.png", imageStore.getImageList("burnt"));
            world.setBackgroundCell(neighbor, b);


        } // end for


        // Now spawn HunterDestroyer
        Random random = new Random();
        int coordx = random.nextInt(world.getNumCols());
        int coordy = random.nextInt(world.getNumRows());
        Point destroyerPosition = new Point(coordx, coordy);

        // Only spawn if the spot is empty
        if (!world.isOccupied(destroyerPosition)) {
            HunterDestroyer destroyer = new HunterDestroyer(
                    "destroy_" + System.currentTimeMillis(),
                    destroyerPosition,
                    imageStore.getImageList("hammer"), // hammer image
                    0.3,  // actionPeriod
                    0.3   // animationPeriod (must be non-zero)
            );

            world.addEntity(destroyer);
            destroyer.scheduleActions(scheduler, world, imageStore);
        }


    }




        public void scheduleActions (WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Animatable) {
                    ((Animatable) entity).scheduleActions(scheduler, world, imageStore);
                } else if (entity instanceof Active) {
                    ((Active) entity).scheduleActions(scheduler, world, imageStore);
                }
            }
        }


    private Point mouseToPoint() {
        return view.getViewport().viewportToWorld(mouseX / TILE_WIDTH, mouseY / TILE_HEIGHT);
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP -> dy -= 1;
                case DOWN -> dy += 1;
                case LEFT -> dx -= 1;
                case RIGHT -> dx += 1;
            }
            view.shiftView(dx, dy);
        }
    }

    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME, imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        Arrays.fill(img.pixels, color);
        img.updatePixels();
        return img;
    }

    public void loadImages(String filename) {
        this.imageStore = new ImageStore(createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        try {
            Scanner in = new Scanner(new File(filename));
            WorldModel.loadImages(in, imageStore,this);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void loadWorld(String file, ImageStore imageStore) {
        this.world = new WorldModel();
        try {
            Scanner in = new Scanner(new File(file));
            world.load(in, imageStore, createDefaultBackground(imageStore));
        } catch (FileNotFoundException e) {
            Scanner in = new Scanner(file);
            world.load(in, imageStore, createDefaultBackground(imageStore));
        }
    }

    public void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG -> timeScale = Math.min(FAST_SCALE, timeScale);
                case FASTER_FLAG -> timeScale = Math.min(FASTER_SCALE, timeScale);
                case FASTEST_FLAG -> timeScale = Math.min(FASTEST_SCALE, timeScale);
                default -> loadFile = arg;
            }
        }
    }

    public static void main(String[] args) {
        VirtualWorld.ARGS = args;
        PApplet.main(VirtualWorld.class);
    }

    public static List<String> headlessMain(String[] args, double lifetime){
        VirtualWorld.ARGS = args;

        VirtualWorld virtualWorld = new VirtualWorld();
        virtualWorld.setup();
        virtualWorld.update(lifetime);

        return virtualWorld.world.log();
    }
}

