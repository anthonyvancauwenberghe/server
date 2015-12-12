package org.hyperion.rs2.model.newcombat;


/**
 * A container holds a group of items.
 *
 * @author Graham Edgecombe
 */
public class Container {

    /**
     * The capacity of this container.
     */
    private final int capacity;
    /**
     * The items in this container.
     */
    private final Item[] items;
    /**
     * The container type.
     */
    private final Type type;
    public boolean ignoreOnce = false;
    /**
     * Firing events flag.
     */
    private boolean firingEvents = true;

    /**
     * Creates the container with the specified capacity.
     *
     * @param type     The type of this container.
     * @param capacity The capacity of this container.
     */
    public Container(final Type type, final int capacity) {
        this.type = type;
        this.capacity = capacity;
        this.items = new Item[capacity];
    }

    /**
     * Checks the firing events flag.
     *
     * @return <code>true</code> if events are fired, <code>false</code> if
     * not.
     */
    public boolean isFiringEvents() {
        return firingEvents;
    }

    /**
     * Sets the firing events flag.
     *
     * @param firingEvents The flag.
     */
    public void setFiringEvents(final boolean firingEvents) {
        this.firingEvents = firingEvents;
    }

    /**
     * Gets the next free slot.
     *
     * @return The slot, or <code>-1</code> if there are no available slots.
     */
    public int freeSlot() {
        for(int i = 0; i < items.length; i++){
            if(items[i] == null){
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the number of free slots.
     *
     * @return The number of free slots.
     */
    public int freeSlots() {
        return capacity - size();
    }

    /**
     * Gets an item.
     *
     * @param index The position in the container.
     * @return The item.
     */
    public Item get(final int index) {
        return items[index];
    }

    /**
     * Gets the item id of the item at the given index.
     *
     * @param index The position in the container.
     * @return The item id if the item isn't null, -1 otherwise.
     */
    public int getItemId(final int index) {
        final Item item = get(index);
        if(item == null)
            return -1;
        return item.getId();
    }

    /**
     * Checks if the container contains the given item ids.
     *
     * @param ids
     * @return
     */
    public boolean contains(final int... ids) {
        for(final int id : ids){
            if(!contains(id))
                return false;
        }
        return true;
    }

    /**
     * Gets an item by id.
     *
     * @param id The id.
     * @return The item, or <code>null</code> if it could not be found.
     */
    public Item getById(final int id) {
        for(int i = 0; i < items.length; i++){
            if(items[i] == null){
                continue;
            }
            if(items[i].getId() == id){
                return items[i];
            }
        }
        return null;
    }

    /**
     * Gets a slot by id.
     *
     * @param id The id.
     * @return The slot, or <code>-1</code> if it could not be found.
     */
    public int getSlotById(final int id) {
        for(int i = 0; i < items.length; i++){
            if(items[i] == null){
                continue;
            }
            if(items[i].getId() == id){
                return i;
            }
        }
        return -1;
    }

    /**
     * Sets an item.
     *
     * @param index The position in the container.
     * @param item  The item.
     */
    public void set(final int index, final Item item) {
        items[index] = item;
    }

    /**
     * Gets the capacity of this container.
     *
     * @return The capacity of this container.
     */
    public int capacity() {
        return capacity;
    }

    /**
     * Gets the size of this container.
     *
     * @return The size of this container.
     */
    public int size() {
        int size = 0;
        for(int i = 0; i < items.length; i++){
            if(items[i] != null){
                size++;
            }
        }
        return size;
    }

    /**
     * Returns an array representing this container.
     *
     * @return The array.
     */
    public Item[] toArray() {
        return items;
    }

    /**
     * Checks if a slot is used.
     *
     * @param slot The slot.
     * @return <code>true</code> if an item is present, <code>false</code> otherwise.
     */
    public boolean isSlotUsed(final int slot) {
        return items[slot] != null;
    }

    /**
     * Checks if a slot is free.
     *
     * @param slot The slot.
     * @return <code>true</code> if an item is not present, <code>false</code> otherwise.
     */
    public boolean isSlotFree(final int slot) {
        return items[slot] == null;
    }

    /**
     * Gets the total amount of an item, including the items in stacks.
     *
     * @param id The id.
     * @return The amount.
     */
    public int getCount(final int id) {
        int total = 0;
        for(int i = 0; i < items.length; i++){
            if(items[i] != null){
                if(items[i].getId() == id){
                    total += items[i].getCount();
                }
            }
        }
        //System.out.println(total + "");
        return total;
    }

    /**
     * Removes an item.
     *
     * @param item The item to remove.
     * @return The number of items removed.
     */

    public int getSlot(final Item item) {
        for(int i = 0; i < items.length; i++){
            if(items[i] == item){
                return i;
            }
        }
        return -1;
    }

    /**
     * Checks if the container contains the specified item.
     *
     * @param id The item id.
     * @return <code>true</code> if so, <code>false</code> if not.
     */
    public boolean contains(final int id) {
        return getSlotById(id) != -1;
    }


    /**
     * The type of container.
     *
     * @author Graham Edgecombe
     */
    public enum Type {

        /**
         * A standard container such as inventory.
         */
        STANDARD,

        /**
         * A container which always stacks, e.g. the bank, regardless of the
         * item.
         */
        ALWAYS_STACK,

    }

    /**
     * Checks if there is room in the inventory for an item.
     * @param item The item.
     * @return <code>true</code> if so, <code>false</code> if not.
     */


}
