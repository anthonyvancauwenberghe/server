package org.hyperion.map.pathfinding;

/**
 * A description of an implementation that can find a path from one
 * location on a tile map to another based on information provided
 * by that tile map.
 *
 * @author Kevin Glass
 * @see TileBasedMap
 */
public interface PathFinder {
	Path findPath(int sx, int sy, int tx, int ty);
}