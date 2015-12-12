package org.hyperion.map.pathfinding;


/**
 * A simple test to show some path finding at unit
 * movement for a tutorial at http://www.cokeandcode.com
 *
 * @author Kevin Glass
 */
public class PathTest {
    public final static int maxRegionSize = 25;//*2 in reality
    private static PathTest tester;
    /**
     * The map on which the units will move
     */
    private final GameMap map = new GameMap();
    /**
     * The path finder we'll use to search our map
     */
    private final PathFinder finder;
    public int baseX = 0;
    public int baseY = 0;
    /**
     * The last path found for the current unit
     */
    private Path path;

    /**
     * Create a new test game for the path finding tutorial
     */
    public PathTest() {
        finder = new AStarPathFinder(map, 32, true);
        tester = this;
    }

    public static PathTest getSingleton() {
        return tester;
    }

    public Path getPath(final int x, final int y, int toX, int toY) {
        try{
            baseX = x - maxRegionSize;
            baseY = y - maxRegionSize;
            toX = (toX - baseX);
            toY = (toY - baseY);
            if(toX < 0 || toX > (maxRegionSize * 2) || toY < 0 || toY > (maxRegionSize * 2)){
                return null;
            }
            path = finder.findPath(maxRegionSize, maxRegionSize, toX, toY);
            return path;
        }catch(final Exception e){
            e.printStackTrace();
        }
        return null;
    }

}

