package org.hyperion.rs2.model;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.content.minigame.CastleWars;
import org.hyperion.rs2.model.content.minigame.DangerousPK;
import org.hyperion.util.Misc;

/**
 * Represents a single location in the game world.
 *
 * @author Graham Edgecombe
 */
public class Location {

	/**
	 * The x coordinate.
	 */
	private final int x;

	/**
	 * The y coordinate.
	 */
	private final int y;

	/**
	 * The z coordinate.
	 */
	private final int z;

	/**
	 * Creates a location.
	 *
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 * @return The location.
	 */
	public static Location create(int x, int y, int z) {
		return new Location(x, y, z);
	}

	/**
	 * Creates a location.
	 *
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 */
	private Location(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	private Location() {
		this(0, 0, 0);
	}


	public Location getCloseLocation() {
		return new Location(x - 1 + Misc.random(3), y - 1 + Misc.random(3), z);
	}

	/**
	 * Gets the absolute x coordinate.
	 *
	 * @return The absolute x coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the absolute y coordinate.
	 *
	 * @return The absolute y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets the z coordinate, or height.
	 *
	 * @return The z coordinate.
	 */
	public int getZ() {
		return z;
	}

	/**
	 * Gets the local x coordinate relative to this region.
	 *
	 * @return The local x coordinate relative to this region.
	 */
	public int getLocalX() {
		return getLocalX(this);
	}

	/**
	 * Gets the local y coordinate relative to this region.
	 *
	 * @return The local y coordinate relative to this region.
	 */
	public int getLocalY() {
		return getLocalY(this);
	}

	/**
	 * Gets the local x coordinate relative to a specific region.
	 *
	 * @param l The region the coordinate will be relative to.
	 * @return The local x coordinate.
	 */
	public int getLocalX(Location l) {
		return x - 8 * l.getRegionX();
	}

	/**
	 * Gets the local y coordinate relative to a specific region.
	 *
	 * @param l The region the coordinate will be relative to.
	 * @return The local y coordinate.
	 */
	public int getLocalY(Location l) {
		return y - 8 * l.getRegionY();
	}

	/**
	 * Gets the region x coordinate.
	 *
	 * @return The region x coordinate.
	 */
	public int getRegionX() {
		return (x >> 3) - 6;
	}

	/**
	 * Gets the region y coordinate.
	 *
	 * @return The region y coordinate.
	 */
	public int getRegionY() {
		return (y >> 3) - 6;
	}

	/**
	 * Checks if this location is within range of another.
	 *
	 * @param other The other location.
	 * @return <code>true</code> if the location is in range,
	 * <code>false</code> if not.
	 */
	public boolean isWithinDistance(Location other, int dis) {
		if(z != other.z) {
			return false;
		}
		int deltaX = other.x - x, deltaY = other.y - y;
		//double distance = (double)Math.sqrt(deltaX*deltaX + deltaY*deltaY);
		//return distance <= dis;
		return ! (Math.abs(deltaX) > dis || Math.abs(deltaY) > dis);
	}

	/**
	 * Checks if this location is within range of another.
	 *
	 * @param other The other location.
	 * @return <code>true</code> if the location is in range,
	 * <code>false</code> if not.
	 */
	public boolean isWithinDistance(Location other) {
		if(z != other.z) {
			return false;
		}
		int deltaX = other.x - x, deltaY = other.y - y;
		return deltaX <= 14 && deltaX >= - 15 && deltaY <= 14 && deltaY >= - 15;
	}

	/**
	 * Checks if this location is within interaction range of another.
	 *
	 * @param other The other location.
	 * @return <code>true</code> if the location is in range,
	 * <code>false</code> if not.
	 */
	public boolean isWithinInteractionDistance(Location other) {
		if(z != other.z) {
			return false;
		}
		int deltaX = other.x - x, deltaY = other.y - y;
		return deltaX <= 2 && deltaX >= - 3 && deltaY <= 2 && deltaY >= - 3;
	}

	/**
	 * Checks if this location is within interaction range of another.
	 *
	 * @param other The other location.
	 * @return <code>true</code> if the location is in range,
	 * <code>false</code> if not.
	 */
	public int distance(Location other) {
		int deltaX = other.x - x, deltaY = other.y - y;
		//double dis = Math.sqrt(Math.pow(deltaX, 2D) + Math.pow(deltaY, 2D));
		double dis = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		if(dis > 1.0 && dis < 2)
			return 2;
		return (int) dis;
	}

	@Override
	public int hashCode() {
		return z << 30 | x << 15 | y;
	}

	@Override
	public boolean equals(Object other) {
		if(! (other instanceof Location)) {
			return false;
		}
		Location loc = (Location) other;
		return loc.x == x && loc.y == y && loc.z == z;
	}

	@Override
	public String toString() {
		return x + "	" + y + "	" + z;
	}

	/**
	 * Creates a new location based on this location.
	 *
	 * @param diffX X difference.
	 * @param diffY Y difference.
	 * @param diffZ Z difference.
	 * @return The new location.
	 */
	public Location transform(int diffX, int diffY, int diffZ) {
		return Location.create(x + diffX, y + diffY, z + diffZ);
	}

	/**
	 * Checks if we're in a specific arena based on location objects.
	 *
	 * @param minLocation The min location to check.
	 * @param maxLocation The max location to check.
	 * @return True if we're in the area, false it not.
	 */
	public boolean isInArea(Location minLocation, Location maxLocation) {
		return isInArea(x, y, z, minLocation.getX(), minLocation.getY(), minLocation.getZ(), maxLocation.getX(), maxLocation.getY(), maxLocation.getZ());
	}

	/**
	 * Checks if we're in a specific arena based on simple coordinates.
	 *
	 * @param minX      The minimum x coordinate.
	 * @param minY      The minimum y coordinate.
	 * @param minHeight the minimum height.
	 * @param maxX      The maximum x coordinate.
	 * @param maxY      The maximum y coordinate.
	 * @param maxHeight The maximum height.
	 * @return True if we're in the area, false it not.
	 */
	public static boolean isInArea(int x, int y, int z, int minX, int minY, int minHeight, int maxX, int maxY, int maxHeight) {
		if(z != minHeight || z != maxHeight) {
			return false;
		}
		return (x >= minX && y >= minY) && (x <= maxX && y <= maxY);
	}

	/**
	 * Checks whether the player is in the ardy PvP area.
	 *
	 * @return
	 */
	public boolean inArdyPvPArea() {
		if(x >= 2649 && x <= 2658) {
			if(y >= 3280 && y <= 3287)
				return false;
		}
		if(x >= 2597 && x <= 2687) {
			if(y >= 3262 && y <= 3332)
				return true;
		}
		return false;
	}

    /**
     * Checks whether the player is in the Corporal Beast area.
     *
     * @return
     */
    public boolean inCorpBeastArea() {
        //Corporal beast area
        if(x >= 2350 && x <= 2540 && y >= 4625 && y <= 4665) {
            return true;
        }
        //donator place area
        if(x >= 2343 && x <= 2354 && y >= 9823 && y <= 9834) {
            return true;
        }
        else {
            return false;
        }
    }

	/**
	 * Checks whether a player is in the Fun pk area.
	 *
	 * @return
	 */
	public boolean inFunPk() {
		/**
		 * Old funpk
		 */
		/*if(x >= 2420 && x <= 2557 && y >= 3264 && y <= 3335)
			return true;
		return (x >= 3271 && x <= 3307 && y >= 3012 && y <= 3039);*/
		return x >= 2580 && y >= 3152 && x <= 2608 && y <= 3169;
	}

	/**
	 * In ardy PvP area of wilderness.
	 *
	 * @return
	 */
	public boolean inPvPArea() {
		return Combat.getWildLevel(x, y) > 0 || inFunPk();
	}
	public boolean disabledMagic() {
		return x >= 3072 && y >= 3519 && x <= 3108 && y <= 3543;
	}
	public boolean disabledRange() {
		return x >= 2957 && y >= 3579 && x <= 2992 && y <= 3614;
	}
	public static boolean inAttackableArea(Player player) {
		if(player == null || player.cE == null)
			return false;
		return 	player.getLocation().inPvPArea()
				|| World.getWorld().getContentManager().handlePacket(6, player, 30000, - 1, - 1, - 1)
				|| player.duelAttackable > 0
				|| CastleWars.getCastleWars().isInGame(player)
				|| (player.cE.getAbsX() >= 2460 && player.cE.getAbsX() <= 2557 && player.cE.getAbsY() >= 3264 && player.cE.getAbsY() <= 3335)
				|| (player.cE.getAbsX() >= 3271 && player.cE.getAbsX() <= 3307 && player.cE.getAbsY() >= 3012 && player.cE.getAbsY() <= 3039) 
				|| WalkingQueue.hasSnowball(player)
				|| DangerousPK.inDangerousPK(player)
				|| OSPK.inArea(player);
	}
	
	public boolean cannotMax() { 
		return inPvPArea() ||
				(x <= 2382 && y >= 4940 && y <= 4980 && x >= 2347);
	}

    public boolean inDungeonLobby() {
        return false;
    }
	
	public boolean inDuel() {
		return x >= 3353
				&& y >= 3264
				&& x <= 3385
				&& y <= 3283;
	}

	static {
		CommandHandler.submit(new Command("ardypvp", Rank.PLAYER) {
			@Override
			public boolean execute(Player player, String input) throws Exception {
				Magic.teleport(player, 2663, 3307, 0, false);
				return true;
			}

		});
	}
}
