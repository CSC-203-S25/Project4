For this assignment you must modify the pathing behavior of all entities that move within the world. In case your code for project 2 is completely broken, you can use this starter code Download this starter code.

Objectives
To modify the code to use the specified PathingStrategy interface (that in turn uses streams to build a list of neighbors)
Further to integrate the use of this pathing strategy and understand the associated code example which uses filter and collect
Implement A star pathing algorithm in the existing code by implementing a new PathingStrategy subclass building off prior exercises.
Overview
This assignment deviates from the pattern of previous assignments. Though this assignment does introduce/leverage some design strategies, the primary goal is to improve the functionality of some entities in the virtual world.

In particular, as you are likely very aware of by now, the Dudes’ and Fairy’s movements are very simplistic. You have likely seen an entity get stuck on an obstacle or on another entity. You will improve the pathing strategy as part of this assignment.

Pathing algorithms are quite interesting, in and of themselves, but our exploration of pathing in this assignment also motivates the use of some design patterns and techniques. Applying these patterns will also improve the flexibility of the implementation.

Supporting Variety — Strategy Pattern
When an entity attempts to move, it needs to know the next step to take. How that next step is computed is, in many respects, irrelevant to the code within the corresponding entity. In fact, we may want to change that strategy for different builds of the program (to experiment), each time the program is executed (based on configuration), or dynamically during execution. The Strategy pattern allows you to encapsulate each pathing algorithm and switch between them as desired.

Your implementation must use the given PathingStrategy interface (discussed below).

interface PathingStrategy
{
   /*
    * Returns a prefix of a path from the start point to a point within reach
    * of the end point.  This path is only valid ("clear") when returned, but
    * may be invalidated by movement of other entities.
    *
    * The prefix includes neither the start point nor the end point.
    */
   List<Point> computePath(Point start, Point end,
      Predicate<Point> canPassThrough,
      BiPredicate<Point, Point> withinReach,
      Function<Point, Stream<Point>> potentialNeighbors);

   static final Function<Point, Stream<Point>> CARDINAL_NEIGHBORS =
      point ->
         Stream.<Point>builder()
            .add(new Point(point.x, point.y - 1))
            .add(new Point(point.x, point.y + 1))
            .add(new Point(point.x - 1, point.y))
            .add(new Point(point.x + 1, point.y))
            .build();
}
This strategy declares only a single method, computePath, to compute a path of points (returned as a list) from the start point to the end point (this is only expected to be a prefix, excluding the start and end points, of a real path; it need not represent a full path).

In order to compute this path, the pathing algorithm needs to know the directions in which travel might be able to proceed (determined by potentialNeighbors). In addition, in order to explore potential paths, the pathing algorithm must be able to determine if a given point can be traversed (i.e., is both a valid position in the world and a location to which the traveler can move; determined by canPassThrough). Finally, it is unlikely that the pathing algorithm should actually attempt to move to the end point (it is quite likely occupied, of course). Instead, the pathing algorithm will determine that a path is complete when a point is reached that is withinReach of the end point.

Single-Step Pathing
As an example of defining a pathing strategy, consider the following implementation of the single-step pathing algorithm (SingleStepPathingStrategy) used to this point by the pathing entities (this specific implementation leverages the stream library).

Modify the appropriate entities to use a PathingStrategy (referencing the interface, of course). Use the given implementation to verify that your changes work.

class SingleStepPathingStrategy
   implements PathingStrategy
{
   public List<Point> computePath(Point start, Point end,
      Predicate<Point> canPassThrough,
      BiPredicate<Point, Point> withinReach,
      Function<Point, Stream<Point>> potentialNeighbors)
   {
      /* Does not check withinReach.  Since only a single step is taken
       * on each call, the caller will need to check if the destination
       * has been reached.
       */
      return potentialNeighbors.apply(start)
         .filter(canPassThrough)
         .filter(pt ->
            !pt.equals(start)
            && !pt.equals(end)
            && Math.abs(end.x - pt.x) <= Math.abs(end.x - start.x)
            && Math.abs(end.y - pt.y) <= Math.abs(end.y - start.y))
         .limit(1)
         .collect(Collectors.toList());
   }
}
Of course, this implementation only matches the original pathing algorithm if potentialNeighbors returns the same neighbor points (in the same order) as before. Experiment with adding other points to the Stream returned by potentialNeighbors; perhaps allow the addition of diagonal movement, only allow diagonal movement, or remove the option to move straight up or down and replace them with the corresponding diagonals. Each of these approaches can be tried simply by changing the function passed to computePath.

A* Pathing
Define a new PathingStrategy subclass called AStarPathingStrategy that implements the A* search algorithmLinks to an external site.. As before, an entity will take only one step along the computed path so the computePath method will be invoked multiple times to allow movement to the intended destination (see below for alternatives). As such, take care in how you maintain state relevant to the algorithm.

Testing
You are strongly encouraged to write unit tests for this strategy. Since your implementation must conform to a specified interface, part of the grading will be based on instructor unit tests.

Once you’re finished with Lab 6, it provides a great testing program for your A* search. It’s a great way to visualize the algorithm running.

Alternate Traversal Approaches
After completing the above, you might notice an indecisive Dude or Fairy ping-ponging between two points. This is an artifact of attempting to move to the nearest ore and only following one step of any computed path. That one step moves the Dude closer to a different Plant, which results in the computation of a new path … that brings the Dude right back to the previous point.

Consider some alternatives (implementation of these is entirely optional; any such changes will be in the entity code, not in the pathing strategy).

Non-fickle: Once a path is computed, continue to follow that path as long as the target entity (e.g., ore) has not been collected by another. This approach skips the check for the “nearest target” as long as the previous target is available.

Determined: Once a path is computed, follow it to the end. This approach skips the check for “nearest target” until a new path must be computed.

Ol' College Try: Once a path is computed, follow it at least X steps (or until exhausted) before giving up. This approach skips the check for “nearest” target until it has consumed a fixed number of steps (e.g., five) in the current path (or it has consumed the entire path). After this initial effort, if the destination has not been reached, then check for the “nearest target” and compute a new path.

Warning
Of course, it is important to note that an implementation of any of these alternate approaches (since each continues to traverse a computed path) must take care to not move into an occupied cell. Keep in mind that the path was clear when it was originally computed, but other entities will move during this path traversal.

Grading
40% - Your code correctly uses the PathingStrategy interface and SingleStepPathingStrategy. Do this first and get it working before moving on to AStar!

60% - Your AStarPathingStrategy works correctly both with your project and in my own unit tests.

Submission
Submit your files on CANVAS