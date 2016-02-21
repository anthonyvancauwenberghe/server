package org.hyperion.map.pathfinding;


/**
 * A simple test to show some path finding at unit
 * movement for a tutorial at http://www.cokeandcode.com
 *
 * @author Kevin Glass
 */
public class PathTest {
	/**
	 * The map on which the units will move
	 */
	private GameMap map = new GameMap();
	/**
	 * The path finder we'll use to search our map
	 */
	private PathFinder finder;
	/**
	 * The last path found for the current unit
	 */
	private Path path;

	private static PathTest tester;

	/**
	 * Create a new test game for the path finding tutorial
	 */
	public PathTest() {
		finder = new AStarPathFinder(map, 32, true);
		tester = this;
	}

	public int baseX = 0;
	public int baseY = 0;

	public static PathTest getSingleton() {
		return tester;
	}

	public final static int maxRegionSize = 25;//*2 in reality

	public Path getPath(int x, int y, int toX, int toY) {
		try {
			baseX = x - maxRegionSize;
			baseY = y - maxRegionSize;
			toX = (toX - baseX);
			toY = (toY - baseY);
			if(toX < 0 || toX > (maxRegionSize * 2) || toY < 0 || toY > (maxRegionSize * 2)) {
				return null;
			}
			path = finder.findPath(maxRegionSize, maxRegionSize, toX, toY);
			return path;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

