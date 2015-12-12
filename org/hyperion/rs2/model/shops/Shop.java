package org.hyperion.rs2.model.shops;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.ShopManager;

/**
 * @author Arsen Maxyutov.
 */
public abstract class Shop {

    public static final int LEGENDARY_TICKET = 13663;

    public static final int COINS_ID = 995;

    public static final int MAX_STATIC_ITEMS = 40;

    public static final int SHOP_INTERFACE_ID = 3900;

    private final Item[] static_items = new Item[MAX_STATIC_ITEMS];

    private final String name;

    private final Container container;

    private final int id;

    private final boolean general;

    /**
     * Constructs a new shop.
     *
     * @param name
     * @param container
     */
    public Shop(final int id, final String name, final Container container, final boolean general) {
        this.id = id;
        this.container = container;
        this.name = name.replace("_", " ");
        this.general = general;
    }

    /**
     * Gets the shop for the id.
     *
     * @param id
     * @return
     */
    public static Shop forId(final int id) {
        return ShopManager.forId(id);
    }

    public boolean isGeneral() {
        return general;
    }

    public Item[] getStaticItems() {
        return static_items;
    }

    /**
     * Gets the item at the specified index.
     *
     * @param index
     * @return
     */
    public Item get(final int index) {
        return static_items[index];
    }

    /**
     * Gets the static item with the specified id
     *
     * @param id
     * @return the item if found, null if not.
     */
    public Item getStaticItem(final int id) {
        for(final Item item : static_items){
            if(item != null){
                if(item.getId() == id)
                    return item;
            }
        }
        return null;
    }

    public boolean isStatic(final int id) {
        for(final Item item : static_items){
            if(item != null){
                if(item.getId() == id)
                    return true;
            }
        }
        return false;
    }

    public void addStaticItem(final Item item) {
        if(item == null)
            return;
        if(item.getId() < 1)
            return;
        for(int i = 0; i < static_items.length; i++){
            if(static_items[i] == null){
                static_items[i] = item;
                break;
            }
        }
    }

    public Container getContainer() {
        return container;
    }

    public String getName() {
        return name;
    }

    public void updatePlayers() {
        for(final Player player : World.getWorld().getPlayers()){
            if(player == null)
                continue;
            if(this.id == player.getShopId()){
                player.getActionSender().sendUpdateItems(SHOP_INTERFACE_ID, container.toArray());
            }
        }
    }

    public abstract void sellToShop(Player player, Item item);

    public abstract void buyFromShop(Player player, Item item);

    public abstract void valueBuyItem(Player player, Item item);

    public abstract void valueSellItem(Player player, Item item);

    public abstract void process();

}
