package org.hyperion.rs2.model;

public class GlobalItem {
    public Player owner;
    public long createdTime = System.currentTimeMillis();
    public boolean itemHidden = true;
    private Location location;
    private Item item;

    public GlobalItem(final Player player, final int x, final int y, final int z, final Item item) {
        location = Location.create(x, y, z);
        this.item = item;
        this.owner = player;
    }

    public GlobalItem(final Player player, final Location loc, final Item item) {
        location = loc;
        this.item = item;
        this.owner = player;
    }

    public void destroy() {
        owner = null;
        item = null;
        location = null;
    }

    public Location getLocation() {
        return location;
    }

    public Item getItem() {
        return item;
    }

    public void setNewItem(final Item item) {
        this.item = item;
    }
}
