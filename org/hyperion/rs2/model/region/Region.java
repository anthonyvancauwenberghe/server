package org.hyperion.rs2.model.region;

import org.hyperion.rs2.model.GameObject;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a single region.
 *
 * @author Graham Edgecombe
 */
public class Region {

	/**
	 * The region coordinates.
	 */
	private RegionCoordinates coordinate;

	/**
	 * A list of players in this region.
	 */
	private List<Player> players = new LinkedList<Player>();

	/**
	 * A list of NPCs in this region.
	 */
	private List<NPC> npcs = new LinkedList<NPC>();

	/**
	 * A list of objects in this region.
	 */
	private List<GameObject> objects = new LinkedList<GameObject>();

	/**
	 * Creates a region.
	 *
	 * @param coordinate The coordinate.
	 */
	public Region(RegionCoordinates coordinate) {
		this.coordinate = coordinate;
	}

	/**
	 * Gets the region coordinates.
	 *
	 * @return The region coordinates.
	 */
	public RegionCoordinates getCoordinates() {
		return coordinate;
	}

	/**
	 * Gets the list of players.
	 *
	 * @return The list of players.
	 */
	public Collection<Player> getPlayers() {
		return Collections.unmodifiableCollection(new LinkedList<>(players));
	}

	/**
	 * Gets the list of NPCs.
	 *
	 * @return The list of NPCs.
	 */
	public Collection<NPC> getNpcs() {
		return Collections.unmodifiableCollection(new LinkedList<>(npcs));
	}

	/**
	 * Gets the list of objects.
	 *
	 * @return The list of objects.
	 */
	public Collection<GameObject> getGameObjects() {
		return objects;
	}

	/**
	 * Adds a new player.
	 *
	 * @param player The player to add.
	 */
	public void addPlayer(Player player) {
		players.add(player);
	}

	/**
	 * Removes an old player.
	 *
	 * @param player The player to remove.
	 */
	public void removePlayer(Player player) {
		players.remove(player);
	}

	/**
	 * Adds a new NPC.
	 *
	 * @param npc The NPC to add.
	 */
	public void addNpc(NPC npc) {
		if(! npcs.contains(npc))
			npcs.add(npc);
	}

	/**
	 * Removes an old NPC.
	 *
	 * @param npc The NPC to remove.
	 */
	public void removeNpc(NPC npc) {
		npcs.remove(npc);
	}

}
