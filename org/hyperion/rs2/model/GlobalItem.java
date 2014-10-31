package org.hyperion.rs2.model;

public class GlobalItem {
	private Location location;

	private Item item;

	public GlobalItem(Player player, int x, int y, int z, Item item) {
		location = Location.create(x, y, z);
		this.item = item;
		this.owner = player;
	}

	public void destroy() {
		owner = null;
		item = null;
		location = null;
	}

	public GlobalItem(Player player, Location loc, Item item) {
		location = loc;
		this.item = item;
		this.owner = player;
	}

	public Player owner;

	public Location getLocation() {
		return location;
	}

	public Item getItem() {
		return item;
	}

	public void setNewItem(Item item) {
		this.item = item;
	}

	public long createdTime = System.currentTimeMillis();

	public boolean itemHidden = true;
}
